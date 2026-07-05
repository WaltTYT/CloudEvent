import request from '@/utils/request'

export const addCategory = (data) =>
  request.post('/category', data)

export const listCategory = () =>
  request.get('/category')

export const categoryDetail = (id) =>
  request.get('/category/detail', { params: { id } })

export const updateCategory = (data) =>
  request.put('/category', data)

export const deleteCategory = (id) =>
  request.delete('/category', { params: { id } })
