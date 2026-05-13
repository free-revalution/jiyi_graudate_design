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
                    <el-table-column prop="userId" label="学生ID" width="100" align="center" />
                    <el-table-column prop="username" label="学生姓名" width="120" align="center">
                        <template #default="{ row }">
                            {{ row.username || `学生${row.userId}` }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="submitTime" label="提交时间" min-width="160" align="center" />
                    <el-table-column label="得分" width="140" align="center">
                        <template #default="{ row }">
                            <el-input-number v-model="row.score" :min="0" :max="100" :step="1" size="small"
                                controls-position="right" style="width: 110px;" />
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="180" align="center">
                        <template #default="{ row }">
                            <el-button type="primary" size="small" @click="viewDetail(row)">查看答题</el-button>
                            <el-button type="success" size="small" @click="confirmScore(row)" :loading="row._scoring">确认</el-button>
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

        <!-- 学生答题详情弹窗 -->
        <el-dialog v-model="detailVisible" :title="`${detailStudent} 的答题详情`" width="75%" destroy-on-close>
            <div v-if="detailLoading" style="text-align:center;padding:40px;">
                <el-icon class="is-loading" :size="24"><Loading /></el-icon>
                <p style="color:#999;margin-top:10px;">加载中...</p>
            </div>
            <div v-else-if="detailQuestions.length > 0" class="detail-content">
                <div v-for="(q, idx) in detailQuestions" :key="idx" class="detail-question">
                    <div class="dq-header">
                        <span class="dq-num">{{ idx + 1 }}.</span>
                        <el-tag size="small" :type="getTypeTag(q.type)">{{ getTypeName(q.type) }}</el-tag>
                    </div>
                    <div class="dq-title" v-html="q.content"></div>

                    <!-- 选择题/判断题：显示学生选的选项 -->
                    <div v-if="q.type === 'single' || q.type === 'multiple' || q.type === 'judge'" class="dq-answer">
                        <span class="dq-label">学生答案：</span>
                        <el-tag v-if="q.studentAnswer" :type="q.isCorrect ? 'success' : 'danger'">
                            {{ q.studentAnswer }}
                        </el-tag>
                        <el-tag v-else type="info">未作答</el-tag>
                        <span class="dq-label" style="margin-left:20px">正确答案：</span>
                        <el-tag type="success">{{ q.answer || '-' }}</el-tag>
                    </div>

                    <!-- 填空题 -->
                    <div v-else-if="q.type === 'fill'" class="dq-answer">
                        <span class="dq-label">学生答案：</span>
                        <span v-if="q.studentAnswer" class="dq-text">{{ q.studentAnswer }}</span>
                        <el-tag v-else type="info">未作答</el-tag>
                    </div>

                    <!-- 代码题 -->
                    <div v-else-if="q.type === 'code'" class="dq-answer">
                        <div class="dq-label">学生提交的代码：</div>
                        <pre v-if="q.studentAnswer" class="dq-code">{{ q.studentAnswer }}</pre>
                        <el-tag v-else type="info">未提交代码</el-tag>
                        <div v-if="q.referenceCode" style="margin-top:15px">
                            <div class="dq-label">参考答案：</div>
                            <pre class="dq-code ref-code">{{ q.referenceCode }}</pre>
                        </div>
                    </div>
                </div>
            </div>
            <el-empty v-else description="暂无答题数据" />
        </el-dialog>
    </div>
</template>

<script setup lang="ts" name="courseManageHomeworkReview">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Loading } from "@element-plus/icons-vue";
import { getHomeworkDetail, getHomeworkAnswers, gradeHomeworkScore } from "@/api/modules/homework";

const route = useRoute();
const router = useRouter();
const courseId = Number(route.query.courseId) || 1;
const homeworkId = Number(route.params.id);

const goBack = () => { router.back(); };

