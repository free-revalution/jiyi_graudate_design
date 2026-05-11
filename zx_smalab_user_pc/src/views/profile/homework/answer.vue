<template>
  <div class="homework-answer-page">
    <div class="container">
      <!-- 顶部作业信息区 -->
      <div class="homework-header-card">
        <div class="header-left">
          <el-button type="primary" plain @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <h1 class="homework-title">{{ homeworkInfo.name }}</h1>
        </div>
        <div class="header-right">
          <div class="stats-list">
            <div class="stat-tag">
              <span class="label">单选题</span>
              <span class="value">{{ questionStats.single }} 道</span>
            </div>
            <div class="stat-tag">
              <span class="label">多选题</span>
              <span class="value">{{ questionStats.multiple }} 道</span>
            </div>
            <div class="stat-tag">
              <span class="label">判断题</span>
              <span class="value">{{ questionStats.judge }} 道</span>
            </div>
            <div class="stat-tag">
              <span class="label">填空题</span>
              <span class="value">{{ questionStats.fill }} 道</span>
            </div>
            <div class="stat-tag" v-if="codeQuestions.length > 0">
              <span class="label">代码题</span>
              <span class="value">{{ questionStats.code }} 道</span>
            </div>
            <div class="stat-tag total">
              <span class="label">总计</span>
              <span class="value">{{ questionStats.total }} 道</span>
            </div>
          </div>
          <el-button type="success" size="large" @click="submitHomework">提交作业</el-button>
        </div>
      </div>

      <!-- 题目列表区 -->
      <div class="questions-card">
        <!-- 单选题 -->
        <div class="question-section" v-if="singleQuestions.length">
          <div class="section-header">
            <span class="section-title">一、单选题</span>
            <span class="section-count">（共{{ singleQuestions.length }}题）</span>
          </div>
          <div class="question-list">
            <div v-for="(q, index) in singleQuestions" :key="q.id" class="question-item">
              <div class="question-num">{{ index + 1 }}.</div>
              <div class="question-content">
                <div class="question-text" v-html="q.title"></div>
                <el-radio-group v-model="answers[q.id]" class="options-group">
                  <el-radio v-for="opt in q.options" :key="opt.key" :value="opt.key" class="option-item"> {{ opt.key }}. {{ opt.value }} </el-radio>
                </el-radio-group>
              </div>
            </div>
          </div>
        </div>

        <!-- 多选题 -->
        <div class="question-section" v-if="multipleQuestions.length">
          <div class="section-header">
            <span class="section-title">二、多选题</span>
            <span class="section-count">（共{{ multipleQuestions.length }}题）</span>
          </div>
          <div class="question-list">
            <div v-for="(q, index) in multipleQuestions" :key="q.id" class="question-item">
              <div class="question-num">{{ index + 1 }}.</div>
              <div class="question-content">
                <div class="question-text" v-html="q.title"></div>
                <el-checkbox-group v-model="answers[q.id]" class="options-group">
                  <el-checkbox v-for="opt in q.options" :key="opt.key" :value="opt.key" class="option-item"> {{ opt.key }}. {{ opt.value }} </el-checkbox>
                </el-checkbox-group>
              </div>
            </div>
          </div>
        </div>

        <!-- 判断题 -->
        <div class="question-section" v-if="judgeQuestions.length">
          <div class="section-header">
            <span class="section-title">三、判断题</span>
            <span class="section-count">（共{{ judgeQuestions.length }}题）</span>
          </div>
          <div class="question-list">
            <div v-for="(q, index) in judgeQuestions" :key="q.id" class="question-item">
              <div class="question-num">{{ index + 1 }}.</div>
              <div class="question-content">
                <div class="question-text" v-html="q.title"></div>
                <el-radio-group v-model="answers[q.id]" class="options-group">
                  <el-radio value="true" class="option-item">正确</el-radio>
                  <el-radio value="false" class="option-item">错误</el-radio>
                </el-radio-group>
              </div>
            </div>
          </div>
        </div>

        <!-- 填空题 -->
        <div class="question-section" v-if="fillQuestions.length">
          <div class="section-header">
            <span class="section-title">四、填空题</span>
            <span class="section-count">（共{{ fillQuestions.length }}题）</span>
          </div>
          <div class="question-list">
            <div v-for="(q, index) in fillQuestions" :key="q.id" class="question-item">
              <div class="question-num">{{ index + 1 }}.</div>
              <div class="question-content">
                <div class="question-text" v-html="q.title"></div>
                <el-input v-model="answers[q.id]" placeholder="请输入答案" style="max-width: 400px" />
              </div>
            </div>
          </div>
        </div>

        <!-- 代码题 -->
        <div class="question-section" v-if="codeQuestions.length">
          <div class="section-header">
            <span class="section-title">五、代码题</span>
            <span class="section-count">（共{{ codeQuestions.length }}题）</span>
          </div>
          <div class="question-list">
            <div v-for="(q, index) in codeQuestions" :key="q.id" class="question-item code-question-item">
              <div class="question-num">{{ index + 1 }}.</div>
              <div class="question-content">
                <div class="question-text" v-html="q.title"></div>
                <div v-if="q.initialCode" class="initial-code-hint">
                  <el-alert type="info" :closable="false" show-icon title="初始代码模板" style="margin-bottom:10px" />
                  <pre class="initial-code-preview">{{ q.initialCode }}</pre>
                </div>
                <div class="code-editor-area">
                  <div class="code-editor-header">
                    <el-select v-model="codeLanguages[q.id]" size="small" style="width:140px" @change="handleLangChange(q.id)">
                      <el-option label="Python" value="python" />
                      <el-option label="C/C++" value="cpp" />
                      <el-option label="Java" value="java" />
                    </el-select>
                    <el-button size="small" @click="resetCode(q)">重置代码</el-button>
                  </div>
                  <textarea
                    v-model="answers[q.id]"
                    class="code-textarea"
                    :placeholder="getCodePlaceholder(codeLanguages[q.id])"
                    :rows="18"
                  ></textarea>
                </div>
                <div v-if="q.referenceCode" class="reference-code-hint">
                  <el-alert type="success" :closable="false" show-icon title="示例代码" style="margin-bottom:10px" />
                  <pre class="reference-code-preview">{{ q.referenceCode }}</pre>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ArrowLeft } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getHomeworkQuestions, submitHomework as submitHomeworkApi } from "@/api/homework";

