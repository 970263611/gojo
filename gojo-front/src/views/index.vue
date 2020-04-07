<template>
  <el-container>
    <el-header class="logo"><h1>gojo</h1></el-header>
    <el-main>
      <el-row>
        <el-col :xs="24" :sm="24" :md="9" :lg="8" :xl="7">
          <div :style="dynamicDiv">
            <el-tabs v-model="activeName">
              <el-tab-pane label="聊天" name="first">
                <div class="outside" :style="outside_left">
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                  <div class="chat">
                    <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                    <span>12345613</span>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="好友" name="second">配置管理</el-tab-pane>
              <el-tab-pane label="群组" name="third">角色管理</el-tab-pane>
              <el-tab-pane v-for="(item,index) in temp">
                <el-tab-pane :label="item.label" :name="item.name" :key="index">{{item.text}}</el-tab-pane>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-col>
        <el-col :xs="0":xl="1">
          <div style="height: 10px;">

          </div>
        </el-col>
        <el-col :xs="24" :sm="24" :md="15" :lg="16" :xl="16">
          <div :style="dynamicDiv">
            <div class="outside" :style="outside_right">
              <el-tabs :tab-position="tabPosition">
                <el-tab-pane label="在线人员">配置管理</el-tab-pane>
                <el-tab-pane label="热门动态">角色管理</el-tab-pane>
                <el-tab-pane label="大花博客">
                  <iframe :src="bokeUrl" frameborder="0" :style="bokeStyle"></iframe>
                </el-tab-pane>
                <el-tab-pane label="我的资料">角色管理</el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-main>
    <el-footer class="foot"><h1>gojo</h1></el-footer>
  </el-container>
</template>

<script>
  import {temp} from './config'
  export default {
    data () {
      return {
        path:"ws:127.0.0.1:3210/gojo/websocket",
        socket:null,
        params:"",
        count: 10,
        loading: false,
        activeName: 'first',
        dynamicDiv:{
          'height': ''
        },
        outside_left:{
          'height': ''
        },
        outside_right:{
          'height': ''
        },
        tabPosition: 'left',
        bokeUrl:'https://www.dahuaboke.com/index',
        bokeStyle:{
          'height': '',
          'width': '100%',
          'display': 'block'
        }
      }
    },
    created() {
      //头部加上尾部模块，每个60，一共120
      //菜单40px，原本有margin，在app.vue里面已经去掉了
      this.dynamicDiv.height=window.innerHeight-120+'px';
      this.outside_left.height=window.innerHeight-120-40+'px';
      this.outside_right.height=window.innerHeight-120+'px';
      this.bokeStyle.height=window.innerHeight-120+'px';
      if(window.innerWidth<768){
        this.tabPosition = 'top'
      }
    },
    mounted () {
      console.log(temp)
      this.init()
    },
    methods: {
      load () {
        this.loading = true
        setTimeout(() => {
          this.count += 2
          this.loading = false
        }, 2000)
      },
      init() {
        if(typeof(WebSocket) === "undefined"){
          alert("您的浏览器不支持socket")
        }else{
          this.socket = new WebSocket(this.path)
          // 监听打开
          this.socket.onopen= this.onopen;
          // 监听关闭
          this.socket.onclose = this.onclose;
          //监听异常
          this.socket.onerror = this.onerror;
          //监听服务器发送的消息
          this.socket.onmessage = this.onmessage;
        }
      },
      //发送消息
      send() {
        const json = {}
        json.toUserId = 1
        json.msg = "test"
        this.params = JSON.stringify(json)
        this.socket.send(this.params)
      },
      //连接发生错误的回调方法
      onerror() {
      },
      //连接成功建立的回调方法
      onopen() {
      },
      //接收到消息的回调方法
      onmessage(msg) {
        debugger
      },
      //连接关闭的回调方法
      onclose() {
      }
    },
  }
</script>

<style scoped>
  h1{
    color: black;
    text-align: center;
    margin: 0;
  }
  .logo{
    font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
  }
  .foot{
    /*position: fixed;*/
    /*bottom: 0;*/
    background-color: #2d2d2d;
    width: 100%;
  }
  .chat{
    height: 60px;
    margin-top: 10px;
    background-color: white;
    border-radius: 15px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
  }
  .outside{
    overflow-x: hidden;
    overflow-y: scroll;
  }
  .outside::-webkit-scrollbar {
    display: none;
  }
  .el-avatar{
    margin-top: 5px;
    margin-left: 10px;
  }
  main{
    padding-top: 0;
    padding-bottom: 0;
  }
</style>
