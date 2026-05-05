<template>
  <el-card class="course-card" :body-style="{ padding: '0px' }" shadow="hover" @click="goToCourse">
    <div class="course-image">
      <img :src="course.image" :alt="course.title" />
      <div class="course-badge" v-if="course.isHot">
        <el-tag type="danger" effect="dark">热门</el-tag>
      </div>
    </div>
    
    <div class="course-content">
      <h3 class="course-title">{{ course.title }}</h3>
      <p class="course-desc">{{ course.description }}</p>
      
      <div class="course-meta">
        <!-- <div class="meta-item">
          <el-icon><User /></el-icon>
          <span>{{ course.teacher }}</span>
        </div> -->
        <div class="meta-item">
          <el-icon><Clock /></el-icon>
          <span>{{ course.duration }}</span>
        </div>
        <div class="meta-item">
          <el-icon><Reading /></el-icon>
          <span>{{ course.students }}人学习</span>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { defineProps } from 'vue'
import { useRouter } from 'vue-router'
import { User, Clock, Reading } from '@element-plus/icons-vue'

const props = defineProps({
  course: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const goToCourse = () => {
  router.push(`/course/detail/${props.course.id}`)
}
</script>

<style lang="scss" scoped>
.course-card {
  cursor: pointer;
  transition: transform 0.3s;
  height: 100%;
  border-radius: 10px;
  
  &:hover {
    transform: translateY(-5px);
  }

  .course-image {
    position: relative;
    height: 200px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .course-badge {
      position: absolute;
      top: 10px;
      right: 10px;
    }
  }

  .course-content {
    padding: 20px;

    .course-title {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 10px;
      color: #303133;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .course-desc {
      font-size: 14px;
      color: #909399;
      margin-bottom: 15px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      min-height: 40px;
    }

    .course-meta {
      display: flex;
      gap: 15px;
      margin-bottom: 15px;
      flex-wrap: wrap;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 5px;
        font-size: 13px;
        color: #606266;
      }
    }

    .course-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 15px;
      border-top: 1px solid #ebeef5;

      .course-price {
        .price {
          font-size: 24px;
          font-weight: bold;
          color: #f56c6c;

          &.free {
            color: #67c23a;
          }
        }
      }
    }
  }
}
</style>
