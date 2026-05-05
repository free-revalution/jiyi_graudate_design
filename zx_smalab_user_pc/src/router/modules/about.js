export default {
  path: '/about',
  name: 'About',
  component: () => import('@/views/about/index.vue'),
  meta: { 
    title: '关于我们',
    showInNav: true,
    navOrder: 4,
    showNavbar: true,
    showFooter: true
  }
}
