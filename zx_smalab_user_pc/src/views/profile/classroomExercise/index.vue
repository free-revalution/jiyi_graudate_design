<template>
  <div class="homework-page">
    <div class="container">
      <!-- 顶部课程信息区 -->
      <div class="course-header-card">
        <!-- 面包屑导航 -->
        <div class="breadcrumb-section">
          <el-breadcrumb separator=">">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: '/profile/index' }">个人中心</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: `/profile/course/${route.params.id}` }">{{ courseInfo.title }}</el-breadcrumb-item>
            <el-breadcrumb-item>课堂练习</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 课程信息区 -->
        <div class="course-info-section">
          <!-- 左侧：封面和标题 -->
          <div class="course-left">
            <img :src="courseInfo.coverImage" :alt="courseInfo.title" class="course-cover" />
            <div class="course-title-area">
              <div class="tags-row">
                <el-tag effect="dark" type="success" size="small">智慧慕课</el-tag>
                <el-tag effect="dark" type="warning" size="small">认证学习</el-tag>
              </div>
              <h1 class="course-title">{{ courseInfo.title }}</h1>
            </div>
          </div>

          <!-- 中间：课程统计信息 -->
          <div class="course-stats">
            <el-descriptions :column="8" size="small" direction="vertical" class="stats-descriptions">
              <el-descriptions-item label="章节">{{ courseStats.chapters }}</el-descriptions-item>
              <el-descriptions-item label="单元">{{ courseStats.units }}</el-descriptions-item>
              <el-descriptions-item label="课堂练习">{{ courseStats.classExercises }}</el-descriptions-item>
              <el-descriptions-item label="实践关卡">{{ courseStats.practiceCards }}</el-descriptions-item>
              <el-descriptions-item label="课后练习">{{ courseStats.homework }}</el-descriptions-item>
              <el-descriptions-item label="视频">{{ courseStats.videos }}</el-descriptions-item>
              <el-descriptions-item label="学习人数">{{ courseStats.learners }}</el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 右侧：分享 -->
          <!-- <div class="share-area">
            <span class="share-label">分享</span>
            <div class="share-icons">
              <div class="share-icon wechat">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="share-icon weibo">
                <el-icon><Share /></el-icon>
              </div>
              <div class="share-icon qq">
                <el-icon><ChatLineRound /></el-icon>
              </div>
            </div>
          </div> -->
        </div>
      </div>

      <!-- 练习列表区 -->
      <div class="homework-list-card">
        <!-- 搜索区 -->
        <div class="search-section">
          <el-input v-model="searchForm.title" placeholder="练习标题" clearable style="width: 200px" @keyup.enter="handleSearch" />
          <el-select v-model="searchForm.status" placeholder="练习状态" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="待提交" value="待提交" />
            <el-option label="已提交" value="已提交" />
            <el-option label="待完成" value="待完成" />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>

        <!-- 表格 -->
        <el-table :data="homeworkList"  stripe style="width: 100%">
          <el-table-column type="index" label="序号" width="80" align="center" />
          <el-table-column prop="name" label="练习名称"/>
          <el-table-column prop="dateRange" label="练习日期" width="380" />
          <el-table-column prop="status" label="练习状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewClassroomExercise(row)">查看</el-button>
              <el-button type="warning" size="small" @click="editClassroomExercise(row)" :disabled="row.status === '已提交'">修改</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-section">
          <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize" :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

import { ElMessage } from "element-plus";
import { getMyExercises } from "@/api/exercise";

const route = useRoute();
const router = useRouter();

// 课程信息
const courseInfo = ref({
  id: "",
  title: "",
  coverImage: ""
});

// 课程统计
const courseStats = ref({
  chapters: 0,
  units: 0,
  classExercises: 0,
  practiceCards: 0,
  homework: 0,
  videos: 0,
  learners: 0,
  rating: 0,
  starRating: 0
});

