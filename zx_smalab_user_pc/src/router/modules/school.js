export default {
  path: '/school',
  name: 'School',
  component: () => import('@/views/school/index.vue'),
  meta: { 
    title: '学校',
    showInNav: true,
    navOrder: 3,
    showNavbar: true,
    showFooter: true
  }
}
