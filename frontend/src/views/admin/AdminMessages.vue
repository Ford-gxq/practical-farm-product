<template>
  <div>
    <div class="toolbar">
      <div>
        <h1>留言管理</h1>
        <p class="page-tip">微信小程序“联系站长”提交的留言会保存到 message_contents 表，并在这里展示。</p>
      </div>
      <el-button type="primary" @click="loadData">刷新</el-button>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            clearable
            placeholder="姓名 / 电话 / 留言内容"
            style="width: 260px"
            @keyup.enter="search"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 140px">
            <el-option label="未处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="records" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="110" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="content" label="留言内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="source" label="来源" width="150">
          <template #default="{ row }">
            <el-tag type="success" v-if="row.source === 'wechat_mini_program'">微信小程序</el-tag>
            <el-tag v-else>{{ row.source || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="处理状态" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">未处理</el-tag>
            <el-tag v-else type="success">已处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="留言时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">查看/处理</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-sizes="[10, 20, 50]"
          v-model:current-page="query.pageNo"
          v-model:page-size="query.pageSize"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="留言详情" width="620px">
      <el-descriptions :column="1" border v-if="current">
        <el-descriptions-item label="姓名">{{ current.name }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ current.phone }}</el-descriptions-item>
        <el-descriptions-item label="来源">
          {{ current.source === 'wechat_mini_program' ? '微信小程序' : current.source }}
        </el-descriptions-item>
        <el-descriptions-item label="留言时间">{{ current.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="留言内容">
          <div class="message-content">{{ current.content }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <el-form :model="handleForm" label-width="100px" class="handle-form">
        <el-form-item label="处理状态">
          <el-radio-group v-model="handleForm.status">
            <el-radio :value="0">未处理</el-radio>
            <el-radio :value="1">已处理</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="管理员备注">
          <el-input
            v-model="handleForm.adminRemark"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="例如：已电话联系，待补充商品图片。"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveHandle">保存处理结果</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteMessage, getAdminMessages, updateMessageStatus } from '../../api/admin'

const loading = ref(false)
const records = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const current = ref(null)

const query = reactive({
  pageNo: 1,
  pageSize: 10,
  keyword: '',
  status: null
})

const handleForm = reactive({
  status: 1,
  adminRemark: ''
})

async function loadData() {
  loading.value = true
  try {
    const res = await getAdminMessages(query)
    records.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function search() {
  query.pageNo = 1
  loadData()
}

function reset() {
  query.pageNo = 1
  query.keyword = ''
  query.status = null
  loadData()
}

function openDialog(row) {
  current.value = row
  handleForm.status = row.status
  handleForm.adminRemark = row.adminRemark || ''
  dialogVisible.value = true
}

async function saveHandle() {
  if (!current.value) return
  await updateMessageStatus(current.value.id, handleForm)
  ElMessage.success('处理结果已保存')
  dialogVisible.value = false
  loadData()
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除 ${row.name} 的留言吗？`, '删除确认', { type: 'warning' })
  await deleteMessage(row.id)
  ElMessage.success('留言已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-tip { margin: 4px 0 0; color: #6b7280; font-size: 14px; }
.filter-card { margin-bottom: 16px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
.message-content { white-space: pre-wrap; line-height: 1.8; color: #374151; }
.handle-form { margin-top: 22px; }
</style>
