<template>
  <div class="admin-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <el-button
            icon="Menu"
            size="small"
            class="menu-toggle"
            @click="isCollapse = !isCollapse"
        />
        <div class="logo">
          <img src="/src/assets/img/logo.png" alt="Logo" class="logo-img" />
          <span class="logo-title">bug管理系统</span>
        </div>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
              <img v-if="data.user_pic" :src="data.user_pic"
                   style="height: 40px; width: 40px; border-radius: 50%; object-fit: cover;">
              <el-avatar v-else icon="User" class="avatar" />
              <span class="username">{{data.username}}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item  @click="router.push('/manager/userinfo')"
              >个人中心</el-dropdown-item>
              <el-dropdown-item @click="exit()">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <div class="main-content">
      <!-- 侧边导航菜单 -->
      <el-aside
          class="sidebar"
          :width="isCollapse ? '64px' : '200px'"
          :style="{ transition: 'width 0.3s ease' }"
      >
        <el-menu
            :default-active="router.currentRoute.value.path"
            :collapse="isCollapse"
            :collapse-transition="false"
            background-color="#1e293b"
            text-color="#cbd5e1"
            active-text-color="#165dff"
            router
        >
          <el-menu-item index="/manager/home">
            <el-icon>
              <HomeFilled />
            </el-icon>
            <template #title>主页</template>
          </el-menu-item>
          <el-menu-item index="/manager/userList">
            <el-icon>
              <User />
            </el-icon>
            <template #title>用户表</template>
          </el-menu-item>
          <el-menu-item index="/manager/bugList">
            <el-icon>
              <Document />
            </el-icon>
            <template #title>BUG管理表</template>
          </el-menu-item>


          <el-menu-item index="/manager/notice">
            <el-icon>
              <Bell />
            </el-icon>
            <template #title>公告管理</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区域 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script setup>
import router from "@/router/index.js";
import "@/assets/css/manager.css";
import {ref, reactive, onMounted} from "vue";
import {ElMessage} from "element-plus";
import axios from "@/utils/axios.js";
const data = reactive({
  token: JSON.parse(localStorage.getItem("xm-pro-user")),
  user_pic:'',
  username:''
});
onMounted(()=>{
  axios.get("/user/userInfo",{
    headers: {
      'Authorization': data.token
    }
  }).then(res=>{
    if (res.code ===0){
      data.user_pic =res.data.userPic
      data.username =res.data.username
    }else {
      console.error(res.message)
    }
  })
})
const exit = () => {
  localStorage.removeItem("xm-pro-user");
  data.user = null;
  ElMessage.success("已退出登录");
  location.href = "/login";
};
// 控制菜单折叠状态
const isCollapse = ref(false);
</script>

<style scoped></style>
