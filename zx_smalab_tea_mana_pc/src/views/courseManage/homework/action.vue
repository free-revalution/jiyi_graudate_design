<template>
    <div class="main-tab">
        <!-- 顶部：作业基本设置 -->
        <div class="bg-white radius8 p20 mb10">
            <div class="flx-center mb10">
                <el-button icon="ArrowLeft" type="warning" plain @click="goBack">返回</el-button>
                <el-input v-model="homeworkForm.name" class="homework-name-input ml10" placeholder="请输入作业名称" />
            </div>
            <el-row :gutter="20" class="mt20">
                <el-col :span="8">
                    <div class="form-item">
                        <span class="form-label">作业类型</span>
                        <el-radio-group v-model="homeworkForm.type">
                            <el-radio value="question">题目型作业</el-radio>
                            <el-radio value="answer">答题卡作业</el-radio>
                        </el-radio-group>
                    </div>
                </el-col>
                <el-col :span="10">
                    <div class="form-item">
                        <span class="form-label">评分机制</span>
                        <el-radio-group v-model="homeworkForm.scoreType">
                            <el-radio value="average">百分制 <span class="text-gray">(平均分配每道题的分值)</span></el-radio>
                            <el-radio value="custom">自定义 <span class="text-gray">(自行设置每道题的分值)</span></el-radio>
                        </el-radio-group>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="form-item">
                        <span class="form-label">题型设置</span>
                        <el-radio-group v-model="homeworkForm.questionGroup">
                            <el-radio value="byType">按题型归类</el-radio>
                            <el-radio value="noType">不按题型归类</el-radio>
                        </el-radio-group>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 主体内容区域 -->
        <div class="main-content">
            <!-- 左侧：题目总览 -->
            <div class="question-overview">
                <div class="overview-header">
                    <span>题量 {{ questionList.length }}，总分 {{ totalScore }}</span>
                </div>
                <div class="question-type-list">
                    <template v-for="(group, type) in groupedQuestions" :key="type">
                        <div class="type-title">{{ getQuestionTypeIndex(String(type)) }} {{
                            getQuestionTypeName(String(type)) }}</div>
                        <div class="question-items-list">
                            <div v-for="(item, index) in group" :key="item.id" class="question-item-row"
                                :class="{ active: currentQuestion?.id === item.id }" @click="selectQuestion(item)">
                                <span class="item-index">{{ index + 1 }}、</span>
                                <span class="item-content" v-html="stripHtml(item.content)"></span>
                            </div>
                        </div>
                    </template>
                </div>
            </div>

            <!-- 右侧：题目编辑区域 -->
            <div class="question-editor">
                <!-- 添加题目类别 -->
                <div class="add-question-bar">
                    <span class="add-label">添加题目 <el-icon>
                            <InfoFilled />
                        </el-icon></span>
                    <div class="question-types">
                        <el-button :type="currentType === 'single' ? 'primary' : 'default'" plain
                            @click="selectQuestionType('single')">单选题</el-button>
                        <el-button :type="currentType === 'multiple' ? 'primary' : 'default'" plain
                            @click="selectQuestionType('multiple')">多选题</el-button>
                        <el-button :type="currentType === 'fill' ? 'primary' : 'default'" plain
                            @click="selectQuestionType('fill')">填空题</el-button>
                        <el-button :type="currentType === 'code' ? 'primary' : 'default'" plain
                            @click="selectQuestionType('code')">代码题</el-button>                    </div>
                    <div class="import-actions">
                        <el-button type="primary" link>智能导入</el-button>
                        <el-button type="primary" link>选题</el-button>
                        <!-- <el-button type="warning" icon="View" @click="toPreview">预览</el-button> -->
                    </div>
                </div>

                <!-- 题目编辑表单 -->
                <div v-if="currentType" class="question-form">
                    <div class="question-form-header">
                        <span class="question-number">{{ editingIndex }}</span>
                        <span class="question-type-label">{{ getQuestionTypeName(currentType) }}</span>
                        <el-button type="danger" link :icon="Delete" class="delete-btn"
                            @click="deleteCurrentQuestion" />
                    </div>

                    <!-- 富文本编辑器 -->
                    <div class="editor-wrapper">
                        <WangEditor v-model:value="questionForm.content" height="200px" />
                    </div>

                    <!-- 选项区域（单选/多选） -->
                    <div v-if="currentType === 'single' || currentType === 'multiple'" class="options-area">
                        <div v-for="(option, index) in questionForm.options" :key="index" class="option-item">
                            <span class="option-label" :class="{ selected: isOptionSelected(index) }"
                                @click="toggleAnswer(index)">{{ String.fromCharCode(65 + index) }}</span>
                            <el-input autosize type="textarea" v-model="option.content" placeholder="请输入选项内容" />
                            <el-button v-if="questionForm.options.length > 2" type="danger" link :icon="Delete"
                                @click="removeOption(index)" />
                        </div>
                        <el-button type="primary" link :icon="Plus" @click="addOption">添加选项</el-button>
                        <div class="answer-hint">
                            <span class="hint-label">正确答案：</span>
                            <span class="hint-value">{{ questionForm.answer || '请点击选项字母选择答案' }}</span>
                        </div>
                    </div>

                    <!-- 代码题专属区域 -->
                    <div v-if="currentType === 'code'" class="code-area">
                        <div class="code-config">
                            <span class="config-label">编程语言</span>
                            <el-select v-model="questionForm.language" placeholder="选择语言" style="width: 200px">
                                <el-option label="Python" value="python" />
                                <el-option label="Java" value="java" />
                                <el-option label="C/C++" value="cpp" />
                                <el-option label="JavaScript" value="javascript" />
                            </el-select>
                        </div>
                        <div class="code-field">
                            <span class="field-label">初始代码（学生看到的模板）</span>
                            <el-input :rows="8" type="textarea" v-model="questionForm.initialCode" placeholder="# 请在此处编写代码" style="font-family: Consolas, monospace" />
                        </div>
                        <div class="code-field">
                            <span class="field-label">参考答案</span>
                            <el-input :rows="8" type="textarea" v-model="questionForm.referenceCode" placeholder="输入参考代码答案" style="font-family: Consolas, monospace" />
                        </div>
                    </div>

                    <!-- 答案解析 -->
                    <div class="answer-area">
                        <el-input :rows="3" type="textarea" v-model="questionForm.analysis" placeholder="输入答案解析" />
                    </div>

                    <!-- 题目配置 -->
                    <div class="question-config">
                        <div class="config-item">
                            <span class="config-label">难度</span>
                            <el-select v-model="questionForm.difficulty" placeholder="请选择">
                                <el-option label="0.8 (易)" value="0.8" />
                                <el-option label="0.6 (中)" value="0.6" />
                                <el-option label="0.4 (难)" value="0.4" />
                            </el-select>
                        </div>
                        <div class="config-item">
                            <span class="config-label">知识点</span>
                            <el-button type="primary" link :icon="Plus">关联知识点</el-button>
                        </div>
                        <div class="config-item">
                            <span class="config-label">标签</span>
                            <el-button type="primary" link :icon="Plus">关联标签</el-button>
                        </div>
                    </div>

                    <!-- 操作按钮 -->
                    <div class="form-actions">
                        <el-button :icon="Refresh" @click="resetForm">重置</el-button>
                        <el-button type="primary" @click="saveQuestion">完成</el-button>
                        <el-button type="success" @click="saveHomework">保存</el-button>
                    </div>
                </div>

                <!-- 无选择状态 -->
                <div v-else class="empty-editor">
                    <el-empty description="请选择题目类型开始添加" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts" name="courseManageHomeworkDetail">
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Delete, Plus, Refresh, InfoFilled } from "@element-plus/icons-vue";
import WangEditor from "@/components/WangEditor/index.vue";
import { createHomework, updateHomework, getHomeworkDetail } from "@/api/modules/homework";

