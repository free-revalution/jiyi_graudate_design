export default {
  path: '/course',
  name: 'Course',
  redirect: '/course/list',  // 默认重定向到课程列表
  meta: {
    showNavbar: true,
    showFooter: true
  },
  children: [
    {
      path: 'list',  // 子路由路径,完整路径为 /course/list
      name: 'CourseList',
      component: () => import('@/views/course/index.vue'),
      meta: { 
        title: '课程中心',
        showInNav: true,
        navOrder: 2,
        showNavbar: true,
        showFooter: true
      }
    },
    {
      path: 'detail/:id',  // 子路由路径,完整路径为 /course/detail/:id
      name: 'CourseDetail',
      component: () => import('@/views/course/courseDetail/index.vue'),
      meta: { 
        title: '课程详情',
        showInNav: false,
        showNavbar: true,
        showFooter: true
      }
    }
  ]
}
