<template>
  <div style="text-align: center">
    <img src="@/assets/img/404.png" alt="">
    <div>
      <el-button 
        type="primary" 
        style="width: 150px; height: 68px; font-size: 20px" 
        @click="goHome"
      >
        返回上一页
      </el-button>
    </div>
  </div>
</template>

<script setup>


const goHome = () => {
  // location.href = 'manager/home'
  history.back(); 
}
</script>