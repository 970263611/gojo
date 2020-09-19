<template>
  <div id="allTag">
    <h2>添加新的朋友和群组吧</h2>
    <div>
      <h4>新的朋友</h4>
      <el-tag
        v-for="tag in users"
        :key="tag.name"
        closable
        @close="userClose(tag)"
        effect="dark"
        :color="getRandomColor()">
        {{ tag.name }}
      </el-tag>
    </div>
    <h4>新的群组</h4>
    <div>
      <el-tag
        v-for="tag in groups"
        :key="tag.name"
        closable
        @close="groupClose(tag)"
        effect="dark"
        :color="getRandomColor()">
        {{ tag.name }}
      </el-tag>
    </div>
  </div>
</template>

<script>
import {getUserCookies} from "../assets/js/user-cookies";

export default {
  data() {
    return {
      users: [],
      groups: []
    };
  },
  created() {
    const fun = this
    this.$http.post('/gojo/getNotFriendAndGroup').then(function (res) {
      if (res.data.success) {
        for (const u of res.data.obj.users) {
          fun.users.push({
            name: u.username,
            key: u.id
          })
        }
        for (const g of res.data.obj.groups) {
          fun.groups.push({
            name: g.groupName,
            key: g.groupId
          })
        }
      }
    }).catch(function (error) {
      console.log(error);
    })
  },
  mounted() {
    setTimeout(() => {
      this.changeIcon()
    }, 500)
    // this.$nextTick(() => {
    //   this.changeIcon()
    // })
  },
  methods: {
    userClose(tag) {
      this.users.splice(this.users.indexOf(tag), 1);
      this.addFriendOrGroup('addFriend', tag.key)
    },
    groupClose(tag) {
      this.groups.splice(this.groups.indexOf(tag), 1);
      this.addFriendOrGroup('addGroup', tag.key)
    },
    getRandomColor() {
      this.r = Math.floor(Math.random() * 255);
      this.g = Math.floor(Math.random() * 255);
      this.b = Math.floor(Math.random() * 255);
      return 'rgba(' + this.r + ',' + this.g + ',' + this.b + ',0.5)';
    },
    changeIcon() {
      const objs = document.getElementById("allTag").getElementsByTagName("i");
      for (let o of objs) {
        if (o.className.indexOf("el-icon-close") != -1) {
          o.classList.remove("el-icon-close")
          o.classList.add("el-icon-plus")
        }
      }
    },
    addFriendOrGroup(type, id) {
      const fun = this
      fun.$http.post('/gojo/' + type, {
        'id': id,
      }).then(function (res) {
        if (res.data.success) {
          getUserCookies(fun.$cookies, fun.$http)
        }
      }).catch(function (error) {
        console.log(error);
      })
    }
  }
}
</script>
<style>
  .el-tag + .el-tag {
    margin-left: 10px;
  }
</style>
