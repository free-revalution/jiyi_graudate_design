<template>
  <div class="table-box">
    <div class="bg-white radius8">
      <div class="card mb10 pt0 pb0 border-none shadow-none">
        <SelectFilter :data="selectFilterData" :default-values="selectFilterValues" @change="changeSelectFilter" />
      </div>
      <el-divider border-style="dashed" style="margin: 0 !important;" />
      <div class="card mb10 pt10 pb10 flx-justify-between border-none shadow-none">
        <div>
          <el-button type="primary" @click="openCourseDialog()" icon="Plus">添加课程</el-button>
          <el-button type="warning" @click="" icon="view">观看权限</el-button>
        </div>
        <div>
          <el-form class="t_a_form">
            <el-form-item label="课程名称">
              <el-input v-model="searchName" placeholder="请输入课程名称" style="width: 200px;" clearable @keyup.enter="fetchCourseList"></el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <div class="card mt10 mb10 pt0 pb0 pr0 pl0 border-none" style="background: transparent; box-shadow: none;">
      <div v-for="item in courseList" :key="item.id" class="course-card mb20 bg-white radius8 p20">
        <!-- 上半部分：课程基本信息 -->
        <div class="course-header flx-justify-between">
          <div class="course-info flx-start">
            <el-image :src="item.cover" class="course-cover radius8" fit="cover" />
            <div class="course-detail ml20">
              <h3 class="course-title font-bold text-lg mb10">{{ item.name }}</h3>
              <div class="course-meta text-gray mb10">
                <span class="mr20">上传日期：{{ item.uploadDate }}</span>
                <span class="mr20">学期数：{{ item.terms.length }}</span>
              </div>
              <div class="course-desc text-gray text-sm line-clamp-2" style="color: #666;">
                {{ item.description }}
              </div>
            </div>
          </div>
          <div class="course-actions">
            <el-button v-if="item.status === 'draft'" type="success" icon="Upload" @click="handlePublishCourse(item.id)">发布</el-button>
            <el-button v-if="item.status === 'published'" type="warning" icon="CircleClose" @click="handleCloseCourse(item.id)">结束</el-button>
            <el-button type="warning" icon="view" @click="toCourseManage(item.id)">详情</el-button>
            <el-button type="primary" icon="EditPen">修改</el-button>
            <el-button type="danger" icon="Delete" @click="handleDeleteCourse(item.id)">删除</el-button>
          </div>
        </div>
        <!-- 下半部分：学期表格 -->
        <div v-if="item.terms.length > 0">
          <el-timeline style="padding-left: 0;">
            <el-timeline-item color="#009688" class="pb0">
              <div class="course-table mt20">
                <el-table :data="item.terms" border :style="{ width: '100%' }"
                  :header-cell-style="{ background: '#f5f7fa', color: '#606266' }" :max-height="300">
                  <el-table-column prop="name" label="学期名称" min-width="120" />
                  <el-table-column prop="startTime" label="开课时间" width="120" />
                  <el-table-column prop="info" label="学期信息" min-width="150" show-overflow-tooltip />
                  <el-table-column prop="content" label="教学内容" min-width="150" show-overflow-tooltip />
                  <el-table-column prop="status" label="学期状态" width="100" align="center">
                    <template #default="scope">
                      <el-tag :type="scope.row.status === '进行中' ? 'success' : 'info'" effect="plain">{{ scope.row.status
                      }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="学习页面" width="90" align="center">
                    <template #default>
                      <el-tooltip content="查看学习页面" placement="top">
                        <el-button type="primary" link icon="View" />
                      </el-tooltip>
                    </template>
                  </el-table-column>
                  <el-table-column label="统计报表" width="90" align="center">
                    <template #default>
                      <el-tooltip content="查看统计报表" placement="top">
                        <el-button type="primary" link icon="DataLine" />
                      </el-tooltip>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="220" align="center">
                    <template #default>
                      <el-button type="primary" icon="Upload" link @click="handlePublishCourse(item.id)">发布</el-button>
                      <el-button type="warning" icon="CircleClose" link @click="handleCloseCourse(item.id)">结束</el-button>
                      <el-button type="danger" icon="Delete" link @click="handleDeleteCourse(item.id)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-timeline-item>
            <el-timeline-item style="display: none;" />
          </el-timeline>
        </div>
      </div>
      <div v-if="courseList.length == 0" class="card mb10 pt0 pb0 border-none shadow-none flex-center"
        style="height: 200px;">
        <div class=" text-gray-400 py-8">暂无课程</div>
      </div>
      <!-- 分页 -->
      <div class="pagination-box flx-center mt20 mb20">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 20, 50]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" background @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>

      <div>
        <el-dialog v-model="coursedialogVisible" title="添加课程" width="50%" :before-close="handleClose">
          <el-form :model="courseForm" label-width="100px" class="mt20">
            <el-form-item label="课程名称">
              <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
            </el-form-item>
            <el-row>
              <el-col :span="7">
                <el-form-item label="任课教师">
                  <el-input v-model="courseForm.teacher" placeholder="请输入任课教师" />
                </el-form-item>
              </el-col>
              <el-col :span="5" />
              <el-col :span="8">
                <el-form-item label="职称">
                  <el-select v-model="courseForm.title" placeholder="请选择职称" style="width: 100%">
                    <el-option v-for="item in titleOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="所在院校">
                  <el-input v-model="courseForm.department" placeholder="请输入所在院校" />
                </el-form-item>
              </el-col>
              <el-col :span="4" />
              <el-col :span="11">
                <el-form-item label="所在院系">
                  <el-select v-model="courseForm.institute" placeholder="请选择所在院系" style="width: 100%">
                    <el-option v-for="item in instituteOptions" :key="item.value" :label="item.label"
                      :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="选择学年">
                  <el-select v-model="courseForm.year" placeholder="请选择学年" style="width: 100%">
                    <el-option v-for="item in yearOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="4" />
              <el-col :span="8">
                <el-form-item label="选择学期">
                  <el-select v-model="courseForm.semester" placeholder="请选择学期" style="width: 100%">
                    <el-option v-for="item in semesterOptions" :key="item.value" :label="item.label"
                      :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>

            </el-row>

            <el-row>
              <el-col :span="8">
                <el-form-item label="开始时间">
                  <el-date-picker v-model="courseForm.startTime" type="date" placeholder="选择开始时间" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="4" />
              <el-col :span="11">
                <el-form-item label="总学时">
                  <el-input-number v-model="courseForm.totalHours" :min="0" placeholder="请输入" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="课程简介">
              <el-input v-model="courseForm.description" type="textarea" placeholder="请输入课程简介" />
            </el-form-item>
            <el-form-item label="课程封面">
              <UploadImg v-model:image-url="courseForm.cover" width="250px" height="135px" :file-size="5">
                <template #empty>
                  <el-icon>
                    <Plus />
                  </el-icon>
                  <span>请上传封面</span>
                </template>
              </UploadImg>
            </el-form-item>
          </el-form>

          <template #footer>
            <div class="dialog-footer">
              <el-button @click="coursedialogVisible = false">取消</el-button>
              <el-button type="primary" @click="handleCreateCourse">
                确认
              </el-button>
            </div>
          </template>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script setup lang="tsx" name="topAudit">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import SelectFilter from "@/components/SelectFilter/index.vue";
