<template>
    <div class="page-container">

        <!-- 成绩权重 -->
        <div class="card weight-container">
            <div class="weight-header">
                <div class="weight-actions">
                    <el-button type="primary" :icon="CirclePlus" @click="addDimension">添加维度</el-button>
                    <el-button type="primary" :icon="CirclePlus" @click="addSubDimension">添加子维度</el-button>
                </div>
                <div class="weight-actions">
                    <el-button type="success" :icon="Setting" @click="saveWeight">保存</el-button>
                    <el-button :icon="Refresh" @click="resetWeight">重置</el-button>
                </div>
            </div>
            <el-table :data="weightTableData" :span-method="weightSpanMethod" border
                :header-cell-style="{ background: '#f5f7fa', color: '#606266' }" class="weight-table">
                <!-- 勾选框列 -->
                <el-table-column width="60" align="center" label="选择">
                    <template #default="{ row, $index }">
                        <el-checkbox v-if="getDimensionStartIndex(row.dimension) === $index"
                            v-model="selectedDimensions[row.dimension]" :disabled="row.isEditing" />
                    </template>
                </el-table-column>
                <!-- 评分维度 -->
                <el-table-column prop="dimension" label="评分维度" width="200" align="center">
                    <template #default="{ row }">
                        <el-input v-if="row.isEditing" autosize type="textarea" v-model="row.dimension"
                            placeholder="评分维度" size="small" />
                        <span v-else>{{ row.dimension }}</span>
                    </template>
                </el-table-column>
                <!-- 维度分值 -->
                <el-table-column prop="dimensionScore" label="分值" width="150" align="center">
                    <template #default="{ row }">
                        <el-input-number v-if="row.isEditing" v-model="row.dimensionScore" :min="0" :max="100"
                            size="small" controls-position="right" />
                        <span v-else>{{ row.dimensionScore }}</span>
                    </template>
                </el-table-column>
                <!-- 评分子维度 -->
                <el-table-column prop="subDimension" label="评分子维度" min-width="150" align="center">
                    <template #default="{ row }">
                        <el-input v-if="row.isEditing" autosize type="textarea" v-model="row.subDimension"
                            placeholder="评分子维度" size="small" />
                        <span v-else>{{ row.subDimension }}</span>
                    </template>
                </el-table-column>
                <!-- 子维度分值 -->
                <el-table-column prop="subScore" label="分值" width="150" align="center">
                    <template #default="{ row }">
                        <el-input-number v-if="row.isEditing" v-model="row.subScore" :min="0" :max="100" size="small"
                            controls-position="right" />
                        <span v-else>{{ row.subScore }}</span>
                    </template>
                </el-table-column>
                <!-- 评分标准 -->
                <el-table-column prop="standard" label="评分标准" min-width="250" align="center">
                    <template #default="{ row }">
                        <el-input v-if="row.isEditing" autosize type="textarea" v-model="row.standard"
                            placeholder="评分标准" size="small" />
                        <span v-else>{{ row.standard }}</span>
                    </template>
                </el-table-column>
                <!-- 操作 -->
                <el-table-column label="操作" width="180" align="center" fixed="right">
                    <template #default="{ row, $index }">
                        <template v-if="row.isEditing">
                            <el-button type="success" link :icon="Check"
                                @click="saveWeightRow(row, $index)">保存</el-button>
                            <el-button type="info" link :icon="Close"
                                @click="cancelWeightRow(row, $index)">取消</el-button>
                        </template>
                        <template v-else>
                            <el-button type="primary" link :icon="EditPen" @click="editWeightRow(row)">编辑</el-button>
                            <el-button type="danger" link :icon="Delete"
                                @click="deleteWeightRow(row, $index)">删除</el-button>
                        </template>
                    </template>
                </el-table-column>
            </el-table>
        </div>

    </div>
</template>
<script setup lang="ts" name="useTreeFilter">
import { ref, reactive, onMounted } from "vue";
import ProTable from "@/components/ProTable/index.vue";
import { ProTableInstance, ColumnProps } from "@/components/ProTable/interface";
import { CirclePlus, Delete, Download, View, User, Refresh, Setting, Search, MoreFilled, EditPen, Check, Close, ArrowLeft } from "@element-plus/icons-vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { getScoreWeight, saveScoreWeight } from "@/api/modules/scoreWeight";

