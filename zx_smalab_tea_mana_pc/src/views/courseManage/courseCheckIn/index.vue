<template>
    <div class="checkin-page-container">
        <div class="bg-white radius8 p20 mb10 ">
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
        <div class="card pt3 pb3 mb10 tab-header">
            <el-tabs v-model="checkInTabActive">
                <el-tab-pane label="签到记录" name="a" />
                <el-tab-pane label="统计分析" name="b" />
            </el-tabs>
        </div>
        <div class="table-main" v-if="checkInTabActive === 'a'">
            <ProTable ref="proTable" :columns="columns" :data="checkInList" class="checkInTable" row-key="id">
                <!-- 表格 header 按钮 -->
                <template #tableHeader="scope">
                    <el-button type="primary" :icon="CirclePlus" @click="openCreateDialog">新建签到</el-button>
                    <el-button type="danger" :icon="Delete" plain :disabled="!scope.isSelected"
                        @click="batchDelete(scope.selectedListIds)">批量删除</el-button>
                </template>
                <template #operation="scope">
                    <el-button type="warning" link :icon="View" @click="toDetail(scope.row)">详情</el-button>
                    <el-button type="primary" link :icon="EditPen">编辑</el-button>
                    <el-button type="danger" link :icon="Delete" @click="deleteCheckIn(scope.row)">删除</el-button>
                </template>
            </ProTable>
        </div>
        <div v-else>
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
            <ProTable ref="proTableNumbers" :columns="numbersColumns" :data="memberList" row-key="id">
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

        <!-- 新建签到弹出框 -->
        <el-dialog v-model="createDialogVisible" title="新建签到" width="60%" destroy-on-close
            class="create-checkin-dialog">
            <div class="dialog-scroll-content">
                <el-form :model="checkInForm" label-position="left" label-width="80px">
                    <el-form-item label="签到标题">
                        <el-input v-model="checkInForm.title" placeholder="请输入签到标题" />
                    </el-form-item>
                </el-form>

                <el-tabs v-model="checkInForm.type" class="mt20">
                    <el-tab-pane label="二维码签到" name="qrcode">
                        <!-- 指定签到地点 -->
                        <div class="form-row">
                            <span class="form-label">指定签到地点</span>
                            <el-switch v-model="checkInForm.enableLocation" />
                        </div>

                        <!-- 地点设置区域 -->
                        <div v-if="checkInForm.enableLocation" class="location-box">
                            <div class="form-row">
                                <span class="form-label">定位</span>
                                <el-button type="primary" link :icon="Location">选择位置</el-button>
                            </div>
                            <div class="form-row">
                                <span class="form-label">签到范围</span>
                                <el-select v-model="checkInForm.locationRange" style="width: 100px">
                                    <el-option label="100" :value="100" />
                                    <el-option label="200" :value="200" />
                                    <el-option label="500" :value="500" />
                                    <el-option label="1000" :value="1000" />
                                </el-select>
                                <span class="ml10">米</span>
                            </div>
                            <div class="form-tip">定位精确度与环境、网络、基站等有关，可能存在一定误差</div>
                        </div>

                        <!-- 自动更新二维码 -->
                        <div class="form-row">
                            <span class="form-label">自动更新二维码</span>
                            <el-switch v-model="checkInForm.autoRefreshQrcode" />
                        </div>

                        <!-- 二维码更新频率 -->
                        <div v-if="checkInForm.autoRefreshQrcode" class="form-row">
                            <span class="form-label">二维码更新频率</span>
                            <el-select v-model="checkInForm.qrcodeRefreshRate" style="width: 100px">
                                <el-option label="5秒" :value="5" />
                                <el-option label="10秒" :value="10" />
                                <el-option label="30秒" :value="30" />
                                <el-option label="60秒" :value="60" />
                            </el-select>
                        </div>

                        <!-- 活动时长 -->
                        <div class="form-row">
                            <span class="form-label">活动时长</span>
                            <el-select v-model="checkInForm.durationDays" style="width: 70px">
                                <el-option v-for="i in 31" :key="i - 1" :label="i - 1" :value="i - 1" />
                            </el-select>
                            <span class="ml5 mr10">天</span>
                            <el-select v-model="checkInForm.durationHours" style="width: 70px">
                                <el-option v-for="i in 24" :key="i - 1" :label="i - 1" :value="i - 1" />
                            </el-select>
                            <span class="ml5 mr10">小时</span>
                            <el-select v-model="checkInForm.durationMinutes" style="width: 70px">
                                <el-option v-for="i in 60" :key="i - 1" :label="i - 1" :value="i - 1" />
                            </el-select>
                            <span class="ml5 mr80">分钟</span>
                            <span style="width: 90px;color: #606266;">手动结束</span>
                            <el-switch v-model="checkInForm.manualEnd" />
                        </div>

                        <!-- 结束后迟到设置 -->
                        <div class="form-row late-setting">
                            <span style="width: 95px;color: #606266;">结束后</span>
                            <el-select v-model="checkInForm.lateMinutes" style="width: 80px">
                                <el-option v-for="i in [5, 10, 15, 20, 30]" :key="i" :label="i" :value="i" />
                            </el-select>
                            <span class="ml10">分钟以内签到，自动标记为迟到，之后不允许参与</span>
                        </div>

                        <!-- 下课签退 -->
                        <div class="form-row">
                            <span class="form-label">下课签退</span>
                            <el-switch v-model="checkInForm.enableSignOut" />
                            <el-tooltip content="开启后，学生需要在下课时再次签到确认离开" placement="top">
                                <el-icon class="ml5">
                                    <QuestionFilled />
                                </el-icon>
                            </el-tooltip>
                        </div>
                    </el-tab-pane>

                    <el-tab-pane label="位置签到" name="location">
                        <div class="empty-tip">位置签到功能开发中...</div>
                    </el-tab-pane>
                </el-tabs>
            </div>

            <template #footer>
                <el-button @click="createDialogVisible = false">取消</el-button>
                <el-button @click="saveCheckIn">保存</el-button>
                <el-button type="primary" @click="startCheckIn">立即开始</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="tsx" name="courseManageCourseCheckIn">
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from "vue";
import { ElMessage, ElMessageBox, ElTag } from "element-plus";
import ProTable from "@/components/ProTable/index.vue";
import { ProTableInstance, ColumnProps } from "@/components/ProTable/interface";
import { CirclePlus, Delete, EditPen, PieChart, Location, QuestionFilled, View } from "@element-plus/icons-vue";
import { useRoute, useRouter } from "vue-router";
import * as echarts from "echarts";
import { getCheckInList } from "@/api/modules/checkin";

