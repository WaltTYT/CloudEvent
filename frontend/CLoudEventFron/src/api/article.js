import request from '@/utils/request'

export const addArticle = (data) =>
  request.post('/article', data)

export const listArticle = (params) =>
  request.get('/article', { params })

export const listPublishedArticle = (params) =>
  request.get('/article/published', { params })

export const articleDetail = (id) =>
  request.get('/article/detail', { params: { id } })

export const updateArticleState = (id, state) =>
  request.patch('/article/state', { id, state })

export const deleteArticle = (id) =>
  request.delete('/article', { params: { id } })

export const uploadFile = (file) => {
  const form = new FormData()
  form.append('file', file)
  return request.post('/upload', form)
}
