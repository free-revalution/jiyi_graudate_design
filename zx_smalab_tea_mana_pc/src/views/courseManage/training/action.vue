<template>
    <div class="training-detail-container">
        <!-- 顶部：实训基本设置 -->
        <div class="bg-white radius8 p20 mb10">
            <div class="flx-center mb10">
                <el-button icon="ArrowLeft" type="warning" plain @click="goBack">返回</el-button>
                <el-input v-model="trainingForm.name" class="training-name-input ml10" placeholder="请输入实训名称" />
            </div>
        </div>

        <!-- 主体内容区域 -->
        <el-splitter class="main-content">
            <!-- 左侧：目录树 -->
            <el-splitter-panel :size="320" :min="200" collapsible>
                <div class="tree-overview">
                    <div class="tree-header">
                        <el-button type="primary" link :icon="CirclePlus" @click="addSiblingNode">同级目录</el-button>
                        <el-button type="primary" link :icon="CirclePlus" @click="addChildNode">子目录</el-button>
                    </div>
                    <div class="tree-content">
                        <el-tree ref="treeRef" :data="treeData" :props="treeProps" node-key="id" default-expand-all
                            :expand-on-click-node="false" :highlight-current="true" draggable
                            @node-click="handleNodeClick" @node-drop="handleNodeDrop">
                            <template #default="{ node, data }">
                                <div class="tree-node" @mouseenter="handleMouseEnter(data)"
                                    @mouseleave="handleMouseLeave(data)">
                                    <span class="node-index">{{ data.index }}</span>
                                    <div class="node-content">
                                        <span v-if="!data.isEditing" class="node-label">{{ data.label }}</span>
                                        <el-input v-else v-model="data.label" size="small" class="node-input"
                                            @blur="finishEdit(data)" @keyup.enter="finishEdit(data)" />
                                    </div>
                                    <div v-if="(data.showActions || data.dropdownOpen) && !data.isEditing"
                                        class="node-actions" @click="handleActionsClick(data)">
                                        <el-icon class="action-icon drag-icon">
                                            <Rank />
                                        </el-icon>
                                        <el-dropdown trigger="click" placement="bottom-end"
                                            @visible-change="(visible: boolean) => handleDropdownVisibleChange(visible, data)"
                                            @command="(cmd: string) => handleCommand(cmd, data, node)">
                                            <el-icon class="action-icon">
                                                <MoreFilled />
                                            </el-icon>
                                            <template #dropdown>
                                                <el-dropdown-menu>
                                                    <el-dropdown-item command="rename">重命名</el-dropdown-item>
                                                    <el-dropdown-item command="delete" divided>
                                                        <span class="danger-text">删除</span>
                                                    </el-dropdown-item>
                                                </el-dropdown-menu>
                                            </template>
                                        </el-dropdown>
                                    </div>
                                </div>
                            </template>
                        </el-tree>
                    </div>
                </div>
            </el-splitter-panel>

            <!-- 右侧：内容编辑区域 -->
            <el-splitter-panel :min="400" collapsible>
                <div class="content-editor">
                    <div v-if="currentNode" class="editor-area">
                        <div class="editor-header">
                            <span class="editor-title">{{ currentNode.label }}</span>
                        </div>
                        <div class="editor-wrapper">
                            <WangEditor v-model:value="currentNode.content" height="100%" />
                        </div>
                        <div class="editor-actions">
                            <el-button type="primary" :loading="saving" @click="saveContent">保存内容</el-button>
                        </div>
                    </div>
                    <div v-else class="empty-editor">
                        <el-empty description="请选择左侧目录节点进行编辑" />
                    </div>
                </div>
            </el-splitter-panel>
        </el-splitter>
    </div>
</template>

