<template>
    <div class="main-box">
        <div class="table-box">
            <ProTable ref="proTable" :columns="columns" :data="questionBankList" row-key="id">
                <!-- 表格 header 按钮 -->
                <template #tableHeader>
                    <el-button type="primary" :icon="CirclePlus" @click="openCreateDialog">创建新题库</el-button>
                </template>
                <template #operation="scope">
                    <el-button type="primary" link :icon="EditPen" @click="editQuestionBank(scope.row)">编辑</el-button>
                    <el-button type="danger" link :icon="Delete" @click="deleteQuestionBank(scope.row)">删除</el-button>
                </template>
            </ProTable>
        </div>

        <!-- 创建题库弹出框 -->
        <el-dialog v-model="createDialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
            <div class="p20">
                <el-form :model="questionBankForm" :rules="formRules" ref="formRef" label-width="80px"
                    label-position="left">
                    <el-form-item label="题库名称" prop="name">
                        <el-input v-model="questionBankForm.name" placeholder="请输入题库名称" />
                    </el-form-item>
                    <el-form-item label="题库分类" prop="category">
                        <el-select v-model="questionBankForm.category" placeholder="题库分类" style="width: 100%">
                            <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label"
                                :value="item.value" />
                        </el-select>
                    </el-form-item>
                </el-form>
            </div>
            <template #footer>
                <el-button @click="createDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitForm">{{ isEdit ? '保存' : '创建' }}</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="tsx" name="courseManageQuestionBank">
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox, ElTag, FormInstance, FormRules } from "element-plus";
import ProTable from "@/components/ProTable/index.vue";
import { ProTableInstance, ColumnProps } from "@/components/ProTable/interface";
import { CirclePlus, Delete, EditPen } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import { getQuestionBankList } from "@/api/modules/questionBank";

const router = useRouter();

// ProTable 实例
const proTable = ref<ProTableInstance>();

// 分类选项
const categoryOptions = ref([
    { label: "编程基础", value: "programming" },
    { label: "数据结构", value: "dataStructure" },
    { label: "算法", value: "algorithm" },
    { label: "数据库", value: "database" },
    { label: "网络", value: "network" },
    { label: "其他", value: "other" }
]);

// 使用状态选项
const statusOptions = ref([
    { label: "启用", value: "enabled" },
    { label: "禁用", value: "disabled" }
]);

// 表格配置项
const columns = reactive<ColumnProps<any>[]>([
    { type: "index", label: "序号", width: 70 },
    { prop: "name", label: "题库名称", search: { el: "input" }, minWidth: 150 },
    {
        prop: "category",
        label: "分类",
        width: 120,
        enum: categoryOptions,
        search: { el: "select" },
        fieldNames: { label: "label", value: "value" }
    },
    { prop: "creator", label: "创建人", width: 100 },
    { prop: "department", label: "所属院系", width: 150 },
    { prop: "questionCount", label: "题量", width: 80, align: "center" },
    {
        prop: "createTime",
        label: "创建时间",
        width: 180,
        search: { el: "date-picker", props: { type: "daterange", valueFormat: "YYYY-MM-DD" }, span: 1 }
    },
    {
        prop: "status",
        label: "使用状态",
        width: 100,
        align: "center",
        enum: statusOptions,
        search: { el: "select" },
        fieldNames: { label: "label", value: "value" },
        render: (scope: any): any => {
            const isEnabled = scope.row.status === "enabled";
            return <ElTag type={isEnabled ? "success" : "info"}>{isEnabled ? "启用" : "禁用"}</ElTag>;
        }
    },
    { prop: "operation", label: "操作", fixed: "right", width: 150 }
]);

/** 题库列表数据 */
const questionBankList = ref<any[]>([]);

/** 加载题库列表 */
const fetchQuestionBankList = async () => {
    try {
        const res: any = await getQuestionBankList({});
        const data = res.data || {};
        questionBankList.value = data.list || data || [];
    } catch (error) {
        ElMessage.error("获取题库列表失败");
    }
};

onMounted(() => {
    fetchQuestionBankList();
});

/** 新建题库弹出框 */
const createDialogVisible = ref(false);
const formRef = ref<FormInstance>();
const isEdit = ref(false);
const dialogTitle = computed(() => isEdit.value ? "编辑题库" : "创建题库");

/** 题库表单 */
const questionBankForm = ref({
    name: "",
    category: ""
});

/** 表单验证规则 */
const formRules = reactive<FormRules>({
    name: [{ required: true, message: "请输入题库名称", trigger: "blur" }],
    category: [{ required: true, message: "请选择题库分类", trigger: "change" }]
});

/** 打开新建题库弹出框 */
const openCreateDialog = () => {
    isEdit.value = false;
    questionBankForm.value = {
        name: "",
        category: ""
    };
    createDialogVisible.value = true;
};

/** 提交表单 */
const submitForm = async () => {
    if (!formRef.value) return;
    await formRef.value.validate((valid) => {
        if (valid) {
            ElMessage.success(isEdit.value ? "题库修改成功" : "题库创建成功");
            createDialogVisible.value = false;
        }
    });
};

/** 编辑题库 */
const editQuestionBank = (row: any) => {
    isEdit.value = true;
    questionBankForm.value = {
        name: row.name,
        category: row.category
    };
    createDialogVisible.value = true;
};

/** 删除题库 */
const deleteQuestionBank = (row: any) => {
    ElMessageBox.confirm(`确定要删除"${row.name}"吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    }).then(() => {
        ElMessage.success("删除成功");
    });
};

/** 返回上一页 */
const goBack = () => {
    router.back();
};
</script>

<style scoped lang="scss">
.p20 {
    padding: 20px;
}

.bg-white {
    background-color: var(--el-bg-color);
}

.radius8 {
    border-radius: 8px;
}


:deep(.el-dialog .el-dialog__header) {
    border-bottom: 1px solid var(--el-color-primary) !important;
}

:deep(.el-dialog__title) {
    color: var(--el-color-primary);
}

:deep(.el-input__wrapper) {
    border: none !important;
    border-bottom: 1px solid #e0e0e0 !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}

:deep(.el-select__wrapper) {
    border: none !important;
    border-bottom: 1px solid #e0e0e0 !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}
</style>
