<template>
  <div class="container">
    <div class="title">
      <h2>公告管理</h2>
    </div>
    
    <div class="top-box">
      <div class="left">
        <el-button type="primary" @click="handleAdd">新增公告</el-button>
        <el-button type="danger" @click="handleDelete()" :disabled="selectedIds.length === 0">批量删除</el-button>
      </div>
      <div class="right">
        <el-input
          v-model="searchForm.title"
          placeholder="请输入公告标题"
          style="width: 300px"
          clearable
          @keyup.enter="load"
        >
          <template #append>
            <el-button @click="load">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <div class="table-box">
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="公告标题" min-width="200" />
        <el-table-column prop="content" label="公告内容" min-width="300" :show-overflow-tooltip="true" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-box">
      <el-pagination
        v-model:current-page="pageForm.currentPage"
        v-model:page-size="pageForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="load"
        @current-change="load"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增公告' : '编辑公告'"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="5"
            placeholder="请输入公告内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/axios.js'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const selectedIds = ref([])

// 搜索表单
const searchForm = reactive({
  title: ''
})

// 分页表单
const pageForm = reactive({
  currentPage: 1,
  pageSize: 10
})

// 表单数据
const formData = reactive({
  id: null,
  title: '',
  content: ''
})

// 表单规则
const formRules = reactive({
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
})

// 表格数据
const tableData = ref([])
const total = ref(0)

// 加载数据
const load = async () => {
  try {
    loading.value = true
    const params = {
      title: searchForm.title,
      pageNum: pageForm.currentPage,
      pageSize: pageForm.pageSize
    }
    const res = await axios.get('/notices', { params })
    if (res.code === 0) {
      tableData.value = res.data.list
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  formData.id = null
  formData.title = ''
  formData.content = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  formData.id = row.id
  formData.title = row.title
  formData.content = row.content
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  try {
    let res
    if (dialogType.value === 'add') {
      res = await axios.post('/notices', formData)
    } else {
      res = await axios.put(`/notices/${formData.id}`, formData)
    }
    if (res.code === 0 || res.code === 200) {
      ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
      dialogVisible.value = false
      load()
    } else {
      ElMessage.error(res.message || (dialogType.value === 'add' ? '新增失败' : '编辑失败'))
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
  }
}

// 删除
const handleDelete = async (id) => {
  // 如果id是数字，说明是单个删除；否则是批量删除
  const ids = (typeof id === 'number') ? [id] : selectedIds.value
  if (ids.length === 0) {
    ElMessage.warning('请选择要删除的公告')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要删除选中的公告吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 确保ids是数字数组，直接转换为字符串
    const idStr = ids.filter(item => item != null).join(',')
    
    const res = await axios.delete(`/notices/${idStr}`)
    if (res.code === 0 || res.code === 200) {
      ElMessage.success('删除成功')
      load()
      selectedIds.value = []
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('网络异常，请稍后重试')
    }
  }
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title {
  margin-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 10px;
}

.title h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.top-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.table-box {
  margin-bottom: 20px;
  overflow-x: auto;
}

.pagination-box {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}
</style>