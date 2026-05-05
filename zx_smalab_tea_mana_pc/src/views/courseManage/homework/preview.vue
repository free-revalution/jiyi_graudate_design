<template>
    <div class="homework-preview-container">
        <div class="preview-content bg-white radius8 p20">
            <!-- 头部 -->
            <div class="preview-header flx-justify-between mb20">
                <div class="flx-center">
                    <el-button icon="ArrowLeft" type="warning" plain @click="goBack">返回</el-button>
                    <h2 class="preview-title ml20">{{ homeworkInfo.name }}</h2>
                </div>
                <div class="show-answer-switch ">
                    <el-checkbox v-model="showAnswer">显示答案</el-checkbox>
                    <el-button class="ml20" type="primary" @click="goToEditHomework">编辑</el-button>
                </div>
            </div>

            <!-- 显示答案开关 -->


            <!-- 题目列表 -->
            <div class="question-list">
                <template v-for="(group, type) in groupedQuestions" :key="type">
                    <div class="question-type-section">
                        <h3 class="type-title">{{ getTypeChineseIndex(type as string) }}、{{ getQuestionTypeName(type as
                            string) }}（共{{ group.length }}题，{{ getTypeScore(group.length) }}分）</h3>
                        <div v-for="(question, qIndex) in group" :key="question.id" class="question-item">
                            <div class="question-content">
                                <span class="question-index">{{ qIndex + 1 }}.</span>
                                <div class="question-text" v-html="question.content"></div>
                            </div>

                            <!-- 选项 -->
                            <div v-if="question.type === 'single' || question.type === 'multiple'" class="options-list">
                                <div v-for="(option, oIndex) in question.options" :key="oIndex" class="option-item">
                                    <span class="option-label">{{ String.fromCharCode(65 + Number(oIndex)) }}.</span>
                                    <span class="option-content">{{ option.content }}</span>
                                </div>
                            </div>

                            <!-- 答案区域 -->
                            <div v-if="showAnswer" class="answer-section">
                                <div class="answer-row">
                                    <span class="answer-label">答案：</span>
                                    <span class="answer-value">{{ question.answer || '未设置' }}</span>
                                </div>
                                <div class="answer-row">
                                    <span class="answer-label">答案解析：</span>
                                    <span class="answer-value text-gray">{{ question.analysis || '暂无解析' }}</span>
                                </div>
                                <div class="answer-row">
                                    <span class="answer-label">难度：</span>
                                    <span class="answer-value text-gray">{{ getDifficultyText(question.difficulty)
                                    }}</span>
                                </div>
                                <div class="answer-row">
                                    <span class="answer-label">知识点：</span>
                                    <span class="answer-value text-gray">{{ question.knowledgePoints?.join('、') || '暂无'
                                        }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts" name="courseManageHomeworkPreview">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getHomeworkDetail } from "@/api/modules/homework";

const route = useRoute();
const router = useRouter();
const courseId = Number(route.query.courseId) || 1;

/** 显示答案 */
const showAnswer = ref(true);

/** 作业信息 */
const homeworkInfo = ref({
    id: route.params.id,
    name: "加载中..."
});

/** 题目列表 */
const questionList = ref<any[]>([]);

/** 按题型分组 */
const groupedQuestions = computed(() => {
    const groups: Record<string, any[]> = {};
    questionList.value.forEach(q => {
        if (!groups[q.type]) groups[q.type] = [];
        groups[q.type].push(q);
    });
    return groups;
});

/** 获取题型名称 */
const getQuestionTypeName = (type: string) => {
    const map: Record<string, string> = { single: "单选题", multiple: "多选题", fill: "填空题", blank: "填空题" };
    return map[type] || "未知题型";
};

/** 获取题型中文序号 */
const getTypeChineseIndex = (type: string) => {
    const types = Object.keys(groupedQuestions.value);
    const index = types.indexOf(type);
    const chineseNumbers = ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十"];
    return chineseNumbers[index] || String(index + 1);
};

/** 获取题型分数 */
const getTypeScore = (count: number) => {
    const totalQuestions = questionList.value.length;
    if (totalQuestions === 0) return 0;
    return ((100 / totalQuestions) * count).toFixed(1);
};

/** 获取难度文本 */
const getDifficultyText = (difficulty: string) => {
    const map: Record<string, string> = { "0.8": "0.8 (易)", "0.6": "0.6 (中)", "0.4": "0.4 (难)" };
    return map[difficulty] || difficulty;
};

/** 返回上一页 */
const goBack = () => {
    router.back();
};

/** 跳转编辑页面 */
const goToEditHomework = () => {
    // 将作业数据存储到 sessionStorage
    const homeworkData = {
        homeworkInfo: homeworkInfo.value,
        questionList: questionList.value
    };
    sessionStorage.setItem("homeworkEditData", JSON.stringify(homeworkData));
    router.push({ path: `/courseManage/homework/action/${route.params.id}`, query: { courseId } });
};

onMounted(async () => {
    // 优先从后端 API 加载
    try {
        const homeworkId = Number(route.params.id);
        if (!isNaN(homeworkId)) {
            const res: any = await getHomeworkDetail(courseId, homeworkId);
            const data = res.data || res;
            if (data) {
                homeworkInfo.value = {
                    id: data.id,
                    name: data.name || "未命名作业"
                };
                if (data.questions && data.questions.length > 0) {
                    questionList.value = data.questions.map((q: any) => ({
                        id: q.id,
                        type: q.type,
                        content: q.content,
                        options: (q.options || []).map((o: any) => ({ content: o.content })),
                        answer: q.answer,
                        analysis: q.analysis,
                        difficulty: String(q.difficulty || "0.6"),
                        knowledgePoints: q.knowledgePoints || []
                    }));
                }
                return;
            }
        }
    } catch (error) {
        console.warn("从后端加载作业详情失败，回退到 sessionStorage", error);
    }

    // 回退：从 sessionStorage 获取数据
    const savedData = sessionStorage.getItem("homeworkEditData");
    if (savedData) {
        const data = JSON.parse(savedData);
        if (data.homeworkInfo) {
            homeworkInfo.value = data.homeworkInfo;
        }
        if (data.questionList) {
            questionList.value = data.questionList;
        }
    }
});
</script>

<style scoped lang="scss">
.homework-preview-container {
    background: #f5f7fa;
    min-height: calc(100vh - 100px);
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

.preview-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .preview-title {
        font-size: 20px;
        font-weight: bold;
        margin: 0;
    }
}

.ml20 {
    margin-left: 20px;
}

.show-answer-switch {
    padding: 10px 0;
    border-bottom: 1px solid #ebeef5;
}

.question-type-section {
    margin-bottom: 30px;

    .type-title {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
        margin: 20px 0 15px;
    }
}

.question-item {
    margin-bottom: 25px;
    padding-bottom: 20px;
    border-bottom: 1px dashed #ebeef5;

    &:last-child {
        border-bottom: none;
    }
}

.question-content {
    display: flex;
    margin-bottom: 10px;

    .question-index {
        font-weight: bold;
        margin-right: 5px;
        flex-shrink: 0;
    }

    .question-text {
        flex: 1;

        :deep(p) {
            margin: 0;
        }
    }
}

.options-list {
    padding-left: 20px;

    .option-item {
        display: flex;
        margin-bottom: 8px;
        line-height: 1.6;

        .option-label {
            margin-right: 8px;
            flex-shrink: 0;
        }
    }
}

.answer-section {
    margin-top: 15px;
    padding: 10px 15px;
    background: var(--el-color-primary-light-9);
    border-radius: 4px;

    .answer-row {
        margin-bottom: 5px;
        font-size: 14px;

        &:last-child {
            margin-bottom: 0;
        }

        .answer-label {
            font-weight: bold;
            color: #303133;
        }

        .answer-value {
            color: var(--el-color-primary);
        }
    }
}

.text-gray {
    color: #909399;
}
</style>