const loading = ref(false);
const homeworkName = ref("作业批阅");
const answerList = ref<any[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 详情弹窗
const detailVisible = ref(false);
const detailLoading = ref(false);
const detailStudent = ref("");
const detailQuestions = ref<any[]>([]);

const fetchReviewData = async () => {
    loading.value = true;
    try {
        const [detailRes, answersRes]: any[] = await Promise.all([
            getHomeworkDetail(courseId, homeworkId).catch(() => null),
            getHomeworkAnswers(courseId, homeworkId).catch(() => null)
        ]);

        if (detailRes) {
            const detailData = detailRes.data || detailRes;
            homeworkName.value = detailData.name || "作业批阅";
        }

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

const viewDetail = async (row: any) => {
    detailStudent.value = row.username || `学生${row.userId}`;
    detailVisible.value = true;
    detailLoading.value = true;

    try {
        // 获取作业题目
        const detailRes = await getHomeworkDetail(courseId, homeworkId);
        const detailData = detailRes.data || detailRes;
        const questions = detailData.questions || [];

        // 学生的答案对象（answers 是 JSON 字符串，需要解析）
        let studentAnswers = {};
        try {
            studentAnswers = typeof row.answers === 'string' ? JSON.parse(row.answers) : (row.answers || {});
        } catch (e) {
            studentAnswers = {};
        }

        // 组合题目+答案
        detailQuestions.value = questions.map((q: any) => {
            const ans = studentAnswers[q.id];
            return {
                ...q,
                studentAnswer: ans !== undefined && ans !== null ? ans : "",
                isCorrect: ans === q.answer
            };
        });
    } catch (e) {
        detailQuestions.value = [];
    } finally {
        detailLoading.value = false;
    }
};

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

const getTypeName = (type: string) => {
    const map: Record<string, string> = { single: "单选", multiple: "多选", judge: "判断", fill: "填空", code: "代码" };
    return map[type] || "其他";
};

const getTypeTag = (type: string) => {
    const map: Record<string, string> = { single: "", multiple: "warning", judge: "info", fill: "success", code: "danger" };
    return map[type] || "info";
};

const handleSizeChange = (val: number) => { pageSize.value = val; currentPage.value = 1; fetchReviewData(); };
const handleCurrentChange = (val: number) => { currentPage.value = val; fetchReviewData(); };

onMounted(() => {
    if (!isNaN(homeworkId)) fetchReviewData();
    else ElMessage.error("无效的作业ID");
});
</script>

<style scoped lang="scss">
.table-box { padding: 0; }
.bg-white { background: var(--el-bg-color); }
.radius8 { border-radius: 8px; }
.p20 { padding: 20px; }
.mb20 { margin-bottom: 20px; }
.mt20 { margin-top: 20px; }
.ml20 { margin-left: 20px; }

.review-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .review-title { font-size: 20px; font-weight: bold; margin: 0; }
}

.flex-center { display: flex; align-items: center; justify-content: center; }
.pagination-box { display: flex; justify-content: center; align-items: center; }

.detail-content {
    max-height: 70vh;
    overflow-y: auto;
    padding-right: 10px;

    .detail-question {
        padding: 18px;
        margin-bottom: 15px;
        background: #fafafa;
        border-radius: 8px;
        border-left: 4px solid #409eff;

        .dq-header {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 10px;
            .dq-num { font-weight: 600; font-size: 15px; color: #409eff; }
        }

        .dq-title {
            font-size: 14px;
            color: #303133;
            line-height: 1.6;
            margin-bottom: 12px;
        }

        .dq-answer {
            .dq-label {
                font-size: 13px;
                color: #909399;
                margin-bottom: 6px;
            }

            .dq-text {
                font-size: 14px;
                color: #303133;
                background: #fff;
                padding: 6px 12px;
                border-radius: 4px;
                border: 1px solid #e4e7ed;
            }

            .dq-code {
                background: #1e1e1e;
                color: #d4d4d4;
                padding: 12px 15px;
                border-radius: 6px;
                font-family: Consolas, "Courier New", monospace;
                font-size: 13px;
                line-height: 1.6;
                overflow-x: auto;
                margin: 6px 0 0;

                &.ref-code {
                    background: #f0f9eb;
                    color: #67c23a;
                    border: 1px solid #e1f3d8;
                }
            }
        }
    }
}
</style>
