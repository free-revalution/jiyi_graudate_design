<template>
    <div class="page-container">
        <div class="card pt3 pb3 mb10 tab-header">
            <div class="tab-with-back">
                <el-button type="warning" plain :icon="ArrowLeft" @click="goBack">{{ currentClassName || '返回班级列表' }}</el-button>
                <el-tabs v-model="classTabActive">
                    <el-tab-pane label="班级管理" name="a" />
                    <el-tab-pane label="教师团队管理" name="b" />
                    <el-tab-pane label="成绩权重" name="c" />
                    <el-tab-pane label="课程管理" name="d" />
                    <el-tab-pane label="操作日志" name="e" />
                    <el-tab-pane label="课程评审" name="f" />
                </el-tabs>
            </div>
        </div>

        <!-- 班级管理 -->
        <div v-if="classTabActive === 'a'" class="main-box">
            <!-- 左侧班级列表 -->
            <div class="card class-list-card">
                <div class="class-header">
                    <el-button type="primary" link :icon="CirclePlus">新建班级</el-button>
                    <el-button type="primary" link :icon="Setting">管理班级</el-button>
                </div>
                <div class="class-search">
                    <el-input v-model="searchClass" placeholder="搜索班级" :suffix-icon="Search" />
                </div>
                <div class="class-content">
                    <div v-for="item in classList" :key="item.id" class="class-item"
                        :class="{ active: currentClassId === item.id }" @click="selectClass(item)">
                        <span class="class-name">{{ item.name }}</span>
                        <el-dropdown trigger="click" @command="(cmd: string) => handleClassCommand(cmd, item)">
                            <el-icon class="more-icon" @click.stop>
                                <MoreFilled />
                            </el-icon>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="edit">编辑</el-dropdown-item>
                                    <el-dropdown-item command="delete">删除</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </div>
            </div>
            <!-- 右侧表格 -->
            <div class="table-box">
                <ProTable ref="proTable" :columns="columns" :data="studentList" :init-param="initParam"
                    :tool-button="['refresh', 'search']" :search-col="{ xs: 1, sm: 1, md: 2, lg: 3, xl: 3 }">
                    <!-- 表格 header 按钮 -->
                    <template #tableHeader>
                        <el-button type="primary" :icon="CirclePlus">新增学生</el-button>
                        <el-button type="primary" :icon="User" plain>分组管理</el-button>
                        <el-button type="primary" :icon="Download" plain>导出学生名单</el-button>
                    </template>
                    <!-- 表格操作 -->
                    <template #operation>
                        <el-button type="primary" link :icon="View">查看</el-button>
                        <el-button type="primary" link :icon="Delete">移除</el-button>
                    </template>
                </ProTable>
            </div>
        </div>

        <!-- 成绩权重 -->
        <div v-else-if="classTabActive === 'c'" class="card weight-container">
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

        <!-- 其他 tab 占位 -->
        <div v-else class="card other-tab-placeholder">
            <el-empty description="功能开发中..." />
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
import { getStudents, getClasses } from "@/api/modules/course";

const router = useRouter();
const route = useRoute();

const COURSE_ID = Number(route.query.courseId) || 1;

// ProTable 实例
const proTable = ref<ProTableInstance>();
const initParam = reactive({ departmentId: "1" });

// 当前 tab
const classTabActive = ref("a");

/** 返回上一页 */
const goBack = () => router.back();

// 班级搜索
const searchClass = ref("");
// 当前选中班级（从路由参数获取）
const currentClassId = ref(Number(route.params.id) || 1);
// 返回按钮显示的班级名称
const currentClassName = ref("");
// 班级列表
const classList = ref<any[]>([]);
// 学生列表
const studentList = ref<any[]>([]);

/** 加载班级列表 */
const fetchClassList = async () => {
    try {
        const res: any = await getClasses(COURSE_ID);
        classList.value = res.data || [];
        // 设置返回按钮的班级名称
        const target = classList.value.find((c: any) => c.id === currentClassId.value);
        if (target) {
            currentClassName.value = target.name;
        }
    } catch (error) {
        ElMessage.error("获取班级列表失败");
    }
};

