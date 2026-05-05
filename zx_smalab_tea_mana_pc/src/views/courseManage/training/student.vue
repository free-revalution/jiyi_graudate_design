<template>
    <div class="table-box">
        <div class="bg-white radius8 p20 mb20">
            <div class="flx-justify-between ">
                <div class="flx-center">
                    <el-button icon="ArrowLeft" @click="goBack">返回</el-button>
                    <h2 class="ml20 mb0 mt0">{{ trainingInfo.name }}</h2>
                    <el-tag effect="dark" :type="getStatusType(trainingInfo.status)" class="ml10">{{
                        getStatusText(trainingInfo.status) }}</el-tag>
                </div>
                <div>
                    <el-button type="primary" icon="Edit">导出实训报告</el-button>
                </div>
            </div>
        </div>
        <!-- 总体评价 -->
        <div class="bg-white radius8 p20 mb20">
            <div class="evaluation-header flx-justify-between mb20 ">
                <span class="evaluation-title">总体评价</span>
                <el-button type="primary" plain icon="EditPen">点评作业</el-button>
            </div>

            <!-- 学生基本信息 -->
            <div class="student-info-card flx-align-center  mb20">
                <el-avatar :size="64" :src="studentInfo.avatar" class="mr20" />
                <div class="student-detail">
                    <div class="student-name mb10">
                        <span class="name">{{ studentInfo.name }}</span>
                        <el-tag :type="getStatusType(studentInfo.trainingStatus)" class="ml10">{{
                            getStatusText(studentInfo.trainingStatus) }}</el-tag>
                    </div>
                    <div class="student-meta">
                        <span class="meta-item">学号：{{ studentInfo.studentId }}</span>
                        <span class="meta-item">分班：{{ studentInfo.className }}</span>
                    </div>
                    <div class="student-meta">
                        <span class="meta-item">截止前完成关卡：{{ studentInfo.passBeforeDeadline }}</span>
                        <span class="meta-item">最新完成关卡：{{ studentInfo.latestPass }}</span>
                        <span class="meta-item">完成效率：{{ studentInfo.efficiency }}</span>
                        <span class="meta-item">课堂最高完成效率：{{ studentInfo.maxEfficiency }}</span>
                    </div>
                </div>
            </div>

            <!-- 实训数据 -->
            <el-descriptions :column="7" border direction="vertical">
                <el-descriptions-item label="通关时间">{{ studentInfo.passTime }}</el-descriptions-item>
                <el-descriptions-item label="计时规则">{{ studentInfo.timeRule }}</el-descriptions-item>
                <el-descriptions-item label="实训总耗时">{{ studentInfo.totalTime }}</el-descriptions-item>
                <el-descriptions-item label="评测次数">{{ studentInfo.evalCount }}</el-descriptions-item>
                <el-descriptions-item label="迟交扣分">{{ studentInfo.lateDeduction }}</el-descriptions-item>
                <el-descriptions-item label="最终成绩">{{ studentInfo.finalScore }}</el-descriptions-item>
                <el-descriptions-item label="总评">{{ studentInfo.comment }}</el-descriptions-item>
            </el-descriptions>
        </div>

        <!-- 阶段成绩 -->
        <div class="bg-white radius8 p20 mb20">
            <div class="section-title mb20">阶段成绩</div>
            <el-table :data="stageScoreList" border :style="{ width: '100%' }"
                :header-cell-style="{ background: '#f5f7fa', color: '#606266' }">
                <el-table-column prop="stage" label="关卡" width="60" align="center" />
                <el-table-column prop="taskName" label="任务名称" min-width="150" />
                <el-table-column prop="startTime" label="开启时间" width="100" align="center" />
                <el-table-column prop="codeChanges" label="代码修改行数" width="130" align="center" />
                <el-table-column prop="evalCount" label="评测次数" width="90" align="center" />
                <el-table-column prop="finishTime" label="完成时间" width="100" align="center" />
                <el-table-column prop="trainingTime" label="实训耗时" width="90" align="center" />
                <el-table-column prop="viewAnswer" label="查看答案" width="110" align="center" />
                <el-table-column prop="exp" label="经验值" width="80" align="center" />
                <el-table-column prop="stageScore" label="关卡得分" width="90" align="center" />
                <el-table-column prop="deduction" label="调分" width="80" align="center">
                    <template #default="{ row }">
                        <span class="deduction-text">{{ row.deduction }}</span>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script setup lang="ts" name="courseManageTrainingStudent">
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { getTrainingStudentDetail } from "@/api/modules/training";

