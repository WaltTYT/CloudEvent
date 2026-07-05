<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { userLogin, userRegister } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const isRegister = ref(false)
const form = ref({
  username: '',
  password: '',
  rePassword: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^\S{5,16}$/, message: '用户名需5-16位非空字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{5,16}$/, message: '密码需5-16位非空字符', trigger: 'blur' }
  ],
  rePassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.value.password) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const formRef = ref(null)

const toggleMode = () => {
  formRef.value?.resetFields()
  isRegister.value = !isRegister.value
}

const handleSubmit = async () => {
  if (isRegister.value) {
    await userRegister(form.value.username, form.value.password)
    ElMessage.success('注册成功，正在自动登录...')
    const res = await userLogin(form.value.username, form.value.password)
    localStorage.setItem('token', res.data)
    await userStore.fetchUser()
    router.push('/dashboard')
  } else {
    const res = await userLogin(form.value.username, form.value.password)
    localStorage.setItem('token', res.data)
    await userStore.fetchUser()
    ElMessage.success('登录成功')
    router.push('/dashboard')
  }
}
</script>

<template>
  <div class="login-container">
    <h1 class="welcome-title">欢迎来到云事件</h1>
    <div class="login-card">
      <div class="card-header">
        <h2 class="title">{{ isRegister ? '用户注册' : '用户登录' }}</h2>
        <el-button text type="primary" @click="toggleMode">
          {{ isRegister ? '去登录' : '去注册' }}
        </el-button>
      </div>
      <Transition name="form-fade" mode="out-in">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="0" :key="isRegister">
          <el-form-item prop="username">
            <el-input v-model="form.username" :prefix-icon="User" placeholder="用户名" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" :prefix-icon="Lock" type="password" placeholder="密码" show-password />
          </el-form-item>
          <el-form-item v-if="isRegister" prop="rePassword">
            <el-input v-model="form.rePassword" :prefix-icon="Lock" type="password" placeholder="确认密码" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="submit-btn" @click="handleSubmit">
              {{ isRegister ? '注册' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </Transition>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: url('@/assets/YunShiJian.png') center/cover no-repeat;
}
.welcome-title {
  color: #fff;
  font-size: 36px;
  font-weight: bold;
  text-shadow: 0 2px 8px rgba(0,0,0,.3);
  margin-bottom: 30px;
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,.1);
  animation: fadeSlideUp 0.6s ease;
}
@keyframes fadeSlideUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
}
.title {
  margin: 0;
  color: #333;
  font-size: 22px;
}
.submit-btn {
  width: 100%;
}

.form-fade-enter-active,
.form-fade-leave-active {
  transition: all 0.3s ease;
}
.form-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}
.form-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
