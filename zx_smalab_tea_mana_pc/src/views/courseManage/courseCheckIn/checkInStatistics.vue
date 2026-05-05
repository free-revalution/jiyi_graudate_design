<template>
    <div>
        <div class="bg-white radius8 p20 mb10">
            <div class="flx-justify-between">
                <div class="flx-center">
                    <el-button icon="ArrowLeft" @click="goBack">返回</el-button>
                    <h2 class="ml20 mb0 mt0">{{ checkInInfo.name }}</h2>
                    <el-tag effect="dark" :type="getStatusType(checkInInfo.status)" class="ml10">{{
                        getStatusText(checkInInfo.status) }}</el-tag>
                </div>
                <div>
                    <el-button type="primary" icon="Edit">导出实训报告</el-button>
                </div>
            </div>
        </div>

        <!-- 统计数据 -->
        <div class="bg-white radius8 p20 mb10">
            <div class="section-title mb20">签到统计</div>
            <el-row :gutter="20">
                <el-col :span="8">
                    <div class="statistic-card">
                        <el-statistic title="平均出勤率" :value="statisticsData.avgAttendanceRate" suffix="%" />
                    </div>
                </el-col>
                <el-col :span="8">
                    <div class="statistic-card">
                        <el-statistic title="考勤次数" :value="statisticsData.attendanceCount" />
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 出勤率折线图 -->
        <div class="bg-white radius8 p20 mb10">
            <div class="flx-justify-between mb20">
                <div class="section-title">出勤率趋势</div>
                <el-radio-group v-model="dateRange" @change="handleDateRangeChange">
                    <el-radio-button value="7">近7天</el-radio-button>
                    <el-radio-button value="14">近14天</el-radio-button>
                    <el-radio-button value="30">近30天</el-radio-button>
                </el-radio-group>
            </div>
            <div ref="chartRef" class="chart-container"></div>
        </div>

        <!-- 成员详情列表 -->
        <ProTable ref="proTable" :columns="columns" :data="memberList" row-key="id">
            <template #tableHeader>
                <div class="section-title">成员详情</div>
            </template>
            <template #memberInfo="scope">
                <div class="member-info">
                    <el-avatar :size="32" :src="scope.row.avatar" />
                    <div class="member-detail">
                        <span class="member-name">{{ scope.row.name }} {{ scope.row.studentId }}</span>
                        <!-- <span class="member-id">{{ scope.row.studentId }}</span> -->
                    </div>
                </div>
            </template>
            <template #attendanceRate="scope">
                <el-tag
                    :type="scope.row.attendanceRate >= 90 ? 'success' : scope.row.attendanceRate >= 70 ? 'warning' : 'danger'">
                    {{ scope.row.attendanceRate }}%
                </el-tag>
            </template>
        </ProTable>
    </div>
</template>

