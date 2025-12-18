<template>
  <div>
    <h1>主页1111</h1>
  </div>
  
</template>
<script setup>

import request from '@/assets/utils/request';
import { onMounted } from 'vue';
import {ElMessage} from "element-plus";
const findAll = () => {
  fetch('http://localhost:8080/user/all', {
  // fetch('http://localhost:5173/user/all', {
    method: 'get'
  })
      .then(response => {
        return response.json();
      })
      .then(result => {
        console.log('后端完整响应:', result);
        if (result.code === 0) {
          const users = result.data;
          console.log('获取到的用户列表:', users);
          // 这里可以将 users 赋值给响应式变量，用于页面渲染
        } else {
          console.log('获取用户失败:', result.msg); // 输出失败原因
          ElMessage.error(result.msg || '获取用户列表失败');
        }
      })
      .catch(err => {
        console.error('请求失败:', err);
        ElMessage.error('网络异常，请稍后重试');
      });
};

// 组件挂载后自动触发 findAll 方法（默认触发）
onMounted(() => {
  findAll(); // 页面加载完成后立即调用
});


</script>