<template>
  <el-container class="layout">
    <el-aside>
      <CourseMenu />
    </el-aside>
    <el-container>
      <el-header>
        <CourseHeader />
      </el-header>
      <el-main class="course-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts" name="courseManageLayout">
import CourseMenu from './components/CourseMenu.vue';
import CourseHeader from './components/CourseHeader.vue';
</script>

<style scoped lang="scss">
.layout {
  width: 100%;
  height: 100%;

  :deep(.el-aside) {
    width: auto;
    background-color: var(--el-menu-bg-color);
    border-right: 1px solid var(--el-aside-border-color);
  }

  .el-header {
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 55px;
    padding: 0 15px;
    background-color: var(--el-header-bg-color);
    border-bottom: 1px solid var(--el-header-border-color);
  }

  .course-main {
    box-sizing: border-box;
    padding: 10px 12px;
    overflow-x: hidden;
    background-color: var(--el-bg-color-page);
  }
}
</style>
