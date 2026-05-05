<template>
  <div class="navbar">
    <div class="container navbar-content">
      <div class="logo" @click="goHome">
        <img :src="logoUrl" alt="实验平台" class="logo-image" />
      </div>

      <el-menu :default-active="activeMenu" mode="horizontal" :ellipsis="false" @select="handleSelect" class="navbar-menu">
        <!-- 固定的实验单项 -->
        <el-popover placement="bottom-start" :width="900" trigger="hover" popper-class="course-popover">
          <template #reference>
            <el-menu-item>实验</el-menu-item>
          </template>
          <div class="course-menu-content">
            <div class="course-menu-header">
              <span class="header-text">实验平台，为每一位学生和老师服务</span>
            </div>
            <div class="course-categories">
              <div class="category-card smart-course">
                <div class="card-content">
                  <h3 class="category-title">智慧实验</h3>
                  <p class="category-desc">AI深度赋能教与学，提供全新实验学习体验</p>
                </div>
                <el-button type="primary" class="category-btn" @click="goToCourseList('smart')">查看详情</el-button>
              </div>
              <div class="category-card cert-course">
                <div class="card-content">
                  <h3 class="category-title">认证学习</h3>
                  <p class="category-desc">为你提供认证成绩和证书，以及AI高效学习服务</p>
                </div>
                <el-button type="warning" class="category-btn" @click="goToCourseList('cert')">查看详情</el-button>
              </div>
            </div>
          </div>
        </el-popover>

        <!-- 动态渲染的菜单项 -->
        <el-menu-item v-for="route in navRoutes" :key="route.path" :index="route.path">
          {{ route.meta.title }}
        </el-menu-item>
      </el-menu>

      <div class="navbar-actions">
        <el-input v-model="searchText" placeholder="🔥 期末不挂科" class="search-input" @keyup.enter="handleSearch">
          <template #suffix>
            <el-button type="success" :icon="Search" circle size="small" @click="handleSearch" />
          </template>
        </el-input>
        <span class="auth-links">
          <template v-if="isLoggedIn">
            <router-link to="/profile" class="auth-link">个人中心</router-link>
            <span class="divider">|</span>
            <span class="auth-link logout-link" @click="handleLogout">退出登录</span>
          </template>
          <template v-else>
            <router-link to="/login" class="auth-link">登录</router-link>
            <span class="divider">|</span>
            <router-link to="/register" class="auth-link">注册</router-link>
          </template>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { Search } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import request from "@/api";
import logoUrl from "@/assets/navLogo.png";

const router = useRouter();
const route = useRoute();
const searchText = ref("");
const loading = ref(false);
const isLoggedIn = ref(!!localStorage.getItem("token"));

const activeMenu = computed(() => route.path);

// 获取需要在导航栏显示的路由
const navRoutes = computed(() => {
  return router
    .getRoutes()
    .filter(route => route.meta?.showInNav)
    .sort((a, b) => (a.meta.navOrder || 0) - (b.meta.navOrder || 0));
});

const handleSelect = index => {
  router.push(index);
};

const goHome = () => {
  router.push("/");
};

const goToProfile = () => {
  router.push("/profile");
};

const handleSearch = () => {
  if (searchText.value.trim()) {
    router.push({ path: "/course/list", query: { search: searchText.value } });
  }
};

const goToCourseList = (type) => {
  router.push({ path: "/course/list", query: { category: type } });
};

