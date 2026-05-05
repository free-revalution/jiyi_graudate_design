export default {
  path: '/register',
  name: 'Register',
  component: () => import('@/views/register/index.vue'),
  meta: {
    title: '注册',
    showNavbar: false,
    showFooter: false
  }
}
