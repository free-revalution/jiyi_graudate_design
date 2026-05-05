<template>
  <div class="dataVisualize-box">
    <div class="card top-box">
      <div class="top-content">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
            <div class="gitee-traffic traffic-box">
              <div class="traffic-img">
                <img src="./images/add_person.png" alt="" />
              </div>
              <div class="traffic-content">
                <div class="item-value">{{ overviewData.studentCount?.toLocaleString() || '--' }}</div>
                <div class="traffic-name sle">学生数</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
            <div class="gitHub-traffic traffic-box">
              <div class="traffic-img">
                <img src="./images/add_team.png" alt="" />
              </div>
              <div class="traffic-content">
                <div class="item-value">{{ overviewData.teacherCount?.toLocaleString() || '--' }}</div>
                <div class="traffic-name sle">教师数</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
            <div class="today-traffic traffic-box">
              <div class="traffic-img">
                <img src="./images/today.png" alt="" />
              </div>
              <div class="traffic-content">
                <div class="item-value">{{ overviewData.courseCount?.toLocaleString() || '--' }}</div>
                <div class="traffic-name sle">课程数</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
            <div class="yesterday-traffic traffic-box">
              <div class="traffic-img">
                <img src="./images/book_sum.png" alt="" />
              </div>
              <div class="traffic-content">
                <div class="item-value">{{ overviewData.homeworkCount?.toLocaleString() || '--' }}</div>
                <div class="traffic-name sle">作业数</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-tabs v-model="tabActive" class="demo-tabs" @tab-change="handleTabChange">
        <el-tab-pane v-for="item in tab" :key="item.name" :label="item.label" :name="item.name"></el-tab-pane>
      </el-tabs>


      <!-- 下拉选择框，用于选择时间段（近三天、近七天等） -->
      <div class="cu-filter-section-box">
        <div class="cu-filter-section-item">
          <el-form-item label="时段">
            <el-select class="cu-select-0030" v-model="selectedTimePeriod" placeholder="选择时段"
              @change="handleTimePeriodChange">
              <el-option v-for="item in dateList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </div>
      </div>

      <!-- 显示两个折线统计图用于展示用户数量、帖子数量 -->
      <div class="charts-section">
        <el-row :gutter="20">
          <el-col v-if="tabActive === 1" :span="24">
            <div class="chart-container">
              <div class="chart-title">学生数量</div>
              <div class="chart-content">
                <ECharts :option="studentChartOption" />
              </div>
            </div>
          </el-col>
          <el-col v-if="tabActive === 2" :span="24">
            <div class="chart-container">
              <div class="chart-title">课程数量</div>
              <div class="chart-content">
                <ECharts :option="courseChartOption" />
              </div>
            </div>
          </el-col>
          <el-col v-if="tabActive === 3" :span="24">
            <div class="chart-container">
              <div class="chart-title">作业数量</div>
              <div class="chart-content">
                <ECharts :option="homeworkChartOption" />
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="homeIndex">
import { ref, computed, onMounted, onBeforeUnmount } from "vue"
import { ECOption } from "@/components/ECharts/config"
import ECharts from "@/components/ECharts/index.vue"
import { ElMessage } from "element-plus"
import DashboardApi from "@/api/modules/dashboard"

const tab = [{ label: "学生数据", name: 1 }, { label: "课程数据", name: 2 }, { label: "作业数据", name: 3 },]
const dateList = [
  { label: "近5天", value: "5" },
  { label: "近15天", value: "15" },
  { label: "近30天", value: "30" },
  { label: "近60天", value: "60" },
]


// 响应式数据
const tabActive = ref(1)
const selectedTimePeriod = ref("5")

const overviewData = ref({} as any)
const studentTrendData = ref({ dates: [] as string[], counts: [] as number[], cumulativeCounts: [] as number[] })
const courseTrendData = ref({ dates: [] as string[], counts: [] as number[], cumulativeCounts: [] as number[] })
const homeworkTrendData = ref({ dates: [] as string[], counts: [] as number[], cumulativeCounts: [] as number[] })

/** 切换tab */
const handleTabChange = () => {
  updateEchartsData()
}

/** 获取总览数据 */
const fetchOverviewData = async () => {
  try {
    const res: any = await DashboardApi.getOverviewData()
    overviewData.value = res.data
  } catch (error) {
    ElMessage.error("获取总览数据失败")
  }
}

/** 用户类型选择 (保留空函数，界面无此控件) */
const handleUserTypeChange = async () => {
  await updateEchartsData()
}

/** 时间选择 */
const handleTimePeriodChange = async () => {
  await updateEchartsData()
}

