<template>
  <el-card class="card">
    <h1>我提交的bug列表</h1>
    <el-input
        v-model="searchTitle"
        class="input1"
        placeholder="请输入BUG名称查询"
        prefix-icon="Search"
    />
    <el-button type="primary" @click="load">搜索</el-button>
    <div class="action-group">
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
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="BUG名称" show-overflow-tooltip width="120" />
      <el-table-column prop="severity" label="严重度" width="180">
        <template #default="scope">
          <span v-if="scope.row.severity === 1">一般</span>
          <span v-else>
            {{ scope.row.severity === 2 ? '严重' : scope.row.severity === 3 ? '致命' : '无' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="BUG优先级" width="180">
        <template #default="scope">
          <span v-if="scope.row.priority === 1">低</span>
          <span v-else>
            {{ scope.row.priority === 2 ? '中' : scope.row.priority === 3 ? '高' : '无' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="module" label="所属模块" width="180">
        <template #default="scope">
          <span v-if="scope.row.module === 1">前端界面</span>
          <span v-else>
            {{
              scope.row.module === 2 ? '后端逻辑' : scope.row.module === 3 ? '数据库' : scope.row.module === 4 ? '其他' : '无'
            }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="处理状态" width="120">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="handlerName" label="处理人" width="120">
        <template #default="scope">
          {{ scope.row.handlerName || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="create_time" width="200">
        <template #default="scope">
          {{ formatTime(scope.row.create_time) }}
        </template>
      </el-table-column>
      <el-table-column prop="remark" show-overflow-tooltip label="复现步骤" />
      <el-table-column prop="document" show-overflow-tooltip label="附件" />
      <el-table-column label="操作" width="300px">
        <template #default="scope">
          <el-button
              size="small"
              type="primary"
              :icon="Postcard"
              @click="goToBugDetail(scope.row.id)"
          >
            详细
          </el-button>
          <el-button
              size="small"
              type="primary"
              :icon="Edit"
              @click="openEdit(scope.row)"
          >
            编辑
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
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
          v-model:current-page="data.pageNum"
          v-model:page-size="data.pageSize"
          :page-sizes="[5, 10, 15, 20]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="data.total"
      />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog
        title="编辑BUG信息"
        v-model="data.dialogVisible"
        width="700px"
        :close-on-click-modal="false"
        @close="handleDialogClose"
    >
      <el-form
          ref="dataFormRef"
          :model="data.bug_list"
          :rules="data.formRules"
          label-width="100px"
          status-icon
      >
        <!-- 标题 -->
        <el-form-item label="标题" prop="title">
          <el-input v-model="data.bug_list.title" placeholder="请输入标题" />
        </el-form-item>
        <!-- 严重度 -->
        <el-form-item label="严重度" prop="severity">
          <el-select v-model="data.bug_list.severity" placeholder="请选择严重度">
            <el-option label="一般" :value="1" />
            <el-option label="严重" :value="2" />
            <el-option label="致命" :value="3" />
          </el-select>
        </el-form-item>

        <!-- 优先级 -->
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="data.bug_list.priority" placeholder="请选择优先级">
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
          </el-select>
        </el-form-item>

        <!-- 所属模块 -->
        <el-form-item label="所属模块" prop="module">
          <el-select v-model="data.bug_list.module" placeholder="请选择模块">
            <el-option label="前端界面" :value="1" />
            <el-option label="后端逻辑" :value="2" />
            <el-option label="数据库" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>

        <!-- 备注 -->
        <el-form-item label="备注" prop="remark">
          <el-input v-model="data.bug_list.remark" placeholder="请输入备注" type="textarea" />
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
              limit="5"
              :disabled="isUploading"
              multiple
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
      </el-form>
      <template #footer>
        <el-button @click="data.dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确认</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import axios from "@/utils/axios.js";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from "dayjs";
import { DeleteFilled, Edit, Postcard, UploadFilled, Loading } from "@element-plus/icons-vue";
import router from "@/router/index.js";
import { jwtDecode } from "jwt-decode";

// 搜索关键词单独提取（原代码中data.tableData.title不合理）
const searchTitle = ref("");

const data = reactive({
  id: 0,
  dialogType: "",
  deleteDialogVisible: false,
  dialogVisible: false,
  tableData: [],
  bug_list: {
    id: "",
    title: "",
    severity: null,
    priority: null,
    module: null,
    remark: "",
    document: "",
    user_id: 0,
  },
  ids: [], // 多选
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formRules: {
    title: [{ required: true, message: "请输入Bug标题", trigger: "blur" }],
    severity: [{ required: true, message: "请选择严重度", trigger: "change" }],
    priority: [{ required: true, message: "请选择优先级", trigger: "change" }],
    module: [{ required: true, message: "请选择所属模块", trigger: "change" }],
  },
});

const formatTime = (isoTime) => {
  if (!isoTime) return "-";
  return dayjs(isoTime).format("YYYY-MM-DD HH:mm:ss");
};

// 状态选项
const statusOptions = [
  { value: 0, label: "待处理", type: "warning" },
  { value: 1, label: "处理中", type: "primary" },
  { value: 2, label: "已解决", type: "success" },
  { value: 3, label: "已关闭", type: "info" },
  { value: 4, label: "已拒绝", type: "danger" },
];

// 获取状态对应的标签类型
const getStatusType = (status) => {
  const found = statusOptions.find((opt) => opt.value === Number(status));
  return found?.type || "info";
};

// 获取状态标签文本
const getStatusLabel = (status) => {
  const found = statusOptions.find((opt) => opt.value === Number(status));
  return found?.label || "未知";
};

onMounted(() => {
  const token = localStorage.getItem("xm-pro-user");
  if (token) {
    const user = jwtDecode(token);
    data.id = user.claims.id;
  }
  load();
});

// 查询
const load = () => {
  axios
      .post("/bugs/query", {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        user_id: data.id || null,
        title: searchTitle.value || null, // 使用单独的搜索关键词
      })
      .then((res) => {
        if (res.code === 0) {
          data.tableData = res.data.list.map((bug) => ({
            ...bug,
            status: bug.status ?? 0,
            handlerName: bug.handlerName || bug.handler_name || "-",
          }));
          data.total = res.data.total;
        } else {
          ElMessage.error("查询失败：" + res.message);
        }
      })
      .catch((err) => {
        console.error("查询接口异常：", err);
        ElMessage.error("网络错误，请稍后重试");
      });
};

// 分页处理
const handlePageChange = (val) => {
  data.pageNum = val;
  load();
};

const handleSizeChange = (val) => {
  data.pageSize = val;
  load();
};

const dataFormRef = ref(null); // 表单引用

// 上传相关状态
const fileList = ref([]);
const isUploading = ref(false);

// 解析原有附件为上传组件需要的格式
const parseDocumentFiles = (documentStr) => {
  if (!documentStr || documentStr.trim() === "") {
    return [];
  }
  // 分割多个文件URL
  const fileUrls = documentStr.split(",").filter((url) => url.trim());
  // 构造el-upload的文件列表格式
  return fileUrls.map((url, index) => {
    const fileName = url.split("/").pop() || `附件${index + 1}`;
    return {
      name: fileName, // 文件名
      url: url, // 文件链接（预览用）
      raw: null, // 原生File对象（原有文件为null）
      uid: `old-${index}-${Date.now()}`, // 唯一标识
    };
  });
};

// 打开编辑弹窗
const openEdit = (row) => {
  // 重置表单校验
  if (dataFormRef.value) {
    dataFormRef.value.clearValidate();
  }
  data.bug_list = JSON.parse(JSON.stringify(row));
  fileList.value = parseDocumentFiles(row.document || "");

  data.dialogVisible = true;
};

// 关闭弹窗时重置
const handleDialogClose = () => {
  fileList.value = [];
  if (dataFormRef.value) {
    dataFormRef.value.resetFields();
  }
};

// 保存编辑
const save = () => {
  dataFormRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.error("表单填写有误，请检查后重试");
      return;
    }
    // 构建FormData
    const formData = new FormData();
    // 添加bug的基本字段
    formData.append("id", data.bug_list.id);
    formData.append("title", data.bug_list.title);
    formData.append("severity", data.bug_list.severity);
    formData.append("priority", data.bug_list.priority);
    formData.append("module", data.bug_list.module);
    formData.append("remark", data.bug_list.remark);
    formData.append("user_id", data.bug_list.user_id || data.id);
    // 处理多文件：遍历所有新选择的文件（file.raw存在的是新文件）
    const newFiles = fileList.value.filter((file) => file.raw);
    // 关键：多个文件使用相同的key="file"，FormData会自动处理为数组
    newFiles.forEach((file, index) => {
      formData.append("file", file.raw); // 这里key固定为file，后端用MultipartFile[]接收
    });

    // 处理原有文件的URL：将原有文件的URL拼接后传递，保留原有附件
    const oldFiles = fileList.value.filter((file) => !file.raw);
    const oldFileUrls = oldFiles.map(file => file.url).join(",");
    formData.append("oldDocument", oldFileUrls); // 传递原有附件URL
    isUploading.value = true;
    axios
        .put("/bugs/edit", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((res) => {
          if (res.code === 0 || res.code === 200) {
            ElMessage.success("编辑成功");
            data.dialogVisible = false;
            load(); // 重新加载列表
          } else {
            ElMessage.error(`编辑失败：${res.message || "未知错误"}`);
          }
        })
        .catch((err) => {
          console.error("编辑接口异常：", err);
          ElMessage.error("网络错误或文件上传失败，请稍后重试");
        })
        .finally(() => {
          isUploading.value = false;
        });
  });
};

// 单个删除
const deleteBug = (id) => {
  ElMessageBox.confirm("你确定要删除该数据吗?", "确定删除", { type: "warning" })
      .then(() => {
        axios
            .delete("/bugs/deleteId/" + id)
            .then((res) => {
              if (res.code === 200 || res.code === 0) {
                load();
                ElMessage.success("删除成功");
              } else {
                ElMessage.error(`删除失败：${res.message || "未知错误"}`);
              }
            })
            .catch((err) => {
              console.error("删除接口异常：", err);
              ElMessage.error("网络错误，请稍后重试");
            });
      })
      .catch(() => {
        ElMessage({
          type: "info",
          message: "已取消操作",
        });
      });
};

// 多选事件
const handleSelectionChange = (rows) => {
  data.ids = rows.map((row) => row.id);
};

// 批量删除
const delBatch = () => {
  if (data.ids.length === 0) {
    ElMessage.warning("请选择数据");
    return;
  }
  ElMessageBox.confirm("你确定要删除多个数据吗?", "确定删除", { type: "warning" })
      .then(() => {
        axios
            .delete("/bugs/deleteIds", { data: data.ids })
            .then((res) => {
              if (res.code === 200 || res.code === 0) {
                load();
                ElMessage.success("删除成功");
              } else {
                ElMessage.error(`删除失败：${res.message || "未知错误"}`);
              }
            })
            .catch((err) => {
              console.error("批量删除接口异常：", err);
              ElMessage.error("网络错误，请稍后重试");
            });
      })
      .catch(() => {
        ElMessage({
          type: "info",
          message: "已取消操作",
        });
      });
};

// 跳转到详细页
const goToBugDetail = (bugId) => {
  router.push(`/bugmana/bug_detail/${bugId}`);
};

// 文件上传前验证
const beforeFileUpload = (file) => {
  // 仅支持图片和视频
  const allowedTypes = [
    // 图片类型
    "image/jpeg",
    "image/png",
    "image/gif",
    "image/bmp",
    "image/webp",
    // 视频类型
    "video/mp4",
    "video/avi",
    "video/mpeg",
    "video/quicktime",
    "video/x-msvideo",
    "video/webm",
    "video/ogg",
  ];

  if (!allowedTypes.includes(file.type)) {
    ElMessage.error(
        "不支持的文件类型，仅支持图片(jpg/jpeg/png/gif/bmp/webp)和视频(mp4/avi/mpeg/quicktime/webm/ogg)"
    );
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
  // 区分原有文件和新文件
  const fileType = file.raw ? "新选择的" : "原有";
  ElMessage.success(`已移除${fileType}文件: ${file.name}`);
};

// 清空文件
const clearFiles = () => {
  fileList.value = [];
  ElMessage.success("已清空所有文件");
};
</script>

<style scoped>

.card {
  max-width: 100%;
  margin-bottom: 3px;
  padding: 20px;
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
/* 文件上传相关样式 */
.file-count {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
}
</style>