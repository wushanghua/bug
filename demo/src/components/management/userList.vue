<template>
  <el-card class="card">
    <h1>用户管理表</h1>
    <el-input
        v-model="searchForm.username"
        class="input1"
        placeholder="请输入用户名查询"
        prefix-icon="Search"
    />
    <!-- 角色下拉选择器 -->
<el-select
        v-model="searchForm.role"
        placeholder="请选择角色"
        style="width: 150px; margin-right: 10px"

    >
      <el-option label="取消选择" value="" />
      <el-option label="超级管理员" value="SUPER_ADMIN" />
      <el-option label="管理员" value="ADMIN" />
      <el-option label="普通用户" value="USER" />
    </el-select>
    <el-button type="primary"  @click="load">搜索</el-button>
    <div class="action-group">
      <el-button type="success" @click="openAddDialog">新增</el-button>
      <el-button type="danger" @click="delBatch"> 批量删除 </el-button>
    </div>

    <!-- 表格 -->
    <el-table
        :data="data.tableData"
        stripe
        :default-sort="{ prop: 'createTime', order: 'descending' }"
        style="width: 100%; margin-top: 16px"
        @selection-change="handleSelectionChange"
    >
      <!-- 表格列定义 -->
      <el-table-column fixed="left" type="selection" width="55" />
      <el-table-column prop="username" label="用户名" show-overflow-tooltip width="120" />
      <el-table-column prop="nickname" label="昵称" show-overflow-tooltip width="120" />
      <el-table-column prop="email" label="邮箱" show-overflow-tooltip width="200" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="scope">
          <el-tag
              :type="scope.row.role === 'SUPER_ADMIN' ? 'warning' : (scope.row.role === 'ADMIN' ? 'danger' : 'success')"
          >
            {{
              scope.row.role === 'SUPER_ADMIN' ? '超级管理员' :
                  (scope.row.role === 'ADMIN' ? '管理员' : '普通用户')
            }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="200">
        <template #default="scope">
          {{ formatTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="更新时间" prop="updateTime" width="200">
        <template #default="scope">
          {{ formatTime(scope.row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="user_pic" label="头像" width="120">
        <template #default="scope">
          <el-image
              v-if="scope.row.user_pic || scope.row.userPic"
              :src="scope.row.user_pic || scope.row.userPic"
              fit="cover"
              style="width: 40px; height: 40px; border-radius: 50%"
          />
          <el-icon v-else class="no-avatar"><User /></el-icon>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="200px">
        <template #default="scope">
          <el-button
              size="small"
              type="primary"
              :icon="Edit"
              @click="openEditDialog(scope.row)"
          >
            编辑
          </el-button>
          <el-button
              size="small"
              type="danger"
              :icon="DeleteFilled"
              @click="deleteUser(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--     分页-->
    <div style="margin-top: 10px">
      <el-pagination
          @current-change="load"
          @size-change="load"
          v-model:current-page="data.pageNum"
          v-model:page-size="data.pageSize"
          :page-sizes="[5, 10, 15, 20]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="data.total"
      />
    </div>

    <!-- 编辑/新增弹窗 -->
    <el-dialog
        :title="dialogTitle"
        v-model="dialogVisible"
        width="500px"
        :close-on-click-modal="false"
        @close="handleDialogClose"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
          status-icon
      >
        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <!-- 密码 -->
        <el-form-item v-if="dialogType === 'add'" label="密码" prop="password" :rules="formRules.password">
          <el-input v-model="formData.password" placeholder="请输入密码" type="password" />
        </el-form-item>
        <!-- 昵称 -->
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <!-- 邮箱 -->
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <!-- 角色 -->
        <el-form-item label="角色" prop="role">
        <el-select 
          v-model="formData.role" 
          placeholder="请选择角色"
          :disabled="isEditingSelf"
        >
          <!-- 只有超级管理员可以创建管理员 -->
          <el-option v-if="isSuperAdmin" label="管理员" value="ADMIN" />
          <el-option label="普通用户" value="USER" />
        </el-select>
        <div v-if="isEditingSelf" class="role-tip">
          <el-text type="warning" size="small">不能修改自己的角色</el-text>
        </div>
        </el-form-item>
        <!-- 头像 -->
        <el-form-item label="头像">
          <el-upload
              ref="uploadRef"
              action="#"
              :auto-upload="false"
              :on-change="handleFileChange"
              :file-list="fileList"
              :limit="1"
              accept=".jpg,.jpeg,.png,.gif,.bmp"
              :on-exceed="handleFileExceed"
              :on-remove="handleFileRemove"
          >
            <el-button type="primary" icon="Upload">选择头像</el-button>
          </el-upload>
          <div class="el-upload__tip">
            支持 JPG/PNG/GIF/BMP 图片，单个文件最大 5MB，仅可上传1个文件
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import axios from "@/utils/axios.js";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from 'dayjs';
import { DeleteFilled, Edit, User } from "@element-plus/icons-vue";
import router from "@/router/index.js";
import { jwtDecode } from "jwt-decode";

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
  username: '',
  role: '',
});

// 角色筛选
const roleFilter = ref('');
const isSuperAdmin = ref(false);
const currentUserId = ref(null); // 当前登录用户ID
const isEditingSelf = ref(false); // 是否正在编辑自己

// 设置角色筛选
const setRoleFilter = (role) => {
  roleFilter.value = role;
  load();
};

// 弹窗状态
const dialogVisible = ref(false);
const dialogType = ref('add'); // 'add' 或 'edit'

// 表单数据
const formRef = ref(null);
const formData = reactive({
  id: 0,
  username: '',
  password: '',
  nickname: '',
  email: '',
  role: '',
  user_pic:''
});

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 5, max: 16, message: '用户名长度在5-16个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]{5,16}$/, message: '用户名只能包含英文字母和数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 16, message: '密码长度在5-16个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在2-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
};

// 文件上传
const uploadRef = ref(null);
const fileList = ref([]);
const selectedFile = ref(null);

// 计算属性：动态生成弹窗标题
const dialogTitle = computed(() => {
  return dialogType.value === 'edit' ? '编辑用户' : '新增用户';
});

// 页面加载时获取数据
onMounted(() => {
  load();
  const tokenRaw = localStorage.getItem('xm-pro-user');
  if (tokenRaw) {
    try {
      const claims = jwtDecode(tokenRaw)?.claims || {};
      isSuperAdmin.value = claims.role === 'SUPER_ADMIN';
      currentUserId.value = claims.id; // 保存当前用户ID
      console.log(claims)
    } catch (e) {
      isSuperAdmin.value = false;
    }
  }
});
const token=localStorage.getItem('xm-pro-user');
// 查询数据
const load = () => {
  axios.post("/user/all", searchForm, {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize
    },
    headers: {
      'Authorization': token  // 添加认证头
    }
  })
      .then(res => {
        if (res.code === 0) {
          data.tableData = res.data.list.map(u => ({
            ...u,
            user_pic: u.user_pic || u.userPic || '', // 兼容后端驼峰
            createTime: u.createTime || u.create_time,
            updateTime: u.updateTime || u.update_time
          }));
          data.total = res.data.total;
        } else {
          ElMessage.error("查询失败：" + res.message);

        }
      })
      .catch(err => {
        console.error("查询接口异常：", err);
        ElMessage.error("网络错误，请稍后重试");

      });
};

// 重置表单
const resetForm = () => {
  // 重置表单数据
  Object.assign(formData, {
    id: 0,
    username: '',
    password: '',
    nickname: '',
    email: '',
    role: 'USER',
    user_pic: ''
  });
  // 清空文件列表
  fileList.value = [];
  selectedFile.value = null;
  // 重置表单验证状态
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

// 打开新增弹窗
const openAddDialog = () => {
  resetForm();
  dialogType.value = 'add';
  dialogVisible.value = true;
};

// 打开编辑弹窗
const openEditDialog = (row) => {
  dialogType.value = 'edit';
  // 深拷贝原数据，避免污染
  Object.assign(formData, JSON.parse(JSON.stringify(row)));
  formData.user_pic = row.user_pic || row.userPic || '';
  
  // 判断是否正在编辑自己
  isEditingSelf.value = (row.id === currentUserId.value);
  
  // 清空现有文件列表
  fileList.value = [];
  // 回显头像
  if (formData.user_pic) {
    const fileName = formData.user_pic.split('/').pop();
    fileList.value = [
      {
        name: fileName,
        url: formData.user_pic,
        raw: null,
        status: 'success'
      }
    ];
  }
  dialogVisible.value = true;
};

// 关闭弹窗时的处理
const handleDialogClose = () => {
  resetForm();
  isEditingSelf.value = false; // 重置编辑自己的标志
  dialogVisible.value = false;
};

// 提交表单
const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 构建表单数据
      const formDataToSubmit = new FormData();
      
      if (dialogType.value === 'edit') {
        // 编辑模式：传递用户ID
        formDataToSubmit.append('id', formData.id);
      }
      
      formDataToSubmit.append('username', formData.username);
      
      // 处理密码（仅新增时需要）
      if (dialogType.value === 'add') {
        formDataToSubmit.append('password', formData.password);
      }
      
      formDataToSubmit.append('nickname', formData.nickname);
      formDataToSubmit.append('email', formData.email);
      formDataToSubmit.append('role', formData.role);

      // 处理头像
      if (fileList.value.length > 0 && fileList.value[0].raw) {
        // 有新选择的文件
        formDataToSubmit.append('user_pic', fileList.value[0].raw);
      }

      // 发送请求
      const url = dialogType.value === 'add' ? '/user/admin/add' : '/user/admin/update';
      axios.post(url, formDataToSubmit, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': token  // 添加认证头
        }
      })
          .then(res => {
            if (res.code === 0 || res.code === 200) {
              load();
              ElMessage.success(dialogType.value === 'add' ? "新增成功" : "编辑成功");
              dialogVisible.value = false;
            } else {
              ElMessage.error(res.msg || res.message || (dialogType.value === 'add' ? '新增失败' : '编辑失败'));
            }
          })
          .catch(err => {
            console.error(`${dialogType.value === 'add' ? '新增' : '编辑'}失败：`, err);
            ElMessage.error('网络错误，请稍后重试');
          });
    } else {
      ElMessage.error('请完善必填信息');
      return false;
    }
  });
};

