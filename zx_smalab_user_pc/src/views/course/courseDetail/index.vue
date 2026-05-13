<template>
  <div class="course-detail-page">
    <!-- 课程头部卡片(包含面包屑和课程信息) -->
    <div class="course-header-card">
      <div class="container">
        <div class="header-ccccc">
          <!-- 面包屑导航 -->
          <div class="breadcrumb-section">
            <el-breadcrumb separator=">">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item :to="{ path: '/course/list' }">计算机</el-breadcrumb-item>
              <el-breadcrumb-item>{{ courseInfo.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <!-- 课程主要信息区 -->
          <div class="header-content">
            <!-- 左侧视频区 -->
            <div class="video-section">
              <div class="video-wrapper">
                <template v-if="currentVideoUrl">
                  <video :src="currentVideoUrl" controls class="video-player" />
                </template>
                <template v-else>
                  <img :src="courseInfo.coverImage || ''" :alt="courseInfo.title" class="video-cover" />
                  <div class="play-button" v-if="materialList.length > 0" @click="playFirstVideo">
                    <el-icon :size="60"><VideoPlay /></el-icon>
                    <span>播放</span>
                  </div>
                  <div class="play-button" v-else style="background: rgba(0,0,0,0.2);">
                    <span>暂无视频</span>
                  </div>
                </template>
              </div>
            </div>

            <!-- 右侧信息区 -->
            <div class="info-section">
              <!-- 顶部标签 -->
              <div class="tags-row">
                <el-tag effect="plain" class="course-tag">智慧慕课</el-tag>
                <el-tag effect="plain" type="warning" class="course-tag">认证学习</el-tag>
              </div>

              <!-- 标题和分享行 -->
              <div class="title-share-row">
                <h1 class="course-title">{{ courseInfo.title }}</h1>
                <div class="share-area">
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
                </div>
              </div>

              <!-- 灰色信息卡片 -->
              <div class="info-card">
                <div class="term-select">
                  <span>第1次开课</span>
                  <el-icon><ArrowDown /></el-icon>
                </div>

                <div class="info-row">
                  <span class="label">开课时间：</span>
                  <span class="value">{{ courseInfo.startDate }} ~ {{ courseInfo.endDate }}</span>
                </div>

                <div class="info-row">
                  <span class="label">学时安排：</span>
                  <span class="value">{{ courseInfo.hours }}</span>
                </div>

                <div class="card-footer">
                  <div class="progress-info">
                    <span class="highlight">课程已完结</span>
                    <span class="divider">，</span>
                    <span>共9周</span>
                  </div>
                  <div class="enrolled-info">已有 {{ courseInfo.enrolled }} 人参加</div>
                </div>
              </div>

              <!-- 报名按钮 -->
              <div class="action-btn-area">
                <el-button :type="enrolled ? 'info' : 'success'" class="enroll-btn" :loading="enrollLoading" :disabled="enrolled" @click="handleEnroll">
                  {{ enrolled ? '已参加' : '立即参加' }}
                </el-button>
              </div>
            </div>
          </div>

          <!-- 课程统计信息栏 -->
          <div class="course-stats-bar">
            <div class="stat-item">
              <div class="stat-value">{{ courseStats.chapters }}</div>
              <div class="stat-label">章节</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ courseStats.units }}</div>
              <div class="stat-label">单元</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ courseStats.classExercises }}</div>
              <div class="stat-label">课堂练习</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ courseStats.practiceCards }}</div>
              <div class="stat-label">实践关卡</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ courseStats.homework }}</div>
              <div class="stat-label">课后作业</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ courseStats.videos }}</div>
              <div class="stat-label">视频</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ courseStats.learners }}</div>
              <div class="stat-label">学习人数</div>
            </div>
            <div class="stat-item highlight">
              <div class="stat-value">{{ courseStats.rating }}</div>
              <div class="stat-label">评分</div>
              <div class="star-rating">
                <el-icon v-for="n in 5" :key="n" :color="n <= courseStats.starRating ? '#f7ba2a' : '#e4e7ed'"><StarFilled /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 课程详情和讲师信息 -->
    <div class="course-content">
      <div class="container">
        <div class="content-layout">
          <!-- 左侧主内容 -->
          <div class="main-content">
            <el-tabs v-model="activeTab" class="course-tabs">
              <el-tab-pane label="课程详情" name="detail">
                <div class="course-detail-content">
                  <!-- 左侧课程介绍 -->
                  <div class="course-intro-section">
                    <div class="course-description">
                      <div class="description-content" v-html="courseInfo.description"></div>
                    </div>

                    <!-- 参考教材 -->
                    <div class="reference-section">
                      <div class="section-header">
                        <span>参考教材：</span>
                      </div>
                      <div class="reference-content">
                        <p>• 陈莉云、张景章《<a href="#">编译原理教程</a>（第3版）》，"十二五"普通高等教育本科国家级规划教材，高等教育出版社，ISBN：9787040374087</p>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="课程资料" name="materials">
                <div class="course-detail-content">
                  <div v-if="materialList.length > 0" class="material-list">
                    <div v-for="item in materialList" :key="item.id" class="material-item" @click="playVideo(item)">
                      <el-icon :size="32" color="#409eff"><VideoPlay /></el-icon>
                      <div class="material-info">
                        <span class="material-name">{{ item.name }}</span>
                        <span class="material-meta">{{ formatSize(item.fileSize) }}</span>
                      </div>
                      <el-tag v-if="item.fileType?.startsWith('video/')" type="success" size="small">视频</el-tag>
                    </div>
                  </div>
                  <el-empty v-else description="暂无课程资料" />
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>

          <!-- 右侧边栏 -->
          <div class="sidebar-content">
            <!-- 智能工厂 -->
            <el-card class="smart-factory-card custom-card" shadow="never">
              <template #header>
                <div class="card-header">
                  <span>智能工厂</span>
                </div>
              </template>
              <div class="factory-grid">
                <div class="factory-item" v-for="item in factoryItems" :key="item.name">
                  <div class="factory-icon" :style="{ backgroundColor: item.bgColor }">
                    <el-icon :size="24" :color="item.iconColor"><component :is="item.icon" /></el-icon>
                  </div>
                  <span class="factory-name">{{ item.name }}</span>
                </div>
              </div>
            </el-card>

            <!-- 课程须知 -->
            <el-card class="course-notice-card custom-card" shadow="never">
              <template #header>
                <div class="card-header">
                  <span>课程须知</span>
                </div>
              </template>
              <div class="notice-content">
                <p>{{ courseNotice }}</p>
              </div>
            </el-card>

            <!-- 推荐课程 -->
            <el-card class="recommend-card custom-card" shadow="never">
              <template #header>
                <div class="card-header">
                  <span>推荐课程</span>
                </div>
              </template>
              <div class="recommend-list">
                <el-carousel :autoplay="false" :indicator-position="recommendCourses.length > 3 ? 'outside' : 'none'" :arrow="recommendCourses.length > 3 ? 'always' : 'never'" height="220px">
                  <el-carousel-item v-for="(group, index) in recommendGroups" :key="index">
                    <div v-for="course in group" :key="course.id" class="recommend-item" @click="goToCourse(course.id)">
                      <img :src="course.cover" :alt="course.title" class="recommend-cover" />
                      <div class="recommend-info">
                        <div class="recommend-title">{{ course.title }}</div>
                        <div class="recommend-meta">
                          <span>{{ course.school }}</span>
                          <span class="enrolled">{{ course.enrolled }}人参加</span>
                        </div>
                      </div>
                    </div>
                  </el-carousel-item>
                </el-carousel>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </div>

    <!-- 班级选择弹窗 -->
    <el-dialog v-model="classDialogVisible" title="选择班级" width="400px">
      <el-radio-group v-model="selectedClassId" style="width: 100%;">
        <el-radio v-for="cls in classList" :key="cls.id" :value="cls.id" style="display: block; margin-bottom: 12px;">
          {{ cls.name }}
        </el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="classDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="enrollLoading" @click="confirmEnroll">确认加入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { VideoPlay, ChatDotRound, Share, ChatLineRound, ArrowDown, StarFilled, Headset, Search, Mic, ChatLineSquare, SetUp, Promotion } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { getCourseDetail, enrollCourse, getMaterialList, getCourseClasses } from "@/api/course";
