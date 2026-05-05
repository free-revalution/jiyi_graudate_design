<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>关于我们</h3>
          <p>实验平台致力于更优秀的实验服务</p>
          <p>让实验学习变得更简单、更高效</p>
        </div>
        
        <div class="footer-section">
          <h3>快速链接</h3>
          <ul>
            <li><a href="/">首页</a></li>
            <li><a href="/course/list">实验中心</a></li>
            <li><a href="/about">关于我们</a></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3>联系我们</h3>
          <p><el-icon><Phone /></el-icon> 400-123-4567</p>
          <p><el-icon><Message /></el-icon> contact@smartedu.com</p>
          <p><el-icon><Location /></el-icon> 北京市朝阳区xxx大厦</p>
        </div>
        
        <div class="footer-section">
          <h3>关注我们</h3>
          <div class="social-links">
            <el-button circle><el-icon><ChatDotRound /></el-icon></el-button>
            <el-button circle><el-icon><Share /></el-icon></el-button>
            <el-button circle><el-icon><Star /></el-icon></el-button>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; 2024 实验平台 版权所有 | ICP备案号：京ICP备xxxxxxxx号</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { Phone, Message, Location, ChatDotRound, Share, Star } from '@element-plus/icons-vue'
</script>

<style lang="scss" scoped>
.footer {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 50px 0 20px;
  margin-top: 80px;

  .footer-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 40px;
    margin-bottom: 40px;
  }

  .footer-section {
    h3 {
      font-size: 18px;
      margin-bottom: 20px;
      color: #fff;
    }

    p {
      margin: 10px 0;
      display: flex;
      align-items: center;
      gap: 8px;
      color: #bdc3c7;
    }

    ul {
      list-style: none;
      
      li {
        margin: 10px 0;
        
        a {
          color: #bdc3c7;
          transition: color 0.3s;
          
          &:hover {
            color: #409EFF;
          }
        }
      }
    }

    .social-links {
      display: flex;
      gap: 10px;
    }
  }

  .footer-bottom {
    border-top: 1px solid #34495e;
    padding-top: 20px;
    text-align: center;
    color: #95a5a6;
  }
}
</style>
