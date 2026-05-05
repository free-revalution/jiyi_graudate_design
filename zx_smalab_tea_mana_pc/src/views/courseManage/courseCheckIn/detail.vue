<template>
    <div class="page-container">
        <div class="bg-white radius8 p20 mb10 tab-header">
            <div class="flx-justify-between">
                <div class="flx-center">
                    <el-button icon="ArrowLeft" type="warning" plain @click="goBack">返回</el-button>
                    <h2 class="ml20 mb0 mt0">{{ checkInInfo.name }}</h2>
                    <el-tag effect="dark" :type="getStatusType(checkInInfo.status)" class="ml20">{{
                        getStatusText(checkInInfo.status) }}</el-tag>
                </div>
                <div>
                    <el-button type="primary" icon="Edit">导出数据</el-button>
                </div>
            </div>
        </div>
        <div class="main-box">
            <!-- 左侧收缩按钮 -->
            <div class="collapse-btn" @click="toggleQrcode">
                <el-icon>
                    <ArrowLeft v-if="showQrcode" />
                    <ArrowRight v-else />
                </el-icon>
            </div>

            <!-- 左侧二维码区域 -->
            <div v-show="showQrcode" class="qrcode-card">
                <div class="qrcode-tip">使用学习通或微信扫码</div>
                <div class="qrcode-wrapper">
                    <img src="https://gitee.com/dongyanxiao/hello-gitee/raw/master/dongImg/qr-code-bg.png" alt="签到二维码"
                        class="qrcode-img" />
                    <div v-if="checkInInfo.status === '已结束'" class="qrcode-mask">
                        <span>签到活动已结束</span>
                    </div>
                </div>
                <div v-if="checkInInfo.status !== '已结束'" class="enlarge-btn" @click="enlargeQrcode">
                    <el-icon>
                        <ZoomIn />
                    </el-icon>
                    <span>放大二维码</span>
                </div>
            </div>

            <!-- 右侧签到人员列表 -->
            <div class="table-box">
                <el-tabs v-model="activeTab">
                    <el-tab-pane :label="`已签 (${signedList.length})`" name="signed">
                        <div v-if="signedList.length === 0" class="empty-tip">暂无已签人员</div>
                        <div v-else class="member-list">
                            <div v-for="item in signedList" :key="item.id" class="member-item">
                                <el-avatar :size="36" :src="item.avatar" />
                                <div class="member-info">
                                    <span class="name">{{ item.name }}</span>
                                    <span class="time">{{ item.signTime }}</span>
                                </div>
                                <el-tag v-if="item.isLate" type="warning" size="small">迟到</el-tag>
                            </div>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane :label="`未签 (${unsignedList.length})`" name="unsigned">
                        <div v-if="unsignedList.length === 0" class="empty-tip">暂无未签人员</div>
                        <div v-else class="member-list">
                            <div v-for="item in unsignedList" :key="item.id" class="member-item">
                                <el-avatar :size="36" :src="item.avatar" />
                                <div class="member-info">
                                    <span class="name">{{ item.name }}</span>
                                    <span class="student-id">{{ item.studentId }}</span>
                                </div>
                            </div>
                        </div>
                    </el-tab-pane>
                </el-tabs>
            </div>
        </div>

        <!-- 放大二维码弹窗 -->
        <el-dialog v-model="qrcodeDialogVisible" title="签到二维码" width="400px" align-center>
            <div class="qrcode-dialog-content">
                <img src="https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=checkin123" alt="签到二维码" />
                <p>使用学习通或微信扫码签到</p>
            </div>
        </el-dialog>
    </div>
</template>

<script setup lang="tsx" name="courseManageCheckInStatistics">
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ArrowLeft, ArrowRight, ZoomIn } from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();


/** 签到信息 */
const checkInInfo = ref({
    id: route.params.id,
    name: "实验三课程签到",
    date: "2024-03-01",
    status: "已结束"
});

/** 获取状态类型 */
const getStatusType = (status: string): "success" | "info" | "warning" | "danger" | "primary" => {
    const map: Record<string, "success" | "info" | "warning" | "danger" | "primary"> = {
        "未开始": "info",
        "进行中": "success",
        "已结束": "danger"
    };
    return map[status] || "info";
};

/** 获取状态文本 */
const getStatusText = (status: string) => {
    return status;
};

/** 返回上一页 */
const goBack = () => {
    router.back();
};

/** 二维码显示控制 */
const showQrcode = ref(true);
const toggleQrcode = () => {
    showQrcode.value = !showQrcode.value;
};

/** 放大二维码弹窗 */
const qrcodeDialogVisible = ref(false);
const enlargeQrcode = () => {
    qrcodeDialogVisible.value = true;
};

/** Tab 切换 */
const activeTab = ref("signed");