import request from "@/api/index.js";

const route = useRoute();
const router = useRouter();
const activeTab = ref("detail");

const enrolled = ref(false);
const enrollLoading = ref(false);
const materialList = ref([]);
const currentVideoUrl = ref("");

// 数据分组工具函数
const chunkArray = (arr, size) => {
  const result = [];
  for (let i = 0; i < arr.length; i += size) {
    result.push(arr.slice(i, i + size));
  }
  return result;
};

// 推荐课程分组 (每组3个)
const recommendGroups = computed(() => {
  return chunkArray(recommendCourses.value, 3);
});

// 课程信息
const courseInfo = ref({
  id: "",
  title: "",
  coverImage: "",
  schoolLogo: "",
  startDate: "",
  endDate: "",
  hours: "",
  enrolled: 0,
  description: "",
  teachers: [],
  exercises: []
});

// 课程统计数据
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

// 智能工厂功能项
const iconMap = { Headset, Search, Mic, ChatLineSquare, SetUp, Promotion };
const factoryItems = ref([
  { name: "听听题库", icon: Headset, bgColor: "#e8f4ff", iconColor: "#409eff" },
  { name: "搜搜问答", icon: Search, bgColor: "#fff3e0", iconColor: "#e6a23c" },
  { name: "听听问答", icon: Mic, bgColor: "#e8f5e9", iconColor: "#67c23a" },
  { name: "智能问答", icon: ChatLineSquare, bgColor: "#fce4ec", iconColor: "#f56c6c" },
  { name: "智能工具", icon: SetUp, bgColor: "#f3e5f5", iconColor: "#9c27b0" },
  { name: "AI辅导", icon: Promotion, bgColor: "#e0f7fa", iconColor: "#00bcd4" }
]);

