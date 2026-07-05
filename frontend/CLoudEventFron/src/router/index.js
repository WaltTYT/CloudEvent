import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/',
      component: () => import('@/views/Layout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/Dashboard.vue')
        },
        {
          path: 'article',
          name: 'article',
          component: () => import('@/views/ArticleList.vue')
        },
        {
          path: 'article/add',
          name: 'articleAdd',
          component: () => import('@/views/ArticleEdit.vue')
        },
        {
          path: 'article/edit/:id',
          name: 'articleEdit',
          component: () => import('@/views/ArticleEdit.vue')
        },
        {
          path: 'category',
          name: 'category',
          component: () => import('@/views/Category.vue')
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/UserProfile.vue')
        },
        {
          path: 'chart',
          name: 'chart',
          component: () => import('@/views/Chart.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
