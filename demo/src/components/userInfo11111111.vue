<template>
  <el-card class="personal-card" shadow="hover">
    <!-- 卡片头部：标题和操作按钮 -->
    <template #header>
      <div class="card-header">
        <h1 class="title">个人信息</h1>
        <div class="action-buttons">
          <el-button
              type="primary"
              @click="openPassword"
              class="password-btn"
          >
            <i class="el-icon-edit"></i> 修改密码
          </el-button>
          <template v-if="!state.isEditing">
            <el-button
                type="primary"
                @click="toggleEdit"
                class="edit-btn"
            >
              <i class="el-icon-edit"></i> 编辑资料
            </el-button>
          </template>
          <template v-else>
            <el-button
                type="success"
                @click="saveInfo"
                class="save-btn"
            >
              <i class="el-icon-check"></i> 保存
            </el-button>
            <el-button
                @click="toggleEdit"
                class="cancel-btn"
            >
              <i class="el-icon-close"></i> 取消
            </el-button>
          </template>
        </div>
      </div>
    </template>

    <!-- 卡片内容区 -->
    <div class="card-content">
      <!-- 左侧信息区 -->
      <div class="left-section">
<!--        头像-->
        <div class="avatar-box">
          <el-upload
              class="avatar-uploader"
              action="http://localhost:8080/files/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :disabled="!state.isEditing"
          >
            <img v-if="state.userInfo.userPic" :src="state.userInfo.userPic" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </div>

        <div class="user-status">
          <div class="user-role-box">
            <el-tag :type="getRoleType(state.userInfo.role)" size="large" class="role-tag">
              <i class="el-icon-user"></i> {{ getRoleName(state.userInfo.role) }}
            </el-tag>
          </div>
          <p class="join-date">
            <i class="el-icon-date"></i> 加入时间: {{ formatTime(state.userInfo.createTime) }}
          </p>

        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="right-section">
        <el-form
          ref="infoForm"
          :model="state.userInfo"
          :rules="state.rules"
          label-width="80px"
          class="info-form"
        >
          <!-- 带必填星号的表单项 -->
          <el-form-item label="用户名" prop="username" class="required-item">
            <el-input
              v-model="state.userInfo.username"
              disabled
              placeholder="请输入用户名"
              class="form-input"
            />
          </el-form-item>
          <el-form-item label="姓名" prop="nickname" class="required-item">
            <el-input
              v-model="state.userInfo.nickname"
              :disabled="!state.isEditing"
              placeholder="请输入姓名"
              class="form-input"
            />
          </el-form-item>
          <el-form-item label="邮箱" prop="email" class="required-item">
            <el-input
              v-model="state.userInfo.email"
              :disabled="!state.isEditing"
              placeholder="请输入邮箱"
              class="form-input"
            />
          </el-form-item>
        </el-form>
      </div>
    </div>
  </el-card>
  <!-- 修改密码对话框 -->
  <el-dialog
    v-model="state.showPasswordDialog"
    title="修改密码"
    width="400px"
  >
    <el-form
      ref="passwordForm1"
      :model="state.passwordForm"
      :rules="state.passwordRules"
      label-width="100px"
      class="password-form"
    >
      <el-form-item label="当前密码" prop="oldPassword">
        <el-input 
          v-model="state.passwordForm.oldPassword"
          type="password" 
          placeholder="请输入当前密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input 
          v-model="state.passwordForm.newPassword"
          type="password" 
          placeholder="请输入新密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input 
          v-model="state.passwordForm.confirmPassword"
          type="password" 
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="openPassword">取消</el-button>
      <el-button type="primary" @click="changePassword">确认修改</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, reactive, watchEffect, onMounted} from 'vue';
import { ElMessage } from 'element-plus';
import {Plus} from "@element-plus/icons-vue";
import axios from "@/utils/axios.js";
// import axios from "axios";
import dayjs from 'dayjs';
import { jwtDecode } from 'jwt-decode';