const route = useRoute();
const COURSE_ID = Number(route.query.courseId) || 1;


// ==================== 成绩权重相关 ====================
interface WeightRow {
    id: number;
    dimension: string;
    dimensionScore: number;
    subDimension: string;
    subScore: number;
    standard: string;
    isEditing?: boolean;
    isNew?: boolean;
    _backup?: Partial<WeightRow>;
}

/** 成绩权重表格数据 */
const weightTableData = ref<WeightRow[]>([]);

/** 加载成绩权重 */
const fetchScoreWeight = async () => {
    try {
        const res: any = await getScoreWeight(COURSE_ID);
        weightTableData.value = res.data || [];
    } catch (error) {
        ElMessage.error("获取成绩权重失败");
    }
};

onMounted(() => {
    fetchScoreWeight();
});

/** 选中的维度 */
const selectedDimensions = ref<Record<string, boolean>>({});

/** 获取维度的起始行索引 */
const getDimensionStartIndex = (dimension: string) => {
    return weightTableData.value.findIndex(item => item.dimension === dimension);
};

/** 获取维度的最后一行索引 */
const getDimensionEndIndex = (dimension: string) => {
    let lastIndex = -1;
    weightTableData.value.forEach((item, index) => {
        if (item.dimension === dimension) lastIndex = index;
    });
    return lastIndex;
};

/**
 * 计算单元格合并
 * @description 勾选框、评分维度按维度合并；维度分值、评分标准在同一维度内连续相同时合并
 * @note 编辑中的行或空维度名不参与合并
 */
const weightSpanMethod = ({ row, column, rowIndex }: { row: WeightRow; column: any; rowIndex: number }) => {
    const data = weightTableData.value;
    const currentDimension = row.dimension;

    // 编辑中的行或空维度名不参与合并
    if (row.isEditing || !currentDimension.trim()) {
        return { rowspan: 1, colspan: 1 };
    }

    // 获取当前维度的起始和结束索引（排除编辑中和空维度的行）
    const getDimensionRange = (dimension: string) => {
        let start = -1, end = -1;
        for (let i = 0; i < data.length; i++) {
            const item = data[i];
            // 跳过编辑中或空维度的行
            if (item.isEditing || !item.dimension.trim()) continue;
            if (item.dimension === dimension) {
                if (start === -1) start = i;
                end = i;
            } else if (start !== -1) break;
        }
        return { start, end };
    };

    // 在同一维度内计算连续相同值的合并
    const calcMergeInDimension = (value: any, compareKey: keyof WeightRow) => {
        const { start: dimStart, end: dimEnd } = getDimensionRange(currentDimension);
        if (dimStart === -1) return { rowspan: 1, colspan: 1 };
        // 向上查找连续相同值的起始行
        let startIndex = rowIndex;
        while (startIndex > dimStart) {
            const prevRow = data[startIndex - 1];
            if (prevRow.isEditing || !prevRow.dimension.trim()) break;
            if (prevRow[compareKey] !== value || prevRow.dimension !== currentDimension) break;
            startIndex--;
        }
        // 计算连续相同值的行数
        let rowspan = 1;
        for (let i = startIndex + 1; i <= dimEnd; i++) {
            const nextRow = data[i];
            if (nextRow.isEditing || !nextRow.dimension.trim()) break;
            if (nextRow[compareKey] !== value || nextRow.dimension !== currentDimension) break;
            rowspan++;
        }
        if (rowIndex === startIndex) {
            return { rowspan, colspan: 1 };
        } else {
            return { rowspan: 0, colspan: 0 };
        }
    };

    // 勾选框列（label为"选择"）、评分维度列 - 按维度合并
    const isCheckboxColumn = column.label === "选择";
    if (column.property === "dimension" || isCheckboxColumn) {
        const { start, end } = getDimensionRange(currentDimension);
        if (start === -1) return { rowspan: 1, colspan: 1 };
        const rowspan = end - start + 1;
        if (rowIndex === start) {
            return { rowspan, colspan: 1 };
        } else if (rowIndex > start && rowIndex <= end) {
            return { rowspan: 0, colspan: 0 };
        }
    }

    // 维度分值列 - 在同一维度内，连续相同的分值合并
    if (column.property === "dimensionScore") {
        return calcMergeInDimension(row.dimensionScore, "dimensionScore");
    }

    // 评分标准列 - 在同一维度内，连续相同的标准合并
    if (column.property === "standard") {
        return calcMergeInDimension(row.standard, "standard");
    }

    return { rowspan: 1, colspan: 1 };
};