// 删除单个用户
const deleteUser = (id) => {
  ElMessageBox.confirm(
      '你确定要删除该用户吗?', '确定删除', { type: 'warning' }
  )
      .then(() => {
        axios.delete(`/user/delete/${id}`)
            .then(res => {
              if (res.code === 0 || res.code === 200) {
                load();
                ElMessage.success("删除成功");
              } else {
                ElMessage.error(`删除失败：${res.msg || res.message || '未知错误'}`);
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
    ElMessage.warning("请选择用户");
    return;
  }
  ElMessageBox.confirm(
      '你确定要删除选中的用户吗?', '确定删除', { type: 'warning' }
  )
      .then(() => {
        axios.delete('/user/delete/batch', { data: data.ids })
            .then(res => {
              if (res.code === 0 || res.code === 200) {
                load();
                ElMessage.success("删除成功");
              } else {
                ElMessage.error(`删除失败：${res.msg || res.message || '未知错误'}`);
              }
            });
      })
      .catch(() => {
        ElMessage({ type: 'info', message: '已取消操作' });
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

// 文件上传相关处理
const handleFileExceed = (files, fileList) => {
  if (fileList.length >= 1) {
    ElMessage.error('仅可上传1个文件，请先删除已选文件再重新选择！');
  }
  fileList.splice(1);
};

const handleFileChange = (uploadFile, uploadFiles) => {
  const file = uploadFile.raw;
  if (!file) return;

  const fileSizeMB = (file.size / (1024 * 1024)).toFixed(2);
  const isImage = file.type.startsWith('image/');

  // 检查是否为图片类型
  if (!isImage) {
    ElMessage.error('只能上传图片文件！');
    fileList.value = [];
    selectedFile.value = null;
    return;
  }

  // 检查文件大小
  if (fileSizeMB > 5) {
    ElMessage.error(`图片文件不能超过 5MB，当前 ${fileSizeMB}MB`);
    fileList.value = [];
    selectedFile.value = null;
    return;
  }

  // 检查文件格式
  const fileName = uploadFile.name;
  const suffix = fileName.split('.').pop()?.toLowerCase();
  const allowedSuffix = ['jpg', 'jpeg', 'png', 'gif', 'bmp'];

  if (!allowedSuffix.includes(suffix)) {
    ElMessage.error(`不支持 .${suffix} 格式，请上传 JPG/PNG/GIF/BMP 图片文件！`);
    fileList.value = [];
    selectedFile.value = null;
    return;
  }

  // 保留最后一个文件
  fileList.value = uploadFiles.slice(-1);
  selectedFile.value = uploadFile.raw;
};

const handleFileRemove = () => {
  selectedFile.value = null;
  // 如果是编辑模式，清空原头像路径
  if (dialogType.value === 'edit') {
    formData.user_pic = '';
  }
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
.no-avatar {
  font-size: 40px;
  color: #909399;
}
.role-tip {
  margin-top: 4px;
  font-size: 12px;
}
</style>