// 状态管理
const state = reactive({
  token:localStorage.getItem('xm-pro-user'),
  isEditing: false,
  showPasswordDialog: false,
  user:jwtDecode(localStorage.getItem('xm-pro-user')),
  userInfo: {
    userPic: '',
    id: '',
    username: '',
    nickname: '',
    email: '',
    createTime: '',
    role: '', // 用户权限角色
  },
  originalInfo: {}, // 用于保存原始数据
  passwordForm: {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  },
  // 表单验证规则
  rules: {
    realName: [
      { required: true, message: '请输入姓名', trigger: 'blur' },
      { min: 2, max: 10, message: '姓名长度在2-10个字符之间', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
  },
  passwordRules: {
    oldPassword: [
      { required: true, message: '请输入当前密码', trigger: 'blur' },
      { min: 6, max: 20, message: '当前密码长度在6-20个字符之间', trigger: 'blur' }
    ],
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      { min: 6, max: 20, message: '新密码长度在6-20个字符之间', trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, message: '请确认新密码', trigger: 'blur' },
      { 
        validator: (rule, value, callback) => {
          if (value !== state.passwordForm.newPassword) {
            callback(new Error('两次输入的密码不一致'));
          } else {
            callback();
          }
        }, 
        trigger: 'blur' 
      }
    ]
  }
});

onMounted(()=>{
  axios.get("/user/userInfo",{
    headers: {
      'Authorization': state.token
    }
  }).then(res=>{
    if (res.code ===0){
      state.userInfo=res.data
    }else {
      console.error(res.message)
    }
  })
})
// 表单引用
const infoForm = ref(null);
const passwordForm1 = ref(null);

// 切换编辑状态
const toggleEdit = () => {
  if (state.isEditing) {
    // 取消编辑时恢复原始数据
    state.userInfo = { ...state.originalInfo };
  } else {
    // 开始编辑时保存当前数据
    state.originalInfo = { ...state.userInfo };
  }
  state.isEditing = !state.isEditing;
  infoForm.value?.clearValidate();
};

// 保存个人信息
const saveInfo = () => {
  infoForm.value.validate((valid) => {
    if (valid) {
      // 模拟API请求
      setTimeout(() => {
        state.isEditing = false;
        ElMessage.success('信息保存成功');
      }, 600);
    } else {
      ElMessage.error('请完善必填信息');
      return false;
    }
  });
};

//头像上传
const handleAvatarSuccess = (res) => {

  console.log(state.token)
  const avatarUrl = res.data; // 上传接口返回的头像URL

  console.log('上传头像返回的URL：', avatarUrl);
  axios.patch(
      "/user/updateAvatar",
      {
        headers: {
          'Authorization': state.token // 携带 Token 供后端解析用户
        },
        params: {
          avatarUrl: avatarUrl // 参数名和后端 @RequestParam 一致
        }
      }
  ).then(res => {
    // 假设后端返回 Result 格式：{ code: 0, message: "xxx", data: {} }
    if (res.code === 0) {
      // 3. 同步更新本地状态（页面实时显示新头像）
      state.userInfo.userPic = avatarUrl;
      // 4. 同步更新本地存储的用户信息（避免刷新后丢失）
      // const updatedUser = { ...state.user, claims: { ...state.user.claims, userPic: avatarUrl } };
      // localStorage.setItem("xm-pro-user", JSON.stringify(updatedUser));
      ElMessage.success("头像更新成功");
    } else {
      ElMessage.error(`头像更新失败：${res.message || '未知错误'}`);
    }
  }).catch(err => {
    console.error('头像更新请求失败：', err);
    ElMessage.error("网络错误，请稍后重试");
  });
};

//打开密码
const openPassword = () => {
  state.showPasswordDialog = !state.showPasswordDialog;
  passwordForm1.value?.resetFields();
}

// 修改密码
const changePassword = () => {
  // 先触发表单验证
  passwordForm1.value.validate((valid) => {
    if (valid) {
      axios.post('/user/update_password', {
        username:state.userInfo.username,
        password: state.passwordForm.oldPassword,  // 旧密码
        newPassword: state.passwordForm.newPassword,   // 新密码
      })
          .then(res => {
            if (res.code === 200) {
              openPassword();
              localStorage.removeItem("xm-pro-user");
              location.href = "/login";
              ElMessage.success('密码修改成功，请重新登录');
            } else {
              ElMessage.error(res.message || '修改密码失败');
            }
          }).catch(err => {
          console.error('请求失败：', err);
      });
    }
  });
};

const formatTime = (isoTime) => {
  if (!isoTime) return '-'; // 空值显示为短横线
  // 转换为 "YYYY-MM-DD HH:mm:ss" 格式
  return dayjs(isoTime).format('YYYY-MM-DD HH:mm:ss');
};

// 获取角色中文名称
const getRoleName = (role) => {
  if (!role) return '未知';
  const roleMap = {
    'USER': '普通用户',
    'ADMIN': '管理员',
    'SUPER_ADMIN': '超级管理员'
  };
  return roleMap[role] || role;
};

// 获取角色标签类型（用于显示不同颜色）
const getRoleType = (role) => {
  if (!role) return 'info';
  const typeMap = {
    'USER': 'info',           // 蓝色
    'ADMIN': 'warning',       // 橙色
    'SUPER_ADMIN': 'danger'   // 红色
  };
  return typeMap[role] || 'info';
};
</script>

<style scoped>
.personal-card {
  max-width: 95%;
  margin: 0px auto;
  border-radius: 6px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-btn {
  background-color: #4096ff;
  border-color: #4096ff;
}

.save-btn {
  background-color: #52c41a;
  border-color: #52c41a;
}

.cancel-btn {
  background-color: #fff;
  color: #666;
  border-color: #d9d9d9;
}

.password-btn {
  margin-right: 8px;
}

.card-content {
  display: flex;
  gap: 40px;
  padding: 20px 0;
}

.left-section {
  width: 200px;
  flex-shrink: 0;
}

.avatar-box {
  width: 160px;
  height: 160px;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 20px;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.user-status {
  text-align: left;
}

.user-role-box {
  margin-bottom: 16px;
  display: flex;
  justify-content: center;
}

.role-tag {
  font-size: 14px;
  padding: 8px 16px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.role-tag i {
  font-size: 16px;
}

.user-role {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 5px 0;
}

.user-type {
  color: #666;
  font-size: 14px;
  margin: 0 0 15px 0;
}

.join-date {
  color: #999;
  font-size: 12px;
  margin: 0 0 15px 0;
  display: flex;
  align-items: center;
  gap: 5px;
}

.right-section {
  flex: 1;
  min-width: 0;
}

.info-form {
  width: 100%;
}

.el-form-item {
  margin-bottom: 18px;
}

.required-item .el-form-item__label::before {
  content: '*';
  color: #ff4d4f;
  margin-right: 4px;
}

.el-form-item__label {
  color: #666;
  font-weight: 500;
  font-size: 14px;
}

.form-input, .form-select {
  width: 100%;
  height: 36px;
  border-radius: 4px;
}

.form-textarea {
  width: 100%;
  border-radius: 4px;
  resize: vertical;
}

.el-input__inner:disabled {
  background-color: #f5f7fa;
  color: #333;
  cursor: default;
}

.password-form {
  margin-top: 20px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .card-content {
    flex-direction: column;
  }

  .left-section {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 30px;
  }

  .user-status {
    text-align: center;
  }
}


.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<style>
 .avatar-uploader .el-upload {
   border: 1px dashed var(--el-border-color);
   border-radius: 6px;
   cursor: pointer;
   position: relative;
   overflow: hidden;
   transition: var(--el-transition-duration-fast);
 }

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>