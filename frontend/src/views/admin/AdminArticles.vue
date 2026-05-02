<template>
  <div>
    <!-- 页面顶部工具栏：左侧标题，右侧新增按钮 -->
    <div class="toolbar">
      <h1>内容管理</h1>
      <el-button type="primary" @click="openDialog()">新增内容</el-button>
    </div>

    <!-- 搜索区：按内容类型和关键词筛选 -->
    <div class="search-row">
      <el-select v-model="query.articleType" style="width: 160px" @change="load">
        <el-option label="全部内容" value="" />
        <el-option label="农产品商品" value="product" />
        <el-option label="乡村故事" value="story" />
      </el-select>

      <el-input
        v-model="query.keyword"
        placeholder="搜索标题/简介"
        style="width: 280px"
        @keyup.enter="load"
      />

      <el-button @click="load">搜索</el-button>
    </div>

    <!-- 内容列表：商品和乡村故事共用一张表，使用 articleType 区分 -->
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="70" />

      <el-table-column label="类型" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.articleType === 'story' ? 'warning' : 'success'">
            {{ scope.row.articleType === 'story' ? '乡村故事' : '商品' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="封面图" width="110">
        <template #default="scope">
          <img
            v-if="scope.row.coverImage"
            :src="scope.row.coverImage"
            class="admin-product-img"
            alt="封面图"
          />
          <span v-else>无图</span>
        </template>
      </el-table-column>

      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="categoryName" label="分类" width="120" />

      <el-table-column label="价格" width="130">
        <template #default="scope">
          <span v-if="scope.row.articleType === 'product' && scope.row.price">
            {{ scope.row.price }} {{ scope.row.unit }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>

      <el-table-column prop="originPlace" label="产地/地点" width="150" />

      <el-table-column label="推荐" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.recommended === 1 ? 'success' : 'info'">
            {{ scope.row.recommended === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
            {{ scope.row.status === 1 ? '发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="viewCount" label="浏览" width="90" />

      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 16px"
      layout="prev, pager, next, total"
      :total="total"
      :page-size="query.pageSize"
      @current-change="p => { query.pageNo = p; load() }"
    />

    <!-- 内容表单弹窗 -->
    <el-dialog v-model="visible" title="内容表单" width="1100px" top="4vh">
      <el-form :model="form" label-width="110px">
        <!-- 内容类型决定表单展示商品字段还是故事字段 -->
        <el-form-item label="内容类型">
          <el-radio-group v-model="form.articleType">
            <el-radio label="product">农产品商品</el-radio>
            <el-radio label="story">乡村故事文章</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="form.articleType === 'story' ? '文章标题' : '商品名称'">
              <el-input v-model="form.title" placeholder="请输入标题" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" style="width: 100%" placeholder="请选择分类">
                <el-option
                  v-for="c in categories"
                  :key="c.id"
                  :label="c.name"
                  :value="c.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="封面图片">
          <div class="cover-select-row">
            <el-select
              v-model="form.coverImage"
              placeholder="请选择封面图片"
              filterable
              clearable
              style="width: 460px"
            >
              <el-option
                v-for="img in imageOptions"
                :key="img.path"
                :label="img.name"
                :value="img.path"
              >
                <div class="cover-option">
                  <img :src="img.path" alt="封面" />
                  <span>{{ img.name }}</span>
                  <em>{{ img.path }}</em>
                </div>
              </el-option>
            </el-select>

            <img
              v-if="form.coverImage"
              :src="form.coverImage"
              class="cover-mini-preview"
              alt="封面预览"
            />
          </div>

          <div class="form-tip">
            如需新增图片，请到左侧菜单「封面图片」页面上传。推荐尺寸：800 × 800 px；超过 1000 × 1000 px 会上传失败。
          </div>
        </el-form-item>

        <el-form-item :label="form.articleType === 'story' ? '文章简介' : '商品简介'">
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入简介，前台列表会展示这段内容"
          />
        </el-form-item>

        <!-- 商品才需要价格、单位、成交文案 -->
        <template v-if="form.articleType === 'product'">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="价格">
                <el-input-number
                  v-model="form.price"
                  :precision="2"
                  :min="0"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="单位">
                <el-input v-model="form.unit" placeholder="例如：元/斤、元/瓶、元/桶" />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="成交文案">
                <el-input v-model="form.salesText" placeholder="例如：成交17.8万元" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="农户/作者">
              <el-input v-model="form.farmerName" placeholder="例如：王大叔果园" />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="联系电话">
              <el-input v-model="form.farmerPhone" placeholder="例如：138xxxx8888" />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="产地/地点">
              <el-input v-model="form.originPlace" placeholder="例如：中宁县某某村" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="标签">
          <el-input
            v-model="form.productTags"
            placeholder="多个标签用英文逗号分隔，例如：源头直发,现摘现发"
          />
        </el-form-item>

        <el-form-item label="时令推荐" v-if="form.articleType === 'product'">
          <el-radio-group v-model="form.recommended">
            <el-radio :label="0">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 正文编辑器：直接展示 Markdown 编辑区域，不再显示“商品详情”那一行 -->
        <el-form-item label="发布正文">
          <div class="markdown-wrapper">
            <div class="markdown-header">
              <div class="markdown-title">正文支持 Markdown，可点击编辑器工具栏的图片按钮上传正文插图</div>
            </div>

            <div class="markdown-tip">
              手动写法示例：<code>![图片说明](/asset/images/content/图片名.jpg)</code>
            </div>

            <MdEditor
              v-model="form.content"
              @onUploadImg="handleMarkdownUpload"
              language="zh-CN"
              preview-theme="github"
              code-theme="atom"
              style="height: 560px"
            />
          </div>
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteArticle,
  getAdminArticles,
  getAdminCategories,
  getImageList,
  saveArticle,
  uploadImage
} from '../../api/admin'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

/** 列表数据。 */
const list = ref([])

/** 总条数，给分页组件使用。 */
const total = ref(0)

/** 分类下拉框数据。 */
const categories = ref([])

/** 封面图片下拉框数据，来自后端 /api/admin/images。 */
const imageOptions = ref([])

/** 控制新增/编辑弹窗显示。 */
const visible = ref(false)

/** 当前选择要插入正文的图片路径。 */
const selectedInsertImage = ref('')

/** 查询条件。 */
const query = reactive({
  pageNo: 1,
  pageSize: 10,
  keyword: '',
  articleType: ''
})

/**
 * 表单默认值。
 * 用函数返回新对象，避免多个新增操作共用同一个对象引用。
 */
function emptyForm() {
  return {
    id: null,
    articleType: 'product',
    title: '',
    categoryId: null,
    summary: '',
    coverImage: '',
    content: '',
    status: 0,
    price: null,
    unit: '元/斤',
    salesText: '',
    productTags: '',
    farmerName: '',
    farmerPhone: '',
    originPlace: '',
    recommended: 0
  }
}

/** 当前弹窗表单对象。 */
const form = reactive(emptyForm())

/** 加载内容列表。 */
async function load() {
  const params = { ...query }
  if (!params.articleType) {
    delete params.articleType
  }

  const page = await getAdminArticles(params)
  list.value = page.records || []
  total.value = page.total || 0
}

/** 加载分类。 */
async function loadCategories() {
  categories.value = await getAdminCategories()
}

/** 加载封面图片。 */
async function loadImages() {
  imageOptions.value = await getImageList('cover')
}

/** 打开新增或编辑弹窗。 */
function openDialog(row) {
  Object.assign(
    form,
    row || {
      ...emptyForm(),
      categoryId: categories.value[0]?.id
    }
  )

  selectedInsertImage.value = ''
  visible.value = true
}

/** 把选择的图片插入到 Markdown 正文末尾。 */
function insertImageToContent() {
  if (!selectedInsertImage.value) {
    ElMessage.warning('请先选择一张图片')
    return
  }

  form.content += `\n\n![图片说明](${selectedInsertImage.value})\n\n`
}

/**
 * Markdown 编辑器图片上传。
 * 用户点击编辑器工具栏的图片按钮时，图片会上传到 /asset/images/content/。
 */
async function handleMarkdownUpload(files, callback) {
  const urls = []
  for (const file of files) {
    const res = await uploadImage(file, "content")
    urls.push(res.path)
  }
  callback(urls)
}

/** 保存内容。 */
async function submit() {
  if (!form.title) {
    ElMessage.warning('请输入标题')
    return
  }

  if (!form.categoryId) {
    ElMessage.warning('请选择分类')
    return
  }

  if (!form.content) {
    ElMessage.warning('请输入正文内容')
    return
  }

  // 乡村故事不需要商品价格、成交量、时令推荐，保存前清空，避免脏数据。
  if (form.articleType === 'story') {
    form.price = null
    form.unit = ''
    form.salesText = ''
    form.recommended = 0
  }

  await saveArticle(form)
  ElMessage.success('保存成功')
  visible.value = false
  await load()
}

/** 删除内容，后端执行软删除。 */
async function remove(id) {
  await ElMessageBox.confirm('确认删除该内容？', '删除确认', { type: 'warning' })
  await deleteArticle(id)
  ElMessage.success('删除成功')
  await load()
}

onMounted(async () => {
  await loadCategories()
  await loadImages()
  await load()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.search-row {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.admin-product-img {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 6px;
  background: #f3f4f6;
}

.form-tip {
  color: #6b7280;
  font-size: 13px;
  line-height: 1.8;
  margin-top: 6px;
}

.cover-select-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.cover-mini-preview {
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  background: #f3f4f6;
}

.cover-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cover-option img {
  width: 32px;
  height: 32px;
  object-fit: cover;
  border-radius: 4px;
  background: #f3f4f6;
}

.cover-option span {
  width: 90px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.cover-option em {
  font-style: normal;
  color: #9ca3af;
  font-size: 12px;
}

.markdown-wrapper {
  width: 100%;
}

.markdown-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.markdown-title {
  font-size: 14px;
  font-weight: 700;
  color: #1f2937;
}

.markdown-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.markdown-tip {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 8px;
}

.markdown-tip code {
  background: #f3f4f6;
  padding: 2px 6px;
  border-radius: 4px;
  color: #dc2626;
}
</style>