const router = useRouter();
const route = useRoute();
const COURSE_ID = Number(route.query.courseId) || 1;
/** 实训信息 */
const trainingInfo = ref({
    id: route.params.id,
    name: "人工智能",
    startTime: "2025-01-15 08:00:00",
    endTime: "2025-02-15 23:59:00",
    status: "ongoing",
    createTime: "2025-01-10 10:00:00",
    creator: "张老师",
    description: "本实训旨在帮助学生掌握Python编程基础，包括变量、数据类型、控制流、函数、面向对象等核心概念。通过实践任务，学生将能够独立完成简单的Python程序开发。"
});
const checkInTabActive = ref("a");

/** 新建签到弹出框 */
const createDialogVisible = ref(false);

/** 生成默认签到标题 */
const generateDefaultTitle = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    const day = String(now.getDate()).padStart(2, "0");
    const timestamp = String(now.getTime()).slice(-6);
    return `新建签到${year}${month}${day}${timestamp}`;
};

/** 签到表单 */
const checkInForm = ref({
    title: "",
    type: "qrcode", // qrcode: 二维码签到, location: 位置签到
    enableLocation: true,
    locationRange: 500,
    autoRefreshQrcode: true,
    qrcodeRefreshRate: 10,
    durationDays: 0,
    durationHours: 0,
    durationMinutes: 30,
    manualEnd: false,
    lateMinutes: 10,
    enableSignOut: false
});