const handleLogout = async () => {
  if (loading.value) return;
  
  loading.value = true;
  try {
    const token = localStorage.getItem("token");
    if (token) {
      await request.post(
        "/user_permiss/auth/logout"
      );
    }
    
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("userType");
    isLoggedIn.value = false;

    ElMessage.success("退出成功");
    router.push("/login");
  } catch (error) {
    console.error("退出失败:", error);
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("userType");
    isLoggedIn.value = false;
    router.push("/login");
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";
@import "@/styles/mixins.scss";

.navbar {
  background: $bg-white;
  box-shadow: $navbar-shadow;
  position: sticky;
  top: 0;
  z-index: $navbar-z-index;

  .navbar-content {
    @include flex-align-center;
    justify-content: space-between;
    height: $navbar-height;
  }

  .logo {
    @include flex-align-center;
    cursor: pointer;
    margin-right: $logo-margin-right;

    .logo-image {
      height: $logo-height;
      width: auto;
    }
  }

  .navbar-menu {
    flex: 0;
    margin: 0;
    border: none;
    height: $navbar-height;

    :deep(.el-menu-item) {
      font-size: $font-size-xl;
      color: $text-primary !important;
      padding: 0 $spacing-lg;
      height: $navbar-height;
      line-height: $navbar-height;
      border-bottom: 2px solid transparent !important;
      @include transition(all, $transition-base);
      @include flex-align-center;

      &:hover {
        background: transparent !important;
        color: $primary-color !important;
        border-bottom-color: transparent !important;
      }

      &.is-active {
        background: transparent !important;
        // color: $text-primary !important;
        border-bottom-color: transparent !important;
      }
    }
  }

  .navbar-actions {
    @include flex-align-center;
    gap: $spacing-lg;
    margin-left: auto;

    .search-input {
      width: $search-width;

      :deep(.el-input__wrapper) {
        border-radius: $search-border-radius;
        padding-right: 0;
        padding-left: 0;
        box-shadow: 0 0 0 1px $primary-color inset;
        @include transition(box-shadow, $transition-base);

        &:hover,
        &.is-focus {
          box-shadow: 0 0 0 2px $primary-color inset;
        }
      }

      :deep(.el-input__prefix) {
        margin-left: 12px;
      }

      :deep(.el-input__inner) {
        font-size: $font-size-base;
        padding-left: 12px;
      }

      :deep(.el-input__suffix) {
        display: flex;
        align-items: stretch;
        margin-left: 0;
      }

      :deep(.el-input__suffix-inner) {
        display: flex;
        align-items: center;
        padding: 0;
      }

      :deep(.el-button) {
        background: $primary-color;
        border: none;
        color: white;
        border-radius: 0 calc($search-border-radius - 2px) calc($search-border-radius - 2px) 0;
        padding: 0 20px;
        height: calc(100% - 4px);
        margin: 2px 2px 2px 0;
        @include transition(all, $transition-base);

        &:hover {
          background: $primary-hover;
        }

        &.is-circle {
          border-radius: 0 calc($search-border-radius - 2px) calc($search-border-radius - 2px) 0;
        }
      }
    }

    .auth-links {
        @include flex-align-center;
        gap: $spacing-sm;
        font-size: $font-size-base;
        white-space: nowrap;
        font-size: $font-size-xl;

        .auth-link {
          color: $text-primary;
          text-decoration: none;
          @include transition(color, $transition-base);
          cursor: pointer;

          &:hover {
            color: $primary-color;
          }

          &.logout-link {
            color: #f56c6c;

            &:hover {
              color: #e74c3c;
            }
          }
        }

        .divider {
          color: $border-light;
        }
      }
  }
}

// 课程弹出菜单样式(全局样式,不使用 scoped)
</style>

<style lang="scss">
.course-popover {
  padding: 0 !important;
  border-radius: 12px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1) !important;

  .course-menu-content {
    .course-menu-header {
      padding: 20px 24px;
      background-color: #f8f9fa;
      border-bottom: 1px solid #e9ecef;

      .header-text {
        font-size: 14px;
        color: #666;
      }
    }

    .course-categories {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
      padding: 24px;

      .category-card {
        padding: 24px;
        border-radius: 12px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        min-height: 140px;
        transition: all 0.3s ease;
        cursor: pointer;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        }

        &.smart-course {
          background: linear-gradient(135deg, #e8eaf6 0%, #c5cae9 100%);
        }

        &.cert-course {
          background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
        }

        .card-content {
          flex: 1;

          .category-title {
            margin: 0 0 12px 0;
            font-size: 20px;
            font-weight: 600;
            color: #333;
          }

          .category-desc {
            margin: 0;
            font-size: 13px;
            color: #666;
            line-height: 1.6;
          }
        }

        .category-btn {
          align-self: flex-end;
          margin-top: 16px;
          border-radius: 20px;
          padding: 8px 24px;
          font-size: 13px;
        }
      }
    }
  }
}
</style>