// 课程须知
const courseNotice = ref("本实践课程适用于大学三年级《编译原理和技术》或同类课程的线上配套实验，也适合于计算机软件和系统从业者自学使用。");

// 推荐课程
const recommendCourses = ref([]);

const fetchCourseDetail = async () => {
  try {
    const res = await getCourseDetail(route.params.id);
    const data = res.data;
    const apiUrl = import.meta.env.VITE_API_URL || "";
    if (data) {
      // 后端返回 { course: {...}, teachers: [...], terms: [...] }
      const c = data.course || data;
      courseInfo.value = {
        id: c.id || route.params.id,
        title: c.name || c.title || "",
        coverImage: c.coverUrl ? `${apiUrl}${c.coverUrl}` : (c.coverImage || ""),
        schoolLogo: c.belongUnit || c.schoolLogo || "",
        startDate: c.startTime || c.startDate || "",
        endDate: c.endTime || c.endDate || "",
        hours: c.totalHours || c.hours || "",
        enrolled: c.enrolled || c.studentCount || 0,
        description: c.description || "",
        teachers: data.teachers || [],
        exercises: data.exercises || [],
        terms: data.terms || []
      };
      courseStats.value = {
        chapters: data.chapterCount || 0,
        units: data.unitCount || 0,
        classExercises: data.exerciseCount || 0,
        practiceCards: data.trainingCount || 0,
        homework: data.homeworkCount || 0,
        videos: data.videoCount || 0,
        learners: data.studentCount || 0,
        rating: data.rating || 0,
        starRating: data.rating || 0
      };
      recommendCourses.value = data.recommendCourses || [];
      courseNotice.value = c.notice || courseNotice.value;

      // 检查是否已选课
      try {
        const myRes = await request.get('/api/my/courses');
        const myList = myRes.data || [];
        enrolled.value = myList.some(m => m.id === Number(route.params.id));
      } catch (e) {
        // 未登录或请求失败时忽略
      }
    }
  } catch (error) {
    console.error("获取课程详情失败:", error);
  }
};

const fetchMaterials = async () => {
  try {
    const res = await getMaterialList(route.params.id);
    materialList.value = res.data || [];
  } catch (error) {
    console.error("获取课程资料失败:", error);
  }
};

