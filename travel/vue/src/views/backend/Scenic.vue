<template>
  <div class="scenic-list-container">
    <div class="page-header">
      <h1 class="page-title">景点管理</h1>
      <p class="page-subtitle">Scenic Spot Management</p>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <div class="action-right">
        <el-button type="primary" @click="handleAdd" class="add-btn">
          <i class="el-icon-plus"></i> 新增景点
        </el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="名称">
          <el-input v-model="searchForm.name" placeholder="请输入景点名称" clearable>
            <template #prefix>
              <i class="el-icon-search"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="地区">
          <el-select
              v-model="searchForm.location"
              :options="regionOptions"
              placeholder="请选择地区"
              clearable
              filterable
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option
                v-for="item in categoryOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" class="search-btn">
            <i class="el-icon-search"></i> 搜索
          </el-button>
          <el-button @click="resetSearch" class="reset-btn">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="never">
      <el-table
          v-loading="loading"
          :data="tableData"
          border
          stripe
          style="width: 100%"
          class="scenic-table"
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="名称" width="150">
          <template #default="scope">
            <div class="scenic-name">{{ scope.row.name }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="location" label="地区" width="230" align="center">
          <template #default="scope">
            <el-tag size="small" effect="plain" class="location-tag">
              <i class="el-icon-location"></i> {{ scope.row.location }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="120" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.categoryInfo" size="small" type="success" effect="plain">
              {{ scope.row.categoryInfo.name }}
            </el-tag>
            <el-tag v-else size="small" type="info" effect="plain">未分类</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="票价" width="100" align="center">
          <template #default="scope">
            <span class="price-tag">¥ {{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="openingHours" label="开放时间" width="150" align="center">
          <template #default="scope">
            <span class="time-tag">
              <i class="el-icon-time"></i> {{ scope.row.openingHours }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="imageUrl" label="图片" width="120" align="center">
          <template #default="scope">
            <el-image
                :src="baseAPI + scope.row.imageUrl"
                style="width: 80px; height: 60px; border-radius: 4px"
                fit="cover"
                :preview-teleported="true"
                v-if="scope.row.imageUrl"
                :preview-src-list="[baseAPI + scope.row.imageUrl]"
            >
              <template #error>
                <div class="image-error">
                  <i class="el-icon-picture"></i>
                </div>
              </template>
            </el-image>
            <div v-else class="no-image">
              <i class="el-icon-picture-outline"></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间"  align="center">
          <template #default="scope">
            <span class="date-text">{{ formatDate(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="handleEdit(scope.row)" class="edit-btn">
              <i class="el-icon-edit"></i> 编辑
            </el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(scope.row)" class="delete-btn">
              <i class="el-icon-delete"></i> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :current-page="currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 景点表单对话框 -->
    <el-dialog
        :title="dialogTitle"
        v-model="dialogVisible"
        width="600px"
        @close="resetForm"
        class="scenic-dialog"
    >
      <el-form
          ref="scenicFormRef"
          :model="scenicForm"
          :rules="scenicFormRules"
          label-width="100px"
          class="scenic-form"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="scenicForm.name" placeholder="请输入景点名称"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="scenicForm.description" type="textarea" rows="4" placeholder="请输入描述"></el-input>
        </el-form-item>
        <el-form-item label="地区" prop="location">
          <el-select
              v-model="scenicForm.location"
              :options="regionOptions"
              placeholder="请选择地区"
              clearable
              filterable
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="scenicForm.categoryId" placeholder="请选择分类" clearable style="width: 100%">
            <el-option
                v-for="item in categoryOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="票价" prop="price">
          <el-input v-model="scenicForm.price" type="number" placeholder="请输入票价">
            <template #prefix>
              <span class="price-prefix">¥</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="开放时间" prop="openingHours">
          <el-input v-model="scenicForm.openingHours" placeholder="请输入开放时间">
            <template #prefix>
              <i class="el-icon-time"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="景点图片">
          <div class="image-list-uploader">
            <div class="image-list" v-if="imageList.length > 0">
              <draggable
                  v-model="imageList"
                  class="image-grid"
                  item-key="url"
                  :animation="200"
              >
                <template #item="{element, index}">
                  <div class="image-item">
                    <img :src="getImageUrl(element.url)" class="image-preview" />
                    <div class="image-overlay">
                      <el-icon class="icon-btn" @click="setMainImage(index)" v-if="!element.isMain" title="设为主图">
                        <Star />
                      </el-icon>
                      <el-icon class="icon-btn" @click="removeImageFromList(index)" title="删除图片">
                        <Delete />
                      </el-icon>
                    </div>
                    <div class="main-badge" v-if="element.isMain">主图</div>
                  </div>
                </template>
              </draggable>
            </div>

            <el-upload
                class="image-uploader"
                action="#"
                :auto-upload="true"
                :show-file-list="false"
                :http-request="customUploadImage"
                :before-upload="beforeImageUpload"
                v-if="imageList.length < 5"
            >
              <div class="upload-box">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div class="upload-text">上传图片</div>
              </div>
            </el-upload>
          </div>
          <div class="upload-tip">
            • 支持上传最多5张图片，可拖拽排序<br>
            • 点击星标设置主图，主图将显示在列表页<br>
            • 建议图片尺寸：1920x1080，大小不超过2MB
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Plus, Star } from '@element-plus/icons-vue'
import draggable from 'vuedraggable'
import request from '@/utils/request'
import { formatDate } from '@/utils/dateUtils'
import ChinaRegionData from '@/assets/中国地区数据.json'

const baseAPI = import.meta.env.VITE_API_BASE_URL || '/api'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const categoryOptions = ref([])
const allCategories = ref([])

const imageList = ref([])

const formatRegionData = () => {
  return ChinaRegionData.map(province => ({
    value: province.province,
    label: province.province
  }))
}

const regionOptions = ref(formatRegionData())

const searchForm = reactive({
  name: '',
  location: '',
  categoryId: null
})

const scenicFormRef = ref(null)
const dialogVisible = ref(false)
const dialogType = ref('add')
const dialogTitle = ref('新增景点')
const submitLoading = ref(false)

const scenicForm = reactive({
  id: null,
  name: '',
  description: '',
  location: '',
  categoryId: null,
  price: '',
  openingHours: '',
  imageUrl: ''
})

const scenicFormRules = {
  name: [
    { required: true, message: '请输入景点名称', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入票价', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ]
}

onMounted(() => {
  fetchCategories()
  fetchScenicSpots()
})

const fetchScenicSpots = async () => {
  loading.value = true
  try {
    const params = {
      name: searchForm.name,
      location: searchForm.location,
      categoryId: searchForm.categoryId,
      currentPage: currentPage.value,
      size: pageSize.value
    }
    await request.get('/scenic/page', params, {
      onSuccess: (res) => {
        const records = res.records || []
        records.forEach(item => {
          if (item.categoryId != null) {
            item.categoryId = Number(item.categoryId)
          }
          if (item.categoryId) {
            const category = allCategories.value.find(c => Number(c.id) === Number(item.categoryId))
            if (category) {
              item.categoryInfo = category
            }
          }
        })
        tableData.value = records
        total.value = res.total || 0
      }
    })
  } catch (error) {
    console.error('获取景点列表失败:', error)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchScenicSpots()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchScenicSpots()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchScenicSpots()
}

const handleAdd = () => {
  dialogType.value = 'add'
  dialogTitle.value = '新增景点'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogTitle.value = '编辑景点'
  Object.keys(scenicForm).forEach(key => {
    if (key in row) {
      scenicForm[key] = row[key]
    }
  })

  imageList.value = parseImageList(row.imageList)
  if (imageList.value.length === 0 && row.imageUrl) {
    imageList.value = [{ url: row.imageUrl, isMain: true }]
  }

  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该景点吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/scenic/delete/${row.id}`, {
        successMsg: '删除成功'
      })
      fetchScenicSpots()
    } catch (error) {
      console.error('删除景点失败:', error)
    }
  }).catch(() => {})
}

const submitForm = () => {
  scenicFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const formData = { ...scenicForm }

        formData.imageList = JSON.stringify(imageList.value)

        if (dialogType.value === 'add') {
          await request.post('/scenic/add', formData, {
            successMsg: '添加景点成功'
          })
        } else {
          await request.put(`/scenic/${formData.id}`, formData, {
            successMsg: '更新景点成功'
          })
        }
        dialogVisible.value = false
        fetchScenicSpots()
      } catch (error) {
        console.error('提交表单失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const resetForm = () => {
  if (scenicFormRef.value) {
    scenicFormRef.value.resetFields()
  }
  Object.keys(scenicForm).forEach(key => {
    if (key === 'id' || key === 'categoryId') {
      scenicForm[key] = null
    } else {
      scenicForm[key] = ''
    }
  })
  imageList.value = []
}

const getImageUrl = (url) => {
  if (!url) return ''
  return url.startsWith('http') ? url : baseAPI + url
}

const parseImageList = (imageListStr) => {
  if (!imageListStr) return []
  try {
    const list = JSON.parse(imageListStr)
    return Array.isArray(list) ? list : []
  } catch (e) {
    console.error('解析图片列表失败:', e)
    return []
  }
}

const setMainImage = (index) => {
  imageList.value.forEach(img => img.isMain = false)
  imageList.value[index].isMain = true
  scenicForm.imageUrl = imageList.value[index].url
}

const removeImageFromList = (index) => {
  const removedImage = imageList.value[index]
  imageList.value.splice(index, 1)

  if (removedImage.isMain && imageList.value.length > 0) {
    imageList.value[0].isMain = true
    scenicForm.imageUrl = imageList.value[0].url
  } else if (imageList.value.length === 0) {
    scenicForm.imageUrl = ''
  }
}

const beforeImageUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isJPG && !isPNG) {
    ElMessage.error('图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const customUploadImage = async (options) => {
  try {
    const { file } = options
    const formData = new FormData()
    formData.append('file', file)
    await request.post('/file/upload/img', formData, {
      headers: {
        token: localStorage.getItem('token') || ''
      },
      transformRequest: [(data) => data],
      successMsg: '图片上传成功',
      errorMsg: '图片上传失败',
      onSuccess: (data) => {
        const newImage = {
          url: data,
          isMain: imageList.value.length === 0
        }
        imageList.value.push(newImage)

        if (newImage.isMain) {
          scenicForm.imageUrl = data
        }

        options.onSuccess({ data })
      },
      onError: (error) => {
        options.onError(new Error(error.message || '上传失败'))
      }
    })
  } catch (error) {
    options.onError(error)
    console.error('图片上传过程发生错误:', error)
  }
}

const fetchCategories = async () => {
  try {
    await request.get('/scenic-category/tree', {}, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        categoryOptions.value = res || []
        allCategories.value = flattenCategories(res || [])
      }
    })
  } catch (error) {
    console.error('获取分类列表失败:', error)
    categoryOptions.value = []
    allCategories.value = []
  }
}

const flattenCategories = (categories) => {
  let result = []
  categories.forEach(category => {
    if (category.id != null) {
      category.id = Number(category.id)
    }
    result.push(category)
    if (category.children && category.children.length > 0) {
      result = result.concat(flattenCategories(category.children))
    }
  })
  return result
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    if (key === 'categoryId') {
      searchForm[key] = null
    } else {
      searchForm[key] = ''
    }
  })
  currentPage.value = 1
  fetchScenicSpots()
}
</script>

<style lang="scss" scoped>
.scenic-list-container {
  padding: 20px;
  background-color: #f9fafc;
  min-height: calc(100vh - 120px);

  .page-header {
    margin-bottom: 24px;
    text-align: left;

    .page-title {
      font-size: 24px;
      color: #34495e;
      margin: 0 0 8px 0;
      font-weight: 500;
    }

    .page-subtitle {
      font-size: 14px;
      color: #7f8c8d;
      margin: 0;
      font-style: italic;
    }
  }

  .action-bar {
    margin-bottom: 20px;
    display: flex;
    justify-content: flex-end;
    align-items: center;

    .action-right {
      display: flex;
      justify-content: flex-end;

      .add-btn {
        background-color: #2ecc71;
        border-color: #2ecc71;

        &:hover, &:focus {
          background-color: #27ae60;
          border-color: #27ae60;
        }
      }
    }
  }

  .search-card {
    margin-bottom: 20px;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: none;
  }

  .search-form {
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .el-form-item {
      margin-bottom: 10px;
      margin-right: 16px;
    }

    .search-btn {
      background-color: #3498db;
      border-color: #3498db;

      &:hover, &:focus {
        background-color: #2980b9;
        border-color: #2980b9;
      }
    }

    .reset-btn {
      color: #7f8c8d;
      border-color: #bdc3c7;

      &:hover, &:focus {
        color: #34495e;
        border-color: #95a5a6;
        background-color: #f5f5f5;
      }
    }
  }

  .table-card {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: none;

    .scenic-table {
      border-radius: 4px;
      overflow: hidden;

      :deep(thead) {
        background-color: #ecf0f1;

        th {
          background-color: #ecf0f1;
          color: #34495e;
          font-weight: 500;
          padding: 12px 0;
        }
      }

      :deep(tbody tr) {
        transition: all 0.3s;

        &:hover {
          background-color: #f8f9fa;
        }
      }

      .scenic-name {
        font-weight: 500;
        color: #2980b9;
      }

      .location-tag {
        background-color: #e8f4fd;
        color: #3498db;
        border-color: #3498db;
      }

      .price-tag {
        color: #e74c3c;
        font-weight: 500;
      }

      .time-tag {
        color: #7f8c8d;
      }

      .date-text {
        color: #7f8c8d;
        font-size: 12px;
      }

      .edit-btn {
        padding: 5px 12px;
        margin-right: 8px;
      }

      .delete-btn {
        padding: 5px 12px;
      }

      .image-error, .no-image {
        width: 80px;
        height: 60px;
        background-color: #ecf0f1;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #95a5a6;
        border-radius: 4px;

        i {
          font-size: 24px;
        }
      }
    }
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    padding: 0 20px;
  }

  .scenic-dialog {
    :deep(.el-dialog__header) {
      border-bottom: 1px solid #ecf0f1;
      padding: 20px;

      .el-dialog__title {
        font-size: 18px;
        color: #34495e;
        font-weight: 500;
      }
    }

    :deep(.el-dialog__body) {
      padding: 30px 20px;
    }

    :deep(.el-dialog__footer) {
      border-top: 1px solid #ecf0f1;
      padding: 15px 20px;
    }

    .scenic-form {
      .price-prefix {
        color: #e74c3c;
        font-weight: bold;
      }
    }
  }

  .image-list-uploader {
    width: 100%;

    .image-list {
      margin-bottom: 12px;
    }

    .image-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 12px;
      margin-bottom: 12px;
    }

    .image-item {
      position: relative;
      aspect-ratio: 4/3;
      border-radius: 8px;
      overflow: hidden;
      border: 1px solid #dcdfe6;
      cursor: move;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);

        .image-overlay {
          opacity: 1;
        }
      }

      .image-preview {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .image-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 16px;
        opacity: 0;
        transition: opacity 0.3s;

        .icon-btn {
          font-size: 20px;
          color: white;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            transform: scale(1.2);
            color: #409eff;
          }
        }
      }

      .main-badge {
        position: absolute;
        top: 8px;
        left: 8px;
        background: linear-gradient(45deg, #409eff, #66b1ff);
        color: white;
        padding: 4px 10px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 500;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      }
    }

    .image-uploader {
      .upload-box {
        width: 150px;
        height: 112px;
        border: 2px dashed #d9d9d9;
        border-radius: 8px;
        cursor: pointer;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        transition: all 0.3s;
        background-color: #fafafa;

        &:hover {
          border-color: #409eff;
          background-color: #f0f7ff;

          .upload-icon {
            color: #409eff;
          }
        }

        .upload-icon {
          font-size: 28px;
          color: #8c939d;
          margin-bottom: 8px;
          transition: color 0.3s;
        }

        .upload-text {
          font-size: 14px;
          color: #8c939d;
        }
      }
    }
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
    line-height: 1.6;
    margin-top: 8px;
  }
}
</style>