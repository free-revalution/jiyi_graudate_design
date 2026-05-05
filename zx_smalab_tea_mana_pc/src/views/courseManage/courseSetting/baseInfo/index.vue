<template>
    <div class="course-base-info">
        <!-- 课程基本信息卡片 -->
        <el-card class="info-card" shadow="never">
            <template #header>
                <div class="card-header">
                    <span class="card-title">课程基本信息</span>
                    <el-button v-if="!isEditing" type="primary" :icon="Edit" @click="startEdit">编辑</el-button>
                    <div v-else>
                        <el-button @click="cancelEdit">取消</el-button>
                        <el-button type="warning" @click="saveEdit" :loading="saveLoading">保存</el-button>
                    </div>
                </div>
            </template>

            <el-descriptions border :column="3" direction="vertical">
                <el-descriptions-item label="课程封面" :span="3">
                    <div class="cover-wrapper">
                        <div v-if="!isEditing">
                            <el-image :src="courseInfo.coverUrl" fit="cover" class="cover-image" />
                        </div>
                        <div class="cover-edit" v-else>
                            <UploadImg v-model:image-url="editForm.coverUrl" width="250px" height="150px" :file-size="5"
                                :api="uploadFile">
                                <template #empty>
                                    <el-icon>
                                        <Plus />
                                    </el-icon>
                                </template>
                                <template #tip>
                                    <el-tag>建议尺寸：250x150像素</el-tag>
                                </template>
                            </UploadImg>
                        </div>
                    </div>
                </el-descriptions-item>

                <el-descriptions-item label="课程ID" :span="3">
                    <div class="info-text">{{ courseInfo.courseId || "-" }}
                    </div>
                </el-descriptions-item>

                <el-descriptions-item label="课程名称">
                    <div class="info-text" v-if="!isEditing">{{ courseInfo.courseName || "-" }}</div>
                    <el-input v-else v-model="editForm.courseName" placeholder="请输入课程名称" />
                </el-descriptions-item>

                <el-descriptions-item label="课程英文名称">
                    <div class="info-text" v-if="!isEditing">{{ courseInfo.courseEnglishName || "-" }}</div>
                    <el-input v-else v-model="editForm.courseEnglishName" placeholder="请输入课程英文名称" />
                </el-descriptions-item>

                <el-descriptions-item label="课程教师">
                    <div class="info-text" v-if="!isEditing">{{ courseInfo.teacherName || "-" }}</div>
                    <el-input v-else v-model="editForm.teacherName" placeholder="请输入课程教师" />
                </el-descriptions-item>

                <el-descriptions-item label="课程归属单位">
                    <div class="info-text" v-if="!isEditing">{{ courseInfo.belongUnit || "-" }}</div>
                    <el-select v-else v-model="editForm.belongUnit" placeholder="请选择归属单位" style="width: 100%">
                        <el-option v-for="item in unitOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </el-descriptions-item>

                <el-descriptions-item label="课程所属院系">
                    <div class="info-text" v-if="!isEditing">{{ courseInfo.department || "-" }}</div>
                    <el-select v-else v-model="editForm.department" placeholder="请选择所属院系" style="width: 100%">
                        <el-option v-for="item in departmentOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </el-descriptions-item>

                <el-descriptions-item label="课程分类">
                    <div class="info-text" v-if="!isEditing">{{ courseInfo.category || "-" }}</div>
                    <el-select v-else v-model="editForm.category" placeholder="请选择课程分类" style="width: 100%">
                        <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </el-descriptions-item>

                <el-descriptions-item label="课程说明" :span="3">
                    <div class="intro-text" v-if="!isEditing">{{ courseInfo.description || "-" }}</div>
                    <el-input v-else v-model="editForm.description" type="textarea"
                        :autosize="{ minRows: 3, maxRows: 6 }" placeholder="请输入课程说明" maxlength="500" show-word-limit />
                </el-descriptions-item>
            </el-descriptions>
        </el-card>
    </div>
</template>

