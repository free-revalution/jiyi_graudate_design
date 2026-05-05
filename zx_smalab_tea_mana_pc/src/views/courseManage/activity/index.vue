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
    </div>
</template>

<script setup lang="ts" name="courseManageActivity">
import { ref } from "vue";
import { ElMessage } from "element-plus";

// 分页数据
const currentPage = ref(1);
const pageSize = ref(5);
const total = ref(20);

/** 每页多少条 */
const handleSizeChange = (val: number) => {
    pageSize.value = val;
};

/** 当前页 */
const handleCurrentChange = (val: number) => {
    currentPage.value = val;
};

/** 模拟班级数据 */
const classList = ref([
    {
        id: 1,
        name: "2024级软件工程1班",
        studentCount: 45
    },
    {
        id: 2,
        name: "2024级计算机科学2班",
        studentCount: 42
    },
    {
        id: 3,
        name: "2024级软件工程2班",
        studentCount: 48
    },
    {
        id: 4,
        name: "2024级人工智能1班",
        studentCount: 35
    }
]);

/** 搜索参数 */
const searchParams = ref({
    name: ""
});

/** 发布班级弹窗 */
const openClassDialog = () => {
    ElMessage.info("打开发布班级弹窗");
};

/** 上课 */
const toClass = (item: any) => {
    ElMessage.info(`进入上课：${item.name}`);
};

/** 活动列表 */
const toActivityList = (item: any) => {
    ElMessage.info(`查看活动列表：${item.name}`);
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
    background-color: #ffffff;
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