/** 添加新维度 */
const addDimension = () => {
    const newRow: WeightRow = {
        id: Date.now(),
        dimension: "",
        dimensionScore: 0,
        subDimension: "",
        subScore: 0,
        standard: "",
        isEditing: true,
        isNew: true
    };
    weightTableData.value.push(newRow);
};

/** 添加子维度 */
const addSubDimension = () => {
    const selectedKeys = Object.keys(selectedDimensions.value).filter(key => selectedDimensions.value[key]);
    if (selectedKeys.length === 0) {
        ElMessage.warning("请先选择一个评分维度");
        return;
    }
    if (selectedKeys.length > 1) {
        ElMessage.warning("只能选择一个评分维度");
        return;
    }
    const dimension = selectedKeys[0];
    const endIndex = getDimensionEndIndex(dimension);
    const refRow = weightTableData.value[endIndex];
    const newRow: WeightRow = {
        id: Date.now(),
        dimension: refRow.dimension,
        dimensionScore: refRow.dimensionScore,
        subDimension: "",
        subScore: 0,
        standard: "",
        isEditing: true,
        isNew: true
    };
    weightTableData.value.splice(endIndex + 1, 0, newRow);
};

/** 编辑行 */
const editWeightRow = (row: WeightRow) => {
    row._backup = { dimension: row.dimension, dimensionScore: row.dimensionScore, subDimension: row.subDimension, subScore: row.subScore, standard: row.standard };
    row.isEditing = true;
};

/** 保存行 */
const saveWeightRow = (row: WeightRow, index: number) => {
    if (!row.dimension.trim()) {
        ElMessage.warning("请输入评分维度");
        return;
    }
    if (!row.subDimension.trim()) {
        ElMessage.warning("请输入评分子维度");
        return;
    }
    row.isEditing = false;
    row.isNew = false;
    delete row._backup;
    // 同步同一维度的其他行的维度名称和分值
    weightTableData.value.forEach(item => {
        if (item.dimension === row.dimension && item.id !== row.id) {
            item.dimensionScore = row.dimensionScore;
        }
    });
    ElMessage.success("保存成功");
};

/** 取消编辑 */
const cancelWeightRow = (row: WeightRow, index: number) => {
    if (row.isNew) {
        weightTableData.value.splice(index, 1);
    } else if (row._backup) {
        Object.assign(row, row._backup);
        row.isEditing = false;
        delete row._backup;
    }
};

/** 删除行 */
const deleteWeightRow = (row: WeightRow, index: number) => {
    ElMessageBox.confirm(`确定要删除该评分子维度"${row.subDimension}"吗？`, "删除确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    }).then(() => {
        weightTableData.value.splice(index, 1);
        ElMessage.success("删除成功");
    }).catch(() => { });
};
/**保存 */
const saveWeight = async () => {
    try {
        await saveScoreWeight(COURSE_ID, weightTableData.value);
        ElMessage.success("保存成功");
    } catch (error) {
        ElMessage.error("保存失败");
    }
};
/**重置 */
const resetWeight = () => {
    fetchScoreWeight();
};
</script>

<style scoped lang="scss">
.page-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow: hidden;

    .tab-header {
        flex-shrink: 0;
    }
}


/*去掉tabs底部的下划线*/
:deep(.el-tabs__nav-wrap::after) {
    background-color: transparent !important;
}

:deep(.el-tabs__header) {
    margin-bottom: 6px !important;
}

/* 成绩权重样式 */
.weight-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .weight-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        flex-shrink: 0;

        .weight-title {
            font-size: 16px;
            font-weight: bold;
            color: #303133;
        }
    }

    .weight-table {
        flex: 1;
    }
}

/* 其他 tab 占位 */
.other-tab-placeholder {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>
