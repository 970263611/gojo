<template>
  <div style="margin-left:30px;margin-top: 20px;">
    <el-select v-model="group" filterable placeholder="请选择群组" @change="groupChange" @focus="groupFocus"
               style="margin-bottom: 20px;">
      <el-option
        v-for="item in groups"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      >
      </el-option>
    </el-select>
    <div style="margin-top: 20px;">
      <el-transfer
        style="text-align: left; display: inline-block"
        v-model="value"
        filterable
        target-order="original"
        :titles="['我的好友', '群成员']"
        :button-texts="['移除', '添加']"
        :format="{
          noChecked: '${total}',
          hasChecked: '${checked}/${total}'
        }"
        @change="handleChange()"
        :data="data">
      </el-transfer>
    </div>
  </div>
</template>

<script>
import {getUserCookies, setUserCookies} from '../assets/js/user-cookies.js'

export default {
  data() {
    return {
      data: [],
      value: [],
      groups: [],
      group: '',
      author: false,
      loginUserId: ''
    }
  },
  methods: {
    handleChange() {
      if (!this.author) {
        for (const d of this.data) {
          for (const v of this.value) {
            if (d.key === v && d.disabled === false) {
              d.disabled = true
            }
          }
        }
      }
      this.$http.post('/gojo/web/updateGroupUsers', {
        'groupId': this.group,
        'userIds': this.value
      })
      getUserCookies(this.$cookies, this.$http)
    },
    groupChange(value) {
      setTimeout(() => {
      }, 500)
      const fun = this
      fun.data = []
      fun.value = []
      fun.author = false
      const userMsg = setUserCookies(this.$cookies)
      const users = JSON.parse(userMsg.friends)
      const groups = JSON.parse(userMsg.groups)
      for (const g of groups) {
        if (g.groupId === this.group) {
          if (g.author) {
            fun.author = true
            fun.loginUserId = g.createUser
          }
        }
      }
      this.$http.post('/gojo/web/getGroupUsers', {
        'groupId': this.group
      }).then(function (response) {
        if (response.data.success) {
          for (const d of response.data.obj) {
            if (d.id === fun.loginUserId) {
              fun.data.push({
                key: d.id,
                label: d.username,
                disabled: true
              })
            } else {
              fun.data.push({
                key: d.id,
                label: d.username,
                disabled: !fun.author
              })
            }
            fun.value.push(d.id)
          }
          for (const u of users) {
            let index = false
            for (const d of fun.data) {
              if (d.key === u.userId) {
                index = true
              }
            }
            if (!index) {
              fun.data.push({
                key: u.userId,
                label: u.username,
                disabled: false
              })
            }
          }
        }
      }).catch(function (error) {
        console.log(error);
      })
    },
    groupFocus() {
      const userMsg = setUserCookies(this.$cookies)
      this.groups = JSON.parse(userMsg.groups).map(item => {
        return {value: item.groupId, label: item.groupName};
      })
    }
  }
};
</script>
<style>

</style>
