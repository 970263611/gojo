<template>
  <div class="body">
      <h1>登录</h1>
        <div>
          <input v-model="username" placeholder="username"/>
        </div>
        <div>
          <input v-model="password" placeholder="password"/>
        </div>
        <button @click="toLogin">登录</button>
    </div>
</template>

<script>
    export default {
      data() {
        return{
          username:"",
          password:""
        }
      },
      methods: {
        toLogin() {
          const my = this
          if(this.username == "" || this.username.length<6 || this.password == "" || this.password.length<6){
            alert("用户名和密码不能为空，且长度大于6")
            return false
          }
          this.$http.post('/gojo/login', {
            'username': this.username,
            'password': this.password
          })
          .then(function (response) {
            if(response.data.flag){
              my.$router.push('/index')
            }else{
              alert(response.data.msg)
              return false
            }
          })
          .catch(function (error) {
            console.log(error);
          });
        }
      }
    }
</script>

<style scoped>
  input {
    width: 270px;
    height: 42px;
    line-height:42px;
    margin-top: 25px;
    padding: 0 15px;
    background: #2d2d2d;
    *background-color:transparent;
    background: rgba(45,45,45,.15);
    -moz-border-radius: 6px;
    -webkit-border-radius: 6px;
    border-radius: 6px;
    border: 1px solid #3d3d3d;
    border: 1px solid rgba(255,255,255,.15);
    -moz-box-shadow: 0 2px 3px 0 rgba(0,0,0,.1) inset;
    -webkit-box-shadow: 0 2px 3px 0 rgba(0,0,0,.1) inset;
    box-shadow: 0 2px 3px 0 rgba(0,0,0,.1) inset;
    font-family: 'PT Sans', Helvetica, Arial, sans-serif;
    font-size: 14px;
    color: #fff;
    text-shadow: 0 1px 2px rgba(0,0,0,.1);
    -o-transition: all .2s;
    -moz-transition: all .2s;
    -webkit-transition: all .2s;
    -ms-transition: all .2s;
  }
  button {
    cursor: pointer;
    width: 300px;
    height: 44px;
    margin-top: 25px;
    padding: 0;
    background: #ef4300;
    -moz-border-radius: 6px;
    -webkit-border-radius: 6px;
    border-radius: 6px;
    border: 0px;
    -moz-box-shadow:
      0 15px 30px 0 rgba(255,255,255,.25) inset,
      0 2px 7px 0 rgba(0,0,0,.2);
    -webkit-box-shadow:
      0 15px 30px 0 rgba(255,255,255,.25) inset,
      0 2px 7px 0 rgba(0,0,0,.2);
    box-shadow:
      0 15px 30px 0 rgba(255,255,255,.25) inset,
      0 2px 7px 0 rgba(0,0,0,.2);
    font-family: 'PT Sans', Helvetica, Arial, sans-serif;
    font-size: 14px;
    font-weight: 700;
    color: #fff;
    text-shadow: 0 1px 2px rgba(0,0,0,.1);
    -o-transition: all .2s;
    -moz-transition: all .2s;
    -webkit-transition: all .2s;
    -ms-transition: all .2s;
  }

  button:hover {
    -moz-box-shadow:
      0 15px 30px 0 rgba(255,255,255,.15) inset,
      0 2px 7px 0 rgba(0,0,0,.2);
    -webkit-box-shadow:
      0 15px 30px 0 rgba(255,255,255,.15) inset,
      0 2px 7px 0 rgba(0,0,0,.2);
    box-shadow:
      0 15px 30px 0 rgba(255,255,255,.15) inset,
      0 2px 7px 0 rgba(0,0,0,.2);
  }

  button:active {
    -moz-box-shadow:
      0 15px 30px 0 rgba(255,255,255,.15) inset,
      0 2px 7px 0 rgba(0,0,0,.2);
    -webkit-box-shadow:
      0 15px 30px 0 rgba(255,255,255,.15) inset,
      0 2px 7px 0 rgba(0,0,0,.2);
    box-shadow:
      0 5px 8px 0 rgba(0,0,0,.1) inset,
      0 1px 4px 0 rgba(0,0,0,.1);

    border: 0px solid #ef4300;
  }
</style>