/** 初始化图表数据 */
const updateEchartsData = async () => {
  try {
    const params = { days: selectedTimePeriod.value }
    const res: any = await DashboardApi.getUserTrendData(params)
    const trendList = res.data || []

    // Map API trend data to each tab
    const dates = trendList.map((item: any) => item.date)
    const studentCounts = trendList.map((item: any) => item.studentCount)
    const courseCounts = trendList.map((item: any) => item.courseCount)
    const homeworkCounts = trendList.map((item: any) => item.homeworkCount)

    // Compute cumulative counts
    const cumStudentCounts: number[] = []
    const cumCourseCounts: number[] = []
    const cumHomeworkCounts: number[] = []
    let sumS = 0, sumC = 0, sumH = 0
    for (let i = 0; i < dates.length; i++) {
      sumS += studentCounts[i] || 0
      sumC += courseCounts[i] || 0
      sumH += homeworkCounts[i] || 0
      cumStudentCounts.push(sumS)
      cumCourseCounts.push(sumC)
      cumHomeworkCounts.push(sumH)
    }

    studentTrendData.value = { dates, counts: studentCounts, cumulativeCounts: cumStudentCounts }
    courseTrendData.value = { dates, counts: courseCounts, cumulativeCounts: cumCourseCounts }
    homeworkTrendData.value = { dates, counts: homeworkCounts, cumulativeCounts: cumHomeworkCounts }
  } catch (error) {
    ElMessage.error("获取趋势数据失败")
  }
}


// 日增量颜色配置
const addLineColorOptions = {
  color: '#FF6B59',
  areaStartColor: 'rgba(255, 107, 89, 0.3)',
  areaEndColor: 'rgba(255, 107, 89, 0.05)'
}

// 总量颜色配置
const totalLineColorOptions = {
  color: '#66B1FF',
  areaStartColor: 'rgba(102, 177, 255, 0.3)',
  areaEndColor: 'rgba(102, 177, 255, 0.05)'
}


/** 图表配置 */
const EChartsSetting = {
  tooltip: { trigger: 'axis', backgroundColor: 'rgba(50, 50, 50, 0.8)', textStyle: { color: '#fff' } },
  legend: { show: true, orient: 'horizontal', left: 'center', top: 'top' },
  grid: { left: '3%', right: '4%', bottom: '10%', top: '10%', containLabel: true },
  xAxis: { axisLine: { lineStyle: { color: '#c0c0c0' } }, axisLabel: { color: '#a1a1a1' } },
  yAxis: { axisLine: { show: false }, axisTick: { show: false }, splitLine: { lineStyle: { color: '#e0e0e0' } }, axisLabel: { color: '#a1a1a1' }, minInterval: 1 },
  seriesTotalOptions: { type: 'line', smooth: true, lineStyle: { color: totalLineColorOptions.color, width: 3 }, itemStyle: { color: totalLineColorOptions.color }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: totalLineColorOptions.areaStartColor }, { offset: 1, color: totalLineColorOptions.areaEndColor }] } } },
  seriesAddOptions: { type: 'line', smooth: true, lineStyle: { color: addLineColorOptions.color, width: 3 }, itemStyle: { color: addLineColorOptions.color }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: addLineColorOptions.areaStartColor }, { offset: 1, color: addLineColorOptions.areaEndColor }] } } }
}


/** 学生数量图表配置 */
const studentChartOption = computed((): ECOption => ({
  tooltip: { borderColor: '#6B9DFE', ...EChartsSetting.tooltip } as any,
  legend: {
    data: ['学生日增量', '学生总量'], ...EChartsSetting.legend
  },
  grid: EChartsSetting.grid,
  xAxis: {
    type: 'category',
    data: studentTrendData.value.dates,
    ...EChartsSetting.xAxis
  },
  yAxis: {
    type: 'value',
    ...EChartsSetting.yAxis
  },
  series: [
    {
      name: '学生日增量',
      data: studentTrendData.value.counts,
      ...EChartsSetting.seriesAddOptions as any
    },
    {
      name: '学生总量',
      data: studentTrendData.value.cumulativeCounts,
      ...EChartsSetting.seriesTotalOptions as any
    },
  ]
}))

/** 课程数量图表配置 */
const courseChartOption = computed((): ECOption => ({
  tooltip: { borderColor: '#FF9900', ...EChartsSetting.tooltip } as any,
  grid: EChartsSetting.grid,
  legend: { data: ['课程日增量', '课程总量'], ...EChartsSetting.legend },
  xAxis: {
    type: 'category',
    data: courseTrendData.value.dates,
    ...EChartsSetting.xAxis
  },
  yAxis: {
    type: 'value',
    ...EChartsSetting.yAxis
  },
  series: [
    {
      name: '课程日增量',
      data: courseTrendData.value.counts,
      ...EChartsSetting.seriesAddOptions as any
    },
    {
      name: '课程总量',
      data: courseTrendData.value.cumulativeCounts,
      ...EChartsSetting.seriesTotalOptions as any
    }
  ]
}))