/** 打开新建签到弹出框 */
const openCreateDialog = () => {
    checkInForm.value = {
        title: generateDefaultTitle(),
        type: "qrcode",
        enableLocation: true,
        locationRange: 500,
        autoRefreshQrcode: true,
        qrcodeRefreshRate: 10,
        durationDays: 0,
        durationHours: 0,
        durationMinutes: 30,
        manualEnd: false,
        lateMinutes: 10,
        enableSignOut: false
    };
    createDialogVisible.value = true;
};

/** 保存签到 */
const saveCheckIn = () => {
    ElMessage.success("签到已保存");
    createDialogVisible.value = false;
};

/** 立即开始签到 */
const startCheckIn = () => {
    ElMessage.success("签到已开始");
    createDialogVisible.value = false;
};

// ProTable 实例
const proTable = ref<ProTableInstance>();

// 表格配置项
const columns = reactive<ColumnProps<any>[]>([
    { type: "selection", fixed: "left", width: 70 },
    { prop: "name", label: "名称", search: { el: "input" }, minWidth: 150 },
    { prop: "date", label: "日期", width: 160 },
    { prop: "totalCount", label: "人数", width: 80, align: "center" },
    { prop: "absentCount", label: "缺勤人数", width: 100, align: "center" },
    {
        prop: "status",
        label: "状态",
        width: 100,
        align: "center",
        render: (scope: any): any => {
            const statusMap: Record<string, { text: string; type: string }> = {
                "未开始": { text: "未开始", type: "info" },
                "进行中": { text: "进行中", type: "success" },
                "已结束": { text: "已结束", type: "danger" }
            };
            const status = statusMap[scope.row.status] || { text: "未知", type: "info" };
            return <ElTag type={status.type}>{status.text}</ElTag>;
        }
    },
    { prop: "operation", label: "操作", fixed: "right", width: 220 }
]);

/** 签到列表数据 */
const checkInList = ref<any[]>([]);

/** 加载签到列表 */
const fetchCheckInList = async () => {
    try {
        const res: any = await getCheckInList(COURSE_ID);
        checkInList.value = res.data || [];
    } catch (error) {
        ElMessage.error("获取签到列表失败");
    }
};

// 初始加载签到列表
fetchCheckInList();