onMounted(() => {
  fetchCourseDetail();
  fetchMaterials();
});

const playVideo = (material) => {
  if (material && material.fileUrl) {
    currentVideoUrl.value = material.fileUrl;
  }
};

const playFirstVideo = () => {
  const video = materialList.value.find(m => m.fileType?.startsWith("video/"));
  if (video) currentVideoUrl.value = video.fileUrl;
};

const formatSize = (bytes) => {
  if (!bytes) return "";
  const units = ["B", "KB", "MB", "GB"];
  let i = 0;
  let size = bytes;
  while (size >= 1024 && i < units.length - 1) { size /= 1024; i++; }
  return size.toFixed(1) + " " + units[i];
};

const classDialogVisible = ref(false);
const classList = ref([]);
const selectedClassId = ref(null);

const handleEnroll = async () => {
  if (enrolled.value || enrollLoading.value) return;
  if (!localStorage.getItem('token')) {
    ElMessage.warning("请先登录后再参加课程");
    router.push('/login');
    return;
  }
  enrollLoading.value = true;
  try {
    let classId = 0;
    try {
      const res = await getCourseClasses(route.params.id);
      const list = res.data || [];
      if (list.length > 0) {
        classList.value = list;
        selectedClassId.value = list[0].id;
        classDialogVisible.value = true;
        return;
      }
    } catch (e) {
      // 获取班级列表失败，直接选课不选班级
    }
    await enrollCourse(route.params.id, 0);
    enrolled.value = true;
    ElMessage.success("加入课程成功");
  } catch (error) {
    // 拦截器已显示错误
  } finally {
    enrollLoading.value = false;
  }
};

const confirmEnroll = async () => {
  if (!selectedClassId.value) { ElMessage.warning("请选择班级"); return; }
  enrollLoading.value = true;
  try {
    await enrollCourse(route.params.id, selectedClassId.value);
    enrolled.value = true;
    classDialogVisible.value = false;
    ElMessage.success("加入课程成功");
  } catch (error) {
    // 拦截器已显示错误
  } finally {
    enrollLoading.value = false;
  }
};

const goToCourse = id => {
  router.push(`/course/detail/${id}`);
};

</script>

