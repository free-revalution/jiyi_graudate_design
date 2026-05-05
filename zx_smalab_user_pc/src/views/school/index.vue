<template>
  <div class="school-page">
    
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">学校</h1>
        <p class="page-subtitle">合作院校展示</p>
      </div>
    </div>

    <div class="container school-content">
      <el-card class="school-section">
        <h2>学校页面</h2>
        <p>这是学校页面的内容，可以展示合作院校信息。</p>
      </el-card>
    </div>

  </div>
</template>

<script setup>
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.school-page {
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

  .school-content {
    padding: 40px 20px;

    .school-section {
      margin-bottom: 30px;

      h2 {
        font-size: 24px;
        margin-bottom: 20px;
        color: $text-primary;
      }

      p {
        line-height: 1.8;
        color: $text-secondary;
        font-size: 16px;
      }
    }
  }
}
</style>
