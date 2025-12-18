<template>
  <el-card style="max-width: 100%; margin: 0px auto;">
    <template #header>
      <div class="card-header">
        <h1>提交新BUG</h1>
        <p class="sub-title">请填写以下信息提交新的Bug报告</p>
      </div>
    </template>

    <!-- 使用el-form组件 -->
    <el-form
        ref="bugFormRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="bug-form"
    >
      <!-- Bug标题 -->
      <el-form-item label="Bug标题" prop="title" required>
        <el-input
            v-model="formData.title"
            placeholder="请简要描述Bug现象"
            style="width: 100%"
        />
      </el-form-item>

      <!-- 严重度、优先级、所属模块 -->
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="严重度" prop="severity" required>
            <el-select
                v-model="formData.severity"
                placeholder="请选择"
                style="width: 100%"
            >
              <el-option label="致命" style="color: red" value="3"></el-option>
              <el-option label="严重" style="color: #9d5f01" value="2"></el-option>
              <el-option label="一般" value="1"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="优先级" prop="priority" required>
            <el-select
                v-model="formData.priority"
                placeholder="请选择"
                style="width: 100%"
            >
              <el-option label="高" style="color: red" value="3"></el-option>
              <el-option label="中" style="color: #9d5f01" value="2"></el-option>
              <el-option label="低" value="1"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="所属模块" prop="module" required>
            <el-select
                v-model="formData.module"
                placeholder="请选择"
                style="width: 100%"
            >
              <el-option label="前端界面" value="1"></el-option>
              <el-option label="后端逻辑" value="2"></el-option>
              <el-option label="数据库" value="3"></el-option>
              <el-option label="其他" value="4"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 复现步骤 -->
      <el-form-item label="复现步骤">
        <el-input
            type="textarea"
            v-model="formData.remark"
            placeholder="请详细描述复现Bug的操作步骤"
            :rows="5"
            style="width: 100%"
        />
      </el-form-item>

      <!-- 附件上传 -->
      <el-form-item label="附件">
        <el-upload
            v-model:file-list="fileList"
            class="upload-demo"
            action="#"
            :on-remove="handleFileRemove"
            :before-upload="beforeFileUpload"
            :auto-upload="false"
            list-type="picture"
            :disabled="isUploading"
        >
          <el-button type="primary" :loading="isUploading">
            <UploadFilled v-if="!isUploading" />
            <el-icon v-else><Loading /></el-icon>
            {{ isUploading ? '上传中...' : '选择文件' }}
          </el-button>
          <template #tip>
            <div class="el-upload__tip">
              支持格式：jpg、jpeg、png、gif、bmp、webp（图片）和 mp4、avi、mpeg、quicktime、webm、ogg（视频）
            </div>
          </template>
        </el-upload>
        <div v-if="fileList.length > 0" class="file-count">
          已选择 {{ fileList.length }} 个文件
          <el-button text size="small" @click="clearFiles">清空</el-button>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm" :loading="isSubmitting">提交Bug</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElForm, ElFormItem, ElRow, ElCol } from 'element-plus';
import { UploadFilled, Loading } from '@element-plus/icons-vue';
import request from '@/assets/utils/request';
import { jwtDecode } from 'jwt-decode';

// 表单引用
const bugFormRef = ref(null);

// 表单数据
const formData = reactive({
  title: '',
  severity: '',
  priority: '',
  module: '',
  remark: '',
  user_id: 0, // 提交人ID
  document: '' // 附件路径，提交时填充
});

