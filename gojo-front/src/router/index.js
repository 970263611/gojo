import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      component: HelloWorld
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
