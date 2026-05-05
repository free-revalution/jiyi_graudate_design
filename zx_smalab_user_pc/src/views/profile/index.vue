<template>
  <div class="profile-page">
    <!-- 顶部用户信息区 -->
    <div class="profile-header">
      <div class="container header-content">
        <div class="user-section">
          <el-avatar :size="80" :src="userInfo.avatar" />
          <div class="user-info">
            <h2 class="username">{{ userInfo.username }}</h2>
            <p class="user-meta">{{ userInfo.gender }} | 关注{{ userInfo.following }}人 | 粉丝{{ userInfo.followers }}人</p>
          </div>
        </div>
        <div class="stats-section">
          <div class="stat-item">
            <span class="stat-label">主题/回复</span>
            <span class="stat-value">{{ userInfo.topics }}</span>
          </div>
          <el-divider direction="vertical" />
          <div class="stat-item">
            <span class="stat-label">获赞数量</span>
            <span class="stat-value">{{ userInfo.likes }}</span>
          </div>
          <el-divider direction="vertical" />
          <div class="stat-item">
            <span class="stat-label">学习时长</span>
            <span class="stat-value">{{ userInfo.studyTime }}分</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="container main-content">
      <!-- 左侧课程区域 -->
      <div class="left-section">
        <!-- 课程标签页 -->
        <el-tabs v-model="activeTab" class="course-tabs">
          <el-tab-pane label="课程" name="course" />
          <el-tab-pane label="专栏" name="column" />
          <el-tab-pane label="自研课" name="self" />
        </el-tabs>

        <!-- 课程筛选 -->
        <div class="course-filter">
          <span :class="['filter-item', { active: courseFilter === 'all' }]" @click="courseFilter = 'all'">全部</span>
          <span :class="['filter-item', { active: courseFilter === 'cert' }]" @click="courseFilter = 'cert'">认证学习课程</span>
          <span :class="['filter-item', { active: courseFilter === 'smart' }]" @click="courseFilter = 'smart'">智慧课程</span>
        </div>

        <!-- 空状态 -->
        <div v-if="myCourses.length === 0" class="empty-state">
          <img src="@/assets/empty-course.svg" alt="暂无课程" class="empty-img" />
          <p class="empty-text">还没有参加任何课程</p>
          <el-button type="primary" round @click="goDiscover">发现新课程</el-button>
        </div>

        <!-- 课程列表 -->
        <div v-else class="course-list">
          <div v-for="item in myCourses" :key="item.id" class="training-card mb20 bg-white radius8 p20">
            <div class="training-content flx-justify-between">
              <div class="training-info flx-start">
                <el-image :src="item.cover" class="training-cover radius8" fit="cover" />
                <div class="training-detail ml20">
                  <h3 class="training-title font-bold text-lg mb10">
                    {{ item.name }} <el-tag effect="dark" :type="getStatusType(item.status)" class="ml10">{{ item.status }}</el-tag>
                  </h3>

                  <div class="training-meta text-gray mb8">
                    <span>实训时间：{{ item.startTime }} ~ {{ item.endTime }}</span>
                  </div>
                  <div class="training-stats mt10">
                    <el-tag type="success" class="mr10">已做题 {{ item.submittedCount }}</el-tag>
                    <el-tag type="danger">未做题 {{ item.unsubmittedCount }}</el-tag>
                  </div>
                </div>
              </div>
              <div class="training-actions flx-center">
                <el-button type="primary" icon="View" @click="toDetail(item.id)">详情</el-button>
                <el-button type="danger" plain icon="Close" @click="handleUnenroll(item)">退课</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 别人都在学 -->
        <div class="recommend-section">
          <h3 class="section-title">别人都在学</h3>
          <div class="recommend-grid">
            <div v-for="course in recommendCourses" :key="course.id" class="recommend-card" @click="goToCourse(course.id)">
              <img :src="course.image" :alt="course.title" class="recommend-img" />
              <div class="recommend-info">
                <h4 class="recommend-title">{{ course.title }}</h4>
                <p class="recommend-school">{{ course.school }}</p>
                <p class="recommend-students">
                  <el-icon><User /></el-icon>
                  {{ course.students }}人参加
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧工具箱区域 -->
      <div class="right-section">
        <!-- 期末考试会员 -->
        <el-card class="member-card exam-member">
          <div class="member-header">
            <span class="member-title">期末考试会员</span>
            <el-button type="warning" size="small" round>立即开通</el-button>
          </div>
          <div class="member-tags">
            <el-tag size="small">期末突击/高分课</el-tag>
            <el-tag size="small">实时答疑</el-tag>
            <el-tag size="small">考卷画重点</el-tag>
          </div>
        </el-card>

        <!-- 认证会员 -->
        <el-card class="member-card cert-member">
          <div class="member-header">
            <span class="member-title">认证会员</span>
            <el-button type="warning" size="small" round>立即开通</el-button>
          </div>
          <div class="member-tags">
            <el-tag size="small">认证成绩和证书</el-tag>
            <el-tag size="small">智能问答/解析</el-tag>
            <el-tag size="small">刷题学习辅助</el-tag>
          </div>
        </el-card>

        <!-- 工具箱 -->
        <el-card class="toolbox-card">
          <h3 class="toolbox-title">工具箱</h3>
          <div class="tool-grid">
            <div class="tool-item">
              <el-icon :size="24"><ChatDotRound /></el-icon>
              <span>讨论专区</span>
            </div>
            <div class="tool-item">
              <el-icon :size="24"><Setting /></el-icon>
              <span>我的证书</span>
            </div>
            <div class="tool-item">
              <el-icon :size="24"><Calendar /></el-icon>
              <span>学习计划</span>
            </div>
            <div class="tool-item">
              <el-icon :size="24"><Document /></el-icon>
              <span>我的文章</span>
            </div>
          </div>

          <h4 class="ai-title"><span class="ai-tag">AI</span>系列</h4>
          <div class="tool-grid">
            <div class="tool-item">
              <el-icon :size="24"><DocumentCopy /></el-icon>
              <span>文档翻译</span>
            </div>
            <div class="tool-item">
              <el-icon :size="24"><View /></el-icon>
              <span>OCR</span>
            </div>
            <div class="tool-item">
              <el-icon :size="24"><ChatLineSquare /></el-icon>
              <span>文档问答</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import CourseCard from "@/components/CourseCard.vue";
