import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref({
    id: null,
    name: '',
    email: '',
    avatar: '',
    isLogin: false
  })

  const myCourses = ref([])
  const favorites = ref([])

  const login = (userData) => {
    userInfo.value = {
      ...userData,
      isLogin: true
    }
  }

  const logout = () => {
    userInfo.value = {
      id: null,
      name: '',
      email: '',
      avatar: '',
      isLogin: false
    }
    myCourses.value = []
    favorites.value = []
  }

  const addCourse = (course) => {
    if (!myCourses.value.find(c => c.id === course.id)) {
      myCourses.value.push(course)
    }
  }

  const addFavorite = (course) => {
    if (!favorites.value.find(c => c.id === course.id)) {
      favorites.value.push(course)
    }
  }

  const removeFavorite = (courseId) => {
    favorites.value = favorites.value.filter(c => c.id !== courseId)
  }

  return {
    userInfo,
    myCourses,
    favorites,
    login,
    logout,
    addCourse,
    addFavorite,
    removeFavorite
  }
})
