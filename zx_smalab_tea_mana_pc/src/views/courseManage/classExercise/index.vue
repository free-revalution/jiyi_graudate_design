<template>
    <div class="table-box">
        <ProTable ref="proTable" :columns="columns" :data="exerciseList" row-key="id">
            <!-- 表格 header 按钮 -->
            <template #tableHeader="scope">
                <el-button type="primary" :icon="CirclePlus" @click="addExercise">新建练习</el-button>
                <el-button type="danger" :icon="Delete" plain :disabled="!scope.isSelected"
                    @click="batchDelete(scope.selectedListIds)">批量删除</el-button>
            </template>
            <template #operation="scope">
                <el-button type="primary" link :icon="View" @click="toDetail(scope.row)">详情</el-button>
                <el-button type="primary" link :icon="EditPen">编辑</el-button>
                <el-button type="danger" link :icon="Delete" @click="deleteExercise(scope.row)">删除</el-button>
            </template>
        </ProTable>

    </div>
</template>

<script setup lang="tsx" name="courseManageClassExercise">
import { ref, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox, ElTag } from "element-plus";
import ProTable from "@/components/ProTable/index.vue";
import { ProTableInstance, ColumnProps } from "@/components/ProTable/interface";
import { CirclePlus, Delete, EditPen, View } from "@element-plus/icons-vue";
import { getExerciseList } from "@/api/modules/exercise";

const router = useRouter();
const route = useRoute();
const COURSE_ID = Number(route.query.courseId) || 1;

// ProTable 实例
const proTable = ref<ProTableInstance>();

// 表格配置项
const columns = reactive<ColumnProps<any>[]>([
    { type: "selection", fixed: "left", width: 70 },
    { prop: "name", label: "练习名称", search: { el: "input" } },
    { prop: "questionCount", label: "题目数量", width: 90, align: "center" },
    { prop: "startTime", label: "开始时间", width: 160 },
    { prop: "endTime", label: "结束时间", width: 160 },
    {
        prop: "status",
        label: "状态",
        width: 100,
        align: "center",
        render: (scope: any): any => {
            const statusMap: Record<string, { text: string; type: string }> = {
                "未发布": { text: "未发布", type: "info" },
                "未开始": { text: "未开始", type: "warning" },
                "进行中": { text: "进行中", type: "success" },
                "已截止": { text: "已截止", type: "danger" }
            };
            const status = statusMap[scope.row.status] || { text: "未知", type: "info" };
            return <ElTag type={status.type}>{status.text}</ElTag>;
        }
    },
    { prop: "completedCount", label: "已完成", width: 80, align: "center" },
    { prop: "uncompletedCount", label: "未完成", width: 80, align: "center" },
    { prop: "createTime", label: "创建时间", width: 160 },
    { prop: "operation", label: "操作", fixed: "right", width: 220 }
]);

/** 练习列表数据 */
const exerciseList = ref<any[]>([]);

/** 加载练习列表 */
const fetchExerciseList = async () => {
    try {
        const res: any = await getExerciseList(COURSE_ID, {});
        const data = res.data || {};
        exerciseList.value = data.list || data || [];
    } catch (error) {
        ElMessage.error("获取练习列表失败");
    }
};

onMounted(() => {
    fetchExerciseList();
});


/** 查看详情 */
const toDetail = (row: any) => {
    // router.push(`/courseManage/classExercise/detail/${row.id}`);
    ElMessage.info("待开发");
};
/**新建练习 */
const addExercise = (row: any) => {
    router.push({
        path: "/courseManage/classExercise/action/add",
        query: { courseId: COURSE_ID }
    });
};

/** 删除练习 */
const deleteExercise = (row: any) => {
    ElMessageBox.confirm(`确定要删除练习"${row.name}"吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    }).then(() => {
        ElMessage.success("删除成功");
    });
};

/** 批量删除 */
const batchDelete = (ids: string[]) => {
    ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 个练习吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    }).then(() => {
        ElMessage.success("批量删除成功");
        proTable.value?.clearSelection();
    });
};
</script>

<style scoped lang="scss">
.exercise-dialog-content {
    margin-top: 20px;
}

.exercise-input {
    width: 100%;
}

:deep(.exercise-input .el-input__wrapper) {
    border: none !important;
    border-bottom: 1px solid #e0e0e0 !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}

:deep(.el-dialog .el-dialog__header) {
    border-bottom: 1px solid var(--el-color-primary) !important;
}

:deep(.el-dialog__title) {
    color: var(--el-color-primary);
}
</style>