<style lang="scss" scoped>
.course-detail-page {
  background: #f5f7fa;
  min-height: 100vh;

  // 统一的容器样式
  .container {
    max-width: calc(100vw - 200px);
    min-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  // 课程头部卡片(包含面包屑和课程信息)
  .course-header-card {
    padding: 10px 0;
    margin-top: 10px;
    .header-ccccc {
      background-color: white;
      padding: 0px 20px 40px 20px;
      border-radius: 8px;
    }
    // 课程统计信息栏
    .course-stats-bar {
      display: flex;
      align-items: center;
      background: linear-gradient(to right, #00c261 0%, #17ddae 100%);
      border-radius: 8px;
      padding: 15px 20px;
      margin-top: 25px;

      .stat-item {
        flex: 1;
        text-align: center;
        position: relative;

        &:not(:last-child)::after {
          content: "";
          position: absolute;
          right: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 1px;
          height: 40px;
          background-color: rgba(255, 255, 255, 0.3);
        }

        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #fff;
          margin-bottom: 5px;
        }

        .stat-label {
          font-size: 13px;
          color: rgba(255, 255, 255, 0.9);
        }

        &.highlight {
          .stat-value {
            color: #fff;
          }

          .star-rating {
            display: flex;
            justify-content: center;
            gap: 2px;
            margin-top: 5px;

            .el-icon {
              font-size: 14px;
            }
          }
        }
      }
    }

    .breadcrumb-section {
      padding: 15px 0;
      border-bottom: 1px solid #e4e7ed;

      :deep(.el-breadcrumb__inner) {
        color: #606266;

        &:hover {
          color: #409eff;
        }
      }
    }

    .header-content {
      display: grid;
      grid-template-columns: 500px 1fr;
      gap: 40px;
      margin-top: 20px;

      .video-section {
        .video-wrapper {
          position: relative;
          width: 100%;
          height: 320px;
          background: #000;
          border-radius: 8px;
          overflow: hidden;
          cursor: pointer;

          .video-cover {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .video-player {
            width: 100%;
            height: 100%;
            object-fit: contain;
          }

          .play-button {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 10px;
            color: white;
            background: rgba(0, 0, 0, 0.5);
            padding: 20px 40px;
            border-radius: 50px;
            transition: all 0.3s;

            &:hover {
              background: rgba(0, 0, 0, 0.7);
              transform: translate(-50%, -50%) scale(1.1);
            }

            span {
              font-size: 16px;
              font-weight: 500;
            }
          }
        }
      }

      .info-section {
        .tags-row {
          display: flex;
          gap: 10px;
          margin-bottom: 15px;

          .course-tag {
            border-radius: 4px;
          }
        }

        .title-share-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;

          .course-title {
            font-size: 32px;
            font-weight: 600;
            color: #303133;
            margin: 0;
          }

          .share-area {
            display: flex;
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
                transition: opacity 0.3s;

                &:hover {
                  opacity: 0.9;
                }

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

        .info-card {
          background-color: #f8f9fa;
          padding: 20px;
          border-radius: 8px;
          margin-bottom: 25px;

          .term-select {
            display: flex;
            align-items: center;
            gap: 5px;
            font-size: 16px;
            color: #303133;
            font-weight: 500;
            margin-bottom: 15px;
            cursor: pointer;

            &:hover {
              color: #409eff;
            }
          }

          .info-row {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            font-size: 14px;
            color: #606266;

            .label {
              color: #909399;
              margin-right: 5px;
            }
          }

          .card-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 20px;
            padding-top: 15px;
            border-top: 1px dashed #e4e7ed;

            .progress-info {
              color: #00c185;
              font-size: 14px;

              .divider {
                color: #dcdfe6;
              }
            }

            .enrolled-info {
              color: #909399;
              font-size: 13px;
            }
          }
        }

        .action-btn-area {
          .enroll-btn {
            background-color: #00c185;
            border-color: #00c185;
            width: 180px;
            height: 48px;
            font-size: 18px;
            border-radius: 24px;

            &:hover {
              background-color: #00a873;
              border-color: #00a873;
            }
          }
        }
      }
    }
  }

  .course-content {
    padding: 10px 0;

    .content-layout {
      display: grid;
      grid-template-columns: 1fr 350px;
      gap: 30px;

      .main-content {
        .course-tabs {
          background: white;
          border-radius: 8px;
          padding: 20px;

          // 增大 Tab 字体
          :deep(.el-tabs__item) {
            font-size: 18px;
            color: #606266;

            &.is-active {
              color: #00c185; // 使用主题绿
              font-weight: 600;
            }
          }

          // 练习题列表样式
          .exercises-list {
            height: 100%;
            background-color: #fff;
            display: flex;
            flex-direction: column;
            overflow: hidden;

            .exercises-header {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 20px 24px;
              border-bottom: 1px solid #f0f0f0;
              background: linear-gradient(to right, #00c261 0%, #17ddae 100%);
              color: white;
              flex-shrink: 0;
              border-radius: 5px 0 0 0;

              h3 {
                margin: 0;
                font-size: 18px;
                font-weight: 600;
              }
            }

            .exercises-content {
              flex: 1;
              overflow-y: auto;
              overflow-x: hidden;
              padding: 10px 0;

              // 滚动条样式
              &::-webkit-scrollbar {
                width: 6px;
              }

              &::-webkit-scrollbar-track {
                background: #f1f1f1;
              }

              &::-webkit-scrollbar-thumb {
                background: #c1c1c1;
                border-radius: 3px;

                &:hover {
                  background: #a8a8a8;
                }
              }

              .exercise-item {
                display: flex;
                align-items: center;
                gap: 16px;
                padding: 16px 24px;
                cursor: pointer;
                transition: all 0.3s;
                border-left: 3px solid transparent;
                border-bottom: 1px solid #f0f0f0;

                &:hover {
                  background-color: #f5f7fa;
                }

                &.active {
                  background-color: #e8f4ff;
                  border-left-color: #409eff;
                }

                .exercise-check {
                  flex-shrink: 0;
                  width: 36px;
                  height: 36px;
                  border-radius: 50%;
                  background-color: #e6e6e6;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  font-weight: 600;

                  .check-icon {
                    color: #fff;
                    font-size: 20px;
                  }

                  .exercise-num {
                    color: #666;
                    font-size: 16px;
                  }
                }

                &.completed .exercise-check {
                  background-color: #67c23a;
                }

                .exercise-info {
                  flex: 1;
                  display: flex;
                  flex-direction: column;
                  gap: 8px;

                  .exercise-title {
                    font-size: 15px;
                    color: #333;
                    font-weight: 500;
                  }

                  .exercise-meta {
                    display: flex;
                    align-items: center;
                    gap: 10px;

                    .score {
                      font-size: 12px;
                      color: #67c23a;
                      font-weight: 500;
                    }
                  }
                }

                &.completed .exercise-info .exercise-title {
                  color: #999;
                }
              }
            }
          }

          // 练习题详情样式
          .exercise-detail {
            height: 100%;
            background-color: #fff;
            overflow: hidden;

            .detail-placeholder {
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              height: 100%;
              color: #909399;
              gap: 20px;

              p {
                margin: 0;
                font-size: 16px;
              }
            }

            .editor-container {
              height: 100%;
              display: flex;
              flex-direction: column;
              overflow: hidden;

              .editor-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 20px 24px;
                border-bottom: 1px solid #e4e7ed;
                background-color: #fafafa;
                flex-shrink: 0;

                .header-left {
                  display: flex;
                  align-items: center;
                  gap: 12px;

                  h3 {
                    margin: 0;
                    font-size: 18px;
                    font-weight: 600;
                    color: #303133;
                  }
                }

                .header-actions {
                  display: flex;
                  gap: 10px;
                }
              }

              .editor-description {
                padding: 16px 24px;
                background-color: #f9f9f9;
                border-bottom: 1px solid #e4e7ed;
                flex-shrink: 0;

                h4 {
                  margin: 0 0 8px 0;
                  font-size: 14px;
                  font-weight: 600;
                  color: #606266;
                }

                p {
                  margin: 0;
                  font-size: 14px;
                  color: #606266;
                  line-height: 1.6;
                }
              }

              .monaco-editor-wrapper {
                flex: 1;
                min-height: 300px;
                position: relative;
                padding: 0;
              }

              .code-output {
                padding: 16px 24px;
                background-color: #1e1e1e;
                border-top: 1px solid #e4e7ed;
                max-height: 200px;
                overflow-y: auto;
                flex-shrink: 0;

                // 滚动条样式
                &::-webkit-scrollbar {
                  width: 6px;
                  height: 6px;
                }

                &::-webkit-scrollbar-track {
                  background: #2d2d2d;
                }

                &::-webkit-scrollbar-thumb {
                  background: #555;
                  border-radius: 3px;

                  &:hover {
                    background: #666;
                  }
                }

                h4 {
                  margin: 0 0 10px 0;
                  font-size: 14px;
                  font-weight: 600;
                  color: #67c23a;
                }

                pre {
                  margin: 0;
                  font-family: "Consolas", "Monaco", "Courier New", monospace;
                  font-size: 13px;
                  color: #d4d4d4;
                  line-height: 1.6;
                  white-space: pre-wrap;
                  word-wrap: break-word;
                }
              }
            }
          }

          // 课程详情内容布局
          .course-detail-content {
            .section-header {
              display: flex;
              align-items: center;
              gap: 8px;
              font-size: 18px;
              font-weight: 600;
              color: #303133;
              margin-bottom: 20px;
              padding-bottom: 12px;
              border-bottom: 2px solid #00c185;

              .el-icon {
                color: #00c185;
              }
            }

            // 左侧课程介绍
            .course-intro-section {
              .course-description {
                .description-content {
                  line-height: 1.8;
                  color: #606266;
                  text-align: justify;

                  :deep(h4) {
                    font-size: 16px;
                    font-weight: 600;
                    color: #303133;
                    margin: 20px 0 10px;
                  }

                  :deep(p) {
                    margin: 10px 0;
                  }

                  :deep(ul),
                  :deep(ol) {
                    padding-left: 20px;
                    margin: 10px 0;

                    li {
                      margin: 8px 0;
                    }
                  }

                  :deep(strong) {
                    color: #303133;
                    font-weight: 600;
                  }
                }
              }

              .reference-section {
                margin-top: 30px;
                padding: 20px;
                background-color: #f8f9fa;
                border-radius: 8px;

                .section-header {
                  font-size: 16px;
                  border-bottom: none;
                  margin-bottom: 10px;
                  padding-bottom: 0;
                }

                .reference-content {
                  p {
                    margin: 0;
                    font-size: 14px;
                    color: #606266;
                    line-height: 1.8;

                    a {
                      color: #409eff;
                      text-decoration: none;

                      &:hover {
                        text-decoration: underline;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      .sidebar-content {
        display: flex;
        flex-direction: column;
        gap: 20px;

        // 自定义卡片样式
        .custom-card {
          border-radius: 12px;
          border: none;

          // 如果需要保留边框，可以使用下面这行，但去除了阴影通常配合无边框或浅边框
          // border: 1px solid #ebeef5;
        }

        // 智能工厂卡片
        .smart-factory-card {
          .factory-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 15px;

            .factory-item {
              display: flex;
              flex-direction: column;
              align-items: center;
              gap: 8px;
              cursor: pointer;
              transition: transform 0.3s;

              &:hover {
                transform: translateY(-3px);
              }

              .factory-icon {
                width: 50px;
                height: 50px;
                border-radius: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
              }

              .factory-name {
                font-size: 12px;
                color: #606266;
              }
            }
          }
        }

        // 课程须知卡片
        .course-notice-card {
          .notice-content {
            p {
              margin: 0;
              font-size: 14px;
              color: #606266;
              line-height: 1.8;
            }
          }
        }

        .recommend-card,
        .smart-factory-card,
        .course-notice-card {
          .card-header {
            font-weight: 600;
            color: #303133;
            display: flex;
            align-items: center;
            font-size: 20px;

            &::before {
              content: "";
              width: 4px;
              height: 20px;
              background-color: #00c185;
              margin-right: 8px;
              border-radius: 2px;
            }
          }

          // 轮播图样式调整
          :deep(.el-carousel__arrow) {
            background-color: rgba(31, 45, 61, 0.2);

            &:hover {
              background-color: rgba(31, 45, 61, 0.4);
            }
          }

          // 轮播图指示器样式
          :deep(.el-carousel__indicators) {
            --el-carousel-indicator-padding-horizontal: 4px;

            .el-carousel__button {
              width: 8px;
              height: 8px;
              border-radius: 50%;
              background-color: #e4e7ed;
              opacity: 1;
            }

            .is-active .el-carousel__button {
              background-color: #67c23a; // 绿色
            }
          }

          .recommend-list {
            .recommend-item {
              display: flex;
              gap: 12px;
              padding: 12px 0;
              border-bottom: 1px solid #ebeef5;
              cursor: pointer;
              transition: all 0.3s;

              &:last-child {
                border-bottom: none;
              }

              &:hover {
                background: #f5f7fa;
                padding-left: 10px;
              }

              .recommend-cover {
                width: 120px;
                height: 80px;
                object-fit: cover;
                border-radius: 4px;
              }

              .recommend-info {
                flex: 1;
                display: flex;
                flex-direction: column;
                justify-content: space-between;

                .recommend-title {
                  font-size: 14px;
                  color: #303133;
                  font-weight: 500;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                }

                .recommend-meta {
                  display: flex;
                  justify-content: space-between;
                  font-size: 12px;
                  color: #909399;

                  .enrolled {
                    color: #67c23a;
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
.material-list {
  .material-item {
    display: flex; align-items: center; gap: 12px;
    padding: 14px 16px; border: 1px solid #ebeef5; border-radius: 8px;
    margin-bottom: 10px; cursor: pointer; transition: all 0.2s;
    &:hover { border-color: #409eff; background: #ecf5ff; }
    .material-info {
      flex: 1; display: flex; justify-content: space-between; align-items: center;
      .material-name { font-size: 14px; color: #303133; }
      .material-meta { font-size: 12px; color: #909399; }
    }
  }
}</style>
