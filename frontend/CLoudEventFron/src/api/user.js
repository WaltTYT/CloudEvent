import request from '@/utils/request'

export const userRegister = (username, password) =>
  request.post('/user/register', null, { params: { username, password } })

export const userLogin = (username, password) =>
  request.post('/user/login', null, { params: { username, password } })

export const getUserInfo = () =>
  request.get('/user/userInfo')

export const updateUser = (data) =>
  request.put('/user/update', data)

export const updateAvatar = (avatarUrl) =>
  request.patch('/user/updateAvatar', null, { params: { avatarUrl } })

export const updatePwd = (data) =>
  request.patch('/user/updatePwd', data)
