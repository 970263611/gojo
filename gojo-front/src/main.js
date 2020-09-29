import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import 'element-ui/lib/theme-chalk/base.css';
import axios from 'axios'
import VueCookies from 'vue-cookies'

Vue.use(VueCookies)
Vue.use(ElementUI)

Vue.prototype.$http = axios
Vue.config.productionTip = false

// http request 拦截器
axios.interceptors.request.use(
  config => {
    config.headers.Authorization = localStorage.token;  //将token设置成请求头
    return config;
  },
  err => {
    return Promise.reject(err);
  }
);

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
