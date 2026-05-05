<template>
    <div class="table-box">
        <div class="bg-white radius8 p20 mb20">
            <div class="flx-justify-between mb20">
                <div class="flx-center">
                    <el-button icon="ArrowLeft" @click="goBack">返回</el-button>
                    <h2 class="ml20 mb0 mt0">{{ trainingInfo.name }}</h2>
                    <el-tag effect="dark" :type="getStatusType(trainingInfo.status)" class="ml10">{{
                        getStatusText(trainingInfo.status) }}</el-tag>
                </div>
                <div>
                    <el-button type="primary" icon="Edit" @click="editTraining">编辑实训</el-button>
                </div>
            </div>

            <el-descriptions :column="3" border>
                <el-descriptions-item label="实训名称">{{ trainingInfo.name }}</el-descriptions-item>
                <el-descriptions-item label="开始时间">{{ trainingInfo.startTime }}</el-descriptions-item>
                <el-descriptions-item label="结束时间">{{ trainingInfo.endTime }}</el-descriptions-item>
                <el-descriptions-item label="实训描述" :span="3">{{ trainingInfo.description }}</el-descriptions-item>
            </el-descriptions>
        </div>

        <div class="bg-white radius8">
            <el-tabs v-model="activeTab" class="training-tabs">
                <el-tab-pane label="实训列表" name="list">
                    <div class="card mt10 pt0 pb0 border-none shadow-none">
                        <SelectFilter :data="selectFilterData" :default-values="selectFilterValues"
                            @change="changeSelectFilter" />
                    </div>
                    <ProTable ref="proTable" :columns="columns" :data="trainingTaskList" row-key="id">
                        <template #tableHeader="scope">
                            <el-button type="primary" :icon="View">代码查重</el-button>
                            <el-button type="primary" :icon="View">更新成绩</el-button>
                            <el-button type="primary" :icon="View">一键点评</el-button>
                            <el-button type="primary" :icon="View">一键打回</el-button>
                            <el-button type="primary" :icon="View">导出</el-button>
                        </template>
                        <template #operation="scope">
                            <el-button type="primary" link :icon="View" @click="viewTask(scope.row)">查看</el-button>
                        </template>
                    </ProTable>
                </el-tab-pane>

                <el-tab-pane label="代码查重" name="plagiarism">
                    <div class="plagiarism-content p20">
                    </div>
                </el-tab-pane>

                <el-tab-pane label="实训描述" name="description">
                    <div class="description-content p20">
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script setup lang="tsx" name="courseManageTrainingDetail">
import { ref, reactive, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { View, EditPen, Delete } from "@element-plus/icons-vue";
import ProTable from "@/components/ProTable/index.vue";
import { ProTableInstance, ColumnProps } from "@/components/ProTable/interface";
import SelectFilter from "@/components/SelectFilter/index.vue";
import { getTrainingDetail, getTrainingStudents } from "@/api/modules/training";

const router = useRouter();
const route = useRoute();

const activeTab = ref("list");
const proTable = ref<ProTableInstance>();

/** 实训信息 */
const trainingInfo = ref<any>({
    id: route.params.id,
    name: "",
    startTime: "",
    endTime: "",
    status: "",
    description: ""
});

/** 表格配置项 */
const columns = reactive<ColumnProps<any>[]>([
    { type: "selection", fixed: "left", width: 70 },
    { prop: "name", label: "姓名", width: 120, search: { el: "input" } },
    { prop: "studentId", label: "学号", width: 120 },
    { prop: "className", label: "分班" },
    {
        prop: "trainingStatus", label: "实训状态", width: 100,
        render: (scope: any): any => {
            const statusMap: Record<string, { text: string; type: string }> = {
                completed: { text: "已完成", type: "success" },
                ongoing: { text: "进行中", type: "primary" },
                notStarted: { text: "未开始", type: "info" }
            };
            const status = statusMap[scope.row.trainingStatus] || { text: "未知", type: "info" };
            return <el-tag type={status.type}>{status.text}</el-tag>;
        }
    },
    { prop: "totalTime", label: "总耗时", width: 90 },
    { prop: "passRate", label: "通关情况", width: 100 },
    { prop: "evalCount", label: "评测次数", width: 100 },
    { prop: "finalScore", label: "最终成绩", width: 100 },
    { prop: "rejectCount", label: "打回次数", width: 100 },
    {
        prop: "reviewStatus", label: "评阅状态", width: 100,
        render: (scope: any): any => {
            const statusMap: Record<string, { text: string; type: string }> = {
                reviewed: { text: "已评阅", type: "success" },
                pending: { text: "待评阅", type: "warning" },
                rejected: { text: "已打回", type: "danger" }
            };
            const status = statusMap[scope.row.reviewStatus] || { text: "未知", type: "info" };
            return <el-tag type={status.type}>{status.text}</el-tag>;
        }
    },
    { prop: "operation", label: "操作", fixed: "right", width: 120 }
]);

/** 实训任务列表 */
const trainingTaskList = ref<any[]>([]);

/** selectFilter 数据 */
const selectFilterData = reactive([
    {
        title: "任务状态",
        key: "taskStatus",
        options: [
            { label: "全部", value: "" },
            { label: "未开启", value: "ongoing" },
            { label: "未通关", value: "notStarted" },
            { label: "按时通关", value: "ended" },
            { label: "迟交通关", value: "ended" }
        ]
    },
    {
        title: "分班情况",
        key: "classId",
        options: [
            { label: "全部", value: "" },
            { label: "2021届数据结构1班", value: "1" },
            { label: "2021届数据结构2班", value: "2" },
            { label: "2021届数据结构3班", value: "3" },
        ]
    }
]);
const selectFilterValues = ref({ taskStatus: "", classId: "" });

const changeSelectFilter = (value: typeof selectFilterValues.value) => {
    ElMessage.success("筛选条件已更新");
    selectFilterValues.value = value;
};

/** 加载实训详情数据 */
onMounted(async () => {
    const courseId = Number(route.query.courseId) || 1;
    const id = Number(route.params.id);
    try {
        const detailRes = await getTrainingDetail(courseId, id);
        trainingInfo.value = detailRes.data || detailRes;
    } catch (e) {
        ElMessage.error("获取实训详情失败");
    }
    try {
        const studentsRes = await getTrainingStudents(courseId, id);
        trainingTaskList.value = studentsRes.data || studentsRes || [];
    } catch (e) {
        ElMessage.error("获取学生列表失败");
    }
});


/** 获取状态类型 */
const getStatusType = (status: string): "success" | "info" | "warning" | "danger" | "primary" => {
    const map: Record<string, "success" | "info" | "warning" | "danger" | "primary"> = {
        "未发布": "info",
        "未开始": "warning",
        "进行中": "success",
        "已截止": "danger",
    };
    return map[status] || "info";
};

/** 获取状态文本 */
const getStatusText = (status: string) => {
    const map: Record<string, string> = {
        "未发布": "未发布",
        "未开始": "未开始",
        "进行中": "进行中",
        "已截止": "已截止",
    };
    return map[status] || "未知";
};


/** 查看学生实训情况 */
const viewTask = (row: any) => {
    router.push({ path: `/courseManage/training/detail/${route.params.id}/student/${row.userId}`, query: { courseId: route.query.courseId } });
};

/** 返回上一页 */
const goBack = () => {
    router.push({ path: "/courseManage/training", query: { courseId: route.query.courseId } });
};

/** 编辑实训 */
const editTraining = () => {
    router.push({ path: `/courseManage/training/action/${route.params.id}`, query: { courseId: route.query.courseId } });
};
</script>

<style scoped lang="scss">
.bg-white {
    background-color: var(--el-bg-color);
}

.radius8 {
    border-radius: 8px;
}

.p20 {
    padding: 20px;
}

.mb20 {
    margin-bottom: 20px;
}

.mb0 {
    margin-bottom: 0;
}

.mt0 {
    margin-top: 0;
}

.ml10 {
    margin-left: 10px;
}

.ml20 {
    margin-left: 20px;
}

.training-tabs {
    :deep(.el-tabs__header) {
        padding: 0 20px;
        margin: 0;
    }

    :deep(.el-tabs__content) {
        padding: 0;
    }
}


:deep(.el-table__body) {
    min-height: 150px;
}

.border-none {
    border: none !important;
}

.shadow-none {
    box-shadow: none;
}


:deep(.el-dialog .el-dialog__header) {
    border-bottom: 1px solid var(--el-color-primary) !important;
}

:deep(.el-dialog__title) {
    color: var(--el-color-primary);
}

:deep(.table-search) {
    border: none;
    box-shadow: none;
}
</style>