import { User, ChatDotRound, Setting, Calendar, Document, DocumentCopy, View, ChatLineSquare, Close } from "@element-plus/icons-vue";
import { ElMessageBox, ElMessage } from "element-plus";
import { getUserInfo, getMyCourses, unenrollCourse } from "@/api/user";

const router = useRouter();
const activeTab = ref("course");
const courseFilter = ref("all");

const userInfo = ref({
  avatar: "",
  username: "",
  gender: "其他",
  following: 0,
  followers: 0,
  topics: 0,
  likes: 0,
  studyTime: 0
});

const myCourses = ref([]);

const recommendCourses = ref([]);

const fetchProfileData = async () => {
  try {
    const [userRes, coursesRes] = await Promise.all([getUserInfo(), getMyCourses()]);
    if (userRes.data) {
      const u = userRes.data;
      userInfo.value = {
        avatar: u.avatar || "",
        username: u.username || u.name || "",
        gender: u.gender || "其他",
        following: u.following || 0,
        followers: u.followers || 0,
        topics: u.topics || 0,
        likes: u.likes || 0,
        studyTime: u.studyTime || 0
      };
    }
    if (coursesRes.data) {
      myCourses.value = coursesRes.data;
    }
  } catch (error) {
    console.error("获取个人中心数据失败:", error);
  }
};

onMounted(() => {
  fetchProfileData();
});

const goDiscover = () => {
  router.push("/course");
};

const goToCourse = id => {
  router.push(`/course/${id}`);
};

const toDetail = id => {
  router.push(`/profile/course/${id}`);
};

