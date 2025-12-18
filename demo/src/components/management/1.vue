<template>
  <div class="bug-stat-container">
    <!-- 卡片容器 -->
    <el-card class="stat-card">
      <!-- 头部标题区域 -->
      <div class="card-header">
        <h2 class="card-title">近7天BUG提交统计</h2>
        <div class="total-count">
          累计提交：<span class="count-value">{{ data.total }}</span> 个BUG
        </div>
      </div>
      <!-- 图表区域 -->
      <div class="chart-wrapper">
        <div id="line" class="chart-container"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import * as echarts from 'echarts';
import {onMounted, reactive} from "vue";
import axios from "@/utils/axios.js";
import {ElMessage} from "element-plus";

const data = reactive({
  total:0
})
const lineOption = {
  title: {
    text: '近7天总用户BUG提交量'
  },
  tooltip: {},
  legend: {
    data: ['提交量']
  },
  xAxis: {
    data: []
  },
  yAxis: {},
  series: [
    {
      name: '提交量',
      type: 'line',
      smooth:true,
      data: []
    }
  ]
};
onMounted(()=>{
  lineData()
  getTotal()
})
// bug获取图表
const lineData = () => {
  const lineChart =echarts.init(document.getElementById('line'));
  axios.get("/bugs/lineData").then(res=>{
    lineOption.xAxis.data = res.data.date; // 横轴数据
    lineOption.series[0].data = res.data.count; // 纵轴的数据
    // 使用刚指定的配置项和数据显示图表
    lineChart.setOption(lineOption);
  })
}
const getTotal = () => {
  axios.get("/bugs/bugAll").then(res=>{
    if (res.code === 0 ) {
      data.total =res.data.length
    }else {
      ElMessage.error("连接失败");
    }

  }).catch(err => {
    console.error("接口异常：", err);
    ElMessage.error("网络错误，请稍后重试");
  })
}
</script>

<style scoped>
.bug-stat-container {
  padding: 20px;
  min-width: 600px;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f5f5f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.total-count {
  font-size: 14px;
  color: #666;
}

.count-value {
  color: #409eff;
  font-size: 16px;
  font-weight: 600;
  margin: 0 4px;
}

.chart-wrapper {
  padding: 20px;
}

.chart-container {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .bug-stat-container {
    padding: 10px;
    min-width: auto;
  }

  .chart-container {
    height: 300px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>