// 表单验证规则（el-form内置验证）
const formRules = ref({
  title: [
    { required: true, message: '请输入Bug标题', trigger: 'blur' }
  ],
  severity: [
    { required: true, message: '请选择严重度', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  module: [
    { required: true, message: '请选择所属模块', trigger: 'change' }
  ]
});

// 上传相关状态
const fileList = ref([]);
const isUploading = ref(false);
const isSubmitting = ref(false);

// 初始化用户信息
onMounted(() => {
  try {
    const tokenStr = localStorage.getItem("xm-pro-user");
    if (tokenStr) {
      // token可能是JSON字符串，需要先解析
      let token = tokenStr;
      try {
        const parsed = JSON.parse(tokenStr);
        if (typeof parsed === 'string') {
          token = parsed;
        } else if (typeof parsed === 'object' && parsed !== null) {
          token = parsed.token || tokenStr;
        }
      } catch (e) {
        // 如果不是JSON，直接使用
        token = tokenStr;
      }
      // 去除可能的引号
      if (typeof token === 'string') {
        token = token.trim();
        if (token.startsWith('"') && token.endsWith('"')) {
          token = token.substring(1, token.length - 1);
        }
      }
      const userInfo = jwtDecode(token);
      // jwtDecode返回的payload中，claims可能在顶层，也可能在claims字段中
      formData.user_id = userInfo.id || (userInfo.claims && userInfo.claims.id);
    }
  } catch (err) {
    console.error('解析用户信息失败：', err);
    ElMessage.error('获取用户信息失败，请重新登录');
  }
});

// 文件上传前验证
const beforeFileUpload = (file) => {
  // 仅支持图片和视频
  const allowedTypes = [
    // 图片类型
    'image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/webp',
    // 视频类型
    'video/mp4', 'video/avi', 'video/mpeg', 'video/quicktime', 'video/x-msvideo',
    'video/webm', 'video/ogg'
  ];

  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('不支持的文件类型，仅支持图片(jpg/jpeg/png/gif/bmp/webp)和视频(mp4/avi/mpeg/quicktime/webm/ogg)');
    return false;
  }

  const maxSize = 10 * 1024 * 1024; // 10MB
  if (file.size > maxSize) {
    ElMessage.error(`文件 ${file.name} 大小超过10MB限制`);
    return false;
  }
  return true;
};


// 移除文件
const handleFileRemove = (file, files) => {
  fileList.value = [...files];
  ElMessage.success(`已移除文件: ${file.name}`);
};

// 清空文件
const clearFiles = () => {
  fileList.value = [];
  ElMessage.success('已清空所有文件');
};

// 提交表单
const submitForm = async () => {
  // 1. 触发表单验证
  const valid = await bugFormRef.value.validate();
  if (!valid) return;
  // 2. 验证用户信息
  if (!formData.user_id) {
    ElMessage.error('未获取到用户信息，请先登录');
    return;
  }

  // 3. 构建FormData，直接提交到/bugs/submit接口
  isSubmitting.value = true;
  try {
    const submitFormData = new FormData();
    
    // 添加Bug表单字段
    submitFormData.append('title', formData.title);
    // 确保数字字段转换为整数
    submitFormData.append('severity', String(formData.severity));
    submitFormData.append('priority', String(formData.priority));
    submitFormData.append('module', String(formData.module));
    if (formData.remark) {
      submitFormData.append('remark', formData.remark);
    }
    
    // 添加文件（如果有）
    if (fileList.value.length > 0) {
      fileList.value.forEach(file => {
        submitFormData.append('file', file.raw);
      });
    }

    const res = await request.post('/bugs/submit', submitFormData, {
      onUploadProgress: (e) => {
        if (e.total) {
          const percent = Math.round((e.loaded / e.total) * 100);
          console.log(`提交进度: ${percent}%`);
        }
      }
    });
    
    if (res.code === 200 || res.code === 0) {
      ElMessage.success("提交成功");
      resetForm(); // 重置表单
    } else {
      ElMessage.error(`提交失败：${res.message || res.msg || '未知错误'}`);
    }
  } catch (err) {
    console.error('提交异常：', err);
    if (err.response) {
      // 有响应但状态码不是2xx
      if (err.response.status === 401) {
        ElMessage.error('未登录或登录已过期，请重新登录');
      } else if (err.response.status === 403) {
        ElMessage.error('没有权限执行此操作');
      } else {
        const errorMsg = err.response.data?.message || err.response.data?.msg || '服务器错误';
        ElMessage.error(`提交失败：${errorMsg}`);
      }
    } else {
      ElMessage.error('提交接口异常，请稍后重试');
    }
  } finally {
    isSubmitting.value = false;
  }
};

// 重置表单
const resetForm = () => {
  bugFormRef.value.resetFields();
  formData.remark = '';
  fileList.value = [];
};
</script>

<style scoped>
.bug-form {
  width: 100%;
  padding-top: 10px;
}

.file-count {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-button .el-icon) {
  vertical-align: middle;
  margin-right: 4px;
}

.card-header {
  padding: 10px 0;
}

.card-header h2 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #333;
}

.sub-title {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 修复el-form-item间距 */
:deep(.el-form-item) {
  margin-bottom: 20px;
}

/* 最后一个按钮区域间距调整 */
:deep(.el-form-item:last-child) {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>