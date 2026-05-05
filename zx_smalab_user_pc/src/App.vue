<template>
  <div id="app" v-if="isRouterReady">
    <Navbar v-if="showNavbar" />
    <router-view />
    <Footer v-if="showFooter" />
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import Footer from '@/components/Footer.vue'

const route = useRoute()
const router = useRouter()
const isRouterReady = ref(false)

// 等待路由准备好后再渲染
onMounted(async () => {
  await router.isReady()
  isRouterReady.value = true
})

// 根据路由meta配置决定是否显示Navbar和Footer
const showNavbar = computed(() => route.meta.showNavbar !== false)
const showFooter = computed(() => route.meta.showFooter !== false)
</script>

<style lang="scss">
#app {
  min-height: 100vh;
  background-color: #f5f7fa;
}
</style>
