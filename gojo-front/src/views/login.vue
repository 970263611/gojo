<template>
  <el-row>
    <el-col :xs="3" :sm="5" :md="7" :lg="9" :xl="10"><div class="grid-content bg-purple"></div></el-col>
    <el-col :xs="18" :sm="14" :md="10" :lg="6" :xl="4">
      <h1>登录</h1>
      <el-input v-model="username" placeholder="请输入用户" clearable />
      <el-input placeholder="请输入密码" v-model="password" show-password></el-input>
      <el-button type="success" @click="toLogin" style="width: 100%;">登录</el-button>
    </el-col>
    <el-col :xs="3" :sm="5" :md="7" :lg="9" :xl="10"><div class="grid-content bg-purple"></div></el-col>
  </el-row>
</template>

<script>
  export default {
    data() {
      return{
        username:"",
        password:"",
        alertMsg:""
      }
    },
    methods: {
      toLogin() {
        const fun = this
        if(this.username.trim() == "" || this.username.trim().length<6 || this.password.trim() == "" || this.password.trim().length<6){
          this.alertMsg = "用户名和密码不能为空，且长度大于6"
          this.open()
          return
        }
        this.$http.post('/gojo/login', {
          'username': this.username.trim(),
          'password': this.password.trim()
        }).then(function (response) {
            if(response.data.flag){
              fun.$router.push('/index')
            }else{
              fun.alertMsg = response.data.msg
              fun.open()
            }
          }).catch(function (error) {
            console.log(error);
          });
      },
      open() {
        this.$message.error(this.alertMsg);
      }
    }
  }
</script>

<style scoped>
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
</style>
