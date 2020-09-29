<template>
  <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm" style="margin-top: 20px;">
    <el-form-item label="我的昵称" prop="username">
      <el-input v-model="ruleForm.username"></el-input>
    </el-form-item>
    <el-form-item label="我的密码" prop="password">
      <el-input v-model="ruleForm.password" show-password></el-input>
    </el-form-item>
    <el-form-item label="我的头像">
      <el-upload
        class="avatar-uploader"
        :action="imgUploadUrl"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload">
        <img v-if="imageUrl" :src="imageUrl" class="avatar">
        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
      </el-upload>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm('ruleForm')">立即修改</el-button>
      <el-button @click="resetForm('ruleForm')">重置</el-button>
    </el-form-item>
  </el-form>
</template>
<script>
import {getUserCookies} from "../assets/js/user-cookies";

export default {
  data() {
    return {
      imageUrl: '',
      imgUploadUrl: '/gojo/imgUploadUrl',
      localImgUrl: '',
      ruleForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          {required: true, message: '请输入昵称', trigger: 'blur'},
          {min: 1, max: 20, message: '长度在需大于1', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 6, max: 15, message: '长度需大于6', trigger: 'blur'}
        ]
      }
    };
  },
  created() {
    this.getMe()
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const fun = this
          this.$http.post('/gojo/web/updateUser', {
            'username': this.ruleForm.username,
            'password': this.ruleForm.password,
            'headImg': this.localImgUrl
          }).then(function (response) {
            if (response.data.success) {
              fun.$message.success('修改资料成功')
              fun.getMe()
            }
          }).catch(function (error) {
            fun.$message.error('修改资料失败')
            console.log(error)
          })
        } else {
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
      this.clearFiles()
    },
    handleAvatarSuccess(res, file) {
      if (res.success) {
        this.imageUrl = URL.createObjectURL(file.raw)
        this.localImgUrl = res.result.message
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg'
      const isPNG = file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isJPG && !isPNG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
        return false
      }
      return true
    },
    clearFiles() {
      this.imageUrl = ''
    },
    getMe(){
      const fun = this
      this.$http.post('/gojo/web/getMy').then(function (response) {
        if (response.data.success) {
          fun.ruleForm.username = response.data.obj.username
          fun.ruleForm.password = response.data.obj.password
          fun.imageUrl = response.data.obj.headImg
        }
      }).catch(function (error) {
        console.log(error)
      })
    }
  }
}
</script>
<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style>
