import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCourseStore = defineStore('course', () => {
  const courses = ref([])
  const categories = ref([
    { id: 'frontend', name: '前端开发' },
    { id: 'backend', name: '后端开发' },
    { id: 'design', name: '设计' },
    { id: 'data', name: '数据分析' }
  ])

  const setCourses = (courseList) => {
    courses.value = courseList
  }

  const getCourseById = (id) => {
    return courses.value.find(c => c.id === parseInt(id))
  }

  const getCoursesByCategory = (category) => {
    if (category === 'all') return courses.value
    return courses.value.filter(c => c.category === category)
  }

  return {
    courses,
    categories,
    setCourses,
    getCourseById,
    getCoursesByCategory
  }
})