import UploadImg from "@/components/Upload/Img.vue";
import router from "@/routers";
import { getCourseList, createCourse, deleteCourse, publishCourse, closeCourse } from "@/api/modules/course";

// 加载状态
const loading = ref(false);
// 分页数据
const currentPage = ref(1);
const pageSize = ref(5);
const total = ref(0);

// 课程列表
const courseList = ref<any[]>([]);

// 搜索参数
const searchName = ref("");

/** 获取课程列表 */
const fetchCourseList = async () => {
  loading.value = true;
  try {
    const res: any = await getCourseList({
      page: currentPage.value,
      limit: pageSize.value,
      name: searchName.value,
      status: selectFilterValues.value.topStatus
    });
    const data = res.data || {};
    const apiUrl = import.meta.env.VITE_API_URL;
    courseList.value = (data.list || []).map((item: any) => ({
      id: item.id,
      name: item.name || item.courseName || "",
      uploadDate: item.createdTime || item.uploadDate || "",
      description: item.description || "",
      cover: item.coverUrl && item.coverUrl.startsWith("http") ? item.coverUrl : (item.coverUrl ? `${apiUrl}${item.coverUrl}` : ""),
      status: item.status || "draft",
      terms: item.terms || []
    }));
    total.value = data.total || 0;
  } catch (error) {
    ElMessage.error("获取课程列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  fetchCourseList();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchCourseList();
};

onMounted(() => {
  fetchCourseList();
});

// selectFilter 数据（用户角色为后台数据）
const selectFilterData = reactive([
  {
    title: "课程状态",
    key: "topStatus",
    options: [
      { label: "全部", value: "" },
      { label: "未发布", value: "draft" },
      { label: "已发布", value: "published" },
      { label: "已结束", value: "closed" }
    ]
  },
]);
// 默认 selectFilter 参数
const selectFilterValues = ref({ topStatus: "" });
const changeSelectFilter = (value: typeof selectFilterValues.value) => {
  selectFilterValues.value = value;
  fetchCourseList();
};



/**新增课程 */
const coursedialogVisible = ref(false);

// 课程表单数据
const courseForm = reactive({
  name: "",           // 课程名称
  teacher: "",        // 任课教师
  title: "",          // 职称
  department: "",     // 所在院校
  institute: "",      // 所在院系
  year: "",           // 选择学年
  semester: "",       // 选择学期
  startTime: "",      // 开始时间
  totalHours: 0,      // 总学时
  description: "",    // 课程简介
  cover: ""           // 课程封面
});

// 职称选项
const titleOptions = [
  { label: "无", value: "none" },
  { label: "中级", value: "middle" },
  { label: "高级", value: "senior" },
  { label: "其它", value: "other" }
];

// 所在院系选项
const instituteOptions = [
  { label: "计算机学院", value: "cs" },
  { label: "软件学院", value: "se" },
  { label: "信息工程学院", value: "ie" },
  { label: "人工智能学院", value: "ai" }
];

// 学年选项
const yearOptions = [
  { label: "2025-2026", value: "2025-2026" },
  { label: "2024-2025", value: "2024-2025" },
  { label: "2023-2024", value: "2023-2024" }
];

// 学期选项
const semesterOptions = [
  { label: "1", value: "1" },
  { label: "2", value: "2" }
];

/**打开弹窗 */
const openCourseDialog = () => {
  Object.assign(courseForm, { name: "", teacher: "", title: "", department: "", institute: "", year: "", semester: "", startTime: "", totalHours: 0, description: "", cover: "" });
  coursedialogVisible.value = true;
}

/** 创建课程 */
const handleCreateCourse = async () => {
  if (!courseForm.name) { ElMessage.warning("请输入课程名称"); return; }
  try {
    await createCourse({
      name: courseForm.name,
      teacherName: courseForm.teacher,
      category: courseForm.institute,
      belongUnit: courseForm.department,
      totalHours: courseForm.totalHours,
      description: courseForm.description,
      coverUrl: courseForm.cover
    });
    ElMessage.success("创建成功");
    coursedialogVisible.value = false;
    fetchCourseList();
  } catch (e: any) {
    ElMessage.error(e?.data?.msg || "创建失败");
  }
};

/** 删除课程 */
const handleDeleteCourse = async (id: number) => {
  try {
    await ElMessageBox.confirm("确定删除该课程？", "提示", { type: "warning" });
    await deleteCourse(id);
    ElMessage.success("删除成功");
    fetchCourseList();
  } catch (e: any) {
    if (e !== "cancel") ElMessage.error(e?.data?.msg || "删除失败");
  }
};

/** 发布课程 */
const handlePublishCourse = async (id: number) => {
  try {
    await publishCourse(id);
    ElMessage.success("发布成功");
    fetchCourseList();
  } catch (e: any) {
    ElMessage.error(e?.data?.msg || "发布失败");
  }
};

/** 关闭课程 */
const handleCloseCourse = async (id: number) => {
  try {
    await closeCourse(id);
    ElMessage.success("关闭成功");
    fetchCourseList();
  } catch (e: any) {
    ElMessage.error(e?.data?.msg || "关闭失败");
  }
};



/**跳转课程管理 */
const toCourseManage = (id: number) => {
  const routeUrl = router.resolve({
    path: "/courseManage",
    query: { courseId: id }
  });
  window.open(routeUrl.href, '_blank');
}

/**确认关闭对话框 */
const handleClose = (done: () => void) => {
  ElMessageBox.confirm('确定关闭？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      done()
    })
}
</script>


<style scoped lang="scss">
$select-width: 400px;
$input-width: 400px;

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.course-info {
  display: flex;
  align-items: flex-start;
}

.course-cover {
  width: 160px;
  height: 100px;
  border-radius: 8px;
  flex-shrink: 0;
}

.course-detail {
  margin-left: 20px;
}

.course-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  margin-top: 0;
}