/** 已签人员列表 */
const signedList = ref<any[]>([
    { id: 1, name: "王浩然", studentId: "2022301001", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", signTime: "2025-03-15 08:02:15", isLate: false },
    { id: 2, name: "李思琪", studentId: "2022301002", avatar: "https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg", signTime: "2025-03-15 08:03:42", isLate: false },
    { id: 3, name: "张明轩", studentId: "2022301003", avatar: "https://fuss10.elemecdn.com/1/34/19aa98b1fcb2781c4fba33d850549jpeg.jpeg", signTime: "2025-03-15 08:05:08", isLate: false },
    { id: 4, name: "刘雨萱", studentId: "2022301004", avatar: "https://fuss10.elemecdn.com/0/6f/e35ff375812e6b0020b6b4e8f9583jpeg.jpeg", signTime: "2025-03-15 08:06:33", isLate: false },
    { id: 5, name: "陈子豪", studentId: "2022301005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", signTime: "2025-03-15 08:08:21", isLate: false },
    { id: 6, name: "杨欣怡", studentId: "2022301006", avatar: "https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg", signTime: "2025-03-15 08:10:45", isLate: true },
    { id: 7, name: "赵文博", studentId: "2022301007", avatar: "https://fuss10.elemecdn.com/1/34/19aa98b1fcb2781c4fba33d850549jpeg.jpeg", signTime: "2025-03-15 08:12:18", isLate: true },
    { id: 8, name: "周雅琳", studentId: "2022301008", avatar: "https://fuss10.elemecdn.com/0/6f/e35ff375812e6b0020b6b4e8f9583jpeg.jpeg", signTime: "2025-03-15 08:15:02", isLate: true },
]);

/** 未签人员列表 */
const unsignedList = ref([
    { id: 1, name: "张三", studentId: "2024001", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 2, name: "李四", studentId: "2024002", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 3, name: "王五", studentId: "2024003", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 4, name: "赵六", studentId: "2024004", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 5, name: "钱七", studentId: "2024005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 5, name: "钱七", studentId: "2024005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 5, name: "钱七", studentId: "2024005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 5, name: "钱七", studentId: "2024005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 5, name: "钱七", studentId: "2024005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 5, name: "钱七", studentId: "2024005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 5, name: "钱七", studentId: "2024005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 5, name: "钱七", studentId: "2024005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 5, name: "钱七", studentId: "2024005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
    { id: 5, name: "钱七", studentId: "2024005", avatar: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },

]);

</script>

<style scoped lang="scss">
.page-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow: hidden;

    .tab-header {
        flex-shrink: 0;
    }
}



.p20 {
    padding: 20px;
}

.bg-white {
    background-color: var(--el-bg-color);
}

.radius8 {
    border-radius: 8px;
}


.flex {
    display: flex;
}

.main-box {
    display: flex;
    gap: 10px;
    position: relative;
    flex: 1;
    overflow: hidden;
}

.collapse-btn {
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 24px;
    height: 48px;
    background: #409eff;
    border-radius: 0 8px 8px 0;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: #fff;
    z-index: 10;

    &:hover {
        background: #66b1ff;
    }
}

.qrcode-card {
    width: 280px;
    min-width: 280px;
    background: var(--el-bg-color);
    border-radius: 8px;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;

    .qrcode-tip {
        color: #909399;
        font-size: 14px;
        margin-bottom: 15px;
    }

    .qrcode-wrapper {
        position: relative;
        width: 200px;
        height: 200px;

        .qrcode-img {
            width: 100%;
            height: 100%;
        }

        .qrcode-mask {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.9);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #409eff;
            font-size: 18px;
            font-weight: bold;
        }
    }

    .enlarge-btn {
        margin-top: 20px;
        color: #409eff;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 5px;
        font-size: 14px;

        &:hover {
            color: #66b1ff;
        }
    }
}

.table-box {
    flex: 1;
    background: var(--el-bg-color);
    border-radius: 8px;
    padding: 0 20px 20px;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    :deep(.el-tabs) {
        display: flex;
        flex-direction: column;
        height: 100%;
    }

    :deep(.el-tabs__content) {
        flex: 1;
        overflow: hidden;
    }

    :deep(.el-tab-pane) {
        height: 100%;
    }

    :deep(.el-tabs__nav-wrap::after) {
        height: 1px;
    }

    :deep(.el-tabs__item.is-active) {
        color: #409eff;
        font-weight: bold;
    }
}

.empty-tip {
    text-align: center;
    color: #909399;
    padding: 100px 0;
    font-size: 14px;
}

.member-list {
    height: 100%;
    overflow-y: auto;

    .member-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #ebeef5;

        &:last-child {
            border-bottom: none;
        }

        .member-info {
            flex: 1;
            margin-left: 12px;
            display: flex;
            flex-direction: column;

            .name {
                font-size: 14px;
                color: #303133;
            }

            .time,
            .student-id {
                font-size: 12px;
                color: #909399;
                margin-top: 4px;
            }
        }
    }
}

.qrcode-dialog-content {
    text-align: center;

    img {
        width: 300px;
        height: 300px;
    }

    p {
        margin-top: 15px;
        color: #909399;
    }
}
</style>
