<template>
  <el-container>
    <el-header class="logo"><h1>gojo</h1></el-header>
    <el-main>
      <el-row>
        <el-col :xs="24" :sm="24" :md="6" :lg="5" :xl="5">
          <transition name="el-fade-in">
            <div v-show="isChat">
              <div :style="dynamicDiv">
                <el-tabs>
                  <el-tab-pane label="聊天">
                    <template v-for="(item) in activeChats">
                      <div v-if="item.type=='user'" class="chat" @click="toChat('user',item.id,item.name)">
                        <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                        <span>{{ item.name }}</span>
                      </div>
                      <div v-if="item.type=='group'" class="chat" @click="toChat('group',item.id,item.name)">
                        <el-avatar icon="el-icon-user-solid" shape="square" :size="50"></el-avatar>
                        <span>{{ item.name }}</span>
                      </div>
                    </template>
                  </el-tab-pane>
                  <el-tab-pane label="我的好友">
                    <div v-for="(item) in loadUsers" class="chat" @click="toChat('user',item.id,item.username)">
                      <el-image v-if="item.headImg !== null && item.headImg !==''"
                                style="width: 60px; height: 60px"
                                :src="item.headImg"
                                fit="fill"></el-image>
                      <el-avatar v-if="item.headImg =='' || item.headImg ==null" icon="el-icon-user-solid"
                                 shape="square" :size="50"></el-avatar>
                      <span>{{ item.username }}</span>
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="我的群组">
                    <div v-for="(item) in loadGroups" class="chat" @click="toChat('group',item.groupId,item.groupName)">
                      <el-image v-if="item.headImg !== null && item.headImg !==''"
                                style="width: 60px; height: 60px"
                                :src="item.headImg"
                                fit="fill"></el-image>
                      <el-avatar v-if="item.headImg =='' || item.headImg ==null" icon="el-icon-user-solid"
                                 shape="square" :size="50"></el-avatar>
                      <span>{{ item.groupName }}</span>
                    </div>
                  </el-tab-pane>
                  <template v-if="isPhone" v-for="(item) in phoneJs">
                    <el-tab-pane :label="item.label" :name="item.name" v-html="item.text"></el-tab-pane>
                  </template>
                </el-tabs>
              </div>
            </div>
          </transition>
          <transition name="el-fade-in">
            <div v-show="!isChat" :style="dynamicDiv">
              <el-card class="box-card" style="height: 100%;position: relative;">
                <div slot="header" class="clearfix">
                  <span>{{ chatName }} 交谈中...</span>
                  <el-button style="float: right; padding: 3px 0" type="text" @click="toList">返回</el-button>
                </div>
                <div id="chatBodyDiv" class="chatBody" :style="chatBodyStyle">
                  <div v-for="item in chats">
                    <div>
                      <span v-if="!item.flag" style="float: left;">
                        {{ item.username }}&nbsp&nbsp{{ item.time }}
                      </span>
                      <span v-if="item.flag" style="float: right;">
                        {{ item.username }}&nbsp&nbsp{{ item.time }}
                      </span>
                      <br>
                      <span v-if="!item.flag" style="float: left;">{{ item.msg }}</span>
                      <span v-if="item.flag" style="float: right;">{{ item.msg }}</span>
                    </div>
                    <br>
                  </div>
                </div>
                <div class="chatInput">
                  <el-input
                    type="textarea"
                    v-model="chatMsg"
                    maxlength="1000"
                    show-word-limit
                    :autosize="{ minRows: 6, maxRows: 6}"
                    suffix-icon="el-icon-check"
                    resize="none"
                  />
                  <el-button type="primary" @click="sendMsg" class="chatBtn">发送</el-button>
                </div>
              </el-card>
            </div>
          </transition>
        </el-col>
        <el-col v-if="!isPhone" :xs="0" :xl="1">
          <div style="height: 10px;">

          </div>
        </el-col>
        <el-col v-if="!isPhone" :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <div :style="dynamicDiv">
            <div class="outside" :style="outside_right">
              <el-tabs :tab-position="tabPosition">
                <template v-for="(item,index) in phoneJs">
                  <el-tab-pane :label="item.label" :name="item.name" :key="index" v-html="item.text"></el-tab-pane>
                </template>
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
import {phoneTemp} from '../assets/js/isPhone.js'
import {
  getActiveChats,
  getUserChatMsg, getUserCookies,
  setActiveChats,
  setUserChatMsg,
  setUserCookies
} from '../assets/js/user-cookies.js'
import {getFormatTime} from '../assets/js/util.js'

