export default {
  path: '/',
  name: 'Home',
  component: () => import('@/views/Home.vue'),
  meta: { 
    title: '首页 - 实验平台',
    showInNav: false,  // 不在导航栏显示
    showNavbar: true,  // 显示导航栏
    showFooter: true   // 显示页脚
  }
}
