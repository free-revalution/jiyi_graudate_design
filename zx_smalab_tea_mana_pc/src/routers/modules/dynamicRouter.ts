import router from "@/routers/index";
import { LOGIN_URL } from "@/config";
import { RouteRecordRaw } from "vue-router";
import { ElNotification } from "element-plus";
import { useUserStore } from "@/stores/modules/user";
import { useAuthStore } from "@/stores/modules/auth";

// 引入 views 文件夹下所有 vue 文件
const modules = import.meta.glob("@/views/**/*.vue");

/**
 * @description 处理路由组件
 */
const processRouteComponent = (item: Menu.MenuOptions) => {
  if (item.component && typeof item.component == "string") {
    item.component = modules["/src/views" + item.component + ".vue"];
  }
};

/**
 * @description 递归处理路由及其子路由的组件，并转换子路由path为相对路径
 */
const processRouteWithChildren = (route: Menu.MenuOptions, parentPath: string = ""): Menu.MenuOptions => {
  const newRoute = JSON.parse(JSON.stringify(route));
  processRouteComponent(newRoute);
  
  if (newRoute.children?.length) {
    newRoute.children = newRoute.children.map((child: Menu.MenuOptions) => {
      const processedChild = processRouteWithChildren(child, newRoute.path);
      // 将子路由的绝对路径转换为相对路径
      if (processedChild.path.startsWith(newRoute.path + "/")) {
        processedChild.path = processedChild.path.substring(newRoute.path.length + 1);
      }
      return processedChild;
    });
  }
  return newRoute;
};

/**
 * @description 获取需要单独注册的扁平化路由（排除 isFull 路由的子路由）
 */
const getFlatRoutesExcludeFullChildren = (menuList: Menu.MenuOptions[]): Menu.MenuOptions[] => {
  const result: Menu.MenuOptions[] = [];
  
  const traverse = (list: Menu.MenuOptions[], parentIsFull: boolean = false) => {
    for (const item of list) {
      const isFull = item.meta?.isFull;
      
      if (isFull) {
        // isFull 路由需要保留其 children，作为嵌套路由注册
        result.push(item);
      } else if (!parentIsFull) {
        // 非 isFull 路由且父级也不是 isFull，正常扁平化处理
        const itemCopy = { ...item };
        delete itemCopy.children;
        result.push(itemCopy);
      }
      // 如果父级是 isFull，子路由不单独添加（会作为嵌套路由）
      
      if (item.children?.length && !isFull) {
        traverse(item.children, parentIsFull);
      }
    }
  };
  
  traverse(menuList);
  return result;
};

/**
 * @description 初始化动态路由
 */
export const initDynamicRouter = async () => {
  const userStore = useUserStore();
  const authStore = useAuthStore();

  try {
    // 1.获取菜单列表 && 按钮权限列表
    await authStore.getAuthMenuList();
    await authStore.getAuthButtonList();

    // 2.判断当前用户有没有菜单权限
    if (!authStore.authMenuListGet.length) {
      ElNotification({
        title: "无权限访问",
        message: "当前账号无任何菜单权限，请联系系统管理员！",
        type: "warning",
        duration: 3000
      });
      userStore.setToken("");
      router.replace(LOGIN_URL);
      return Promise.reject("No permission");
    }

    // 3.添加动态路由
    const routesToAdd = getFlatRoutesExcludeFullChildren(authStore.authMenuListGet);

    routesToAdd.forEach(item => {
      if (item.meta.isFull) {
        // isFull 路由：保留子路由结构，处理所有组件，转换子路由为相对路径
        const processedRoute = processRouteWithChildren(item);
        router.addRoute(processedRoute as unknown as RouteRecordRaw);
      } else {
        // 普通路由：删除 children，单独注册到 layout
        const itemCopy = { ...item };
        delete itemCopy.children;
        processRouteComponent(itemCopy);
        router.addRoute("layout", itemCopy as unknown as RouteRecordRaw);
      }
    });
  } catch (error) {
    // 当按钮 || 菜单请求出错时，重定向到登陆页
    userStore.setToken("");
    router.replace(LOGIN_URL);
    return Promise.reject(error);
  }
};
