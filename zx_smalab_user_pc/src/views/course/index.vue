<template>
  <div class="courses-page">

    <div class="page-header">
      <div class="container">
        <h1 class="page-title">课程中心</h1>
        <p class="page-subtitle">探索海量优质课程，开启学习之旅</p>
      </div>
    </div>

    <div class="container courses-content">
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索课程"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <div class="courses-grid" v-loading="loading">
        <CourseCard v-for="course in courseList" :key="course.id" :course="course" />
      </div>

      <div class="pagination-section" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[8, 12, 16, 20]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <el-empty v-if="!loading && courseList.length === 0" description="暂无课程" />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import CourseCard from "@/components/CourseCard.vue";
import { getCourseList } from "@/api/course";

const route = useRoute();
const keyword = ref(route.query.search || "");
const courseList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(8);
const total = ref(0);

const fetchCourses = async () => {
  loading.value = true;
  try {
    const res = await getCourseList({
      keyword: keyword.value,
      page: currentPage.value,
      limit: pageSize.value
    });
    const list = res.data?.list || res.data?.records || [];
    const apiUrl = import.meta.env.VITE_API_URL || "";
    courseList.value = list.map(item => ({
      ...item,
      title: item.name || "",
      image: item.coverUrl ? `${apiUrl}${item.coverUrl}` : "",
      duration: item.totalHours ? `${item.totalHours}学时` : "",
      students: "--"
    }));
    total.value = res.data?.total || 0;
  } catch (error) {
    console.error("获取课程列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchCourses();
};

const handleSizeChange = () => {
  currentPage.value = 1;
  fetchCourses();
};

const handleCurrentChange = () => {
  fetchCourses();
};

onMounted(() => {
  fetchCourses();
});
</script>

<style lang="scss" scoped>
.courses-page {
  .page-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 60px 0;
    text-align: center;

    .page-title {
      color: white;
      margin-bottom: 10px;
    }

    .page-subtitle {
      font-size: 18px;
      opacity: 0.9;
    }
  }

  .courses-content {
    padding: 40px 20px;

    .search-bar {
      display: flex;
      justify-content: center;
      margin-bottom: 30px;
    }

    .courses-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
      min-height: 300px;
    }

    .pagination-section {
      display: flex;
      justify-content: center;
      margin-top: 30px;
      padding: 20px 0;
    }
  }
}
</style>