<script setup lang="tsx" name="courseManageCheckInStatistics">
import { ref, reactive, onMounted, onUnmounted, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import * as echarts from "echarts";
import ProTable from "@/components/ProTable/index.vue";
import { ColumnProps } from "@/components/ProTable/interface";

const router = useRouter();
const route = useRoute();

// 表格配置项
const columns = reactive<ColumnProps<any>[]>([
    { prop: "memberInfo", label: "成员信息", minWidth: 180 },
    { prop: "absent", label: "缺勤", width: 80, align: "center" },
    { prop: "personalLeave", label: "事假", width: 80, align: "center" },
    { prop: "sickLeave", label: "病假", width: 80, align: "center" },
    { prop: "late", label: "迟到", width: 80, align: "center" },
    { prop: "earlyLeave", label: "早退", width: 80, align: "center" },
    { prop: "officialLeave", label: "公出假", width: 80, align: "center" },
    { prop: "attendanceRate", label: "出勤率", width: 100, align: "center" }
]);

/** 签到信息 */
const checkInInfo = ref({
    id: route.params.id,
    name: "第一周课程签到",
    date: "2024-03-01",
    status: "已结束"
});

/** 统计数据 */
const statisticsData = ref({
    avgAttendanceRate: 95.6,
    attendanceCount: 12
});

/** 日期范围 */
const dateRange = ref("7");

/** 成员列表数据 */
const memberList = ref([
    { id: 1, avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", name: "张三", studentId: "2024001", absent: 0, personalLeave: 1, sickLeave: 0, late: 1, earlyLeave: 0, officialLeave: 0, attendanceRate: 95 },
    { id: 2, avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", name: "李四", studentId: "2024002", absent: 1, personalLeave: 0, sickLeave: 1, late: 0, earlyLeave: 1, officialLeave: 0, attendanceRate: 85 },
    { id: 3, avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", name: "王五", studentId: "2024003", absent: 0, personalLeave: 0, sickLeave: 0, late: 0, earlyLeave: 0, officialLeave: 0, attendanceRate: 100 },
    { id: 4, avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", name: "赵六", studentId: "2024004", absent: 2, personalLeave: 1, sickLeave: 0, late: 2, earlyLeave: 0, officialLeave: 1, attendanceRate: 68 },
    { id: 5, avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", name: "钱七", studentId: "2024005", absent: 0, personalLeave: 0, sickLeave: 1, late: 1, earlyLeave: 0, officialLeave: 0, attendanceRate: 92 }
]);

/** 图表实例 */
const chartRef = ref<HTMLElement>();
let chartInstance: echarts.ECharts | null = null;

/** 图表数据 */
const chartData = {
    "7": {
        dates: ["03-01", "03-02", "03-03", "03-04", "03-05", "03-06", "03-07"],
        rates: [96, 94, 98, 92, 95, 97, 96]
    },
    "14": {
        dates: ["02-23", "02-24", "02-25", "02-26", "02-27", "02-28", "03-01", "03-02", "03-03", "03-04", "03-05", "03-06", "03-07", "03-08"],
        rates: [93, 95, 91, 94, 96, 92, 96, 94, 98, 92, 95, 97, 96, 94]
    },
    "30": {
        dates: ["02-07", "02-08", "02-09", "02-10", "02-11", "02-12", "02-13", "02-14", "02-15", "02-16", "02-17", "02-18", "02-19", "02-20", "02-21", "02-22", "02-23", "02-24", "02-25", "02-26", "02-27", "02-28", "03-01", "03-02", "03-03", "03-04", "03-05", "03-06", "03-07", "03-08"],
        rates: [90, 92, 88, 94, 95, 91, 93, 96, 94, 92, 95, 97, 93, 91, 94, 96, 93, 95, 91, 94, 96, 92, 96, 94, 98, 92, 95, 97, 96, 94]
    }
};

/** 初始化图表 */
const initChart = () => {
    if (!chartRef.value) return;
    chartInstance = echarts.init(chartRef.value);
    updateChart();
};

/** 更新图表 */
const updateChart = () => {
    if (!chartInstance) return;
    const data = chartData[dateRange.value as keyof typeof chartData];
    const option: echarts.EChartsOption = {
        tooltip: {
            trigger: "axis",
            formatter: "{b}<br/>出勤率: {c}%"
        },
        grid: {
            left: "3%",
            right: "4%",
            bottom: "3%",
            containLabel: true
        },
        xAxis: {
            type: "category",
            boundaryGap: false,
            data: data.dates,
            axisLine: { lineStyle: { color: "#dcdfe6" } },
            axisLabel: { color: "#606266" }
        },
        yAxis: {
            type: "value",
            min: 80,
            max: 100,
            axisLine: { show: false },
            axisLabel: { color: "#606266", formatter: "{value}%" },
            splitLine: { lineStyle: { color: "#ebeef5" } }
        },
        series: [{
            name: "出勤率",
            type: "line",
            smooth: true,
            data: data.rates,
            lineStyle: { color: "#409eff", width: 2 },
            areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: "rgba(64, 158, 255, 0.3)" },
                    { offset: 1, color: "rgba(64, 158, 255, 0.05)" }
                ])
            },
            itemStyle: { color: "#409eff" }
        }]
    };
    chartInstance.setOption(option);
};

/** 日期范围变化 */
const handleDateRangeChange = () => {
    updateChart();
};

/** 获取状态类型 */
const getStatusType = (status: string): "success" | "info" | "warning" | "danger" | "primary" => {
    const map: Record<string, "success" | "info" | "warning" | "danger" | "primary"> = {
        "未开始": "info",
        "进行中": "success",
        "已结束": "danger"
    };
    return map[status] || "info";
};

/** 获取状态文本 */
const getStatusText = (status: string) => {
    return status;
};

/** 返回上一页 */
const goBack = () => {
    router.back();
};

/** 窗口大小变化时重新渲染图表 */
const handleResize = () => {
    chartInstance?.resize();
};

onMounted(() => {
    nextTick(() => {
        initChart();
    });
    window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
    window.removeEventListener("resize", handleResize);
    chartInstance?.dispose();
});
</script>

<style scoped lang="scss">
.p20 {
    padding: 20px;
}

.mb10 {
    margin-bottom: 10px;
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

.ml20 {
    margin-left: 20px;
}

.ml10 {
    margin-left: 10px;
}

.bg-white {
    background-color: #ffffff;
}

.radius8 {
    border-radius: 8px;
}

.section-title {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
}

.statistic-card {
    padding: 20px;
    background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
    border-radius: 8px;
    border: 1px solid #ebeef5;
}

.chart-container {
    width: 100%;
    height: 350px;
}

.member-info {
    display: flex;
    align-items: center;
    gap: 10px;

    .member-detail {
        display: flex;
        flex-direction: column;

        .member-name {
            font-weight: 500;
            color: #303133;
        }

        .member-id {
            font-size: 12px;
            color: #909399;
        }
    }
}
</style>
