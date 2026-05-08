<template>
  <div class="practice-level-page">
    <div class="container">
      <div class="course-header-card">
        <div class="breadcrumb-section">
          <el-breadcrumb separator=">">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: '/profile/index' }">个人中心</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: `/profile/course/${route.params.id}` }">课程详情</el-breadcrumb-item>
            <el-breadcrumb-item>课程实训</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </div>

      <div class="practice-list-card">
        <div class="card-header">
          <h3>实训列表</h3>
          <el-tag type="info">共 {{ pagination.total }} 个实训</el-tag>
        </div>

        <div v-if="loading" style="text-align:center;padding:60px;">
          <el-icon class="is-loading" :size="24"><Loading /></el-icon>
          <p style="color:#999;margin-top:10px;">加载中...</p>
        </div>

        <div v-else-if="trainingList.length === 0" class="empty-state">
          <img src="@/assets/empty-course.svg" alt="暂无实训" class="empty-img" />
          <p class="empty-text">暂无实训</p>
        </div>

        <div v-else class="training-list">
          <div v-for="item in trainingList" :key="item.id" class="training-card" @click="selectTraining(item)">
            <div class="training-content">
              <el-image :src="item.cover || defaultCover" class="training-cover" fit="cover" />
              <div class="training-detail">
                <h3 class="training-title">
                  {{ item.name }}
                  <el-tag v-if="item.status" :type="getStatusType(item.status)" size="small" class="ml10">{{ item.status }}</el-tag>
                </h3>
                <p v-if="item.description" class="training-desc">{{ item.description }}</p>
                <div class="training-meta">
                  <span v-if="item.startTime">开始：{{ formatDate(item.startTime) }}</span>
                  <span v-if="item.endTime">截止：{{ formatDate(item.endTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="trainingList.length > 0" class="pagination-section">
          <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="fetchTrainingList" @current-change="fetchTrainingList" />
        </div>
      </div>

      <!-- 实训详情弹窗：展示节点树和内容 -->
      <el-dialog v-if="detailVisible" v-model="detailVisible" :title="selectedTraining?.name" width="85%" :fullscreen="isFullscreen" destroy-on-close>
        <template #header>
          <div class="detail-dialog-header">
            <span class="detail-title">{{ selectedTraining?.name }}</span>
            <div class="detail-actions">
              <el-button :icon="FullScreen" circle size="small" @click="isFullscreen = !isFullscreen" />
              <el-button :icon="Close" circle size="small" @click="detailVisible = false" />
            </div>
          </div>
        </template>
        <div class="detail-dialog-body" :style="{ height: isFullscreen ? 'calc(100vh - 120px)' : '700px' }">
          <el-splitter style="height: 100%">
            <el-splitter-panel size="30%" :min-size="20" collapsible>
              <div class="tree-panel">
                <h4 class="panel-title">实训目录</h4>
                <el-tree
                  v-if="nodeTree.length > 0"
                  :data="nodeTree"
                  :props="{ children: 'children', label: 'label' }"
                  default-expand-all
                  :expand-on-click-node="false"
                  highlight-current
                  @node-click="handleNodeClick"
                >
                  <template #default="{ data }">
                    <span class="tree-node-label">{{ data.label }}</span>
                  </template>
                </el-tree>
                <el-empty v-else description="暂无目录" />
              </div>
            </el-splitter-panel>
            <el-splitter-panel :min-size="40">
              <div class="content-panel">
                <div v-if="currentNode" class="node-content">
                  <h3 class="node-title">{{ currentNode.label }}</h3>
                  <div class="node-body description-content" v-html="currentNode.content || '<p style=color:#999>暂无内容</p>'"></div>
                </div>
                <el-empty v-else description="请从左侧目录选择查看内容" />
              </div>
            </el-splitter-panel>
          </el-splitter>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRoute } from "vue-router";
import { Loading, FullScreen, Close } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { getTrainingList, getTrainingNodes } from "@/api/course";

const route = useRoute();
const defaultCover = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

const loading = ref(false);
const trainingList = ref([]);
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 });

const detailVisible = ref(false);
const isFullscreen = ref(false);
const selectedTraining = ref(null);
const nodeTree = ref([]);
const currentNode = ref(null);