/** 加载学生列表 */
const fetchStudentList = async (classId: number) => {
    try {
        const res: any = await getStudents(COURSE_ID, classId);
        studentList.value = res.data || [];
    } catch (error) {
        ElMessage.error("获取学生列表失败");
    }
};

/** 选择班级 */
const selectClass = (item: any) => {
    currentClassId.value = item.id;
    currentClassName.value = item.name;
    fetchStudentList(item.id);
};

onMounted(() => {
    fetchClassList();
    fetchStudentList(currentClassId.value);
});

/** 处理班级操作 */
const handleClassCommand = (command: string, item: any) => {
    console.log(command, item);
};

// 表格配置项
const columns = reactive<ColumnProps<any>[]>([
    { type: "index", label: "#", width: 80 },
    { prop: "name", label: "姓名", width: 120, search: { el: "input" } },
    { prop: "studentId", label: "学号/工号" },
    { prop: "department", label: "院系" },
    { prop: "major", label: "专业" },
    { prop: "className", label: "班级" },
    { prop: "joinTime", label: "加入时间", width: 180 },
    { prop: "operation", label: "操作", width: 160, fixed: "right" }
]);

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
const weightTableData = ref<WeightRow[]>([
    { id: 1, dimension: "课堂表现", dimensionScore: 20, subDimension: "出勤率", subScore: 10, standard: "KPI各项指标达成情况。不合格:2分;待改进:4分;一般:6分;优良:8分;优秀:10分" },
    { id: 2, dimension: "课堂表现", dimensionScore: 20, subDimension: "课堂互动", subScore: 10, standard: "KPI各项指标达成情况。不合格:2分;待改进:4分;一般:6分;优良:8分;优秀:10分" },
    { id: 3, dimension: "作业成绩", dimensionScore: 30, subDimension: "平时作业", subScore: 15, standard: "1、熟悉本公司工业自动化产品，包括性能、技术原理、应用领域、竟争优劣势。2、理解公司产品解决方案，并有推广复制成功经验。不合格:2分;待改进:4分;一般:6分:优良:8分:优秀:10分" },
    { id: 4, dimension: "作业成绩", dimensionScore: 30, subDimension: "实验报告", subScore: 15, standard: "1、熟悉本公司工业自动化产品，包括性能、技术原理、应用领域、竟争优劣势。2、理解公司产品解决方案，并有推广复制成功经验。不合格:2分;待改进:4分;一般:6分:优良:8分:优秀:10分" },
    { id: 5, dimension: "实训成绩", dimensionScore: 30, subDimension: "实训任务完成度", subScore: 20, standard: "按实训任务完成情况评分" },
    { id: 6, dimension: "实训成绩", dimensionScore: 30, subDimension: "代码质量", subScore: 10, standard: "按代码规范性、可读性评分" },
    { id: 7, dimension: "期末考核", dimensionScore: 20, subDimension: "期末考试", subScore: 20, standard: "按期末考试卷面成绩计算" }
]);

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
const saveWeight = () => {
    ElMessage.info("待开发");
};
/**重置 */
const resetWeight = () => {
    ElMessage.info("待开发");
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

.tab-with-back {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 15px;

    .el-tabs {}
}

.main-box {
    display: flex;
    gap: 10px;
    flex: 1;
    min-height: 0;
    overflow: hidden;
}

.class-list-card {
    width: 280px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .class-header {
        display: flex;
        gap: 20px;
        padding-bottom: 15px;
        border-bottom: 1px solid #ebeef5;
    }

    .class-search {
        padding: 15px 0;
    }

    .class-content {
        flex: 1;
        overflow-y: auto;

        .class-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 12px 10px;
            cursor: pointer;
            border-radius: 4px;
            transition: all 0.2s;

            &:hover {
                background: #f5f7fa;
            }

            &.active {
                background: var(--el-color-primary-light-9);
                color: var(--el-color-primary);
            }

            .class-name {
                flex: 1;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                font-size: 14px;
            }

            .more-icon {
                font-size: 14px;
                color: #909399;
                cursor: pointer;

                &:hover {
                    color: var(--el-color-primary);
                }
            }
        }
    }
}

.table-box {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;
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