const route = useRoute();
const router = useRouter();

// 作业信息
const homeworkInfo = ref({
  id: "",
  name: "",
  courseId: ""
});

// 答案
const answers = reactive({});
// 代码题语言选择
const codeLanguages = reactive({});

// 题目列表（统一格式，type: single单选, multiple多选, judge判断, fill填空）
const questions = ref([]);

// 按类型筛选题目
const singleQuestions = computed(() => questions.value.filter(q => q.type === "single"));
const multipleQuestions = computed(() => questions.value.filter(q => q.type === "multiple"));
const judgeQuestions = computed(() => questions.value.filter(q => q.type === "judge"));
const fillQuestions = computed(() => questions.value.filter(q => q.type === "fill"));
const codeQuestions = computed(() => questions.value.filter(q => q.type === "code"));

// 题目统计
const questionStats = computed(() => {
  const single = singleQuestions.value.length;
  const multiple = multipleQuestions.value.length;
  const judge = judgeQuestions.value.length;
  const fill = fillQuestions.value.length;
  const code = codeQuestions.value.length;
  return {
    single,
    multiple,
    judge,
    fill,
    code,
    total: single + multiple + judge + fill + code
  };
});

const fetchQuestions = async () => {
  try {
    const res = await getHomeworkQuestions(route.params.id, route.params.homeworkId);
    const data = res.data;
    if (data) {
      homeworkInfo.value.name = data.name || data.title || "";
      questions.value = data.questions || data || [];
      // 初始化多选题答案为数组
      questions.value.forEach(q => {
        if (q.type === "multiple") {
          answers[q.id] = [];
        }
        if (q.type === "code") {
          answers[q.id] = q.initialCode || "";
          codeLanguages[q.id] = q.language || "python";
        }
      });
    }
  } catch (error) {
    console.error("获取作业题目失败:", error);
  }
};

// 初始化答案
onMounted(() => {
  homeworkInfo.value.id = route.params.homeworkId;
  homeworkInfo.value.courseId = route.params.id;
  fetchQuestions();
});

// 返回
const goBack = () => {
  router.back();
};

// 重置代码
const resetCode = (q) => {
  answers[q.id] = q.initialCode || "";
};

// 语言切换时的占位提示
const getCodePlaceholder = (lang) => {
  const map = { python: "# 请在此处编写 Python 代码", cpp: "// 请在此处编写 C/C++ 代码", java: "// 请在此处编写 Java 代码" };
  return map[lang] || "// 请在此处编写代码";
};

const handleLangChange = (questionId) => {
  // 语言切换时保留代码内容，只改变语言标记
};

