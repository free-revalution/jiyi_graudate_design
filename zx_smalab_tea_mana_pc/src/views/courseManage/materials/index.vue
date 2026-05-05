<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程资料</span>
          <el-upload :show-file-list="false" :http-request="(opts: any) => handleUpload(opts)" accept="video/*,.pdf,.ppt,.pptx,.doc,.docx">
            <el-button type="primary" icon="Upload">上传资料</el-button>
          </el-upload>
        </div>
      </template>

      <el-table :data="materialList" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="name" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.fileType?.startsWith('video/')" type="success">视频</el-tag>
            <el-tag v-else-if="row.fileType?.includes('pdf')" type="danger">PDF</el-tag>
            <el-tag v-else-if="row.fileType?.includes('image')" type="warning">图片</el-tag>
            <el-tag v-else>文档</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="大小" width="100" align="center">
          <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="createdTime" label="上传时间" width="180" align="center">
          <template #default="{ row }">{{ row.createdTime?.substring(0, 19) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="danger" link icon="Delete" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && materialList.length === 0" description="暂无资料" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { getMaterialList, uploadMaterial, deleteMaterial } from "@/api/modules/course";

const route = useRoute();
const courseId = Number(route.query.courseId) || 1;

const materialList = ref<any[]>([]);
const loading = ref(false);

const fetchList = async () => {
  if (!courseId) return;
  loading.value = true;
  try {
    const res: any = await getMaterialList(courseId);
    materialList.value = res.data || [];
  } catch {
    ElMessage.error("获取资料列表失败");
  } finally {
    loading.value = false;
  }
};

const handleUpload = async (opts: any) => {
  const formData = new FormData();
  formData.append("file", opts.file);
  try {
    await uploadMaterial(courseId, formData);
    ElMessage.success("上传成功");
    fetchList();
    opts.onSuccess({} as any);
  } catch (e: any) {
    ElMessage.error(e?.data?.msg || "上传失败");
    opts.onError(e as any);
  }
};

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm("确定删除该资料？", "提示", { type: "warning" });
    await deleteMaterial(courseId, id);
    ElMessage.success("删除成功");
    fetchList();
  } catch (e: any) {
    if (e !== "cancel") ElMessage.error(e?.data?.msg || "删除失败");
  }
};

const formatSize = (bytes: number) => {
  if (!bytes) return "0 B";
  const units = ["B", "KB", "MB", "GB"];
  let i = 0;
  let size = bytes;
  while (size >= 1024 && i < units.length - 1) { size /= 1024; i++; }
  return size.toFixed(1) + " " + units[i];
};

onMounted(fetchList);
</script>

<style scoped lang="scss">
.page-container {
  padding: 10px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
