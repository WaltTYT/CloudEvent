<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateUser, updateAvatar, updatePwd } from '@/api/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('info')

const userForm = ref({
  nickname: '',
  email: ''
})
const pwdForm = ref({
  old_pwd: '',
  new_pwd: '',
  re_pwd: ''
})

const infoRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { pattern: /^\S{1,10}$/, message: '昵称需1-10位非空字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const pwdRules = {
  old_pwd: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  new_pwd: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  re_pwd: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.value.new_pwd) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const initInfo = () => {
  if (userStore.user) {
    userForm.value.nickname = userStore.user.nickname || ''
    userForm.value.email = userStore.user.email || ''
  }
}

const handleUpdateInfo = async () => {
  await updateUser({ id: userStore.user.id, ...userForm.value })
  await userStore.fetchUser()
  ElMessage.success('修改成功')
}

const handleUploadAvatar = async () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/jpeg,image/png'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    if (!/^image\/(jpeg|png)$/.test(file.type)) {
      ElMessage.error('仅支持 JPG/PNG 格式')
      return
    }
    if (file.size > 0.5 * 1024 * 1024) {
      ElMessage.error('图片大小不能超过0.5MB')
      return
    }
    const img = new Image()
    img.onload = async () => {
      if (img.width > 1000 || img.height > 1000) {
        ElMessage.error('图片尺寸过大，请使用不超过 1000×1000 的图片')
        return
      }
      const form = new FormData()
      form.append('file', file)
      const res = await request.post('/upload', form)
      await updateAvatar(res.data)
      await userStore.fetchUser()
      ElMessage.success('头像更新成功')
    }
    img.onerror = () => {
      ElMessage.error('图片加载失败，请选择有效的图片文件')
    }
    img.src = URL.createObjectURL(file)
  }
  input.click()
}

const handleUpdatePwd = async () => {
  await updatePwd(pwdForm.value)
  ElMessage.success('密码修改成功，请重新登录')
  userStore.clearUser()
  router.push('/login')
}

onMounted(initInfo)
</script>

<template>
  <div class="profile">
    <el-card class="profile-card">
      <template #header><span>个人中心</span></template>
      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="基本信息" name="info">
          <el-form :model="userForm" :rules="infoRules" label-width="100px" class="form-width">
            <el-form-item label="用户名">
              <el-input :model-value="userStore.user?.username" disabled />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="userForm.nickname" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateInfo">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="头像设置" name="avatar">
          <div class="avatar-section">
            <el-avatar :size="120" :src="userStore.user?.userPic" />
            <el-button type="primary" @click="handleUploadAvatar" style="margin-top:20px">更换头像</el-button>
            <span class="avatar-hint">支持 JPG / PNG 格式，建议大小0.5MB以内，建议尺寸 1000×1000以内</span>
          </div>
        </el-tab-pane>
        <el-tab-pane label="修改密码" name="pwd">
          <el-form :model="pwdForm" :rules="pwdRules" label-width="100px" class="form-width">
            <el-form-item label="原密码" prop="old_pwd">
              <el-input v-model="pwdForm.old_pwd" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="new_pwd">
              <el-input v-model="pwdForm.new_pwd" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="re_pwd">
              <el-input v-model="pwdForm.re_pwd" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.profile {
  display: flex;
}
.profile-card {
  width: 100%;
}
.form-width {
  max-width: 600px;
  width: 100%;
}
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 0;
}
.avatar-hint {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}
</style>
