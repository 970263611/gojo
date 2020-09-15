import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      component: () => import("@/views/login")
    },
    {
      path: '/index',
      component: () => import("@/views/index")
    },
    {
      path: '/login',
      component: () => import("@/views/login")
    },
    {
      path: '/registry',
      component: () => import("@/views/registry")
    }
  ]
})
