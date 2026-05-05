<template>
  <div class="home">
    <div class="hero-section">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">选择你的实验</h1>
          <p class="hero-subtitle">专业实验平台</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" :icon="Reading" @click="goToCourses"> 浏览课程 </el-button>
            <el-button size="large" :icon="InfoFilled" @click="scrollToCourses"> 了解更多 </el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="courses-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">预定实验</h2>
          <el-button type="primary" text @click="goToCourses">
            查看更多
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="courses-grid">
          <CourseCard v-for="course in hotCourses" :key="course.id" :course="course" />
        </div>
      </div>
    </div>

    <div class="stats-section">
      <div class="container">
        <div class="stats-grid">
          <div class="stat-item" v-for="stat in stats" :key="stat.label">
            <div class="stat-number">{{ stat.number }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import CourseCard from "@/components/CourseCard.vue";
import { Reading, InfoFilled, ArrowRight, VideoCamera, Medal, Clock, TrendCharts } from "@element-plus/icons-vue";
import { getHotCourses, getHomeStats } from "@/api/course";

const router = useRouter();

const hotCourses = ref([]);
const stats = ref([]);

const fetchHomeData = async () => {
  try {
    const [coursesRes, statsRes] = await Promise.all([getHotCourses(), getHomeStats()]);
    if (coursesRes.data) {
      const apiUrl = import.meta.env.VITE_API_URL || "";
      hotCourses.value = (Array.isArray(coursesRes.data) ? coursesRes.data : []).map(item => ({
        ...item,
        title: item.name || "",
        image: item.coverUrl ? `${apiUrl}${item.coverUrl}` : "",
        duration: item.totalHours ? `${item.totalHours}学时` : "",
        students: "--"
      }));
    }
    if (statsRes.data) {
      stats.value = statsRes.data;
    }
  } catch (error) {
    console.error("获取首页数据失败:", error);
  }
};

onMounted(() => {
  fetchHomeData();
});

const goToCourses = () => {
  router.push("/course/list");
};

const scrollToCourses = () => {
  const el = document.querySelector(".courses-section");
  if (el) el.scrollIntoView({ behavior: "smooth" });
};
</script>

<style lang="scss" scoped>
.home {
  .hero-section {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 120px 0;
    text-align: center;

    .hero-content {
      .hero-title {
        font-size: 48px;
        font-weight: bold;
        margin-bottom: 20px;
      }

      .hero-subtitle {
        font-size: 20px;
        margin-bottom: 40px;
        opacity: 0.9;
      }

      .hero-actions {
        display: flex;
        gap: 20px;
        justify-content: center;
      }
    }
  }

  .courses-section {
    padding: 80px 0;
    background: white;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 40px;
    }

    .courses-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
    }
  }

  .stats-section {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 60px 0;

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 40px;
      text-align: center;

      .stat-item {
        .stat-number {
          font-size: 48px;
          font-weight: bold;
          margin-bottom: 10px;
        }

        .stat-label {
          font-size: 18px;
          opacity: 0.9;
        }
      }
    }
  }
}
</style>