const route = useRoute();
const router = useRouter();
const courseId = Number(route.query.courseId) || 1;

/** 返回上一页 */
const goBack = () => {
    router.back();
};



/** 去除HTML标签 */
const stripHtml = (html: string) => {
    const tmp = document.createElement("div");
    tmp.innerHTML = html;
    return tmp.textContent || tmp.innerText || "";
};

/** 生成作业名称 */
const generateHomeworkName = () => {
    const now = new Date();
    const dateStr = now.getFullYear().toString() +
        (now.getMonth() + 1).toString().padStart(2, "0") +
        now.getDate().toString().padStart(2, "0");
    const timestamp = now.getTime().toString().slice(-6);
    return `新建作业${dateStr}${timestamp}`;
};

/** 作业表单 */
const homeworkForm = reactive({
    name: generateHomeworkName(),
    type: "question",
    scoreType: "average",
    questionGroup: "byType"
});

/** 当前选择的题目类型 */
const currentType = ref<string>("");

/** 当前编辑的题目 */
const currentQuestion = ref<any>(null);

/** 题目列表 */
const questionList = ref<any[]>([]);

/** 题目表单 */
const questionForm = reactive({
    content: "",
    options: [{ content: "" }, { content: "" }, { content: "" }, { content: "" }],
    answer: "",
    analysis: "",
    difficulty: "0.8",
    knowledgePoints: [] as string[],
    tags: [] as string[],
    language: "python",
    initialCode: "",
    referenceCode: ""
});