<script setup lang="ts" name="courseManageBaseInfo">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { Edit, Plus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { uploadFile } from "@/api/modules/upload";
import UploadImg from "@/components/Upload/Img.vue";
import { getCourseBaseInfo, updateCourseBaseInfo } from "@/api/modules/course";

const route = useRoute();
const COURSE_ID = Number(route.query.courseId) || 1;

// 加载状态
const loading = ref(false);

// 是否编辑状态
const isEditing = ref(false);
const saveLoading = ref(false);

// 课程基本信息
const courseInfo = ref({
    courseId: "",
    courseName: "",
    courseEnglishName: "",
    teacherName: "",
    belongUnit: "",
    department: "",
    category: "",
    description: "",
    coverUrl: ""
});

/** 加载课程基本信息 */
const fetchCourseInfo = async () => {
    loading.value = true;
    try {
        const res: any = await getCourseBaseInfo(COURSE_ID);
        courseInfo.value = res.data || {};
    } catch (error) {
        ElMessage.error("获取课程信息失败");
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchCourseInfo();
});

// 编辑表单
const editForm = ref({
    courseName: "",
    teacherName: "",
    courseEnglishName: "",
    belongUnit: "",
    department: "",
    category: "",
    description: "",
    coverUrl: ""
});

// 归属单位选项
const unitOptions = ref([
    { label: "计算机学院", value: "计算机学院" },
    { label: "软件学院", value: "软件学院" },
    { label: "信息学院", value: "信息学院" },
    { label: "数学学院", value: "数学学院" }
]);

// 院系选项
const departmentOptions = ref([
    { label: "人工智能系", value: "人工智能系" },
    { label: "软件工程系", value: "软件工程系" },
    { label: "计算机科学系", value: "计算机科学系" },
    { label: "网络工程系", value: "网络工程系" },
    { label: "信息安全系", value: "信息安全系" }
]);

// 课程分类选项
const categoryOptions = ref([
    { label: "专业必修课", value: "专业必修课" },
    { label: "专业选修课", value: "专业选修课" },
    { label: "公共必修课", value: "公共必修课" },
    { label: "公共选修课", value: "公共选修课" }
]);

// 开始编辑
const startEdit = () => {
    editForm.value = {
        courseName: courseInfo.value.courseName,
        teacherName: courseInfo.value.teacherName,
        courseEnglishName: courseInfo.value.courseEnglishName,
        belongUnit: courseInfo.value.belongUnit,
        department: courseInfo.value.department,
        category: courseInfo.value.category,
        description: courseInfo.value.description,
        coverUrl: courseInfo.value.coverUrl
    };
    isEditing.value = true;
};

// 取消编辑
const cancelEdit = () => {
    isEditing.value = false;
};

// 保存编辑
const saveEdit = async () => {
    if (!editForm.value.courseName) {
        ElMessage.warning("请输入课程名称");
        return;
    }

    saveLoading.value = true;
    try {
        await updateCourseBaseInfo(COURSE_ID, editForm.value);

        // 更新数据
        courseInfo.value = {
            ...courseInfo.value,
            ...editForm.value
        };

        ElMessage.success("保存成功");
        isEditing.value = false;
    } catch (error) {
        ElMessage.error("保存失败");
    } finally {
        saveLoading.value = false;
    }
};
</script>

<style scoped lang="scss">
.course-base-info {
    display: flex;
    flex-direction: column;
    gap: 24px;

    .info-card {
        border-radius: 8px;
    }

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .card-title {
            font-size: 16px;
            font-weight: 500;
            color: #262626;
        }
    }

    // 描述列表样式
    :deep(.el-descriptions) {
        .el-descriptions__label {
            color: #8c8c8c;
            font-size: 14px;
            background: #fafafa;
            width: 100px;
        }

        .el-descriptions__content {
            color: #262626;
            font-size: 16px;
            min-width: 200px;
        }
    }

    :deep(.el-upload__tip) {
        text-align: left;
        margin-top: 10px;
    }

    .info-text {
        font-size: 16px;
        font-weight: 500;
        color: #262626;
        line-height: 24px;
    }

    .cover-wrapper {
        padding: 10px 0;

        .cover-image {
            width: 250px;
            height: 150px;
            border-radius: 8px;
        }
    }

    .intro-text {
        font-size: 16px;
        color: #262626;
        line-height: 26px;
    }
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
    border: none !important;
    border-bottom: 1px solid #e0e0e0 !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}
</style>