export default {
  data() {
    return {
      path: "/gojo/websocket",
      socket: null,
      params: "",
      count: 10,
      loading: false,
      dynamicDiv: {
        'height': ''
      },
      outside_left: {
        'height': ''
      },
      outside_right: {
        'height': ''
      },
      tabPosition: 'left',
      bokeStyle: {
        'height': '',
        'width': '100%',
        'display': 'block'
      },
      phoneJs: '',
      isPhone: false,
      loadUsers: null,
      loadGroups: null,
      loadChat: null,
      isChat: true,
      chatName: '',
      chatMsg: '',
      chatType: '',
      chatId: '',
      chats: {},
      activeChats: '',
      chatBodyStyle: {
        'max-height': ''
      },
      getOfflineMsgSize: 0
    }
  },
  created() {
    //头部加上尾部模块，每个60，一共120
    //菜单40px，原本有margin，在app.vue里面已经去掉了
    this.dynamicDiv.height = window.innerHeight - 120 + 'px';
    this.outside_left.height = window.innerHeight - 120 - 40 + 'px';
    this.outside_right.height = window.innerHeight - 120 + 'px';
    this.bokeStyle.height = window.innerHeight - 120 + 'px';
    //打字框138 padding向下10 头部58 上padding20 和打字框padding20
    this.chatBodyStyle["max-height"] = window.innerHeight - 120 - 138 - 10 - 58 - 20 - 20 + 'px';
    if (window.innerWidth < 768) {
      this.tabPosition = 'top'
      this.isPhone = true
    }
    document.onkeydown = function (e) {
      let key = window.event.keyCode;
      if (key == 13) {
        alert(1)
      }
    }
  },
  mounted() {
    const fun = this
    this.phoneJs = phoneTemp
    const {height, width, display} = this.bokeStyle
    for (let js of this.phoneJs) {
      js.text = js.text.replace('bokeStyle', `height: ${height};width: ${width};display: ${display};`)
    }
    this.$http.post('/gojo/handler/getWebSocketAddr').then(function (response) {
      fun.init("ws:" + response.data + fun.path, localStorage.token)
    }).catch(function (error) {
      console.log(error);
    })
    setInterval(this.onopen, 500);
    setInterval(this.checkToken, 30000);
  },
  methods: {
    load() {
      this.loading = true
      setTimeout(() => {
        this.count += 2
        this.loading = false
      }, 2000)
    },
    toChat(type, id, name) {
      this.chatName = name
      this.chatType = type
      this.chatId = id
      this.chats = getUserChatMsg(this.$cookies, type, id)
      this.isChat = false
    },
    toList() {
      this.isChat = true
    },
    tabClick() {
      alert(1)
    },
    sendMsg() {
      if (this.chatMsg != '') {
        const obj = {}
        const obj1 = {}
        obj.flag = true
        obj.id = this.chatId
        obj.msg = this.chatMsg
        obj.time = getFormatTime(new Date())
        obj1.id = this.chatId
        obj1.name = this.chatName
        obj1.type = this.chatType
        //这样处理避免根据时间排序
        setActiveChats(this.$cookies, obj1)
        this.activeChats = getActiveChats(this.$cookies)
        setUserChatMsg(this.$cookies, this.chatType, obj)
        this.chats = getUserChatMsg(this.$cookies, this.chatType, this.chatId)
        this.send(this.chatType, this.chatId, this.chatMsg, this.chatName)
        this.chatMsg = ''
      }
    },
    init(path, token) {
      if (typeof (WebSocket) === "undefined") {
        alert("您的浏览器不支持socket")
      } else {
        this.socket = new WebSocket(path, [token])
        // this.socket = new WebSocket(path)
        // 监听打开
        this.socket.onopen = this.onopen;
        // 监听关闭
        this.socket.onclose = this.onclose;
        //监听异常
        this.socket.onerror = this.onerror;
        //监听服务器发送的消息
        this.socket.onmessage = this.onmessage;
      }
    },
    //发送消息
    send(type, id, msg, name) {
      const json = {}
      json.type = type
      json.toUserId = id
      json.msg = msg
      json.flag = false
      json.toUserName = name
      this.params = JSON.stringify(json)
      this.socket.send(this.params)
      // document.getElementById('chatBodyDiv').scrollTop = document.getElementById('chatBodyDiv').scrollHeight
    },
    //连接发生错误的回调方法
    onerror() {
      window.location.href = "/"
    },
    //连接成功建立的回调方法
    onopen() {
      if (setUserCookies(this.$cookies).friends != undefined) {
        this.loadUsers = JSON.parse(setUserCookies(this.$cookies).friends).reverse()
      }
      if (setUserCookies(this.$cookies).groups != undefined) {
        this.loadGroups = JSON.parse(setUserCookies(this.$cookies).groups).reverse()
      }
      this.activeChats = getActiveChats(this.$cookies)
      if (this.getOfflineMsgSize < 1) {
        this.$http.post('/gojo/web/getOfflineMsg')
      }
      this.getOfflineMsgSize += 1
    },
    //接收到消息的回调方法
    onmessage(msg) {
      const json = JSON.parse(msg.data)
      const obj = {}
      obj.id = json.fromUserId
      obj.msg = json.msg
      obj.flag = json.flag
      obj.username = json.toUserName
      obj.time = getFormatTime(new Date(json.createTime))
      setUserChatMsg(this.$cookies, json.type, obj)
      this.chats = getUserChatMsg(this.$cookies, json.type, json.fromUserId)
      const obj1 = {}
      obj1.id = json.fromUserId
      obj1.name = json.fromUserName
      obj1.type = json.type
      setActiveChats(this.$cookies, obj1)
      this.activeChats = getActiveChats(this.$cookies)
    },
    //连接关闭的回调方法
    onclose() {

    },
    checkToken() {
      const json = {
        'type': 'token',
        'msg': localStorage.token
      }
      if (this.socket === null || this.socket.readyState !== 1) {
        window.location.href = "/"
      }
      this.socket.send(JSON.stringify(json))
    }
  },
}
</script>

<style scoped>
h1 {
  color: black;
  text-align: center;
  margin: 0;
}

.logo {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}

.foot {
  /*position: fixed;*/
  /*bottom: 0;*/
  background-color: #2d2d2d;
  width: 100%;
}

.chat {
  height: 60px;
  margin-top: 10px;
  background-color: white;
  border-radius: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
  cursor: context-menu;
  user-select: none;
}

.outside {
  overflow-x: hidden;
  overflow-y: scroll;
}

.outside::-webkit-scrollbar {
  display: none;
}

.el-avatar {
  margin-top: 5px;
  margin-left: 10px;
}

main {
  padding-top: 0;
  padding-bottom: 0;
}

.el-card {
  border: 0px solid #EBEEF5;
}

.chatInput {
  position: absolute;
  bottom: 0;
  left: 0;
  padding-bottom: 10px;
  width: 96%;
  padding-left: 2%;
}

.chatBtn {
  position: absolute;
  right: 2%;
  bottom: 13%;
}

.chatBody {
  overflow-y: scroll;
}
</style>
