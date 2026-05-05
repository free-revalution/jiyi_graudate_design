<template>
    <div class="course-header-content">
        <div class="header-left">
            <div class="breadcrumb-box mask-image">
                <el-breadcrumb :separator-icon="ArrowRight">
                    <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="item.path">
                        <div class="el-breadcrumb__inner is-link" @click="onBreadcrumbClick(item, index)">
                            <el-icon v-if="item.meta.icon" class="breadcrumb-icon">
                                <component :is="item.meta.icon"></component>
                            </el-icon>
                            <span class="breadcrumb-title">{{ item.meta.title }}</span>
                        </div>
                    </el-breadcrumb-item>
                </el-breadcrumb>
            </div>
        </div>
        <div class="header-right">
            <el-dropdown trigger="click" @command="handleCommand">
                <div class="user-info">
                    <el-avatar :size="32" :src="avatar" />
                    <span class="username">{{ username }}</span>
                    <el-icon>
                        <ArrowDown />
                    </el-icon>
                </div>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="home">
                            <el-icon>
                                <HomeFilled />
                            </el-icon>
                            返回主系统
                        </el-dropdown-item>
                        <el-dropdown-item command="logout" divided>
                            <el-icon>
                                <SwitchButton />
                            </el-icon>
                            退出登录
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script setup lang="ts" name="CourseHeader">
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/modules/user';
import { useAuthStore } from '@/stores/modules/auth';
import { ArrowDown, ArrowRight, HomeFilled, SwitchButton } from '@element-plus/icons-vue';
import { LOGIN_URL } from '@/config';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const authStore = useAuthStore();

const username = computed(() => userStore.userInfo?.name || '用户');
const avatar = computed(() => 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png');

// 检查路径是否匹配（支持动态路由参数）
const isPathMatch = (menuPath: string, targetPath: string): boolean => {
    // 将菜单路径中的 :param 转换为正则表达式
    const regexPattern = menuPath.replace(/:[^/]+/g, '[^/]+');
    const regex = new RegExp(`^${regexPattern}$`);
    return regex.test(targetPath);
};

// 递归查找当前路由在菜单中的路径
const findRoutePath = (menus: Menu.MenuOptions[], targetPath: string, path: Menu.MenuOptions[] = []): Menu.MenuOptions[] | null => {
    for (const menu of menus) {
        const currentPath = [...path, menu];
        if (isPathMatch(menu.path, targetPath)) {
            return currentPath;
        }
        if (menu.children?.length) {
            const result = findRoutePath(menu.children, targetPath, currentPath);
            if (result) return result;
        }
    }
    return null;
};

// 根据路径查找菜单项
const findMenuByPath = (menus: Menu.MenuOptions[], targetPath: string): Menu.MenuOptions | null => {
    for (const menu of menus) {
        if (menu.path === targetPath) {
            return menu;
        }
        if (menu.children?.length) {
            const result = findMenuByPath(menu.children, targetPath);
            if (result) return result;
        }
    }
    return null;
};

// 面包屑数据
const breadcrumbList = computed(() => {
    // 添加课程管理父级
    const courseManageRoute = authStore.authMenuListGet.find(item => item.path === '/courseManage');
    if (!courseManageRoute) return [];

    const parentItem = {
        path: '/courseManage',
        meta: { icon: courseManageRoute.meta.icon, title: courseManageRoute.meta.title }
    } as Menu.MenuOptions;

    // 如果当前路由有 activeMenu，先找到 activeMenu 对应的菜单，再添加当前路由
    const activeMenu = route.meta.activeMenu as string | undefined;
    if (activeMenu) {
        const activeMenuPath = findRoutePath(courseManageRoute.children || [], activeMenu);
        const currentMenu = findMenuByPath(courseManageRoute.children || [], route.name as string) ||
            (courseManageRoute.children || []).find(item => isPathMatch(item.path, route.path));

        if (activeMenuPath) {
            const currentItem = {
                path: route.path,
                meta: { icon: route.meta.icon || 'Document', title: route.meta.title || '详情' }
            } as Menu.MenuOptions;
            return [parentItem, ...activeMenuPath, currentItem];
        }
    }

    // 递归查找当前路由
    const routePath = findRoutePath(courseManageRoute.children || [], route.path);
    if (routePath) {
        return [parentItem, ...routePath];
    }

    return [parentItem];
});

// 点击面包屑
const onBreadcrumbClick = (item: Menu.MenuOptions, index: number) => {
    if (index !== breadcrumbList.value.length - 1) {
        router.push(item.path);
    }
};

const handleCommand = (command: string) => {
    switch (command) {
        case 'home':
            router.push('/home/index');
            break;
        case 'logout':
            userStore.setToken('');
            router.replace(LOGIN_URL);
            break;
    }
};
</script>

<style scoped lang="scss">
.course-header-content {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.header-left {
    display: flex;
    align-items: center;
    overflow: hidden;

    .breadcrumb-box {
        display: flex;
        align-items: center;
        overflow: hidden;

        .el-breadcrumb {
            white-space: nowrap;

            :deep(.el-breadcrumb__item) {
                position: relative;
                display: inline-block;
                float: none;

                .el-breadcrumb__inner {
                    display: inline-flex;
                    align-items: center;

                    &.is-link {
                        color: var(--el-header-text-color);
                        cursor: pointer;

                        &:hover {
                            color: var(--el-color-primary);
                        }
                    }

                    .breadcrumb-icon {
                        margin-right: 6px;
                        font-size: 16px;
                    }

                    .breadcrumb-title {
                        font-size: 14px;
                    }
                }

                &:last-child .el-breadcrumb__inner,
                &:last-child .el-breadcrumb__inner:hover {
                    color: var(--el-header-text-color-regular);
                    cursor: default;
                }
            }
        }
    }
}

.header-right {
    .user-info {
        display: flex;
        align-items: center;
        cursor: pointer;

        .username {
            margin: 0 8px;
            font-size: 14px;
            color: #606266;
        }
    }
}
</style>