const handleUnenroll = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确定要退出「${item.name}」吗？退课后需要重新参加。`,
      '确认退课',
      { confirmButtonText: '确定退课', cancelButtonText: '取消', type: 'warning' }
    );
    await unenrollCourse(item.id);
    ElMessage.success('退课成功');
    myCourses.value = myCourses.value.filter(c => c.id !== item.id);
  } catch (e) {
    if (e !== 'cancel') {
      console.error("退课失败:", e);
    }
  }
};

/** 获取状态类型 */
const getStatusType = status => {
  const map = {
    未发布: "info",
    未开始: "warning",
    进行中: "primary",
    已截止: "danger"
  };
  return map[status] || "info";
};
</script>

<style lang="scss" scoped>
.profile-page {
  background: #f5f7fa;
  min-height: 100vh;
}

.profile-header {
  background: linear-gradient(to right, #00c261 0%, #17ddae 100%);
  padding: 50px 0;

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .user-section {
    display: flex;
    align-items: center;
    gap: 20px;

    .user-info {
      .username {
        color: #fff;
        font-size: 24px;
        margin: 0 0 8px;
      }

      .user-meta {
        color: rgba(255, 255, 255, 0.9);
        font-size: 14px;
        margin: 0;
      }
    }
  }

  .stats-section {
    display: flex;
    align-items: center;
    background: rgba(54, 54, 54, 0.15);
    border-radius: 8px;
    padding: 15px 30px;

    :deep(.el-divider--vertical) {
      height: 50px;
      margin: 0 30px;
      border-color: rgba(255, 255, 255, 0.3);
    }

    .stat-item {
      text-align: center;

      .stat-value {
        display: block;
        color: #fff;
        font-size: 22px;
        font-weight: bold;
        margin-top: 10px;
      }

      .stat-label {
        color: rgba(255, 255, 255, 0.8);
        font-size: 16px;
      }
    }
  }
}

.main-content {
  display: flex;
  gap: 24px;
  padding: 24px 0;
}

.left-section {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;

  .course-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 0;
    }

    :deep(.el-tabs__item) {
      font-size: 16px;
    }
  }

  .course-filter {
    display: flex;
    justify-content: flex-end;
    gap: 20px;
    padding: 15px 0;
    border-bottom: 1px solid #eee;

    .filter-item {
      color: #666;
      cursor: pointer;
      font-size: 14px;

      &.active {
        color: #409eff;
      }

      &:hover {
        color: #409eff;
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 60px 0;

    .empty-img {
      width: 150px;
      height: auto;
      margin-bottom: 20px;
    }

    .empty-text {
      color: #999;
      margin-bottom: 20px;
    }
  }

  .recommend-section {
    margin-top: 40px;

    .section-title {
      font-size: 18px;
      color: #333;
      margin-bottom: 20px;
    }

    .recommend-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
    }

    .recommend-card {
      display: flex;
      gap: 12px;
      cursor: pointer;
      padding: 10px;
      border-radius: 8px;
      transition: background 0.3s;

      &:hover {
        background: #f5f7fa;
      }

      .recommend-img {
        width: 200px;
        height: 100px;
        object-fit: cover;
        border-radius: 6px;
      }

      .recommend-info {
        flex: 1;

        .recommend-title {
          font-size: 16px;
          color: #333;
          margin: 0 0 6px;
          line-height: 1.4;
        }

        .recommend-school {
          font-size: 12px;
          color: #999;
          margin: 0 0 8px;
        }

        .recommend-students {
          font-size: 12px;
          color: #999;
          margin: 0;
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
}

.right-section {
  width: 340px;
  display: flex;
  flex-direction: column;
  gap: 16px;

  :deep(.el-card) {
    box-shadow: none;
    border-radius: 8px;
  }

  .member-card {
    :deep(.el-card__body) {
      padding: 16px;
    }

    .member-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .member-title {
        font-size: 16px;
        font-weight: bold;
        color: #333;
      }
    }

    .member-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .el-tag {
        background: #fff7e6;
        border-color: #ffd591;
        color: #d48806;
      }
    }

    &.exam-member {
      background: linear-gradient(135deg, #fff7e6 0%, #fff1d6 100%);
      border: none;
    }

    &.cert-member {
      background: linear-gradient(135deg, #fff7e6 0%, #fff1d6 100%);
      border: none;
    }
  }

  .toolbox-card {
    :deep(.el-card__body) {
      padding: 20px;
    }

    .toolbox-title {
      font-size: 16px;
      color: #333;
      margin: 0 0 16px;
    }

    .ai-title {
      font-size: 14px;
      color: #333;
      margin: 20px 0 16px;
      display: flex;
      align-items: center;
      gap: 5px;

      .ai-tag {
        color: #00c261;
        font-size: 14px;
        font-weight: bold;
      }
    }

    .tool-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;

      .tool-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
        cursor: pointer;
        padding: 7px;
        border-radius: 8px;
        transition: background 0.3s;

        &:hover {
          background: #f5f7fa;
        }

        span {
          font-size: 12px;
          color: #666;
        }
      }
    }
  }
}

.training-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.training-info {
  display: flex;
  align-items: flex-start;
}

.training-cover {
  width: 120px;
  height: 80px;
  border-radius: 8px;
  flex-shrink: 0;
}

.training-detail {
  margin-left: 20px;
}

.training-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  margin-top: 0;
}

.training-meta {
  color: #999;
  font-size: 14px;
}

.training-stats {
  display: flex;
  align-items: center;
}

.p20 {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.t_a_form {
  .el-form-item {
    margin-bottom: 0 !important;
  }
}

.bg-white {
  background-color: #ffffff;
}

.border-none {
  border: none !important;
}

.shadow-none {
  box-shadow: none;
}

.radius8 {
  border-radius: 8px !important;
}

.text-gray-400 {
  color: #9ca3af;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-input__wrapper) {
  border: none !important;
  border-bottom: 1px solid #e0e0e0 !important;
  border-radius: 0 !important;
  box-shadow: none !important;
}

:deep(.el-select__wrapper) {
  border: none !important;
  border-bottom: 1px solid #e0e0e0 !important;
  border-radius: 0 !important;
  box-shadow: none !important;
}

:deep(.el-dialog .el-dialog__header) {
  border-bottom: 1px solid var(--el-color-primary) !important;
}

:deep(.el-dialog__title) {
  color: var(--el-color-primary);
}
</style>