const fetchTrainingList = async () => {
  loading.value = true;
  try {
    const res = await getTrainingList(route.params.id);
    const data = res.data;
    trainingList.value = Array.isArray(data?.list) ? data.list : (Array.isArray(data) ? data : []);
    pagination.total = data?.total || trainingList.value.length;
  } catch (e) {
    console.error("获取实训列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const selectTraining = async (item) => {
  selectedTraining.value = item;
  currentNode.value = null;
  detailVisible.value = true;
  try {
    const res = await getTrainingNodes(route.params.id, item.id);
    nodeTree.value = res.data || [];
  } catch (e) {
    nodeTree.value = [];
  }
};

const handleNodeClick = (data) => {
  currentNode.value = data;
};

const formatDate = (dateStr) => {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return dateStr;
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
};

const getStatusType = (status) => {
  const map = { "进行中": "success", "未开始": "info", "已截止": "danger" };
  return map[status] || "info";
};

onMounted(() => {
  fetchTrainingList();
});
</script>

<style lang="scss" scoped>
.practice-level-page {
  background: #f5f7fa;
  min-height: 100vh;
  padding: 20px 0;

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .course-header-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;

    .breadcrumb-section {
      padding-bottom: 0;
    }
  }

  .practice-list-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      padding-bottom: 15px;
      border-bottom: 1px solid #e4e7ed;

      h3 {
        margin: 0;
        font-size: 18px;
        color: #303133;
      }
    }

    .empty-state {
      text-align: center;
      padding: 60px 0;

      .empty-img { width: 150px; margin-bottom: 20px; }
      .empty-text { color: #999; }
    }

    .training-list {
      .training-card {
        padding: 20px;
        margin-bottom: 12px;
        background: #fafafa;
        border-radius: 8px;
        border-left: 4px solid #00c185;
        cursor: pointer;
        transition: box-shadow 0.3s;

        &:hover {
          box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        }

        &:last-child { margin-bottom: 0; }

        .training-content {
          display: flex;
          gap: 20px;

          .training-cover {
            width: 120px;
            height: 80px;
            border-radius: 6px;
            flex-shrink: 0;
          }

          .training-detail {
            flex: 1;
            min-width: 0;

            .training-title {
              font-size: 16px;
              font-weight: 600;
              color: #303133;
              margin: 0 0 8px;

              .ml10 { margin-left: 10px; }
            }

            .training-desc {
              color: #606266;
              font-size: 14px;
              margin: 0 0 8px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            .training-meta {
              color: #909399;
              font-size: 13px;

              span { margin-right: 20px; }
            }
          }
        }
      }
    }

    .pagination-section {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
      padding-top: 15px;
      border-top: 1px solid #e4e7ed;
    }
  }
}

// 弹窗样式
.detail-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;

  .detail-title {
    font-size: 18px;
    font-weight: 600;
  }
}

.detail-dialog-body {
  background: #f5f7fa;

  .tree-panel {
    height: 100%;
    background: #fff;
    padding: 15px;
    overflow-y: auto;

    .panel-title {
      margin: 0 0 15px;
      font-size: 15px;
      color: #303133;
      padding-bottom: 10px;
      border-bottom: 1px solid #e4e7ed;
    }
  }

  .content-panel {
    height: 100%;
    background: #fff;
    padding: 20px;
    overflow-y: auto;

    .node-content {
      .node-title {
        font-size: 18px;
        color: #303133;
        margin: 0 0 20px;
        padding-bottom: 15px;
        border-bottom: 1px solid #e4e7ed;
      }
    }
  }
}

.description-content {
  line-height: 1.8;
  color: #606266;

  :deep(h4) {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 20px 0 10px;
  }

  :deep(p) { margin: 10px 0; }

  :deep(ul), :deep(ol) {
    padding-left: 20px;
    margin: 10px 0;
    li { margin: 8px 0; }
  }

  :deep(strong) { color: #303133; font-weight: 600; }
  :deep(pre) { background: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; }
  :deep(code) { background: #f5f5f5; padding: 2px 6px; border-radius: 3px; font-size: 13px; }
}
</style>
