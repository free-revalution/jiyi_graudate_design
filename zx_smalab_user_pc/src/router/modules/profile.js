export default {
  path: '/profile',
  name: 'Profile',
  component: () => import('@/views/profile/layout.vue'),
  redirect: '/profile/index',
  meta: {
    showNavbar: true,
    showFooter: true,
    requiresAuth: true
  },
  children: [
    {
      path: 'index',
      name: 'ProfileIndex',
      component: () => import('@/views/profile/index.vue'),
      meta: {
        title: '个人中心',
        showInNav: false,
        showNavbar: true,
        showFooter: true
      }
    },
    {
      path: 'course/:id',
      name: 'ProfileCourseDetail',
      component: () => import('@/views/profile/courseDetail/index.vue'),
      meta: {
        title: '课程详情',
        showInNav: false,
        showNavbar: true,
        showFooter: true
      }
    },
    {
      path: 'homework/:id',
      name: 'ProfileHomework',
      component: () => import('@/views/profile/homework/index.vue'),
      meta: {
        title: '课程作业',
        showInNav: false,
        showNavbar: true,
        showFooter: true
      }
    },
    {
      path: 'homework/:id/answer/:homeworkId',
      name: 'ProfileHomeworkAnswer',
      component: () => import('@/views/profile/homework/answer.vue'),
      meta: {
        title: '作业作答',
        showInNav: false,
        showNavbar: true,
        showFooter: true
      }
    },
    {
      path: 'classroomExercise/:id',
      name: 'ProfileClassroomExercise',
      component: () => import('@/views/profile/classroomExercise/index.vue'),
      meta: {
        title: '课堂练习',
        showInNav: false,
        showNavbar: true,
        showFooter: true
      }
    },
    {
      path: 'classroomExercise/:id/exercise/:homeworkId',
      name: 'ProfileClassroomExerciseDetail',
      component: () => import('@/views/profile/classroomExercise/exercise.vue'),
      meta: {
        title: '练习',
        showInNav: false,
        showNavbar: true,
        showFooter: true
      }
    },
    {
      path: 'practicelevel/:id',
      name: 'ProfilePracticelevel',
      component: () => import('@/views/profile/practicelevel/index.vue'),
      meta: {
        title: '实践关卡',
        showInNav: false,
        showNavbar: true,
        showFooter: true
      }
    }
  ]
}