/** 当前编辑序号 */
const editingIndex = computed(() => {
    if (currentQuestion.value) {
        return questionList.value.findIndex(q => q.id === currentQuestion.value.id) + 1;
    }
    return questionList.value.length + 1;
});

/** 计算总分 */
const totalScore = computed(() => questionList.value.length > 0 ? 100 : 0);

/** 按题型分组的题目 */
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
    const map: Record<string, string> = { single: "单选题", multiple: "多选题", fill: "填空题", code: "代码题" };
    return map[type] || "未知题型";
};

/** 获取题型序号 */
const getQuestionTypeIndex = (type: string) => Object.keys(groupedQuestions.value).indexOf(type) + 1;

/** 选择题目类型 */
const selectQuestionType = (type: string) => {
    currentType.value = type;
    currentQuestion.value = null;
    resetQuestionForm();
};

/** 选择已有题目 */
const selectQuestion = (question: any) => {
    currentQuestion.value = question;
    currentType.value = question.type;
    questionForm.content = question.content;
    questionForm.options = question.options ? [...question.options.map((o: any) => ({ ...o }))] : [];
    questionForm.answer = question.answer;
    questionForm.analysis = question.analysis;
    questionForm.difficulty = question.difficulty;
    questionForm.language = question.language || "python";
    questionForm.initialCode = question.initialCode || "";
    questionForm.referenceCode = question.referenceCode || "";
};

/** 重置题目表单 */
const resetQuestionForm = () => {
    questionForm.content = "";
    questionForm.options = [{ content: "" }, { content: "" }, { content: "" }, { content: "" }];
    questionForm.answer = "";
    questionForm.analysis = "";
    questionForm.difficulty = "0.8";
    questionForm.knowledgePoints = [];
    questionForm.tags = [];
    questionForm.language = "python";
    questionForm.initialCode = "";
    questionForm.referenceCode = "";
};

/** 重置表单 */
const resetForm = () => {
    resetQuestionForm();
    currentQuestion.value = null;
};

/** 添加选项 */
const addOption = () => questionForm.options.push({ content: "" });

/** 删除选项 */
const removeOption = (index: number) => {
    const removedLetter = String.fromCharCode(65 + index);
    // 从答案中移除该选项
    if (questionForm.answer.includes(removedLetter)) {
        questionForm.answer = questionForm.answer.replace(removedLetter, "");
    }
    // 更新答案中大于该选项的字母
    let newAnswer = "";
    for (const letter of questionForm.answer) {
        if (letter > removedLetter) {
            newAnswer += String.fromCharCode(letter.charCodeAt(0) - 1);
        } else {
            newAnswer += letter;
        }
    }
    questionForm.answer = newAnswer;
    questionForm.options.splice(index, 1);
};

/** 判断选项是否被选中 */
const isOptionSelected = (index: number) => {
    const letter = String.fromCharCode(65 + index);
    return questionForm.answer.includes(letter);
};

/** 切换答案选择 */
const toggleAnswer = (index: number) => {
    const letter = String.fromCharCode(65 + index);
    if (currentType.value === "single") {
        // 单选：直接替换
        questionForm.answer = letter;
    } else {
        // 多选：切换选中状态
        if (questionForm.answer.includes(letter)) {
            questionForm.answer = questionForm.answer.replace(letter, "");
        } else {
            // 按字母顺序插入
            const letters = (questionForm.answer + letter).split("").sort().join("");
            questionForm.answer = letters;
        }
    }
};

/** 删除当前题目 */
const deleteCurrentQuestion = () => {
    if (currentQuestion.value) {
        const index = questionList.value.findIndex(q => q.id === currentQuestion.value.id);
        if (index > -1) questionList.value.splice(index, 1);
        ElMessage.success("删除成功");
    }
    resetForm();
    currentType.value = "";
};

