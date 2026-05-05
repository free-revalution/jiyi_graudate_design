<template>
    <div class="table-box">
        <div class="bg-white radius8">
            <div class="card mb10 pt0 pb0 border-none shadow-none">
                <SelectFilter :data="selectFilterData" :default-values="selectFilterValues"
                    @change="changeSelectFilter" />
            </div>
            <el-divider border-style="dashed" style="margin: 0 !important;" />
            <div class="card mb10 pt10 pb10 flx-justify-between border-none shadow-none">
                <div>
                    <el-button type="primary" @click="openTraining()" icon="Plus">新建实训</el-button>
                </div>
                <div>
                    <el-form class="t_a_form" :inline="true">
                        <el-form-item label="实训名称">
                            <el-input v-model="searchParams.name" placeholder="请输入实训名称" style="width: 200px;"
                                clearable></el-input>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
        </div>

        <div class="card mt10 mb10 pt0 pb0 pr0 pl0 border-none" style="background: transparent; box-shadow: none;">
            <div v-for="item in trainingList" :key="item.id" class="training-card mb20 bg-white radius8 p20">
                <div class="training-content flx-justify-between">
                    <div class="training-info flx-start">
                        <el-image :src="item.cover" class="training-cover radius8" fit="cover" />
                        <div class="training-detail ml20">
                            <h3 class="training-title font-bold text-lg mb10">{{ item.name }} <el-tag effect="dark"
                                    :type="getStatusType(item.status)" class="ml10">{{ item.status }}</el-tag></h3>

                            <div class="training-meta text-gray mb8">
                                <span>实训时间：{{ item.startTime }} ~ {{ item.endTime }}</span>
                            </div>
                            <div class="training-stats mt10">
                                <el-tag type="success" class="mr10">已做题 {{ item.submittedCount }}</el-tag>
                                <el-tag type="danger">未做题 {{ item.unsubmittedCount }}</el-tag>
                            </div>
                        </div>
                    </div>
                    <div class="training-actions flx-center">
                        <el-button type="success" plain icon="UploadFilled" @click="handlePublish(item)">发布</el-button>
                        <el-button type="primary" icon="View" @click="toDetail(item)">详情</el-button>
                    </div>
                </div>
            </div>

            <div v-if="trainingList.length == 0" class="card mb10 pt0 pb0 border-none shadow-none flex-center"
                style="height: 200px;">
                <div class="text-gray-400 py-8">暂无实训</div>
            </div>

            <!-- 分页 -->
            <div class="pagination-box flx-center mt20 mb20">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[5, 10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper"
                    background @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts" name="courseManageTraining">
import { ref, reactive, onActivated } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import SelectFilter from "@/components/SelectFilter/index.vue";
import { getTrainingList, publishTraining } from "@/api/modules/training";

const router = useRouter();
const route = useRoute();
const COURSE_ID = Number(route.query.courseId) || 1;

// 加载状态
const loading = ref(false);

// 分页数据
const currentPage = ref(1);
const pageSize = ref(5);
const total = ref(0);

/** 搜索参数 */
const searchParams = ref({
    name: ""
});

/** 实训列表 */
const trainingList = ref<any[]>([]);

/** 实训状态筛选 */
const selectFilterData = reactive([
    {
        title: "实训状态",
        key: "status",
        options: [
            { label: "全部", value: "" },
            { label: "未发布", value: "未发布" },
            { label: "未开始", value: "未开始" },
            { label: "进行中", value: "进行中" },
            { label: "已截止", value: "已截止" },
        ]
    }
]);
const selectFilterValues = ref({ status: "" });

/** 实训状态数据变化 */
const changeSelectFilter = (value: typeof selectFilterValues.value) => {
    selectFilterValues.value = value;
    fetchTrainingList();
};

/** 获取实训列表 */
const fetchTrainingList = async () => {
    loading.value = true;
    try {
        const params: any = {
            page: currentPage.value,
            limit: pageSize.value
        };
        if (searchParams.value.name) params.name = searchParams.value.name;
        if (selectFilterValues.value.status) params.status = selectFilterValues.value.status;
        const res: any = await getTrainingList(COURSE_ID, params);
        const data = res.data || {};
        trainingList.value = data.list || [];
        total.value = data.total || 0;
    } catch (error: any) {
        if (error?.code === "ERR_CANCELED" || error?.code === "ECONNABORTED") return;
        console.error("[TrainingList] error:", error?.response?.status, error?.response?.data, error?.message, error);
        const msg = error?.response?.data?.msg || error?.msg || error?.message || "获取实训列表失败";
        ElMessage.error(msg);
    } finally {
        loading.value = false;
    }
};

/** 每页多少条 */
const handleSizeChange = (val: number) => {
    pageSize.value = val;
    fetchTrainingList();
};

/** 当前页 */
const handleCurrentChange = (val: number) => {
    currentPage.value = val;
    fetchTrainingList();
};

// 初始加载
fetchTrainingList();

// keep-alive 激活时刷新数据
onActivated(() => {
    fetchTrainingList();
});


/** 发布实训弹窗 */
const openTraining = () => {
    router.push({
        path: "/courseManage/training/action/add",
        query: { courseId: COURSE_ID }
    });
};

/** 查看详情 */
const toDetail = (item: any) => {
    router.push({
        path: `/courseManage/training/detail/${item.id}`,
        query: { courseId: COURSE_ID }
    });
};

/** 发布实训 */
const handlePublish = async (item: any) => {
    try {
        await publishTraining(COURSE_ID, item.id);
        ElMessage.success("发布成功");
        fetchTrainingList();
    } catch (error: any) {
        const msg = error?.response?.data?.msg || error?.msg || error?.message || "发布失败";
        ElMessage.error(msg);
    }
};


/** 获取状态类型 */
const getStatusType = (status: string): "success" | "info" | "warning" | "danger" | "primary" => {
    const map: Record<string, "success" | "info" | "warning" | "danger" | "primary"> = {
        "未发布": "info",
        "未开始": "warning",
        "进行中": "primary",
        "已截止": "danger",

    };
    return map[status] || "info";
};

</script>

<style scoped lang="scss">
.training-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.training-info {
    display: flex;
    align-items: flex-start;
}

.training-cover {
    width: 120px;
    height: 80px;
    border-radius: 8px;
    flex-shrink: 0;
}

.training-detail {
    margin-left: 20px;
}

.training-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 8px;
    margin-top: 0;
}

.training-meta {
    color: #999;
    font-size: 14px;
}

.training-stats {
    display: flex;
    align-items: center;
}

.p20 {
    padding: 20px;
}

.mb8 {
    margin-bottom: 8px;
}

.t_a_form {
    .el-form-item {
        margin-bottom: 0 !important;
    }
}

.bg-white {
    background-color: var(--el-bg-color);
}

.border-none {
    border: none !important;
}

.shadow-none {
    box-shadow: none;
}

.radius8 {
    border-radius: 8px !important;
}

.text-gray-400 {
    color: #9ca3af;
}

.flex-center {
    display: flex;
    align-items: center;
    justify-content: center;
}

:deep(.el-input__wrapper) {
    border: none !important;
    border-bottom: 1px solid #e0e0e0 !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}

:deep(.el-select__wrapper) {
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