<script setup lang="ts" name="courseManageTrainingAction">
import { ref, reactive, onMounted, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { CirclePlus, Rank, MoreFilled } from "@element-plus/icons-vue";
import WangEditor from "@/components/WangEditor/index.vue";
import type { ElTree } from "element-plus";
import { getTrainingDetail, getTrainingNodes, createTraining, updateTraining, saveTrainingNodes } from "@/api/modules/training";

const route = useRoute();
const router = useRouter();
const courseId = Number(route.query.courseId) || 1;
const isEditMode = route.params.id && route.params.id !== "add";
const trainingId = ref<number>(0);
const saving = ref(false);

/** 树组件引用 */
const treeRef = ref<InstanceType<typeof ElTree>>();

/**
 * 生成实训名称
 * @description 格式：新建实训 + 年月日 + 时间戳后6位
 * @returns 自动生成的实训名称
 */
const generateTrainingName = () => {
    const now = new Date();
    const dateStr =
        now.getFullYear().toString() +
        (now.getMonth() + 1).toString().padStart(2, "0") +
        now.getDate().toString().padStart(2, "0");
    const timestamp = now.getTime().toString().slice(-6);
    return `新建实训${dateStr}${timestamp}`;
};

/** 实训表单数据 */
const trainingForm = reactive({
    name: generateTrainingName()
});

/** 树节点数据类型定义 */
interface TreeNode {
    id: number;
    index: string;
    label: string;
    content: string;
    showActions?: boolean;
    isEditing?: boolean;
    dropdownOpen?: boolean;
    children?: TreeNode[];
}

/** 目录树数据 */
const treeData = ref<TreeNode[]>([]);

/** 树组件配置 */
const treeProps = { children: "children", label: "label" };

/** 当前选中的节点 */
const currentNode = ref<TreeNode | null>(null);

/**
 * 返回上一页
 */
const goBack = () => router.back();

/**
 * 处理节点点击事件
 * @param data 被点击的节点数据
 * @description 非编辑状态下，选中该节点并在右侧显示内容
 */
const handleNodeClick = (data: TreeNode) => {
    if (!data.isEditing) currentNode.value = data;
};

/**
 * 处理节点拖拽完成事件
 * @description 拖拽完成后更新所有节点的序号
 */
const handleNodeDrop = () => {
    updateTreeIndex();
    ElMessage.success("移动成功");
};

/**
 * 更新树节点序号
 * @description 递归遍历所有节点，根据层级和位置重新生成序号（支持多级嵌套）
 */
const updateTreeIndex = () => {
    const update = (nodes: TreeNode[], parentIdx = "") => {
        nodes.forEach((node, i) => {
            // 根级节点使用两位数字（01, 02...），子级使用点分格式（1.1, 1.1.1...）
            node.index = parentIdx ? `${parentIdx}.${i + 1}` : String(i + 1).padStart(2, "0");
            // 递归时传递当前节点的完整序号（去掉根级的前导0）
            const childParentIdx = parentIdx ? node.index : String(i + 1);
            if (node.children?.length) update(node.children, childParentIdx);
        });
    };
    update(treeData.value);
};

/**
 * 添加同级目录节点
 * @description 在当前选中节点的同级位置添加新节点，若未选中则添加到根级
 */
const addSiblingNode = () => {
    const newNode: TreeNode = { id: Date.now(), index: "", label: "新建目录", content: "", isEditing: true };
    if (currentNode.value) {
        const findParent = (nodes: TreeNode[], target: TreeNode, parent: TreeNode[] | null = null): TreeNode[] | null => {
            for (const node of nodes) {
                if (node.id === target.id) return parent;
                if (node.children) {
                    const found = findParent(node.children, target, node.children);
                    if (found) return found;
                }
            }
            return null;
        };
        const parentChildren = findParent(treeData.value, currentNode.value) || treeData.value;
        parentChildren.push(newNode);
    } else {
        treeData.value.push(newNode);
    }
    updateTreeIndex();
};

/**
 * 添加子目录节点
 * @description 在当前选中节点下添加子节点，需先选中一个节点
 */
const addChildNode = () => {
    if (!currentNode.value) {
        ElMessage.warning("请先选择一个目录节点");
        return;
    }
    if (!currentNode.value.children) currentNode.value.children = [];
    currentNode.value.children.push({ id: Date.now(), index: "", label: "新建子目录", content: "", isEditing: true });
    updateTreeIndex();
    nextTick(() => treeRef.value?.store.nodesMap[currentNode.value!.id]?.expand());
};

/**
 * 鼠标进入节点
 * @param data 节点数据
 * @description 显示操作按钮（移动、更多）
 */
const handleMouseEnter = (data: TreeNode) => {
    data.showActions = true;
};

/**
 * 点击操作区域时选中节点
 * @param data 节点数据
 * @description 点击操作按钮时同时选中该节点
 */
const handleActionsClick = (data: TreeNode) => {
    treeRef.value?.setCurrentKey(data.id);
    currentNode.value = data;
};

/**
 * 鼠标离开节点
 * @param data 节点数据
 * @description 隐藏操作按钮，但下拉菜单打开时不隐藏
 */
const handleMouseLeave = (data: TreeNode) => {
    if (!data.dropdownOpen) {
        data.showActions = false;
    }
};

/**
 * 下拉菜单显示状态变化
 * @param visible 是否显示
 * @param data 节点数据
 * @description 记录下拉菜单状态，关闭时隐藏操作按钮
 */
const handleDropdownVisibleChange = (visible: boolean, data: TreeNode) => {
    data.dropdownOpen = visible;
    if (!visible) {
        data.showActions = false;
    }
};

/**
 * 处理下拉菜单命令
 * @param command 命令类型（rename/delete）
 * @param data 节点数据
 * @param node 树节点实例
 */
const handleCommand = (command: string, data: TreeNode, node: any) => {
    if (command === "rename") {
        handleRename(data);
    } else if (command === "delete") {
        handleDelete(data, node);
    }
};

/**
 * 重命名节点
 * @param data 节点数据
 * @description 进入编辑模式，聚焦输入框并选中文本
 */
const handleRename = (data: TreeNode) => {
    data.isEditing = true;
    nextTick(() => {
        const input = document.querySelector(".node-input input") as HTMLInputElement;
        input?.focus();
        input?.select();
    });
};

/**
 * 删除节点
 * @param data 节点数据
 * @param node 树节点实例
 * @description 弹出确认框，确认后删除节点及其子节点
 */
const handleDelete = (data: TreeNode, node: any) => {
    ElMessageBox.confirm(`确定要删除"${data.label}"吗？删除后不可恢复。`, "删除确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    })
        .then(() => {
            const parent = node.parent;
            const children = parent.data.children || parent.data;
            const idx = children.findIndex((d: TreeNode) => d.id === data.id);
            if (idx > -1) children.splice(idx, 1);
            if (currentNode.value?.id === data.id) currentNode.value = null;
            updateTreeIndex();
            ElMessage.success("删除成功");
        })
        .catch(() => { });
};

/**
 * 完成编辑
 * @param data 节点数据
 * @description 退出编辑模式，若名称为空则设为默认名称
 */
const finishEdit = (data: TreeNode) => {
    data.isEditing = false;
    if (!data.label.trim()) data.label = "未命名目录";
};

const buildNodesPayload = (nodes: TreeNode[]): any[] => {
    return nodes.map(node => ({
        nodeIndex: node.index,
        label: node.label,
        content: node.content,
        children: node.children?.length ? buildNodesPayload(node.children) : []
    }));
};

const convertBackendNodes = (nodes: any[]): TreeNode[] => {
    return nodes.map((node, i) => ({
        id: node.id || Date.now() + i,
        index: node.index || node.nodeIndex || String(i + 1).padStart(2, "0"),
        label: node.label || "未命名",
        content: node.content || "",
        children: node.children?.length ? convertBackendNodes(node.children) : []
    }));
};

const saveContent = async () => {
    if (saving.value) return;
    saving.value = true;
    try {
        let id = trainingId.value;
        if (!id) {
            const res = await createTraining(courseId, { name: trainingForm.name });
            id = res.data.id;
            trainingId.value = id;
        } else {
            await updateTraining(courseId, id, { name: trainingForm.name });
        }
        await saveTrainingNodes(courseId, id, buildNodesPayload(treeData.value));
        ElMessage.success("保存成功");
    } catch (e: any) {
        ElMessage.error(e?.message || "保存失败");
    } finally {
        saving.value = false;
    }
};

const loadTrainingData = async () => {
    try {
        const id = Number(route.params.id);
        const [detailRes, nodesRes] = await Promise.all([
            getTrainingDetail(courseId, id),
            getTrainingNodes(courseId, id)
        ]);
        if (detailRes.data) {
            trainingForm.name = detailRes.data.name || "";
        }
        if (nodesRes.data) {
            treeData.value = convertBackendNodes(nodesRes.data);
            trainingId.value = id;
            updateTreeIndex();
        }
    } catch (e) {
        console.error("加载实训数据失败:", e);
    }
};

onMounted(() => {
    updateTreeIndex();
    if (isEditMode) {
        loadTrainingData();
    }
});
</script>

<style scoped lang="scss">
.training-detail-container {
    background: #f5f7fa;
    height: calc(100vh - 100px);
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.bg-white {
    background: var(--el-bg-color);
}

.radius8 {
    border-radius: 8px;
}

.p20 {
    padding: 20px;
}

.mb10 {
    margin-bottom: 10px;
    flex-shrink: 0;
}

.training-name-input {
    :deep(.el-input__wrapper) {
        font-size: 18px;
        font-weight: bold;
        border: none !important;
        border-bottom: 1px solid var(--el-color-primary) !important;
        border-radius: 0 !important;
        box-shadow: none !important;
    }

    :deep(.el-input__inner) {
        color: var(--el-color-primary) !important;
        font-weight: bold;
    }
}

.main-content {
    flex: 1;
    min-height: 0;

    :deep(.el-splitter-panel__content) {
        height: 100%;
    }
}

.tree-overview {
    height: 100%;
    background: var(--el-bg-color);
    border-radius: 8px;
    padding: 15px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    box-sizing: border-box;

    .tree-header {
        display: flex;
        gap: 15px;
        padding-bottom: 15px;
        border-bottom: 1px solid #ebeef5;
        margin-bottom: 10px;
        flex-shrink: 0;
    }

    .tree-content {
        flex: 1;
        overflow-y: auto;
        min-height: 0;

        :deep(.el-tree) {
            .el-tree-node {
                white-space: normal;
            }
        }
    }
}

.tree-node {
    display: flex;
    align-items: flex-start;
    flex: 1;
    padding: 5px 0;
    min-width: 0;

    .node-index {
        color: var(--el-color-primary);
        margin-right: 10px;
        font-weight: 500;
        min-width: 30px;
        flex-shrink: 0;
        line-height: 1.5;
    }

    .node-content {
        flex: 1;
        min-width: 0;
        margin-right: 10px;

        .node-label {
            display: block;
            word-break: break-word;
            line-height: 1.5;
        }

        .node-input {
            width: 100%;

            :deep(.el-input__wrapper) {
                box-shadow: 0 0 0 1px var(--el-color-primary) inset;
            }
        }
    }

    .node-actions {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-shrink: 0;

        .action-icon {
            font-size: 16px;
            color: var(--el-color-primary);
            cursor: pointer;

            &:hover {
                color: var(--el-color-primary-dark-2);
            }

            &.drag-icon {
                cursor: move;
            }
        }
    }
}

.danger-text {
    color: var(--el-color-danger);
}

:deep(.el-tree-node__content) {
    height: auto !important;
    min-height: 32px;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
    background-color: var(--el-color-primary-light-9);
}

.content-editor {
    height: 100%;
    background: var(--el-bg-color);
    border-radius: 8px;
    padding: 20px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    box-sizing: border-box;

    .editor-area {
        display: flex;
        flex-direction: column;
        flex: 1;
        overflow: hidden;

        .editor-header {
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px solid #ebeef5;
            flex-shrink: 0;

            .editor-title {
                font-size: 16px;
                font-weight: bold;
                color: #303133;
            }
        }

        .editor-wrapper {
            flex: 1;
            overflow: hidden;
            min-height: 0;
            margin-bottom: 20px;

            :deep(.editor-box) {
                height: 100%;
                display: flex;
                flex-direction: column;

                .editor-content {
                    flex: 1;
                    overflow-y: auto;
                }
            }
        }

        .editor-actions {
            display: flex;
            justify-content: flex-end;
            flex-shrink: 0;
        }
    }
}

.empty-editor {
    display: flex;
    align-items: center;
    justify-content: center;
    flex: 1;
}
</style>
