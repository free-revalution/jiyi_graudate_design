<template>
    <div class="table-box">
        <div class="bg-white radius8 mb10">
            <div class="card pt10 pb10 flx-justify-between border-none shadow-none">
                <div>
                    <el-button type="primary" @click="openClassDialog()" icon="Plus">新建班级</el-button>
                </div>
                <div>
                    <el-form class="t_a_form" :inline="true">
                        <el-form-item label="班级名称">
                            <el-input v-model="searchParams.name" placeholder="请输入班级名称" style="width: 200px;"
                                clearable></el-input>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
        </div>

        <div class="card mt10  pt0 pb0 pr0 pl0 border-none radius8" style="background: transparent; box-shadow: none">
            <div v-for="(item, index) in classList" :key="item.id">
                <div class="class-card  bg-white  p20">
                    <div class="class-content flx-justify-between">
                        <div class="class-info flx-start">
                            <div class="class-detail">
                                <h3 class="class-title font-bold text-lg mb10">{{ item.name }}</h3>
                                <div class="class-meta text-gray">
                                    <span class="mr20">学生人数：{{ item.studentCount }}</span>
                                </div>
                            </div>
                        </div>
                        <div class="class-actions flx-center">
                            <el-button type="warning" plain icon="DataBoard" @click="toClass(item)">上课</el-button>
                            <el-button type="success" icon="Setting" @click="toClassManage(item)">管理</el-button>
                            <el-button type="primary" icon="Tickets" @click="toActivityList(item)">活动列表</el-button>
                        </div>
                    </div>
                </div>
                <el-divider v-if="classList.length - 1 != index" border-style="dashed"
                    style="margin: 0 !important ; border-top: 0px var(--el-border-color) var(--el-border-style)" />
            </div>

            <div v-if="classList.length == 0" class="card mb10 pt0 pb0 border-none shadow-none flex-center"
                style="height: 200px;">
                <div class="text-gray-400 py-8">暂无班级</div>
            </div>
        </div>
        <!-- 分页 -->
        <div class="pagination-box flx-center mt20 mb20">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 20, 50]"
                :total="total" layout="total, sizes, prev, pager, next, jumper" background
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>

        <!-- 新建班级弹窗 -->
        <el-dialog v-model="classDialogVisible" title="新建班级" width="400px" @close="classForm.name = ''">
            <el-form label-width="80px">
                <el-form-item label="班级名称">
                    <el-input v-model="classForm.name" placeholder="请输入班级名称" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="classDialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="createLoading" @click="handleCreateClass">确认</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts" name="courseManageActivity">
import { ref, reactive } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import router from "@/routers";
import { getClasses, createClass } from "@/api/modules/course";

const route = useRoute();
const COURSE_ID = Number(route.query.courseId) || 1;

// 分页数据
const currentPage = ref(1);
const pageSize = ref(5);
const total = ref(0);

// 加载状态
const loading = ref(false);

/** 每页多少条 */
const handleSizeChange = (val: number) => {
    pageSize.value = val;
};

/** 当前页 */
const handleCurrentChange = (val: number) => {
    currentPage.value = val;
};

/** 班级列表 */
const classList = ref<any[]>([]);

/** 加载班级列表 */
const fetchClassList = async () => {
    loading.value = true;
    try {
        const res: any = await getClasses(COURSE_ID);
        classList.value = res.data || [];
        total.value = classList.value.length;
    } catch (error) {
        ElMessage.error("获取班级列表失败");
    } finally {
        loading.value = false;
    }
};

// 初始加载
fetchClassList();

/** 搜索参数 */
const searchParams = ref({
    name: ""
});

/** 新建班级弹窗 */
const classDialogVisible = ref(false);
const createLoading = ref(false);
const classForm = reactive({ name: "" });

const openClassDialog = () => {
    classForm.name = "";
    classDialogVisible.value = true;
};

const handleCreateClass = async () => {
    if (!classForm.name.trim()) { ElMessage.warning("请输入班级名称"); return; }
    createLoading.value = true;
    try {
        await createClass(COURSE_ID, { name: classForm.name.trim() });
        ElMessage.success("创建成功");
        classDialogVisible.value = false;
        fetchClassList();
    } catch (e: any) {
        ElMessage.error(e?.data?.msg || "创建失败");
    } finally {
        createLoading.value = false;
    }
};

/** 上课 */
const toClass = (item: any) => {
    ElMessage.info(`进入上课：${item.name}`);
};

/** 活动列表 */
const toActivityList = (item: any) => {
    ElMessage.info(`查看活动列表：${item.name}`);
};
/**去往管理页面 */
const toClassManage = (item: any) => {
    router.push({
        path: `/courseManage/classActivity/classManage/${item.id}`,
        query: { courseId: COURSE_ID }
    });
};
</script>

<style scoped lang="scss">
.class-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.class-info {
    display: flex;
    align-items: flex-start;
}

.class-detail {
    margin-left: 20px;
}

.class-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 8px;
    margin-top: 0;
}

.class-meta {
    color: #999;
    font-size: 14px;
}

.p20 {
    padding: 20px;
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

// 输入框只保留底部边框
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
