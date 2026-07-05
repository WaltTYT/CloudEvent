import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)

  const fetchUser = async () => {
    const res = await getUserInfo()
    user.value = res.data
  }

  const clearUser = () => {
    user.value = null
    localStorage.removeItem('token')
  }

  return { user, fetchUser, clearUser }
})
