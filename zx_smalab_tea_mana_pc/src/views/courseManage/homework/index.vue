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
                    <el-button type="primary" @click="openHomework()" icon="Plus">发布作业</el-button>
                    <el-button type="warning" icon="Connection">作业库</el-button>
                </div>
                <div>
                    <el-form class="t_a_form" :inline="true">
                        <el-form-item label="班级">
                            <el-select v-model="searchParams.classId" placeholder="请选择班级" style="width: 200px;"
                                clearable>
                                <el-option v-for="item in classOptions" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="作业名称">
                            <el-input v-model="searchParams.name" placeholder="请输入作业名称" style="width: 200px;"
                                clearable></el-input>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
        </div>

        <div class="card mt10 mb10 pt0 pb0 pr0 pl0 border-none" style="background: transparent; box-shadow: none;">
            <div v-for="item in homeworkList" :key="item.id" class="homework-card mb20 bg-white radius8 p20">
                <div class="homework-content flx-justify-between">
                    <div class="homework-info flx-start">
                        <el-image :src="item.cover" class="homework-cover radius8" fit="cover" />
                        <div class="homework-detail ml20">
                            <h3 class="homework-title font-bold text-lg mb10">
                                {{ item.name }}
                                <el-tag :type="item.status === 'published' ? 'success' : 'info'" class="ml10">
                                    {{ item.status === 'published' ? '已发布' : '草稿' }}
                                </el-tag>
                            </h3>
                            <div class="homework-meta text-gray mb8">
                                <span class="mr20">作答班级：{{ item.className }}</span>
                            </div>
                            <div class="homework-meta text-gray mb8">
                                <span>作答时间：{{ item.startTime }} ~ {{ item.endTime }}</span>
                            </div>
                            <div class="homework-stats mt10">
                                <el-tag type="warning" class="mr10">待批 {{ item.pendingCount }}</el-tag>
                                <el-tag type="success" class="mr10">已交 {{ item.submittedCount }}</el-tag>
                                <el-tag type="danger">未交 {{ item.unsubmittedCount }}</el-tag>
                            </div>
                        </div>
                    </div>
                    <div class="homework-actions flx-center">
                        <el-button v-if="item.status === 'draft'" type="success" icon="Promotion"
                            @click="handlePublish(item)">发布</el-button>
                        <el-button type="warning" icon="View" @click="toView(item)">预览</el-button>
                        <el-button type="primary" icon="EditPen" @click="toEdit(item)">编辑</el-button>
                        <el-button type="info" icon="EditPen" @click="toReview(item)">批阅</el-button>
                    </div>
                </div>
            </div>

            <div v-if="homeworkList.length == 0" class="card mb10 pt0 pb0 border-none shadow-none flex-center"
                style="height: 200px;">
                <div class="text-gray-400 py-8">暂无作业</div>
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

<script setup lang="ts" name="courseManageHomework">
import { ref, reactive, onActivated, onMounted } from "vue";
import SelectFilter from "@/components/SelectFilter/index.vue";
import router from "@/routers";
import { useRoute } from "vue-router";
import { getHomeworkList, publishHomework } from "@/api/modules/homework";
import { ElMessage } from "element-plus";

const route = useRoute();
const COURSE_ID = Number(route.query.courseId) || 1;

// 加载状态
const loading = ref(false);

// 分页数据
const currentPage = ref(1);
const pageSize = ref(5);
const total = ref(0);

/** 作业列表 */
const homeworkList = ref<any[]>([]);

/**作业状态筛选数据 */
const selectFilterData = reactive([
    {
        title: "作业状态",
        key: "status",
        options: [
            { label: "全部", value: "" },
            { label: "草稿", value: "draft" },
            { label: "已发布", value: "published" }
        ]
    }
]);
const selectFilterValues = ref({ status: "" });
/**作业状态数据变化 */
const changeSelectFilter = (value: typeof selectFilterValues.value) => {
    selectFilterValues.value = value;
    fetchHomeworkList();
};

/**搜索参数 */
const searchParams = ref({
    classId: "",
    name: ""
});

/**班级选项 */
const classOptions = ref([
    { label: "2025级软件工程1班", value: "1" },
    { label: "2025级计算机科学2班", value: "2" },
    { label: "l4级人工智能1班", value: "3" }
]);

/** 获取作业列表 */
const fetchHomeworkList = async () => {
    loading.value = true;
    try {
        const res: any = await getHomeworkList(COURSE_ID, {
            page: currentPage.value,
            limit: pageSize.value,
            name: searchParams.value.name,
            classId: searchParams.value.classId,
            status: selectFilterValues.value.status
        });
        const data = res.data || {};
        homeworkList.value = data.list || [];
        total.value = data.total || 0;
    } catch (error) {
        console.warn("获取作业列表失败:", error);
    } finally {
        loading.value = false;
    }
};

/**每页多少条 */
const handleSizeChange = (val: number) => {
    pageSize.value = val;
    fetchHomeworkList();
};
/**当前页 */
const handleCurrentChange = (val: number) => {
    currentPage.value = val;
    fetchHomeworkList();
};

// 初始加载
onMounted(() => {
    fetchHomeworkList();
});

// keep-alive 激活时刷新数据
onActivated(() => {
    fetchHomeworkList();
});

/**发布作业页面*/
const openHomework = () => {
    router.push({
        path: "/courseManage/homework/action/add",
        query: { courseId: COURSE_ID }
    });
};

/**批阅作业 */
const toReview = (item: any) => {
    router.push({ path: `/courseManage/homework/review/${item.id}`, query: { courseId: COURSE_ID } });
};
/**预览 */
const toView = (item: any) => {
    router.push({
        path: `/courseManage/homework/preview/${item.id}`,
        query: { courseId: COURSE_ID }
    });
};
/**编辑 */
const toEdit = (item: any) => {
    router.push({
        path: `/courseManage/homework/action/${item.id}`,
        query: { courseId: COURSE_ID }
    });
};
/**发布作业 */
const handlePublish = async (item: any) => {
    try {
        await publishHomework(COURSE_ID, item.id);
        ElMessage.success("发布成功");
        fetchHomeworkList();
    } catch (error) {
        console.warn("发布失败:", error);
    }
};
</script>

<style scoped lang="scss">
.homework-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.homework-info {
    display: flex;
    align-items: flex-start;
}

.homework-cover {
    width: 120px;
    height: 80px;
    border-radius: 8px;
    flex-shrink: 0;
}

.homework-detail {
    margin-left: 20px;
}

.homework-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 8px;
    margin-top: 0;
}

.homework-meta {
    color: #999;
    font-size: 14px;
}

.homework-stats {
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



//输入框只保留底部边框
:deep(.el-input__wrapper) {
    border: none !important;
    border-bottom: 1px solid #e0e0e0 !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}

// 下拉选择器只保留底部边框
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
