<template>
    <div class="table-box">
        <div class="bg-white radius8 p20">
            <!-- 头部 -->
            <div class="review-header flx-justify-between mb20">
                <div class="flx-center">
                    <el-button icon="ArrowLeft" type="warning" plain @click="goBack">返回</el-button>
                    <h2 class="review-title ml20">{{ homeworkName }}</h2>
                </div>
                <el-tag type="info">共 {{ answerList.length }} 份提交</el-tag>
            </div>

            <!-- 加载状态 -->
            <div v-if="loading" class="loading-box flex-center" style="height: 200px;">
                <el-empty description="加载中..." />
            </div>

            <!-- 学生答题列表 -->
            <div v-else-if="answerList.length > 0">
                <el-table :data="answerList" border style="width: 100%" stripe>
                    <el-table-column prop="userId" label="学生ID" width="120" align="center" />
                    <el-table-column prop="username" label="学生姓名" width="140" align="center">
                        <template #default="{ row }">
                            {{ row.username || `学生${row.userId}` }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="submitTime" label="提交时间" min-width="180" align="center" />
                    <el-table-column label="得分" width="160" align="center">
                        <template #default="{ row }">
                            <el-input-number v-model="row.score" :min="0" :max="100" :step="1" size="small"
                                controls-position="right" style="width: 120px;" />
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="120" align="center">
                        <template #default="{ row }">
                            <el-button type="primary" size="small" @click="confirmScore(row)" :loading="row._scoring">
                                确认
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <!-- 分页 -->
                <div class="pagination-box flx-center mt20 mb20">
                    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                        :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper"
                        background @size-change="handleSizeChange" @current-change="handleCurrentChange" />
                </div>
            </div>

            <!-- 空状态 -->
            <div v-else class="flex-center" style="height: 200px;">
                <el-empty description="暂无学生提交" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts" name="courseManageHomeworkReview">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getHomeworkDetail, getHomeworkAnswers, gradeHomeworkScore } from "@/api/modules/homework";

const route = useRoute();
const router = useRouter();
const courseId = Number(route.query.courseId) || 1;
const homeworkId = Number(route.params.id);

/** 返回上一页 */
const goBack = () => {
    router.back();
};

/** 加载状态 */
const loading = ref(false);

/** 作业名称 */
const homeworkName = ref("作业批阅");

/** 学生答案列表 */
const answerList = ref<any[]>([]);

/** 分页 */
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

/** 加载作业详情和学生答案 */
const fetchReviewData = async () => {
    loading.value = true;
    try {
        // 并行加载作业详情和答案列表
        const [detailRes, answersRes]: any[] = await Promise.all([
            getHomeworkDetail(courseId, homeworkId).catch(() => null),
            getHomeworkAnswers(courseId, homeworkId).catch(() => null)
        ]);

        // 设置作业名称
        if (detailRes) {
            const detailData = detailRes.data || detailRes;
            homeworkName.value = detailData.name || "作业批阅";
        }

        // 设置答案列表
        if (answersRes) {
            const answersData = answersRes.data || answersRes;
            const list = Array.isArray(answersData) ? answersData : (answersData.list || answersData.records || []);
            answerList.value = list.map((item: any) => ({
                ...item,
                score: item.score ?? 0,
                _scoring: false
            }));
            total.value = answerList.value.length;
        }
    } catch (error: any) {
        ElMessage.error(error.message || "加载数据失败");
    } finally {
        loading.value = false;
    }
};

/** 确认打分 */
const confirmScore = async (row: any) => {
    if (row.score === undefined || row.score === null) {
        ElMessage.warning("请输入分数");
        return;
    }
    row._scoring = true;
    try {
        await gradeHomeworkScore(courseId, homeworkId, row.userId, row.score);
        ElMessage.success("打分成功");
    } catch (error: any) {
        ElMessage.error(error.message || "打分失败");
    } finally {
        row._scoring = false;
    }
};

/** 每页条数变化 */
const handleSizeChange = (val: number) => {
    pageSize.value = val;
    currentPage.value = 1;
    fetchReviewData();
};

/** 当前页变化 */
const handleCurrentChange = (val: number) => {
    currentPage.value = val;
    fetchReviewData();
};

onMounted(() => {
    if (!isNaN(homeworkId)) {
        fetchReviewData();
    } else {
        ElMessage.error("无效的作业ID");
    }
});
</script>

<style scoped lang="scss">
.table-box {
    padding: 0;
}

.bg-white {
    background: var(--el-bg-color);
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

.mt20 {
    margin-top: 20px;
}

.ml20 {
    margin-left: 20px;
}

.review-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .review-title {
        font-size: 20px;
        font-weight: bold;
        margin: 0;
    }
}

.flex-center {
    display: flex;
    align-items: center;
    justify-content: center;
}

.pagination-box {
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
