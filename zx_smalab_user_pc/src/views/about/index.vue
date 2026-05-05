<template>
  <div class="about-page">
    
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">关于我们</h1>
        <p class="page-subtitle">致力于提供优质的在线教育服务</p>
      </div>
    </div>

    <div class="container about-content">
      <el-card class="about-section">
        <h2>我们的使命</h2>
        <p>在线课程教育实验平台致力于通过互联网技术，为每一位学习者提供优质、便捷的在线教育服务。我们相信，教育应该是平等的、可及的，每个人都有权利获得最好的学习资源。</p>
      </el-card>

      <el-card class="about-section">
        <h2>联系我们</h2>
        <div class="contact-info">
          <div class="contact-item">
            <el-icon><Phone /></el-icon>
            <span>电话：400-123-4567</span>
          </div>
          <div class="contact-item">
            <el-icon><Message /></el-icon>
            <span>邮箱：contact@smartedu.com</span>
          </div>
          <div class="contact-item">
            <el-icon><Location /></el-icon>
            <span>地址：北京市朝阳区xxx大厦</span>
          </div>
        </div>
      </el-card>
    </div>

  </div>
</template>

<script setup>
import { VideoCamera, Medal, Service, TrendCharts, Phone, Message, Location } from '@element-plus/icons-vue'
</script>

<style lang="scss" scoped>
.about-page {
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

  .about-content {
    padding: 40px 20px;

    .about-section {
      margin-bottom: 30px;

      h2 {
        font-size: 24px;
        margin-bottom: 20px;
        color: #303133;
      }

      p {
        line-height: 1.8;
        color: #606266;
        font-size: 16px;
      }

      .advantages-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 30px;
        margin-top: 20px;

        .advantage-item {
          text-align: center;

          h3 {
            margin: 15px 0 10px;
            color: #303133;
          }

          p {
            color: #909399;
            font-size: 14px;
          }
        }
      }

      .contact-info {
        .contact-item {
          display: flex;
          align-items: center;
          gap: 10px;
          margin: 15px 0;
          font-size: 16px;
          color: #606266;
        }
      }
    }
  }
}
</style>
