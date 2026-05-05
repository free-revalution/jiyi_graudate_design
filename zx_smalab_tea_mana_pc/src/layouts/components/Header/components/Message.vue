<template>
  <div class="message">
    <el-popover placement="bottom" :width="310" trigger="click">
      <template #reference>
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="item">
          <i :class="'iconfont icon-xiaoxi'" class="toolBar-icon"></i>
        </el-badge>
      </template>
      <el-tabs v-model="activeName">
        <el-tab-pane :label="`通知(${unreadCount})`" name="first">
          <div class="message-list" v-if="notifications.length > 0">
            <div class="message-item" v-for="(item, index) in notifications" :key="index"
              :style="{ opacity: item.read ? 0.5 : 1 }" @click="markRead(index)">
              <img src="@/assets/images/msg01.png" alt="" class="message-icon" />
              <div class="message-content">
                <span class="message-title">{{ item.title }}</span>
                <span class="message-date">{{ item.date }}</span>
              </div>
            </div>
          </div>
          <div class="message-empty" v-else>
            <img src="@/assets/images/notData.png" alt="notData" />
            <div>暂无通知</div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="消息(0)" name="second">
          <div class="message-empty">
            <img src="@/assets/images/notData.png" alt="notData" />
            <div>暂无消息</div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="代办(0)" name="third">
          <div class="message-empty">
            <img src="@/assets/images/notData.png" alt="notData" />
            <div>暂无代办</div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-popover>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
const activeName = ref("first");

const notifications = ref([
  { title: "欢迎使用在线课程教育平台", date: "刚刚", read: false },
  { title: "新课程已发布：人工智能导论", date: "一小时前", read: false },
  { title: "系统已更新至最新版本", date: "一天前", read: false },
  { title: "请完善您的个人信息", date: "三天前", read: false },
  { title: "课程作业提醒", date: "一周前", read: false }
]);

const unreadCount = computed(() => notifications.value.filter(n => !n.read).length);

const markRead = (index: number) => {
  notifications.value[index].read = true;
};
</script>

<style scoped lang="scss">
.message-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 260px;
  line-height: 45px;
}
.message-list {
  display: flex;
  flex-direction: column;
  .message-item {
    display: flex;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid var(--el-border-color-light);
    cursor: pointer;
    &:last-child {
      border: none;
    }
    .message-icon {
      width: 40px;
      height: 40px;
      margin: 0 20px 0 5px;
    }
    .message-content {
      display: flex;
      flex-direction: column;
      .message-title {
        margin-bottom: 5px;
      }
      .message-date {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
  }
}
</style>