// 提交作业
const submitHomework = () => {
  ElMessageBox.confirm("确定要提交作业吗？提交后将无法修改。", "提交确认", {
    confirmButtonText: "确定提交",
    cancelButtonText: "取消",
    type: "warning"
  })
    .then(async () => {
      try {
        await submitHomeworkApi(route.params.id, route.params.homeworkId, answers);
        ElMessage.success("作业提交成功！");
        router.back();
      } catch (error) {
        console.error("提交作业失败:", error);
      }
    })
    .catch(() => {});
};
</script>

<style lang="scss" scoped>
.homework-answer-page {
  background: #f5f7fa;
  min-height: 100vh;
  padding: 20px 0;

  .container {
    max-width: calc(100vw - 200px);
    min-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .homework-header-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px 30px;
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
      gap: 20px;

      .homework-title {
        font-size: 22px;
        font-weight: 600;
        color: #303133;
        margin: 0;
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 30px;

      .stats-list {
        display: flex;
        gap: 15px;

        .stat-tag {
          background: #f0f9eb;
          border: 1px solid #e1f3d8;
          border-radius: 6px;
          padding: 8px 15px;
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 4px;

          .label {
            font-size: 12px;
            color: #67c23a;
          }

          .value {
            font-size: 14px;
            font-weight: 600;
            color: #67c23a;
          }

          &.total {
            background: linear-gradient(to right, #00c261 0%, #17ddae 100%);
            border: none;

            .label,
            .value {
              color: #fff;
            }
          }
        }
      }
    }
  }

  .questions-card {
    background: #fff;
    border-radius: 8px;
    padding: 30px;

    .question-section {
      margin-bottom: 40px;

      &:last-child {
        margin-bottom: 0;
      }

      .section-header {
        margin-bottom: 20px;
        padding-bottom: 15px;
        border-bottom: 2px solid #00c185;

        .section-title {
          font-size: 18px;
          font-weight: 600;
          color: #303133;
        }

        .section-count {
          font-size: 14px;
          color: #909399;
          margin-left: 10px;
        }
      }

      .question-list {
        .question-item {
          display: flex;
          gap: 10px;
          padding: 20px;
          margin-bottom: 15px;
          background: #fafafa;
          border-radius: 8px;
          border-left: 4px solid #00c185;

          &:last-child {
            margin-bottom: 0;
          }

          .question-num {
            font-size: 16px;
            font-weight: 600;
            color: #00c185;
            min-width: 30px;
          }

          .question-content {
            flex: 1;

            .question-text {
              font-size: 15px;
              color: #303133;
              line-height: 1.6;
              margin-bottom: 15px;
            }

            .initial-code-hint {
              margin-bottom: 15px;

              .initial-code-preview {
                background: #f5f7fa;
                border: 1px solid #e4e7ed;
                border-radius: 6px;
                padding: 12px 15px;
                font-family: Consolas, "Courier New", monospace;
                font-size: 13px;
                line-height: 1.6;
                color: #606266;
                overflow-x: auto;
                margin: 0;
                white-space: pre-wrap;
              }
            }

            .code-editor-area {
              border: 1px solid #e4e7ed;
              border-radius: 6px;
              overflow: hidden;

              .code-editor-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 8px 15px;
                background: #fafafa;
                border-bottom: 1px solid #e4e7ed;
              }

              .code-textarea {
                width: 100%;
                background: #1e1e1e;
                color: #d4d4d4;
                font-family: Consolas, "Courier New", monospace;
                font-size: 13px;
                line-height: 1.6;
                padding: 15px;
                border: none;
                outline: none;
                resize: vertical;
                box-sizing: border-box;
                tab-size: 2;
              }
            }

            .reference-code-hint {
              margin-top: 15px;

              .reference-code-preview {
                background: #f0f9eb;
                border: 1px solid #e1f3d8;
                border-radius: 6px;
                padding: 12px 15px;
                font-family: Consolas, "Courier New", monospace;
                font-size: 13px;
                line-height: 1.6;
                color: #67c23a;
                overflow-x: auto;
                margin: 0;
                white-space: pre-wrap;
              }
            }

            .options-group {
              display: flex;
              flex-direction: column;
              align-items: flex-start;
              gap: 12px;

              .option-item {
                margin: 0;
                padding: 10px 15px;
                background: #fff;
                border-radius: 6px;
                border: 1px solid #e4e7ed;
                transition: all 0.3s;

                &:hover {
                  border-color: #00c185;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
