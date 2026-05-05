<template>
    <div class="aside-box vertical">

        <!-- <div class="back-btn">
            <el-button link type="primary" @click="goBackToMain">
                <el-icon>
                    <ArrowLeft />
                </el-icon>
                <span>返回主系统</span>
            </el-button>
        </div> -->

        <div class="courseView">
            <el-image :src="courseObject.cover" class="course-cover radius8" fit="cover" />
            <div class="logo-text">{{ courseObject.name }}</div>
        </div>

        <el-scrollbar>
            <el-menu :router="false" :default-active="activeMenu" :collapse="false" :unique-opened="true"
                :collapse-transition="false">
                <SubMenu :menu-list="menuList" />
            </el-menu>
        </el-scrollbar>
    </div>
</template>

<script setup lang="ts" name="CourseMenu">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ArrowLeft } from '@element-plus/icons-vue';
import { useAuthStore } from '@/stores/modules/auth';
import { getCourseDetail } from '@/api/modules/course';
import SubMenu from '@/layouts/components/Menu/SubMenu.vue';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// 递归过滤课程管理侧边栏隐藏菜单
const filterCourseHiddenMenus = (menus: Menu.MenuOptions[]): Menu.MenuOptions[] => {
    return menus
        .filter(item => !item.meta?.isCourseHide)
        .map(item => ({
            ...item,
            children: item.children ? filterCourseHiddenMenus(item.children) : undefined
        }));
};

// 从 authStore 获取课程管理的 children 作为菜单数据，并过滤 isCourseHide 的菜单
const menuList = computed(() => {
    const courseManageRoute = authStore.authMenuListGet.find(item => item.path === '/courseManage');
    const children = courseManageRoute?.children || [];
    return filterCourseHiddenMenus(children);
});

const activeMenu = computed(() => (route.meta.activeMenu ? route.meta.activeMenu : route.path) as string);

const courseObject = ref<{ name: string; cover: string }>({
    name: "",
    cover: ""
});

onMounted(async () => {
    const courseId = Number(route.query.courseId);
    if (courseId) {
        try {
            const res: any = await getCourseDetail(courseId);
            const data = res.data;
            const apiUrl = import.meta.env.VITE_API_URL || "";
            courseObject.value = {
                name: data.name || data.courseName || "",
                cover: data.coverUrl
                    ? (data.coverUrl.startsWith("http") ? data.coverUrl : `${apiUrl}${data.coverUrl}`)
                    : "https://gitee.com/dongyanxiao/hello-gitee/raw/master/dongImg/%E4%BA%BA%E5%B7%A5%E6%99%BA%E8%83%BD.png"
            };
        } catch {
            courseObject.value = { name: "课程管理", cover: "" };
        }
    } else {
        courseObject.value = { name: "课程管理", cover: "" };
    }
});

const goBackToMain = () => {
    router.push('/home/index');
};
</script>

<style scoped lang="scss">
.aside-box {
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 210px;
    background-color: var(--el-menu-bg-color);

    .logo {
        box-sizing: border-box;
        height: 55px;
        display: flex;
        align-items: center;
        justify-content: center;

        .logo-text {
            font-size: 21.5px;
            font-weight: bold;
            color: var(--el-aside-logo-text-color);
            white-space: nowrap;
        }
    }

    .back-btn {
        padding: 10px 15px;
        border-bottom: 1px solid var(--el-border-color-light);
    }

    .el-scrollbar {
        height: calc(100% - 110px);

        :deep(.el-menu) {
            width: 100%;
            overflow-x: hidden;
            border-right: none;
        }
    }
}

.course-cover {
    width: 180px;
    height: 100px;
    border-radius: 8px;
    flex-shrink: 0;
}


.radius8 {
    border-radius: 8px !important;
}

.courseView {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20px 0;

    .logo-text {
        margin-top: 10px;
        font-size: 16px;
        font-weight: bold;
        color: var(--el-aside-logo-text-color);
        white-space: wrap;
    }
}
</style>