const router = useRouter();
const route = useRoute();

/** 实训信息 */
const trainingInfo = ref<any>({
    id: route.params.id,
    name: "",
    status: ""
});

/** 学生信息 */
const studentInfo = ref<any>({
    id: route.params.studentId,
    name: "",
    avatar: "",
    studentId: "",
    className: "",
    trainingStatus: "",
    passBeforeDeadline: "",
    latestPass: "",
    efficiency: "",
    maxEfficiency: "",
    passTime: "",
    timeRule: "",
    totalTime: "",
    evalCount: 0,
    lateDeduction: "",
    finalScore: "",
    comment: ""
});

/** 阶段成绩列表 */
const stageScoreList = ref<any[]>([]);

/** 加载学生实训详情数据 */
onMounted(async () => {
    const courseId = Number(route.query.courseId) || 1;
    const id = Number(route.params.id);
    const userId = Number(route.params.studentId);
    try {
        const res = await getTrainingStudentDetail(courseId, id, userId);
        const data = res.data || res;
        if (data.student) {
            studentInfo.value = { ...studentInfo.value, ...data.student };
        }
        if (data.stageScores) stageScoreList.value = data.stageScores;
    } catch (e) {
        ElMessage.error("获取学生实训详情失败");
    }
});

/** 获取实训状态类型 */
const getStatusType = (status: string): "success" | "info" | "warning" | "danger" | "primary" => {
    const map: Record<string, "success" | "info" | "warning" | "danger" | "primary"> = {
        "未发布": "info",
        "未开始": "warning",
        "进行中": "success",
        "已截止": "danger",
        completed: "success",
        ongoing: "primary",
        notStarted: "info"
    };
    return map[status] || "info";
};

/** 获取实训状态文本 */
const getStatusText = (status: string) => {
    const map: Record<string, string> = {
        "未发布": "未发布",
        "未开始": "未开始",
        "进行中": "进行中",
        "已截止": "已截止",
        completed: "已完成",
        ongoing: "进行中",
        notStarted: "未开始"
    };
    return map[status] || "未知";
};

/** 获取评阅状态类型 */
const getReviewStatusType = (status: string): "success" | "info" | "warning" | "danger" | "primary" => {
    const map: Record<string, "success" | "info" | "warning" | "danger" | "primary"> = {
        reviewed: "success",
        pending: "warning",
        rejected: "danger"
    };
    return map[status] || "info";
};

/** 获取评阅状态文本 */
const getReviewStatusText = (status: string) => {
    const map: Record<string, string> = {
        reviewed: "已评阅",
        pending: "待评阅",
        rejected: "已打回"
    };
    return map[status] || "未知";
};

/** 返回上一页 */
const goBack = () => {
    router.push({ path: `/courseManage/training/detail/${route.params.id}`, query: { courseId: route.query.courseId } });
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

.mr20 {
    margin-right: 20px;
}

.mb10 {
    margin-bottom: 10px;
}

.evaluation-header {
    .evaluation-title {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
    }
}

.student-info-card {
    padding: 20px;
    background-color: #fafafa;
    border-radius: 8px;

    .student-detail {
        flex: 1;

        .student-name {
            .name {
                font-size: 18px;
                font-weight: bold;
                color: #303133;
            }
        }

        .student-meta {
            color: #797a7c;
            font-size: 14px;
            margin-bottom: 8px;

            .meta-item {
                margin-right: 40px;
            }
        }
    }
}

.flx-align-center {
    display: flex;
    align-items: center;
}

.section-title {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
}

.deduction-text {
    color: #67c23a;
}
</style>
