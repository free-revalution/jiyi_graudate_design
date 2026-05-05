<template>
    <div class="table-box">
        <ProTable ref="proTable" :columns="columns" :data="exerciseList" row-key="id">
            <!-- 表格 header 按钮 -->
            <template #tableHeader="scope">
                <el-button type="primary" :icon="CirclePlus">新建练习</el-button>
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

<script setup lang="tsx" name="courseManageClassroomExercise">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox, ElTag } from "element-plus";
import ProTable from "@/components/ProTable/index.vue";
import { ProTableInstance, ColumnProps } from "@/components/ProTable/interface";
import { CirclePlus, Delete, EditPen, View } from "@element-plus/icons-vue";

const router = useRouter();

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
const exerciseList = ref([
    { id: 1, name: "Python基础语法练习", questionCount: 20, startTime: "2024-03-01 08:00:00", endTime: "2024-03-01 09:00:00", status: "未发布", completedCount: 0, uncompletedCount: 45, createTime: "2024-02-28 10:00" },
    { id: 2, name: "数据结构选择题", questionCount: 30, startTime: "2024-03-05 14:00:00", endTime: "2024-03-05 15:00:00", status: "未开始", completedCount: 0, uncompletedCount: 42, createTime: "2024-03-01 14:30" },
    { id: 3, name: "算法设计练习", questionCount: 15, startTime: "2024-03-10 10:00:00", endTime: "2024-03-10 11:30:00", status: "进行中", completedCount: 35, uncompletedCount: 10, createTime: "2024-03-05 09:00" },
    { id: 4, name: "数据库SQL练习", questionCount: 25, startTime: "2024-03-15 09:00:00", endTime: "2024-03-15 10:00:00", status: "已截止", completedCount: 42, uncompletedCount: 3, createTime: "2024-03-10 16:00" }
]);


/** 查看详情 */
const toDetail = (row: any) => {
    router.push(`/courseManage/classroomExercise/detail/${row.id}`);
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