.course-meta {
  color: #999;
  margin-bottom: 10px;
  font-size: 14px;
}

.course-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

.p20 {
  padding: 20px;
}



.notification-dialog-content {
  margin-top: 20px;
}

.notification-title-input,
.notification-content-input,
.notification-time-input {
  width: $input-width;
}

:deep(.notification-title-input .el-input__wrapper),
:deep(.notification-content-input .el-input__wrapper),
:deep(.notification-time-input .el-input__wrapper) {
  border: none !important;
  border-bottom: 1px solid #e0e0e0 !important;
  border-radius: 0 !important;
  box-shadow: none !important;
}

.cu-select-0030 {
  width: $select-width;
}

:deep(.cu-select-0030 .el-select__wrapper) {
  border: none !important;
  border-bottom: 1px solid #e0e0e0 !important;
  border-radius: 0 !important;
  box-shadow: none !important;
}

.flex-between {
  display: flex;
  justify-content: space-between;
}

.t_a_form {
  .el-form-item {
    margin-bottom: 0 !important;
  }
}

.bg-white {
  background-color: var(--el-bg-color);
}

.border-none {
  border: none !important;
}

.shadow-none {
  box-shadow: none
}

.radius8 {
  border-radius: 8px !important;
}


.text-gray-400 {
  color: #9ca3af;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}


//输入框只保留底部边框
:deep(.el-input__wrapper) {
  border: none !important;
  border-bottom: 1px solid #e0e0e0 !important;
  border-radius: 0 !important;
  box-shadow: none !important;
}

// 下拉选择器只保留底部边框
:deep(.el-select__wrapper) {
  border: none !important;
  border-bottom: 1px solid #e0e0e0 !important;
  border-radius: 0 !important;
  box-shadow: none !important;
}

:deep(.el-dialog .el-dialog__header) {
  border-bottom: 1px solid var(--el-color-primary) !important;
}

:deep(.el-dialog__title) {
  color: var(--el-color-primary);
}

.course-actions {
  min-width: 300px
}
</style>