/** 作业数量图表配置 */
const homeworkChartOption = computed((): ECOption => ({
  tooltip: { borderColor: '#008000', ...EChartsSetting.tooltip } as any,
  grid: EChartsSetting.grid,
  legend: { data: ['作业日增量', '作业总量'], ...EChartsSetting.legend },
  xAxis: {
    type: 'category',
    data: homeworkTrendData.value.dates,
    ...EChartsSetting.xAxis
  },
  yAxis: {
    type: 'value',
    ...EChartsSetting.yAxis
  },
  series: [
    {
      name: '作业日增量',
      data: homeworkTrendData.value.counts,
      ...EChartsSetting.seriesAddOptions as any
    },
    {
      name: '作业总量',
      data: homeworkTrendData.value.cumulativeCounts,
      ...EChartsSetting.seriesTotalOptions as any
    }
  ]
}))
// 生命周期
onMounted(() => {
  updateEchartsData()
  fetchOverviewData()

  // 添加窗口大小变化监听，确保图表正确调整大小
  const handleResize = () => {
    // 延迟执行以确保DOM更新完成
    setTimeout(() => {
      window.dispatchEvent(new Event('resize'))
    }, 100)
  }

  window.addEventListener('resize', handleResize)

  // 组件卸载时移除监听器
  onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
  })
})

</script>

<style scoped lang="scss">
.dataVisualize-box {
  .top-box {
    box-sizing: border-box;
    padding: 25px 40px 0;
    margin-bottom: 10px;

    .top-title {
      margin-bottom: 10px;
      font-family: DIN;
      font-size: 18px;
      font-weight: bold;
    }

    .top-content {
      margin-top: 10px;

      .traffic-box {
        box-sizing: border-box;
        display: flex;
        flex-direction: row;
        width: 100%;
        min-height: 140px;
        padding: 25px;
        margin-bottom: 20px;
        border-radius: 30px;
        align-items: center;
        // justify-content: space-around;

        .traffic-img {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 70px;
          height: 70px;
          background-color: var(--el-bg-color);
          border-radius: 19px;
        }

        .traffic-content {
          margin-left: 20px;
          display: flex;
          flex-direction: column;
          justify-content: space-between;
        }
      }

      img {
        width: 33px;
        height: 33px;
      }

      .item-value {
        margin-bottom: 4px;
        font-family: DIN;
        font-size: 28px;
        font-weight: bold;
        color: #1a1a37;
        line-height: 1;
      }

      .traffic-name {
        overflow: hidden;
        font-family: DIN;
        font-size: 15px;
        color: #1a1a37;
        white-space: nowrap;
      }

      .gitee-traffic {
        background: url("./images/1-bg.png");
        background-color: #e8faea;
        background-size: 100% 100%;
      }

      .gitHub-traffic {
        background: url("./images/2-bg.png");
        background-color: #e7e1fb;
        background-size: 100% 100%;
      }

      .today-traffic {
        background: url("./images/3-bg.png");
        background-color: #fdf3e9;
        background-size: 100% 100%;
      }

      .yesterday-traffic {
        background: url("./images/4-bg.png");
        background-color: #f0f5fb;
        background-size: 100% 100%;
      }
    }

    // 筛选区域样式
    .filter-section {
      padding: 10px;
      background: #f8f9fa;
      border-radius: 10px;

      .el-select {
        width: 100%;
      }
    }

    .cu-filter-section-box {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
    }

    .cu-filter-section-item {
      width: 300px;

      .cu-select-0030 {
        :deep(.el-select__wrapper) {
          border: none !important;
          border-bottom: 1px solid #e0e0e0 !important;
          border-radius: 0 !important;
          box-shadow: none !important;
        }
      }
    }

    // 图表区域样式
    .charts-section {
      margin-top: 10px;

      .chart-container {
        background: var(--el-bg-color);
        border-radius: 10px;
        padding: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        height: 450px;
        display: flex;
        flex-direction: column;

        .chart-title {
          font-size: 16px;
          font-weight: bold;
          color: #1a1a37;
          margin-bottom: 20px;
          text-align: center;
          flex-shrink: 0;
        }

        .chart-content {
          flex: 1;
          width: 100%;
          min-height: 0;

          // 确保ECharts容器有正确的高度
          :deep(#echarts) {
            height: 100% !important;
            width: 100% !important;
          }

          // 确保ECharts组件内部元素正确显示
          :deep(.echarts) {
            height: 100% !important;
            width: 100% !important;
          }
        }
      }
    }
  }
}

.bottom-box {
  position: relative;
  padding: 20px 0 0;

  .bottom-title {
    position: absolute;
    top: 75px;
    left: 50px;
    font-family: DIN;
    font-size: 18px;
    font-weight: bold;
  }

  .bottom-tabs {
    padding: 0 50px;
  }

  .curve-echarts {
    box-sizing: border-box;
    height: 400px;
    padding: 0 50px;
  }
}
</style>