/** 删除签到 */
const deleteCheckIn = (row: any) => {
    ElMessageBox.confirm(`确定要删除"${row.name}"吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    }).then(() => {
        ElMessage.success("删除成功");
    });
};

/** 批量删除 */
const batchDelete = (ids: string[]) => {
    ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 条签到记录吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    }).then(() => {
        ElMessage.success("批量删除成功");
        proTable.value?.clearSelection();
    });
};

/** 跳转统计页面 */
const toDetail = (row: any) => {
    router.push(`/courseManage/courseCheckIn/detail/${row.id}`);
};

/** 返回上一页 */
const goBack = () => {
    router.push({ path: "/courseManage/training", query: { courseId: route.query.courseId } });
};

/** 获取状态类型 */
const getStatusType = (status: string): "success" | "info" | "warning" | "danger" | "primary" => {
    const map: Record<string, "success" | "info" | "warning" | "danger" | "primary"> = {
        ongoing: "success",
        finished: "info",
        pending: "warning"
    };
    return map[status] || "info";
};

/** 获取状态文本 */
const getStatusText = (status: string) => {
    const map: Record<string, string> = {
        ongoing: "进行中",
        finished: "已结束",
        pending: "未开始"
    };
    return map[status] || "未知";
};

// 表格配置项
const numbersColumns = reactive<ColumnProps<any>[]>([
    { prop: "memberInfo", label: "成员信息", minWidth: 180 },
    { prop: "absent", label: "缺勤", width: 80, align: "center" },
    { prop: "personalLeave", label: "事假", width: 80, align: "center" },
    { prop: "sickLeave", label: "病假", width: 80, align: "center" },
    { prop: "late", label: "迟到", width: 80, align: "center" },
    { prop: "earlyLeave", label: "早退", width: 80, align: "center" },
    { prop: "officialLeave", label: "公出假", width: 80, align: "center" },
    { prop: "attendanceRate", label: "出勤率", width: 100, align: "center" }
]);


/** 统计数据 */
const statisticsData = ref({
    avgAttendanceRate: 95.6,
    attendanceCount: 12
});

/** 日期范围 */
const dateRange = ref("7");

/** 成员列表数据 */
const memberList = ref([
    { id: 1, avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", name: "张三", studentId: "2025001", absent: 0, personalLeave: 1, sickLeave: 0, late: 1, earlyLeave: 0, officialLeave: 0, attendanceRate: 95 },
    { id: 2, avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", name: "李四", studentId: "2025002", absent: 1, personalLeave: 0, sickLeave: 1, late: 0, earlyLeave: 1, officialLeave: 0, attendanceRate: 85 },
    { id: 3, avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", name: "王五", studentId: "2025003", absent: 0, personalLeave: 0, sickLeave: 0, late: 0, earlyLeave: 0, officialLeave: 0, attendanceRate: 100 },
    { id: 4, avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", name: "赵六", studentId: "2025004", absent: 2, personalLeave: 1, sickLeave: 0, late: 2, earlyLeave: 0, officialLeave: 1, attendanceRate: 68 },
    { id: 5, avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", name: "钱七", studentId: "2025005", absent: 0, personalLeave: 0, sickLeave: 1, late: 1, earlyLeave: 0, officialLeave: 0, attendanceRate: 92 }
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

/** 窗口大小变化时重新渲染图表 */
const handleResize = () => {
    chartInstance?.resize();
};


// 监听 tab 切换，切换到统计分析时初始化图表
watch(checkInTabActive, (val) => {
    if (val === "b") {
        nextTick(() => {
            initChart();
        });
    }
});

onMounted(() => {
    window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
    window.removeEventListener("resize", handleResize);
    chartInstance?.dispose();
});

</script>

<style scoped lang="scss">
.checkin-page-container {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.p20 {
    padding: 20px;
}

.bg-white {
    background-color: var(--el-bg-color);
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
    }
}

:deep(.card .el-form .el-form-item--default) {
    margin-bottom: 0 !important;
}

:deep(.card .operation) {
    display: flex;
    justify-content: flex-end;
}

:deep(.el-dialog .el-dialog__header) {
    border-bottom: 1px solid var(--el-color-primary) !important;
}

:deep(.el-dialog__title) {
    color: var(--el-color-primary);
}

/* 新建签到弹出框样式 */
.form-row {
    display: flex;
    align-items: center;
    margin-bottom: 20px;

    .form-label {
        color: #606266;
        min-width: 110px;
    }
}

.location-box {
    background: #f5f7fa;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 20px;
    margin-left: 100px;

    .form-row {
        margin-bottom: 12px;

        &:last-child {
            margin-bottom: 0;
        }

        .form-label {
            min-width: 70px;
        }
    }
}

.form-tip {
    color: #909399;
    font-size: 12px;
    margin-top: 5px;
}

.late-setting {
    background: #f5f7fa;
    border-radius: 8px;
    padding: 15px;
}

.empty-tip {
    text-align: center;
    color: #909399;
    padding: 40px 0;
}

.ml5 {
    margin-left: 5px;
}

.mr10 {
    margin-right: 10px;
}

.mr20 {
    margin-right: 20px;
}

.mt20 {
    margin-top: 20px;
}

/* 弹出框固定高度滚动 */
:deep(.create-checkin-dialog) {
    .el-dialog__body {
        padding: 0;
    }
}

.dialog-scroll-content {
    max-height: calc(100vh - 400px);
    overflow-y: auto;
    padding: 20px;
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

/*去掉tabs底部的下划线*/
:deep(.el-tabs__nav-wrap::after) {
    background-color: transparent !important;
}
</style>
