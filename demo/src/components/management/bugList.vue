<template>
  <el-card class="card">
    <h1>BUG管理表</h1>
    <el-input
        v-model="searchForm.title"
        class="input1"
        placeholder="请输入标题查询"
        prefix-icon="Search"
    />
    <el-button type="primary" @click="load">搜索</el-button>
    <div class="action-group">
      <el-button type="success" @click="openAddDialog">新增BUG</el-button>
      <el-button :type="severityFilter === null ? 'primary' : 'default'" @click="setSeverityFilter(null)">全部</el-button>
      <el-button :type="severityFilter === 1 ? 'primary' : 'default'" @click="setSeverityFilter(1)">一般</el-button>
      <el-button :type="severityFilter === 2 ? 'primary' : 'default'" @click="setSeverityFilter(2)">严重</el-button>
      <el-button :type="severityFilter === 3 ? 'primary' : 'default'" @click="setSeverityFilter(3)">致命</el-button>
      <el-button type="danger" @click="delBatch"> 批量删除 </el-button>
    </div>

    <!-- 表格 -->
    <el-table
        :data="data.tableData"
        stripe
        :default-sort="{ prop: 'create_time', order: 'descending' }"
        style="width: 100%; margin-top: 16px"
        @selection-change="handleSelectionChange"
    >
      <!-- 表格列定义 -->
      <el-table-column fixed="left" type="selection" width="55" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="remark" label="复现步骤" show-overflow-tooltip min-width="200" />
      <el-table-column prop="severity" label="严重程度" width="120">
        <template #default="scope">
          <el-tag :type="getSeverityType(scope.row.severity)">
            {{ getSeverityName(scope.row.severity) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="100">
        <template #default="scope">
          <el-tag :type="getPriorityType(scope.row.priority)">
            {{ getPriorityName(scope.row.priority) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitterName" label="提交人" width="120">
        <template #default="scope">
          {{ scope.row.submitterName || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="140">
        <template #default="scope">
          <template v-if="isAdmin">
            <el-select
                v-model="scope.row.status"
                size="small"
                style="width: 120px"
                :disabled="statusUpdating"
                @change="(val) => updateStatus(scope.row, val)"
            >
              <el-option
                  v-for="opt in statusOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
              />
            </el-select>
          </template>
          <template v-else>
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column prop="handlerName" label="处理人" width="120">
        <template #default="scope">
          {{ scope.row.handlerName || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="create_time" width="200">
        <template #default="scope">
          {{ formatTime(scope.row.create_time || scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="180px">
        <template #default="scope">
          <el-button
              size="small"
              type="primary"
              @click="viewBugDetail(scope.row.id)"
          >
            查看
          </el-button>
          <el-button
              size="small"
              type="danger"
              :icon="DeleteFilled"
              @click="deleteBug(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 10px">
      <el-pagination
          @current-change="load"
          @size-change="load"
          v-model:current-page="data.pageNum"
          v-model:page-size="data.pageSize"
          :page-sizes="[2, 10, 15, 20]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="data.total"
      />
    </div>

    <!-- 新增BUG对话框 -->
    <el-dialog
        title="新增BUG"
        v-model="dialogVisible"
        width="600px"
        :close-on-click-modal="false"
        @close="handleDialogClose"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
      >
        <el-form-item label="BUG标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入BUG标题" />
        </el-form-item>

        <el-form-item label="严重程度" prop="severity">
          <el-select v-model="formData.severity" placeholder="请选择严重程度" style="width: 100%">
            <el-option label="一般" value="1" />
            <el-option label="严重" value="2" />
            <el-option label="致命" value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-select v-model="formData.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="低" value="1" />
            <el-option label="中" value="2" />
            <el-option label="高" value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属模块" prop="module">
          <el-select v-model="formData.module" placeholder="请选择所属模块" style="width: 100%">
            <el-option label="前端界面" value="1" />
            <el-option label="后端逻辑" value="2" />
            <el-option label="数据库" value="3" />
            <el-option label="其他" value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="复现步骤" prop="remark">
          <el-input
              v-model="formData.remark"
              type="textarea"
              :rows="4"
              placeholder="请详细描述BUG的复现步骤"
          />
        </el-form-item>

        <el-form-item label="附件">
          <el-upload
              ref="uploadRef"
              action="#"
              :auto-upload="false"
              :on-change="handleFileChange"
              :file-list="fileList"
              :limit="5"
              accept="image/*,video/*"
              :on-exceed="handleFileExceed"
              :on-remove="handleFileRemove"
              multiple
          >
            <el-button type="primary">选择文件</el-button>
          </el-upload>
          <div class="el-upload__tip">
            支持图片和视频文件，最多上传5个文件
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import axios from "@/utils/axios.js";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from 'dayjs';
import { DeleteFilled } from "@element-plus/icons-vue";
import { jwtDecode } from "jwt-decode";
import { useRouter } from "vue-router";

// 表格数据
const data = reactive({
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  ids: [] // 多选ID
});

// 搜索表单
const searchForm = reactive({
  title: ''
});

// 严重程度筛选
const severityFilter = ref(null);
const isAdmin = ref(false);
const statusUpdating = ref(false);
const router = useRouter();
const statusOptions = [
  { value: 0, label: '待处理', type: 'warning' },
  { value: 1, label: '处理中', type: 'primary' },
  { value: 2, label: '已解决', type: 'success' },
  { value: 3, label: '已关闭', type: 'info' },
  { value: 4, label: '已拒绝', type: 'danger' }
];

// 对话框相关
const dialogVisible = ref(false);
const formRef = ref(null);
const uploadRef = ref(null);
const fileList = ref([]);

// 表单数据
const formData = reactive({
  title: '',
  severity: '',
  priority: '',
  module: '',
  remark: ''
});

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入BUG标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  severity: [
    { required: true, message: '请选择严重程度', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  module: [
    { required: true, message: '请选择所属模块', trigger: 'change' }
  ],
  remark: [
    { required: true, message: '请输入复现步骤', trigger: 'blur' },
    { min: 10, message: '复现步骤至少10个字符', trigger: 'blur' }
  ]
};

// 设置严重程度筛选
const setSeverityFilter = (severity) => {
  severityFilter.value = severity;
  load();
};

// 页面加载时获取数据
onMounted(() => {
  const token = localStorage.getItem('xm-pro-user');
  if (token) {
    const claims = jwtDecode(token)?.claims || {};
    isAdmin.value = claims.role === 'ADMIN' || claims.role === 'SUPER_ADMIN';
  }
  load();
});

// 查询数据
const load = () => {
  axios.get("/bugs/bugAll")
      .then(res => {
        if (res.code === 0) {
          // 统一字段/类型，避免后端不同命名导致数据显示为空
          let bugs = res.data.map(bug => ({
            ...bug,
            severity: Number(bug.severity),
            priority: Number(bug.priority || 1), // 默认优先级为低
            status: bug.status ?? 0,
            create_time: bug.create_time || bug.createTime,
            submitterName: bug.submitterName || bug.submitter_name || '-',
            handlerName: bug.handlerName || bug.handler_name || '-'
          }));
          // 搜索过滤
          if (searchForm.title) {
            bugs = bugs.filter(bug => 
              bug.title.toLowerCase().includes(searchForm.title.toLowerCase())
            );
          }
          // 严重程度筛选
          if (severityFilter.value !== null) {
            bugs = bugs.filter(bug => bug.severity === severityFilter.value);
          }
          // 按优先级排序（优先级高的在前面：3 > 2 > 1）
          bugs.sort((a, b) => b.priority - a.priority);
          // 分页处理
          const start = (data.pageNum - 1) * data.pageSize;
          const end = start + data.pageSize;
          data.tableData = bugs.slice(start, end);
          data.total = bugs.length;
        } else {
          ElMessage.error("查询失败：" + res.msg);
        }
      })
      .catch(err => {
        console.error("查询接口异常：", err);
        ElMessage.error("网络错误，请稍后重试");
      });
};

// 删除单个bug
const deleteBug = (id) => {
  ElMessageBox.confirm(
      '你确定要删除该bug吗?', '确定删除', { type: 'warning' }
  )
      .then(() => {
        axios.delete(`/bugs/deleteId/${id}`)
            .then(res => {
              if (res.code === 0 || res.code === 200) {
                load();
                ElMessage.success("删除成功");
              } else {
                ElMessage.error(`删除失败：${res.msg || '未知错误'}`);
              }
            });
      })
      .catch(() => {
        ElMessage({ type: 'info', message: '已取消操作' });
      });
};

// 批量删除
const delBatch = () => {
  if (data.ids.length === 0) {
    ElMessage.warning("请选择bug");
    return;
  }
  ElMessageBox.confirm(
      '你确定要删除选中的bug吗?', '确定删除', { type: 'warning' }
  )
      .then(() => {
        axios.delete('/bugs/deleteIds', { data: data.ids })
            .then(res => {
              if (res.code === 0 || res.code === 200) {
                load();
                ElMessage.success("删除成功");
              } else {
                ElMessage.error(`删除失败：${res.msg || '未知错误'}`);
              }
            });
      })
      .catch(() => {
        ElMessage({ type: 'info', message: '已取消操作' });
      });
};

// 更新状态（仅管理员/超级管理员）
const updateStatus = (row, newStatus) => {
  const oldStatus = row.status;
  statusUpdating.value = true;
  axios.patch(`/bugs/${row.id}/status`, null, {
    params: { status: newStatus }
  }).then(res => {
    if (res.code === 0 || res.code === 200) {
      ElMessage.success("状态已更新");
    } else {
      row.status = oldStatus;
      ElMessage.error(res.msg || res.message || "状态更新失败");
    }
  }).catch(err => {
    row.status = oldStatus;
    console.error("状态更新接口异常：", err);
    ElMessage.error("网络错误，状态更新失败");
  }).finally(() => {
    statusUpdating.value = false;
  });
};

// 处理表格选择变化
const handleSelectionChange = (rows) => {
  data.ids = rows.map(row => row.id);
};

// 格式化时间
const formatTime = (isoTime) => {
  if (!isoTime) return '-';
  return dayjs(isoTime).format('YYYY-MM-DD HH:mm:ss');
};

// 获取严重程度对应的标签类型
const getSeverityType = (severity) => {
  switch(severity) {
    case 1:
      return 'success';
    case 2:
      return 'warning';
    case 3:
      return 'danger';
    default:
      return 'info';
  }
};

// 获取严重程度名称
const getSeverityName = (severity) => {
  switch(severity) {
    case 1:
      return '一般';
    case 2:
      return '严重';
    case 3:
      return '致命';
    default:
      return '未知';
  }
};

// 获取优先级对应的标签类型
const getPriorityType = (priority) => {
  switch(priority) {
    case 1:
      return 'info';
    case 2:
      return 'warning';
    case 3:
      return 'danger';
    default:
      return 'info';
  }
};

// 获取优先级名称
const getPriorityName = (priority) => {
  switch(priority) {
    case 1:
      return '低';
    case 2:
      return '中';
    case 3:
      return '高';
    default:
      return '低';
  }
};

// 获取状态对应的标签类型
const getStatusType = (status) => {
  const found = statusOptions.find(opt => opt.value === Number(status));
  return found?.type || 'info';
};

const getStatusLabel = (status) => {
  const found = statusOptions.find(opt => opt.value === Number(status));
  return found?.label || '未知';
};

// 查看bug详情
const viewBugDetail = (bugId) => {
  router.push(`/manager/bug_detail/${bugId}`);
};

// 打开新增对话框
const openAddDialog = () => {
  resetForm();
  dialogVisible.value = true;
};

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    title: '',
    severity: '',
    priority: '',
    module: '',
    remark: ''
  });
  fileList.value = [];
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

// 关闭对话框
const handleDialogClose = () => {
  resetForm();
  dialogVisible.value = false;
};

// 文件选择处理
const handleFileChange = (uploadFile, uploadFiles) => {
  fileList.value = uploadFiles;
};

// 文件超出限制
const handleFileExceed = () => {
  ElMessage.warning('最多只能上传5个文件');
};

// 文件移除
const handleFileRemove = (file, uploadFiles) => {
  fileList.value = uploadFiles;
};

// 提交表单
const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const formDataToSubmit = new FormData();
      formDataToSubmit.append('title', formData.title);
      formDataToSubmit.append('severity', formData.severity);
      formDataToSubmit.append('priority', formData.priority);
      formDataToSubmit.append('module', formData.module);
      formDataToSubmit.append('remark', formData.remark);

      // 添加文件
      if (fileList.value.length > 0) {
        fileList.value.forEach(file => {
          if (file.raw) {
            formDataToSubmit.append('file', file.raw);
          }
        });
      }

      // 发送请求
      axios.post('/bugs/submit', formDataToSubmit, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then(res => {
        if (res.code === 0 || res.code === 200) {
          ElMessage.success('BUG添加成功');
          dialogVisible.value = false;
          load(); // 刷新列表
        } else {
          ElMessage.error(res.msg || res.message || 'BUG添加失败');
        }
      })
      .catch(err => {
        console.error('BUG添加失败：', err);
        ElMessage.error('网络错误，请稍后重试');
      });
    } else {
      ElMessage.error('请完善必填信息');
      return false;
    }
  });
};
</script>

<style scoped>
.card {
  max-width: 100%;
  margin-bottom: 3px;
}
.input1 {
  width: 240px;
  margin-right: 20px;
}
.action-group {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}
</style>