/** 保存题目 */
const saveQuestion = () => {
    if (!questionForm.content || questionForm.content === "<p><br></p>") {
        ElMessage.warning("请输入题目内容");
        return;
    }
    // 单选题和多选题需要验证选项不能为空
    if (currentType.value === "single" || currentType.value === "multiple") {
        const emptyOption = questionForm.options.find(o => !o.content.trim());
        if (emptyOption) {
            ElMessage.warning("选项内容不能为空");
            return;
        }
        // 验证答案
        if (!questionForm.answer) {
            ElMessage.warning("请选择正确答案");
            return;
        }
        // 多选题至少选择两个答案
        if (currentType.value === "multiple" && questionForm.answer.length < 2) {
            ElMessage.warning("多选题至少需要选择两个正确答案");
            return;
        }
    }
    const question = {
        id: currentQuestion.value?.id || Date.now(),
        type: currentType.value,
        content: questionForm.content,
        options: (currentType.value === "single" || currentType.value === "multiple") ? [...questionForm.options.map(o => ({ ...o }))] : [],
        answer: questionForm.answer,
        analysis: questionForm.analysis,
        difficulty: questionForm.difficulty,
        language: questionForm.language,
        initialCode: questionForm.initialCode,
        referenceCode: questionForm.referenceCode
    };
    if (currentQuestion.value) {
        const index = questionList.value.findIndex(q => q.id === currentQuestion.value.id);
        if (index > -1) questionList.value[index] = question;
    } else {
        questionList.value.push(question);
    }
    ElMessage.success("保存成功");
    resetForm();
    currentType.value = "";
};

/** 跳转预览页面 */
const toPreview = () => {
    // 保存当前数据到 sessionStorage
    const homeworkData = {
        homeworkInfo: {
            id: route.params.id,
            name: homeworkForm.name,
            type: homeworkForm.type,
            scoreType: homeworkForm.scoreType,
            questionGroup: homeworkForm.questionGroup
        },
        questionList: questionList.value
    };
    sessionStorage.setItem("homeworkEditData", JSON.stringify(homeworkData));
    router.push({ path: `/courseManage/homework/preview/${route.params.id || "add"}`, query: { courseId } });
};

/** 保存作业到后端 */
const saveHomework = async () => {
    if (!homeworkForm.name.trim()) {
        ElMessage.warning("请输入作业名称");
        return;
    }
    // 自动保存当前正在编辑的题目
    if (currentType.value && questionForm.content && questionForm.content !== "<p><br></p>") {
        saveQuestion();
    }
    if (questionList.value.length === 0) {
        ElMessage.warning("请至少添加一道题目");
        return;
    }
    const payload = {
        name: homeworkForm.name,
        type: homeworkForm.type,
        scoreType: homeworkForm.scoreType,
        questions: questionList.value.map((q, index) => ({
            type: q.type,
            content: q.content,
            options: (q.options || []).map((o: any) => ({ content: o.content })),
            answer: q.answer,
            analysis: q.analysis,
            difficulty: parseFloat(q.difficulty) || 0.6,
            sortOrder: index,
            language: q.language || "",
            initialCode: q.initialCode || "",
            referenceCode: q.referenceCode || ""
        }))
    };
    try {
        const isAdd = route.params.id === "add" || isNaN(Number(route.params.id));
        if (isAdd) {
            await createHomework(courseId, payload);
            ElMessage.success("作业创建成功");
        } else {
            const id = Number(route.params.id);
            await updateHomework(courseId, id, payload);
            ElMessage.success("作业更新成功");
        }
        router.back();
    } catch (error: any) {
        ElMessage.error(error.message || "保存作业失败");
    }
};

onMounted(async () => {
    const isEdit = route.params.id !== "add" && !isNaN(Number(route.params.id));
    if (isEdit) {
        // 编辑模式：从后端加载作业详情
        try {
            const res: any = await getHomeworkDetail(courseId, Number(route.params.id));
            const data = res.data || res;
            if (data) {
                homeworkForm.name = data.name || generateHomeworkName();
                homeworkForm.type = data.type || "question";
                homeworkForm.scoreType = data.scoreType || "average";
                if (data.questions && data.questions.length > 0) {
                    questionList.value = data.questions.map((q: any) => ({
                        id: q.id || Date.now() + Math.random(),
                        type: q.type,
                        content: q.content,
                        options: (q.options || []).map((o: any) => ({ content: o.content })),
                        answer: q.answer,
                        analysis: q.analysis,
                        difficulty: String(q.difficulty || "0.6")
                    }));
                }
            }
        } catch (error) {
            console.warn("加载作业详情失败，回退到 sessionStorage", error);
        }
    }
    // 尝试从 sessionStorage 获取数据进行回显（兜底）
    const savedData = sessionStorage.getItem("homeworkEditData");
    if (savedData && questionList.value.length === 0) {
        const data = JSON.parse(savedData);
        if (data.homeworkInfo) {
            homeworkForm.name = data.homeworkInfo.name || generateHomeworkName();
            homeworkForm.type = data.homeworkInfo.type || "question";
            homeworkForm.scoreType = data.homeworkInfo.scoreType || "average";
            homeworkForm.questionGroup = data.homeworkInfo.questionGroup || "byType";
        }
        if (data.questionList && data.questionList.length > 0) {
            questionList.value = data.questionList;
        }
    }
});
</script>