// 搜索表单
const searchForm = reactive({
  title: "",
  status: ""
});

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 练习列表
const homeworkList = ref([]);

const fetchExerciseList = async () => {
  try {
    const res = await getMyExercises(route.params.id);
    const data = res.data;
    if (data) {
      homeworkList.value = data.exerciseList || data.records || data || [];
      pagination.total = data.total || homeworkList.value.length;
      if (data.courseInfo) {
        courseInfo.value = {
          id: data.courseInfo.id || route.params.id,
          title: data.courseInfo.name || data.courseInfo.title || "",
          coverImage: data.courseInfo.coverImage || data.courseInfo.cover || ""
        };
      }
      if (data.courseStats) {
        courseStats.value = { ...courseStats.value, ...data.courseStats };
      }
    }
  } catch (error) {
    console.error("获取练习列表失败:", error);
  }
};

// 获取状态类型
const getStatusType = status => {
  const map = {
    待提交: "warning",
    已提交: "success",
    待完成: "info"
  };
  return map[status] || "info";
};

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1;
  fetchExerciseList();
};

// 重置
const handleReset = () => {
  searchForm.title = "";
  searchForm.status = "";
  pagination.currentPage = 1;
  fetchExerciseList();
};

// 分页大小改变
const handleSizeChange = val => {
  pagination.pageSize = val;
  fetchExerciseList();
};

// 页码改变
const handleCurrentChange = val => {
  pagination.currentPage = val;
  fetchExerciseList();
};

// 查看练习
const viewClassroomExercise = row => {
  router.push(`/profile/classroomExercise/${route.params.id}/exercise/${row.id}`);
};

// 修改练习
const editClassroomExercise = row => {
  router.push(`/profile/classroomExercise/${route.params.id}/exercise/${row.id}`);
};

onMounted(() => {
  fetchExerciseList();
});
</script>

<style lang="scss" scoped>
.homework-page {
  background: #f5f7fa;
  min-height: 100vh;
  padding: 20px 0;

  .container {
    max-width: calc(100vw - 200px);
    min-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .course-header-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;

    .breadcrumb-section {
      margin-bottom: 20px;
      padding-bottom: 15px;
      border-bottom: 1px solid #e4e7ed;
    }

    .course-info-section {
      display: flex;
      align-items: flex-start;
      gap: 30px;

      .course-left {
        display: flex;
        align-items: center;
        gap: 15px;

        .course-cover {
          width: 120px;
          height: 80px;
          object-fit: cover;
          border-radius: 6px;
        }

        .course-title-area {
          .tags-row {
            display: flex;
            gap: 8px;
            margin-bottom: 10px;
          }

          .course-title {
            font-size: 20px;
            font-weight: 600;
            color: #303133;
            margin: 0;
          }
        }
      }

      .course-stats {
        flex: 1;
        border-radius: 8px;
        padding: 15px 20px;
        background-color: #f5f5f5;
        :deep(.el-descriptions__body) {
          background-color: transparent;
        }
        :deep(.el-descriptions) {
          background-color: transparent;
          .el-descriptions__label {
            font-size: 12px;
            text-align: center;
          }
          .el-descriptions__content {
            font-size: 16px;
            text-align: center;
          }
        }

        .rating-area {
          display: flex;
          align-items: center;
          gap: 4px;

          .el-icon {
            font-size: 12px;
          }
        }
      }

      .share-area {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 10px;

        .share-label {
          color: #606266;
          font-size: 14px;
        }

        .share-icons {
          display: flex;
          gap: 10px;

          .share-icon {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            color: white;

            &.wechat {
              background-color: #07c160;
            }
            &.weibo {
              background-color: #e6162d;
            }
            &.qq {
              background-color: #12b7f5;
            }
          }
        }
      }
    }
  }

  .homework-list-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;

    .search-section {
      display: flex;
      gap: 15px;
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #e4e7ed;
    }

    .pagination-section {
      display: flex;
      justify-content: flex-start;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #e4e7ed;
    }
  }
}
</style>
