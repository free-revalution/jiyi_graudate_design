<template>
    <div class="table-box">
        <ProTable ref="proTable" :columns="columns" :data="teacherList" row-key="id">
            <!-- 表格 header 按钮 -->
            <template #tableHeader>
                <el-button type="primary" :icon="CirclePlus" @click="openTeacherActionDialog">添加成员</el-button>
                <el-button type="primary" :icon="Download" plain>导入分配班级</el-button>
                <el-button type="primary" :icon="Upload" plain>导出教师团队</el-button>
                <el-button type="primary" :icon="Connection" plain>管理成员权限</el-button>
            </template>
            <template #operation="scope">
                <el-button type="primary" link :icon="View" @click="toDetail(scope.row)">详情</el-button>
                <el-button type="primary" link :icon="EditPen" @click="editTeacher(scope.row)">编辑</el-button>
                <el-button type="danger" link :icon="Delete" @click="deleteTeacher(scope.row)">删除</el-button>
            </template>
        </ProTable>

        <!-- 添加老师弹窗 -->
        <el-dialog v-model="teacherActionDialog" title="添加老师" width="50%" destroy-on-close class="add-teacher-dialog">
            <div class="pl30 pt10 pr30">
                <el-tabs v-model="addTabActive">
                    <el-tab-pane label="手动添加" name="manual">
                        <div class="add-teacher-content">
                            <div class="add-tip">输入姓名、手机号或学号/工号即可添加教师</div>
                            <el-form :model="teacherActionForm" label-position="top">
                                <el-form-item label="姓名">
                                    <el-input v-model="teacherActionForm.name" placeholder="请输入姓名" />
                                </el-form-item>
                                <el-form-item label="手机号/学号/工号:">
                                    <el-input v-model="teacherActionForm.identifier" placeholder="请输入"
                                        class="identifier-input">
                                        <template #prepend>
                                            <el-select v-model="teacherActionForm.identifierType" style="width: 100px">
                                                <el-option label="手机号" value="phone" />
                                                <el-option label="学号/工号" value="workNo" />
                                                <el-option label="超星号" value="chaoxing" />
                                            </el-select>
                                        </template>
                                    </el-input>
                                </el-form-item>
                            </el-form>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="从教师库添加" name="library">
                        <div class="add-teacher-content">
                            <el-empty description="从教师库添加功能开发中..." />
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="从其他课程添加" name="course">
                        <div class="add-teacher-content">
                            <el-empty description="从其他课程添加功能开发中..." />
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="批量导入" name="batch">
                        <div class="add-teacher-content">
                            <el-empty description="批量导入功能开发中..." />
                        </div>
                    </el-tab-pane>
                </el-tabs>
            </div>

            <template #footer>
                <el-button @click="teacherActionDialog = false">取消</el-button>
                <el-button type="primary" @click="handleAddTeacher">添加</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="tsx" name="courseManageTeacherTeamManage">
import { ref, reactive, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, ElMessageBox, ElTag } from "element-plus";
import ProTable from "@/components/ProTable/index.vue";
import { ProTableInstance, ColumnProps } from "@/components/ProTable/interface";
import { CirclePlus, Delete, EditPen, View, Download, Upload, Connection } from "@element-plus/icons-vue";
import { getTeachers, addTeacher, deleteTeacher as deleteTeacherApi } from "@/api/modules/course";

const router = useRouter();
const route = useRoute();
const COURSE_ID = Number(route.query.courseId) || 1;

// ProTable 实例
const proTable = ref<ProTableInstance>();

// 表格配置项
const columns = reactive<ColumnProps<any>[]>([
    { type: "selection", fixed: "left", width: 70 },
    { prop: "name", label: "姓名", search: { el: "input" }, minWidth: 100 },
    {
        prop: "role",
        label: "角色",
        width: 120,
        render: (scope: any): any => {
            const roleMap: Record<string, string> = {
                "主讲教师": "primary",
                "助教": "success",
                "辅导员": "warning"
            };
            return <ElTag type={roleMap[scope.row.role] || "info"}>{scope.row.role}</ElTag>;
        }
    },
    { prop: "workNo", label: "学号/工号", width: 140, search: { el: "input" } },
    { prop: "department", label: "院系", minWidth: 150 },
    { prop: "joinTime", label: "加入时间", width: 160 },
    { prop: "operation", label: "操作", fixed: "right", width: 220 }
]);

/** 教师列表数据 */
const teacherList = ref<any[]>([]);

/** 加载教师列表 */
const fetchTeacherList = async () => {
    try {
        const res: any = await getTeachers(COURSE_ID);
        teacherList.value = res.data || [];
    } catch (error) {
        ElMessage.error("获取教师列表失败");
    }
};

onMounted(() => {
    fetchTeacherList();
});

/** 查看详情 */
const toDetail = (row: any) => {
    ElMessage.info("待开发");
};

/** 编辑教师 */
const editTeacher = (row: any) => {
    ElMessage.info("待开发");
};

/** 删除教师 */
const deleteTeacher = async (row: any) => {
    try {
        await ElMessageBox.confirm(`确定要删除成员"${row.name}"吗？`, "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        });
        await deleteTeacherApi(COURSE_ID, row.id);
        ElMessage.success("删除成功");
        fetchTeacherList();
    } catch (e: any) {
        if (e !== "cancel") ElMessage.error(e?.data?.msg || "删除失败");
    }
};


/**弹出框 */
const teacherActionDialog = ref(false);
const addTabActive = ref("manual");

/**打开弹出框 */
const openTeacherActionDialog = () => {
    teacherActionForm.name = "";
    teacherActionForm.identifier = "";
    teacherActionForm.identifierType = "phone";
    addTabActive.value = "manual";
    teacherActionDialog.value = true;
};

const teacherActionForm = reactive({
    name: "",
    identifier: "",
    identifierType: "phone"
});

/** 添加教师 */
const handleAddTeacher = async () => {
    if (!teacherActionForm.name) {
        ElMessage.warning("请输入姓名");
        return;
    }
    if (!teacherActionForm.identifier) {
        ElMessage.warning("请输入手机号/学号/工号");
        return;
    }
    try {
        await addTeacher(COURSE_ID, teacherActionForm);
        ElMessage.success("添加成功");
        teacherActionDialog.value = false;
        fetchTeacherList();
    } catch (e: any) {
        ElMessage.error(e?.data?.msg || "添加失败");
    }
};
</script>

<style scoped lang="scss">
.add-teacher-content {
    padding: 20px;

    .add-tip {
        text-align: center;
        color: var(--el-color-primary);
        font-size: 14px;
        margin-bottom: 20px;
    }
}

:deep(.el-dialog .el-dialog__header) {
    border-bottom: 1px solid var(--el-color-primary) !important;
}

:deep(.el-dialog__title) {
    color: var(--el-color-primary);
}

:deep(.identifier-input) {
    .el-input-group__prepend {
        background-color: var(--el-bg-color);
        box-shadow: none !important;

        .el-select__wrapper {
            box-shadow: none !important;
            border-radius: 0 !important;
        }
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