<style scoped lang="scss">
.main-tab {
    display: flex;
    flex-direction: column;
    height: 100%;
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

.mb10 {
    margin-bottom: 10px;
}

.mt20 {
    margin-top: 20px;
}

.homework-name-input {
    :deep(.el-input__wrapper) {
        font-size: 18px;
        color: black !important;
        font-weight: bold;
        border: none !important;
        border-bottom: 1px solid var(--el-color-primary) !important;
        border-radius: 0 !important;
        box-shadow: none !important;
    }

    :deep(.el-input__inner) {
        color: var(--el-color-primary) !important;
        font-weight: bold;
    }
}

.form-item {
    display: flex;
    align-items: center;
    gap: 15px;

    .form-label {
        color: #606266;
        white-space: nowrap;
    }
}

.text-gray {
    color: #909399;
    font-size: 12px;
}

.main-content {
    display: flex;
    flex: 1;
    gap: 10px;
    min-height: 0;
}

.question-overview {
    width: 200px;
    background: var(--el-bg-color);
    border-radius: 8px;
    padding: 15px;
    flex-shrink: 0;
    overflow-y: auto;

    .overview-header {
        font-size: 14px;
        color: #606266;
        padding-bottom: 10px;
        border-bottom: 1px solid #ebeef5;
        margin-bottom: 10px;
    }

    .type-title {
        font-size: 14px;
        color: #303133;
        margin: 10px 0;
    }

    .question-items-list {
        display: flex;
        flex-direction: column;
        gap: 4px;

        .question-item-row {
            display: flex;
            align-items: center;
            padding: 8px 10px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
            color: #606266;
            transition: all 0.2s;

            .item-index {
                flex-shrink: 0;
            }

            .item-content {
                flex: 1;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }

            &:hover {
                background: #ecf5ff;
            }

            &.active {
                background: var(--el-color-primary-light-9);
                color: var(--el-color-primary);

                .item-index {
                    color: var(--el-color-primary);
                }
            }
        }
    }
}

.question-editor {
    flex: 1;
    background: var(--el-bg-color);
    border-radius: 8px;
    padding: 20px;
    overflow-y: auto;
}

.add-question-bar {
    display: flex;
    align-items: center;
    gap: 15px;
    padding-bottom: 15px;
    border-bottom: 1px solid #ebeef5;

    .add-label {
        color: #606266;
        display: flex;
        align-items: center;
        gap: 5px;
    }

    .question-types {
        display: flex;
        gap: 10px;
    }

    .import-actions {
        margin-left: auto;
    }
}

.question-form {
    padding: 20px 0;

    .question-form-header {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 15px;

        .question-number {
            font-size: 16px;
            font-weight: bold;
            color: #303133;
        }

        .question-type-label {
            color: #606266;
        }

        .delete-btn {
            margin-left: auto;
        }
    }
}

.editor-wrapper {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    margin-bottom: 20px;

    .editor-container {
        min-height: 200px;
    }
}

.options-area {
    margin-bottom: 20px;

    .option-item {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 10px;

        .option-label {
            width: 24px;
            height: 24px;
            border: 1px solid #dcdfe6;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #606266;
            font-size: 12px;
            flex-shrink: 0;
            cursor: pointer;
            transition: all 0.2s;

            &:hover {
                border-color: var(--el-color-primary);
                color: var(--el-color-primary);
            }

            &.selected {
                background: var(--el-color-primary);
                border-color: var(--el-color-primary);
                color: #fff;
            }
        }
    }

    .answer-hint {
        margin-top: 10px;
        font-size: 13px;

        .hint-label {
            color: #606266;
        }

        .hint-value {
            color: var(--el-color-primary);
            font-weight: 500;
        }
    }
}

.answer-area {
    margin-bottom: 20px;
}

.code-area {
    margin-bottom: 20px;

    .code-config {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 15px;

        .config-label {
            font-size: 14px;
            color: #606266;
            white-space: nowrap;
        }
    }

    .code-field {
        margin-bottom: 15px;

        .field-label {
            display: block;
            font-size: 13px;
            color: #909399;
            margin-bottom: 6px;
        }
    }
}

.question-config {
    display: flex;
    flex-direction: column;
    gap: 15px;
    margin-bottom: 20px;

    .config-item {
        display: flex;
        align-items: center;
        gap: 15px;

        .config-label {
            width: 50px;
            color: #606266;
        }
    }
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    padding-top: 15px;
    border-top: 1px solid #ebeef5;
}

.empty-editor {
    padding: 100px 0;
}
</style>
