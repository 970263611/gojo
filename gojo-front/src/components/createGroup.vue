<template>
  <div style="margin-top: 20px;">
    <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
      <el-form-item label="群组名称" prop="groupName">
        <el-input v-model="ruleForm.groupName"></el-input>
      </el-form-item>
      <el-form-item label="群组公告" prop="notice">
        <el-input type="textarea" v-model="ruleForm.notice"></el-input>
      </el-form-item>
      <el-form-item label="群组简介" prop="remark">
        <el-input type="textarea" v-model="ruleForm.remark"></el-input>
      </el-form-item>
      <el-form-item label="群组头像">
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
        <el-button type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
        <el-button @click="resetForm('ruleForm')">重置</el-button>
      </el-form-item>
    </el-form>
<!--  <template>-->
<!--    <changeGroup></changeGroup>-->
<!--  </template>-->
  </div>
</template>

<script>
import {getUserCookies} from '../assets/js/user-cookies.js'
// import changeGroup from '../components/changeGroup'


export default {
  // components:{
  //   changeGroup
  // },
  data() {
    return {
      imageUrl: '',
      imgUploadUrl: '/gojo/web/imgUploadUrl',
      localImgUrl: '',
      ruleForm: {
        groupName: '',
        notice: '',
        remark: ''
      },
      rules: {
        groupName: [
          {required: true, message: '请输入群组名称', trigger: 'blur'},
          {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
        ]
      }
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const fun = this
          this.$http.post('/gojo/web/createGroup', {
            'groupName': this.ruleForm.groupName,
            'notice': this.ruleForm.notice,
            'remark': this.ruleForm.remark,
            'headImg': this.localImgUrl
          }).then(function (response) {
            if (response.data.success) {
              getUserCookies(fun.$cookies, fun.$http)
              fun.resetForm('ruleForm')
              fun.$message.success('创建群组成功');
            }
          }).catch(function (error) {
            fun.$message.error('创建群组失败');
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
