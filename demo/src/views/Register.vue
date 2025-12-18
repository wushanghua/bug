<template>
  <div class="login-container">
    <div class="login-form">
      <h2>BUG提交系统 - 用户注册</h2>
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请设置用户名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请设置密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister">注册</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
        <div class="register-link">
          已有账号？<span @click="returnLogin" class="link">返回登录</span>
        </div>
      </el-form>
    </div>


  </div>
</template>

<script setup>


import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from "@/utils/axios.js";


// 路由实例
const router = useRouter()

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email:''
})
// 表单引用
const registerFormRef = ref(null)
// 注册验证规则
const registerRules = {
  username: [
    { required: true, message: '请设置用户名', trigger: 'blur' },
    { min: 5, max: 15, message: '用户名长度需在5-15个字符之间', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9]+$/,
      message: '用户名只能包含英文和数字',
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请设置邮箱', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
      message: '请输入正确的邮箱格式',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 5,max: 16, message: '密码长度需在5-15个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}



// 注册处理
const handleRegister = () => {
  registerFormRef.value.validate((valid) => {
    if (valid) {
      console.log('测试注册:', registerForm);
      // 最简单的方式测试
      axios.post("/user/register",registerForm)
          .then(res => {
            if (res.code === 200) {
              ElMessage.success('注册成功，返回登录');
              router.push('/login');
              registerFormRef.value?.resetFields()
            } else {
              ElMessage.error(res.message || '注册失败');
            }
          })
          .catch(err => {
            console.error('错误:', err);
            ElMessage.error('请求失败');
          });
    }
  });
};

// 返回登录
const returnLogin = () => {
  router.push('/login');
}


// 重置注册表单
const resetForm = () => {
  if (registerFormRef.value) {
    registerFormRef.value.resetFields()
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-form {
  background-color: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 400px;
}

.login-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.register-link {
  margin-top: 20px;
  text-align: center;
  color: #606266;
}

.register-link .link {
  color: #409eff;
  cursor: pointer;
  text-decoration: none;
}

.register-link .link:hover {
  color: #66b1ff;
  text-decoration: underline;
}
</style>