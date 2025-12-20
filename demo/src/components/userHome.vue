<template>
  <div class="combined-container">
    <!-- 左右布局的父容器 -->
    <div class="layout-wrapper">
      <!-- 系统公告组件（左侧） -->
      <div class="notice-section">
        <el-card class="notice-card">
          <template #header>
            <h1>系统公告</h1>
          </template>
          <!-- 加载状态：添加loading遮罩 -->
          <div v-loading="loading" class="notice-list">
            <div
                v-for="(notice, index) in noticeList"
                :key="index"
                class="notice-item-wrapper"
            >
              <div
                  class="notice-item"
                  @click="handleNoticeClick(notice)"
              >
                <!-- 公告内容区域 -->
                <div class="notice-content">
                  <div class="notice-title">
                    {{ notice.title }}
                    <span class="notice-time">{{ formatTime(notice.createTime) }}</span>
                  </div>
                  <div class="notice-brief">{{ notice.content && notice.content.substring(0, 50) + '...' }}</div>
                </div>

                <el-button
                    size="small"
                    type="primary"
                    @click.stop="handleNoticeClick(notice)"
                >
                  查看详情
                </el-button>
              </div>

              <el-divider v-if="index < noticeList.length - 1" class="notice-divider"></el-divider>
            </div>

            <div v-if="noticeList.length === 0 && !loading" class="empty-notice">
              暂无系统公告
            </div>
          </div>
        </el-card>
      </div>

      <!-- BUG统计组件（右侧） -->
      <div class="bug-stat-section">
        <el-card class="stat-card">
          <!-- 头部标题区域 -->
          <div class="card-header">
            <h1 class="card-title">近7天BUG提交统计</h1>
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
    </div>

    <!-- 公告详情弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        title="公告详情"
        width="60%"
        :close-on-click-modal="false"
    >
      <div v-if="currentNotice" class="notice-detail">
        <div class="detail-title">{{ currentNotice.title }}</div>
        <div class="detail-meta">
          发布人： 系统管理员  |
          发布时间：{{ formatTime(currentNotice.createTime) }}
        </div>

        <div class="detail-content">{{ currentNotice.content }}</div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios.js'
import dayjs from "dayjs";
import * as echarts from 'echarts';
import { jwtDecode } from 'jwt-decode';

// ---------------------- 系统公告相关逻辑 ----------------------
const loading = ref(false)
const noticeList = ref([])
const dialogVisible = ref(false)
const currentNotice = ref(null)

// 时间格式化：适配接口返回的时间格式（如yyyy-MM-dd HH:mm:ss）
const formatTime = (timeStr) => {
  if (!timeStr) return '-';
  return dayjs(timeStr).format('YYYY-MM-DD HH:mm:ss');
};

// 加载公告数据（复用公告管理页面的接口逻辑）
const load = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: 1,
      pageSize: 100
    }
    const res = await axios.get('/notices', { params })
    if (res.code === 0) {
      noticeList.value = res.data.list || []
    } else {
      ElMessage.error(res.message || '公告数据加载失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，公告数据加载失败')
  } finally {
    loading.value = false
  }
}

// 点击公告查看详情
const handleNoticeClick = (notice) => {
  currentNotice.value = notice
  dialogVisible.value = true
}

// ---------------------- BUG统计相关逻辑 ----------------------
const data = reactive({
  total: 0,
  // 解析用户信息（增加token存在性判断，避免报错）
  user: jwtDecode(localStorage.getItem('xm-pro-user')) ,
})

const lineOption = {
  title: {
    text: '近7天用户BUG提交量'
  },
  tooltip: { trigger: 'axis' },
  legend: { data: ['提交量'] },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [
    {
      name: '提交量',
      type: 'line',
      smooth: true,
      data: []
    }
  ]
};

// 定义ECharts实例变量
let lineChart = null;

// 获取BUG图表数据
const lineData = () => {
  const lineChartDom = document.getElementById('line');
  if (!lineChartDom) return;
  if (lineChart) lineChart.dispose();
  lineChartDom.style.width = '100%';
  lineChartDom.style.height = '400px';
  lineChart = echarts.init(lineChartDom);

  axios.get("/bugs/lineData", {
    params: { user_id: data.user?.claims?.id || '' }
  }).then(res => {
    // 移除数据兜底，直接使用接口返回数据
    lineOption.xAxis.data = res.data.date;
    lineOption.series[0].data = res.data.count;
    lineChart.setOption(lineOption);
    window.addEventListener('resize', handleResize);
  }).catch(err => {
    console.error("BUG图表数据获取失败：", err);
    ElMessage.error("BUG统计数据加载失败");
  })
}

// 窗口resize处理函数
const handleResize = () => {
  if (lineChart) lineChart.resize();
}

// 获取BUG累计总数
const getTotal = () => {
  axios.post("/bugs/query", {
    user_id: data.user?.claims?.id || ''
  }).then(res => {
    if (res.code === 0) data.total = res.data.total
    else ElMessage.error("BUG总数获取失败");
  }).catch(err => {
    console.error("接口异常：", err);
    ElMessage.error("网络错误，请稍后重试");
  })
}

// 页面挂载时加载所有数据（延迟确保DOM渲染完成）
onMounted(() => {
  load();
  setTimeout(() => {
    lineData();
    getTotal();
  }, 50);
})

// 页面卸载时清理资源
onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  if (lineChart) lineChart.dispose();
})
</script>

<style scoped>
.combined-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}


.layout-wrapper {
  display: flex;
  gap: 20px;
  width: 100%;
  height: 600px;
}


.notice-section {
  flex: 1;
  min-width: 300px;
  height: 100%;
}
.notice-card {
  width: 100%;
  height: 100%;
}


.bug-stat-section {
  flex: 2;
  min-width: 500px;
  height: 100%;
}
.stat-card {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}


.notice-list {
  padding: 0;
  margin: 10px 0 0 0;
  width: 100%;
  height: calc(100% - 40px);
  overflow-y: auto;
}
.notice-item-wrapper { width: 100%; }
.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.notice-item:hover { background-color: #f5f7fa; }
.notice-content { flex: 1; cursor: pointer; }
.notice-title {
  font-size: 16px;
  font-weight: 500;
  color: #1f2937;
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 8px;
}
.notice-time {
  font-size: 15px;
  padding-right: 20px;
  color: #9ca3af;
  font-weight: normal;
}
.notice-brief {
  font-size: 14px;
  color: #6b7280;
  width: 100%;
}
.notice-divider { margin: 0; padding: 0; }
.empty-notice {
  text-align: center;
  padding: 30px 0;
  color: #9ca3af;
  font-size: 14px;
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
  font-size: 20px;
  font-weight: 500;
  color: #333;
}
.total-count { font-size: 20px; color: #666; }
.count-value {
  color: #409eff;
  font-size: 20px;
  font-weight: 600;
  margin: 0 4px;
}
.chart-wrapper {
  padding: 20px;
  height: calc(100% - 70px);
  box-sizing: border-box;
}

.chart-container {
  width: 100%;
  height: 400px;
  border-radius: 8px;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .layout-wrapper {
    flex-direction: column;
    height: auto;
    min-height: 800px;
  }
  .notice-section, .bug-stat-section {
    min-width: auto;
    width: 100%;
    height: 400px;
  }
  .notice-section { margin-bottom: 20px; }
}

@media (max-width: 768px) {
  .combined-container { padding: 10px; }
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>