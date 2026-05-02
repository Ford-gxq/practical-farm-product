<template>
  <div>
    <div class="toolbar">
      <div>
        <h1>封面图片管理</h1>
        <p class="page-tip">
          建议图片尺寸：<strong>800 × 800 px</strong>。
          上传图片不能超过 <strong>1000 × 1000 px</strong>。
          图片会保存到 <strong>/asset/images/covers/</strong> 目录。
        </p>
      </div>

      <el-upload
        :show-file-list="false"
        :before-upload="beforeUpload"
        :http-request="handleUpload"
        accept="image/*"
      >
        <el-button type="primary">上传封面图片</el-button>
      </el-upload>
    </div>

    <el-alert
      title="图片使用说明"
      type="info"
      show-icon
      :closable="false"
      class="image-alert"
    >
      <template #default>
        商品封面推荐使用正方形图片。上传成功后，内容管理页面可以直接选择这些图片作为封面。
      </template>
    </el-alert>

    <div class="image-grid">
      <div
        v-for="img in images"
        :key="img.path"
        class="image-card"
      >
        <div class="image-box">
          <img :src="img.path" alt="封面图片" />
        </div>

        <div class="image-info">
          <div class="image-name" :title="img.name">
            {{ img.name }}
          </div>

          <div class="image-path" :title="img.path">
            {{ img.path }}
          </div>

          <div class="image-actions">
            <el-button size="small" @click="copyPath(img.path)">
              复制路径
            </el-button>

            <el-button
              size="small"
              type="danger"
              @click="removeImage(img.name)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <el-empty
      v-if="images.length === 0"
      description="暂无封面图片，请先上传"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteImage, getImageList, uploadImage } from '../../api/admin'

const images = ref([])

async function loadImages() {
  images.value = await getImageList('cover')
}

/**
 * 上传前校验：
 * 1. 必须是图片
 * 2. 图片尺寸不能超过 1000x1000
 *
 * 注意：
 * 这里是前端校验，后端也会再次校验，避免绕过前端限制。
 */
function beforeUpload(file) {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return false
  }

  return new Promise((resolve, reject) => {
    const img = new Image()
    const url = URL.createObjectURL(file)

    img.onload = () => {
      URL.revokeObjectURL(url)

      const width = img.width
      const height = img.height

      if (width > 1000 || height > 1000) {
        ElMessage.error(`上传失败：图片尺寸不能超过 1000 × 1000 px，当前图片为 ${width} × ${height} px`)
        reject(false)
        return
      }

      resolve(true)
    }

    img.onerror = () => {
      URL.revokeObjectURL(url)
      ElMessage.error('图片读取失败')
      reject(false)
    }

    img.src = url
  })
}

async function handleUpload(option) {
  try {
    await uploadImage(option.file, 'cover')
    ElMessage.success('上传成功')
    await loadImages()
  } catch (error) {
    console.error('上传失败：', error)
    ElMessage.error(error?.message || '上传失败')
  }
}

async function removeImage(name) {
  await ElMessageBox.confirm(
    `确认删除图片 ${name}？如果商品正在使用这张图片，删除后页面将无法显示。`,
    '删除确认',
    {
      type: 'warning'
    }
  )

  await deleteImage(name, 'cover')
  ElMessage.success('删除成功')
  await loadImages()
}

async function copyPath(path) {
  await navigator.clipboard.writeText(path)
  ElMessage.success('图片路径已复制')
}

onMounted(() => {
  loadImages()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 18px;
}

.toolbar h1 {
  margin: 0 0 8px;
}

.page-tip {
  margin: 0;
  color: #6b7280;
  line-height: 1.8;
}

.page-tip strong {
  color: #16a34a;
}

.image-alert {
  margin-bottom: 18px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 18px;
}

.image-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.image-box {
  width: 100%;
  height: 180px;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-info {
  padding: 12px;
}

.image-name {
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 6px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.image-path {
  font-size: 12px;
  color: #6b7280;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.image-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

@media (max-width: 1200px) {
  .image-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>