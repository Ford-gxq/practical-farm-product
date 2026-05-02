<template>
  <div>
    <div class="toolbar"><h1>商品分类管理</h1><el-button type="primary" @click="openDialog()">新增商品分类</el-button></div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商品分类名称" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" title="商品分类表单" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteCategory, getAdminCategories, saveCategory } from '../../api/admin'

const list = ref([])
const visible = ref(false)
const form = reactive({ id: null, name: '', sortOrder: 0 })
async function load() { list.value = await getAdminCategories() }
function openDialog(row) { Object.assign(form, row || { id: null, name: '', sortOrder: 0 }); visible.value = true }
async function submit() { await saveCategory(form); ElMessage.success('保存成功'); visible.value = false; load() }
async function remove(id) { await ElMessageBox.confirm('确认删除该分类？'); await deleteCategory(id); ElMessage.success('删除成功'); load() }
onMounted(load)
</script>
