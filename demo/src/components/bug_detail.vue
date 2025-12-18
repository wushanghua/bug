<template>
  <div class="bug-detail-container">
    <!-- 页面标题与返回按钮 -->
    <div class="page-header">
      <el-button
          type="primary"
          :icon="ArrowLeft"
          @click="handleBack"
          class="back-btn"
      >
        返回列表
      </el-button>
      <h1 class="page-title">BUG 详情</h1>
    </div>

    <!-- 详情卡片 -->
    <el-card class="detail-card">
      <el-row :gutter="30">
        <!-- 左侧基本信息 -->
        <el-col :span="12">
          <div class="info-item">
            <span class="info-label">BUG 名称：</span>
            <span class="info-value">{{ data.title || '无' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">严重度：</span>
            <!-- 修复：直接绑定转换后的文字，样式选择器改为基于文字内容 -->
            <span :class="['info-value', 'severity-tag', 'severity-' + data.severity]"
            >{{ data.severity }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">优先级：</span>
            <!-- 修复：直接绑定转换后的文字，样式选择器改为基于文字内容 -->
            <span :class="['info-value', 'priority-tag', 'priority-' + data.priority]"
            >{{ data.priority }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">所属模块：</span>
            <span class="info-value">{{ data.module || '无' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">创建时间：</span>
            <span class="info-value">{{ data.create_time || '无' }}</span>
          </div>
        </el-col>

        <!-- 右侧其他信息 -->
        <el-col :span="12">
          <div class="info-item">
            <span class="info-label">附件：</span>
            <span class="info-value">
    <template v-if="data.document && data.document.trim()">
      <el-link
          v-for="(fileUrl, index) in data.document.split(',').filter(url => url.trim())"
          :key="index"
          type="primary"
          @click="downloadFile(fileUrl)"
          style="margin-right: 16px; margin-bottom: 8px; display: inline-block;"
      >
        附件{{ index + 1 }}：{{ fileUrl.split('/').pop() }}  <!-- 显示原文件名，用户更清楚 -->
      </el-link>
    </template>
    <span v-else>无附件</span>
  </span>
          </div>
        </el-col>
      </el-row>

      <!-- 复现步骤 -->
      <div class="info-section">
        <h3 class="section-title">复现步骤</h3>
        <div class="section-content">
          {{ data.remark || '无复现步骤描述' }}
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ArrowLeft } from '@element-plus/icons-vue';
import { ElButton, ElCard, ElRow, ElCol, ElLink } from 'element-plus';
import { ElMessage } from 'element-plus';
import dayjs from "dayjs";
import { useRoute, useRouter } from 'vue-router';
import axios from "@/utils/axios.js";

// 详情数据
const data = ref({
  title: '',
  severity: '',
  priority: '',
  module: '',
  create_time: '',
  remark: '',
  document: '',
});

// 路由实例
const route = useRoute();
const router = useRouter();

// 页面加载时获取BUG详情
onMounted( () => {
  const bugId = route.params.id;
  if (!bugId) {
    ElMessage.error('缺少BUG ID');
    router.push(`/bugmana/bug_list`);
    return;
  }
  onebug(bugId);
});

const onebug = (id) => {
  axios.get("/bugs/onebug/"+id).then(res=>{
    if (res.code===0){
      data.value = {
        ...res.data,
        severity: getSeverityText(res.data.severity),
        priority: getPriorityText(res.data.priority),
        module: getModuleText(res.data.module),
        create_time: formatTime(res.data.create_time)
      };
    }else{
      ElMessage.error("请求失败");
    }
  })
}

// 转换严重度（数字→文字）
const getSeverityText = (severity) => {
  const map = { 1: '一般', 2: '严重', 3: '致命' };
  return map[severity] || '无';
};

// 转换优先级（数字→文字）
const getPriorityText = (priority) => {
  const map = { 1: '低', 2: '中', 3: '高' };
  return map[priority] || '无';
};

// 转换模块（数字→文字）
const getModuleText = (module) => {
  const map = { 1: '前端界面', 2: '后端逻辑', 3: '数据库', 4: '其他' };
  return map[module] || '无';
};

// 格式化时间
const formatTime = (isoTime) => {
  if (!isoTime) return '-';
  return dayjs(isoTime).format('YYYY-MM-DD HH:mm:ss');
};

// 下载附件
const downloadFile = (fileUrl) => {
  if (!fileUrl) return; // 防止空URL
  
  // 确保URL正确编码（处理中文和特殊符号）
  // 如果URL已经编码，decodeURIComponent不会报错；如果未编码，则保持不变
  let encodedUrl = fileUrl;
  try {
    // 分离基础URL和文件名部分
    const urlParts = fileUrl.split('/');
    const fileName = urlParts.pop();
    const baseUrl = urlParts.join('/');
    
    // 如果文件名包含未编码的中文或特殊符号，进行编码
    // 检查是否已经编码（编码后的字符串包含%）
    if (fileName && !fileName.includes('%')) {
      // 未编码，需要编码
      const encodedFileName = encodeURIComponent(fileName);
      encodedUrl = baseUrl + '/' + encodedFileName;
    } else {
      // 已经编码或不需要编码，直接使用
      encodedUrl = fileUrl;
    }
  } catch (e) {
    // 编码失败，使用原始URL
    encodedUrl = fileUrl;
  }
  
  // 1. 提取URL中的原文件名（解码后显示）
  const fileName = decodeURIComponent(fileUrl.split('/').pop() || '');
  
  // 2. 创建a标签下载
  const a = document.createElement('a');
  a.href = encodedUrl;
  a.download = fileName; // 用原文件名下载（比"附件1.jpg"更直观）
  document.body.appendChild(a);
  a.click(); // 触发下载
  // 3. 下载后移除a标签（清理DOM）
  setTimeout(() => {
    document.body.removeChild(a);
  }, 100);
};

// 返回列表页
const handleBack = () => {
  history.back();
};
</script>

<style scoped>
.bug-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.detail-card {
  padding: 20px;
  border-radius: 8px;
}

.info-item {
  margin-bottom: 20px;
  line-height: 1.8;
}

.info-label {
  display: inline-block;
  width: 100px;
  font-weight: 500;
  color: #606266;
}

.info-value {
  color: #303133;
}

/* 严重度标签样式 */
.severity-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
}
.severity-一般 {
  background: #e1f3d8;
  color: #52c41a;
}
.severity-严重 {
  background: #fff7e6;
  color: #faad14;
}
.severity-致命 {
  background: #fee;
  color: #f5222d;
}

/* 优先级标签样式 */
.priority-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
}
.priority-低 {
  background: #f0f9eb;
  color: #52c41a;
}
.priority-中 {
  background: #fdf6ec;
  color: #faad14;
}
.priority-高 {
  background: #fee6e6;
  color: #f5222d;
}

/* 段落信息样式 */
.info-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.section-title {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #1f2329;
  font-weight: 500;
}

.section-content {
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>