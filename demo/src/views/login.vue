<template>
  <div class="login-container">
    <div class="login-form">
      <h2>BUG提交系统 - 登录</h2>
      <el-form ref="loginFormRef" :model="data.form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="data.form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="data.form.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="data.form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="用户" value="USER"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="超级管理员" value="SUPER_ADMIN"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>

          <el-button type="primary" @click="login">登录</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
        <div class="register-link">
          还没有账号？<span @click="openRegisterDialog" class="link">立即注册</span>
        </div>
      </el-form>
    </div>

  </div>
</template>

<script setup>

import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from "@/utils/axios.js"
import { jwtDecode } from 'jwt-decode';

// 路由实例
const router = useRouter()

// 登录表单数据
const data = reactive({
  form: {
    username: '',
    password: '',
    role:'USER',
  }
})

// 登录验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 5, max: 15, message: '用户名长度需在5-15个字符之间', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9]+$/,
      message: '用户名只能包含英文和数字',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5,max: 16, message: '密码长度需在5-15个字符之间', trigger: 'blur' }
  ]
}

// 表单引用
const loginFormRef = ref(null)


// 登录处理
const login = () => {
  loginFormRef.value.validate((valid) => {
    if (!valid) return;
    axios.post('/user/login',
        null,  // Post请求体为空
        {
          params: {  // 参数拼到URL上
            username: data.form.username,
            password: data.form.password,
            role: data.form.role
          }
        }
    )
        .then(res => {
          if (res.code === 200) {
            // 登录成功处理
            localStorage.setItem('xm-pro-user', JSON.stringify(res.data));
            const user=jwtDecode(localStorage.getItem('xm-pro-user'));
            console.log(user)
            ElMessage.success('登录成功');
            setTimeout(() => {
              // 核心逻辑：超级管理员/普通管理员 跳管理端，其他跳用户端
              if ((data.form.role === "SUPER_ADMIN" &&user.claims.role === "SUPER_ADMIN") || (data.form.role === "ADMIN" && user.claims.role === "ADMIN")) {
                router.push('/manager/home');

              } else {
                router.push('/bugmana/userhome');
              }
            }, 500);
          } else {
            // 后端返回业务错误
            ElMessage.error(res.message || '登录失败，请检查账号密码');
          }
        })
  });
};

// 打开注册
const openRegisterDialog = () => {
  router.push('/register');
}
// 重置登录表单
const resetForm = () => {
  if (loginFormRef.value) {
    loginFormRef.value.resetFields()
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