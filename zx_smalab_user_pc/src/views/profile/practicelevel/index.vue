<template>
  <div class="practice-level-page">
    <div class="container">
      <!-- 顶部课程信息区 -->
      <div class="course-header-card">
        <!-- 面包屑导航 -->
        <div class="breadcrumb-section">
          <el-breadcrumb separator=">">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: '/profile/index' }">个人中心</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: `/profile/course/${route.params.id}` }">{{ courseInfo.title }}</el-breadcrumb-item>
            <el-breadcrumb-item>实践关卡</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 课程信息区 -->
        <div class="course-info-section">
          <!-- 左侧：封面和标题 -->
          <div class="course-left">
            <img :src="courseInfo.coverImage" :alt="courseInfo.title" class="course-cover" />
            <div class="course-title-area">
              <div class="tags-row">
                <el-tag effect="dark" type="success" size="small">智慧慕课</el-tag>
                <el-tag effect="dark" type="warning" size="small">认证学习</el-tag>
              </div>
              <h1 class="course-title">{{ courseInfo.title }}</h1>
            </div>
          </div>

          <!-- 中间：课程统计信息 -->
          <div class="course-stats">
            <el-descriptions :column="8" size="small" direction="vertical" class="stats-descriptions">
              <el-descriptions-item label="章节">{{ courseStats.chapters }}</el-descriptions-item>
              <el-descriptions-item label="单元">{{ courseStats.units }}</el-descriptions-item>
              <el-descriptions-item label="课堂练习">{{ courseStats.classExercises }}</el-descriptions-item>
              <el-descriptions-item label="实践关卡">{{ courseStats.practiceCards }}</el-descriptions-item>
              <el-descriptions-item label="课后作业">{{ courseStats.homework }}</el-descriptions-item>
              <el-descriptions-item label="视频">{{ courseStats.videos }}</el-descriptions-item>
              <el-descriptions-item label="学习人数">{{ courseStats.learners }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </div>

      <!-- 实践关卡列表区 -->
      <div class="practice-list-card">
        <!-- 空状态 -->
        <div v-if="practiceLevels.length === 0" class="empty-state">
          <img src="@/assets/empty-course.svg" alt="暂无实践关卡" class="empty-img" />
          <p class="empty-text">暂无实践关卡</p>
        </div>

        <!-- 实践关卡列表 -->
        <div v-else class="practice-list">
          <div v-for="item in practiceLevels" :key="item.id" class="practice-card">
            <div class="practice-content">
              <div class="practice-info">
                <el-image :src="item.cover" class="practice-cover" fit="cover" />
                <div class="practice-detail">
                  <h3 class="practice-title">
                    {{ item.name }}
                    <el-tag effect="dark" :type="getStatusType(item.status)" class="ml10">{{ item.status }}</el-tag>
                  </h3>
                  <div class="practice-meta">
                    <span>关卡时间：{{ item.startTime }} ~ {{ item.endTime }}</span>
                  </div>
                  <div class="practice-stats">
                    <el-tag type="success">已完成 {{ item.completedCount }} 关</el-tag>
                    <el-tag type="danger">未完成 {{ item.uncompletedCount }} 关</el-tag>
                  </div>
                </div>
              </div>
              <div class="practice-actions">
                <el-button type="primary" @click="openLevelDialog(item)">进入关卡</el-button>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-section">
            <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize" :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
          </div>
        </div>
      </div>
    </div>

    <!-- 关卡弹窗 -->
    <el-dialog v-model="levelDialogVisible" :show-close="false" :fullscreen="isDialogFullscreen" :width="isDialogFullscreen ? '100%' : '80%'" class="level-dialog">
      <template #header>
        <div class="level-dialog-header">
          <div class="header-left">
            <span class="level-name">{{ currentLevel.name }}</span>
            <span class="level-time">
              <el-icon><Timer /></el-icon>
              实训总用时：{{ formatTime(totalTime) }}
            </span>
          </div>
          <div class="header-right">
            <div class="menu-item" @click="handleConfig">
              <el-icon><Setting /></el-icon>
              <span>配置与监控</span>
            </div>
            <div class="menu-item" @click="handleResource">
              <el-icon><FolderOpened /></el-icon>
              <span>资源中心</span>
            </div>
            <div class="menu-item" @click="handleDataset">
              <el-icon><Coin /></el-icon>
              <span>数据集</span>
            </div>
            <div class="menu-item" @click="toggleFullscreen">
              <el-icon><FullScreen /></el-icon>
              <span>全屏</span>
            </div>
            <div class="menu-item close" @click="closeLevelDialog">
              <el-icon><Close /></el-icon>
              <span>关闭</span>
            </div>
          </div>
        </div>
      </template>

      <!-- 弹窗内容区域 -->
      <div class="level-dialog-content" :style="{ height: isDialogFullscreen ? 'calc(100vh - 160px)' : 'calc(100vh - 350px)' }">
        <el-splitter style="height: 100%">
          <el-splitter-panel size="30%" :min-size="20" collapsible>
            <div class="left-panel">
              <el-tabs v-model="leftActiveTab" class="left-tabs">
                <el-tab-pane label="任务要求" name="task">
                  <div class="task-content">
                    <div class="description-content" v-html="taskInfo.description"></div>
                    <!-- <div class="task-header">
                      <h3>{{ taskInfo.title }}</h3>
                      <el-tag :type="getDifficultyType(taskInfo.difficulty)" size="small">{{ taskInfo.difficulty }}</el-tag>
                    </div>
                    <div class="task-desc">
                      <h4>一、实验目的</h4>
                      <p>{{ taskInfo.description }}</p>
                    </div>
                    <div class="task-steps">
                      <h4>任务步骤</h4>
                      <ol>
                        <li v-for="(step, index) in taskInfo.steps" :key="index">{{ step }}</li>
                      </ol>
                    </div>
                    <div class="task-tips">
                      <h4>提示</h4>
                      <ul>
                        <li v-for="(tip, index) in taskInfo.tips" :key="index">{{ tip }}</li>
                      </ul>
                    </div> -->
                  </div>
                </el-tab-pane>
                <el-tab-pane label="参考答案" name="answer">
                  <div class="answer-content">
                    <el-alert type="warning" :closable="false" show-icon title="提示：建议先自行完成任务后再查看参考答案" style="margin-bottom: 15px" />
                    <pre class="answer-code">{{ referenceAnswer }}</pre>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="问答" name="qa">
                  <div class="qa-content">
                    <div class="qa-input">
                      <el-input v-model="qaQuestion" type="textarea" :rows="3" placeholder="输入你的问题..." />
                      <el-button type="primary" style="margin-top: 10px" @click="submitQuestion">提交问题</el-button>
                    </div>
                    <el-divider />
                    <div class="qa-list">
                      <div v-for="item in qaList" :key="item.id" class="qa-item">
                        <div class="qa-question">
                          <el-icon><ChatDotRound /></el-icon>
                          <span>{{ item.question }}</span>
                        </div>
                        <div class="qa-answer">
                          <el-icon><ChatLineRound /></el-icon>
                          <span>{{ item.answer }}</span>
                        </div>
                      </div>
                      <el-empty v-if="qaList.length === 0" description="暂无问答记录" />
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-splitter-panel>
          <el-splitter-panel :min-size="50" collapsible>
            <div class="editor-panel">
              <div class="editor-header">
                <div class="header-left">
                  <span class="file-name">main.py</span>
                </div>
                <div class="header-actions">
                  <el-button type="primary" size="small" @click="runCode">运行代码</el-button>
                  <el-button type="success" size="small" @click="submitCode">提交答案</el-button>
                </div>
              </div>
              <div class="monaco-editor-wrapper">
                <CodeEditor v-model="currentCode" language="python" theme="vs-dark" height="100%" @mount="handleEditorMount" />
              </div>
              <div v-if="codeOutput" class="code-output">
                <h4>运行结果</h4>
                <pre>{{ codeOutput }}</pre>
              </div>
            </div>
          </el-splitter-panel>
        </el-splitter>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Timer, Setting, FolderOpened, Coin, FullScreen, Close, ChatDotRound, ChatLineRound } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import CodeEditor from "@/components/CodeEditor.vue";
import { getMyTrainings } from "@/api/training";

const route = useRoute();
const router = useRouter();

// 弹窗相关
const levelDialogVisible = ref(false);
const isDialogFullscreen = ref(false);
const currentLevel = ref({});
const totalTime = ref(0);
let timerInterval = null;

// 左侧面板
const leftActiveTab = ref("task");
const currentCode = ref(
  `# 请在此处编写代码\n\nimport heapq\nimport copy\n\nclass PuzzleState:\n    def __init__(self, board, parent=None, move=\"\"):\n        self.board = board\n        self.parent = parent\n        self.move = move\n        \n        # 计算g(n)和h(n)\n        self.g = 0 if parent is None else parent.g + 1\n        self.h = self.manhattan_distance()\n        self.f = self.g + self.h\n        \n        # 找到空格位置\n        for i in range(3):\n            for j in range(3):\n                if board[i][j] == 0:\n                    self.blank_pos = (i, j)\n                    break\n    \n    def manhattan_distance(self):\n        \"\"\"曼哈顿距离启发函数\"\"\"\n        distance = 0\n        goal_positions = {\n            1: (0, 0), 2: (0, 1), 3: (0, 2),\n            4: (1, 0), 5: (1, 1), 6: (1, 2),\n            7: (2, 0), 8: (2, 1), 0: (2, 2)\n        }\n        \n        for i in range(3):\n            for j in range(3):\n                tile = self.board[i][j]\n                if tile != 0:\n                    goal_i, goal_j = goal_positions[tile]\n                    distance += abs(i - goal_i) + abs(j - goal_j)\n        return distance\n    \n    def get_possible_moves(self):\n        \"\"\"获取所有可能的下一步移动\"\"\"\n        moves = []\n        i, j = self.blank_pos\n        \n        if i > 0:\n            moves.append((\"U\", i-1, j))\n        if i < 2:\n            moves.append((\"D\", i+1, j))\n        if j > 0:\n            moves.append((\"L\", i, j-1))\n        if j < 2:\n            moves.append((\"R\", i, j+1))\n        return moves\n    \n    def generate_next_state(self, move, new_i, new_j):\n        \"\"\"生成下一个状态\"\"\"\n        i, j = self.blank_pos\n        new_board = copy.deepcopy(self.board)\n        new_board[i][j], new_board[new_i][new_j] = new_board[new_i][new_j], new_board[i][j]\n        move_names = {\"U\": \"上\", \"D\": \"下\", \"L\": \"左\", \"R\": \"右\"}\n        return PuzzleState(new_board, self, move_names[move])\n    \n    def is_goal(self):\n        \"\"\"检查是否达到目标状态\"\"\"\n        goal = [[1, 2, 3], [4, 5, 6], [7, 8, 0]]\n        return self.board == goal\n    \n    def get_path(self):\n        \"\"\"获取从初始状态到当前状态的路径\"\"\"\n        path = []\n        current = self\n        while current is not None:\n            path.append(current)\n            current = current.parent\n        return path[::-1]\n    \n    def __lt__(self, other):\n        return self.f < other.f\n    \n    def __eq__(self, other):\n        return self.board == other.board\n    \n    def __hash__(self):\n        return hash(str(self.board))\n\ndef solution():\n    \"\"\"A*算法求解八数码问题\"\"\"\n    # 初始状态\n    initial_board = [[2, 8, 3], \n                     [1, 6, 4], \n                     [7, 0, 5]]\n    \n    start_state = PuzzleState(initial_board)\n    \n    # 检查是否有解\n    def is_solvable(board):\n        flat_board = [tile for row in board for tile in row if tile != 0]\n        inversions = 0\n        for i in range(len(flat_board)):\n            for j in range(i + 1, len(flat_board)):\n                if flat_board[i] > flat_board[j]:\n                    inversions += 1\n        return inversions % 2 == 0\n    \n    if not is_solvable(initial_board):\n        print(\"该八数码问题无解！\")\n        return None\n    \n    print(\"正在使用A*算法求解八数码问题...\")\n    print(\"初始状态：\")\n    for row in initial_board:\n        print(row)\n    \n    open_list = []\n    closed_set = set()\n    heapq.heappush(open_list, start_state)\n    \n    step_count = 0\n    max_steps = 10000\n    \n    while open_list and step_count < max_steps:\n        step_count += 1\n        current_state = heapq.heappop(open_list)\n        \n        if current_state.is_goal():\n            print(f\"\\n找到最优解！共搜索{step_count}步\")\n            print(f\"路径长度：{current_state.g}步\")\n            \n            path = current_state.get_path()\n            print(\"\\n解决方案路径：\")\n            for i, state in enumerate(path):\n                print(f\"步骤{i}: {state.move if i > 0 else '初始状态'}\")\n                for row in state.board:\n                    print(f\"  {row}\")\n                print(f\"  g(n)={state.g}, h(n)={state.h}, f(n)={state.f}\")\n            return path\n        \n        closed_set.add(current_state)\n        moves = current_state.get_possible_moves()\n        \n        for move, new_i, new_j in moves:\n            next_state = current_state.generate_next_state(move, new_i, new_j)\n            \n            if next_state in closed_set:\n                continue\n            \n            in_open = False\n            for state in open_list:\n                if state == next_state:\n                    in_open = True\n                    if next_state.g < state.g:\n                        state.g = next_state.g\n                        state.f = state.g + state.h\n                        state.parent = current_state\n                        state.move = next_state.move\n                        heapq.heapify(open_list)\n                    break\n            \n            if not in_open:\n                heapq.heappush(open_list, next_state)\n    \n    if step_count >= max_steps:\n        print(\"搜索步数超过限制，未找到解！\")\n    else:\n        print(\"未找到解！\")\n    \n    return None\n\n# 执行算法\nif __name__ == \"__main__\":\n    print(\"=\" * 50)\n    print(\"八数码问题A*算法求解\")\n    print(\"=\" * 50)\n    solution()`
);
const codeOutput = ref("");
let editorInstance = null;

// 任务信息
const taskInfo = ref({
  title: "集合运算实现",
  difficulty: "中等",
  description: `
    <h4>一、实验目的</h4>
    <p>熟悉和掌握启发式搜索的定义、估价函数和算法过程，并利用 A*算法求解八数码问题，理解求解流程和搜索顺序。</p>

    <h4>二、实验原理</h4>
    <p>定义 h*(n)为状态 n 到目的状态的最优路径的代价，则当 A 搜索算法的启发函数 h(n)小于等于 h* (n)，即满足:</p>


    <ol>
    <p>h(n) ≤ h*(n)</p>
    </ol>

    <p>对所有结点 n 时，A 搜索算法被称为 A*搜索算法。</p>
    <p>A* 搜索算法是由著名的人工智能学者 Nilsson 提出的，它是目前最有影响的启发式图搜索算法，也称为最佳图搜索算法。</p>
    <p>如果某一问题有解,那么利用 A*搜索算法对该问题进行搜索则一定能搜索到解，并且一定能搜索到最优解。</p>
    <p>A*算法有以下特性：可采纳性、单调性、信息性。</p>
  `,
  steps: ["定义一个函数 union(set1, set2)，返回两个集合的并集", "定义一个函数 intersection(set1, set2)，返回两个集合的交集", "定义一个函数 difference(set1, set2)，返回集合set1与set2的差集", "确保函数能够处理空集合的情况"],
  tips: ["可以使用Python内置的set类型", "注意差集运算的方向性", "测试时会使用多组数据验证你的实现"]
});

// 参考答案
const referenceAnswer = ref(`import heapq
import copy

class PuzzleState:
    def __init__(self, board, parent=None, move=""):
        self.board = board
        self.parent = parent
        self.move = move

        # 计算g(n)和h(n)
        self.g = 0 if parent is None else parent.g + 1
        self.h = self.manhattan_distance()
        self.f = self.g + self.h

        # 找到空格位置
        for i in range(3):
            for j in range(3):
                if board[i][j] == 0:
                    self.blank_pos = (i, j)
                    break

    def manhattan_distance(self):
        """曼哈顿距离启发函数"""
        distance = 0
        goal_positions = {
            1: (0, 0), 2: (0, 1), 3: (0, 2),
            4: (1, 0), 5: (1, 1), 6: (1, 2),
            7: (2, 0), 8: (2, 1), 0: (2, 2)
        }

        for i in range(3):
            for j in range(3):
                tile = self.board[i][j]
                if tile != 0:
                    goal_i, goal_j = goal_positions[tile]
                    distance += abs(i - goal_i) + abs(j - goal_j)
        return distance

    def get_possible_moves(self):
        """获取所有可能的下一步移动"""
        moves = []
        i, j = self.blank_pos

        if i > 0:
            moves.append(("U", i-1, j))
        if i < 2:
            moves.append(("D", i+1, j))
        if j > 0:
            moves.append(("L", i, j-1))
        if j < 2:
            moves.append(("R", i, j+1))
        return moves

    def generate_next_state(self, move, new_i, new_j):
        """生成下一个状态"""
        i, j = self.blank_pos
        new_board = copy.deepcopy(self.board)
        new_board[i][j], new_board[new_i][new_j] = new_board[new_i][new_j], new_board[i][j]
        move_names = {"U": "上", "D": "下", "L": "左", "R": "右"}
        return PuzzleState(new_board, self, move_names[move])

    def is_goal(self):
        """检查是否达到目标状态"""
        goal = [[1, 2, 3], [4, 5, 6], [7, 8, 0]]
        return self.board == goal

    def get_path(self):
        """获取从初始状态到当前状态的路径"""
        path = []
        current = self
        while current is not None:
            path.append(current)
            current = current.parent
        return path[::-1]

    def __lt__(self, other):
        return self.f < other.f

    def __eq__(self, other):
        return self.board == other.board

    def __hash__(self):
        return hash(str(self.board))

def solution():
    """A*算法求解八数码问题"""
    # 初始状态
    initial_board = [[2, 8, 3],
                     [1, 6, 4],
                     [7, 0, 5]]

    start_state = PuzzleState(initial_board)

    # 检查是否有解
    def is_solvable(board):
        flat_board = [tile for row in board for tile in row if tile != 0]
        inversions = 0
        for i in range(len(flat_board)):
            for j in range(i + 1, len(flat_board)):
                if flat_board[i] > flat_board[j]:
                    inversions += 1
        return inversions % 2 == 0

    if not is_solvable(initial_board):
        print("该八数码问题无解！")
        return None

    print("正在使用A*算法求解八数码问题...")
    print("初始状态：")
    for row in initial_board:
        print(row)

    open_list = []
    closed_set = set()
    heapq.heappush(open_list, start_state)

    step_count = 0
    max_steps = 10000

    while open_list and step_count < max_steps:
        step_count += 1
        current_state = heapq.heappop(open_list)

        if current_state.is_goal():
            print(f"\n找到最优解！共搜索{step_count}步")
            print(f"路径长度：{current_state.g}步")

            path = current_state.get_path()
            print("\n解决方案路径：")
            for i, state in enumerate(path):
                print(f"步骤{i}: {state.move if i > 0 else '初始状态'}")
                for row in state.board:
                    print(f"  {row}")
                print(f"  g(n)={state.g}, h(n)={state.h}, f(n)={state.f}")
            return path

        closed_set.add(current_state)
        moves = current_state.get_possible_moves()

        for move, new_i, new_j in moves:
            next_state = current_state.generate_next_state(move, new_i, new_j)

            if next_state in closed_set:
                continue

            in_open = False
            for state in open_list:
                if state == next_state:
                    in_open = True
                    if next_state.g < state.g:
                        state.g = next_state.g
                        state.f = state.g + state.h
                        state.parent = current_state
                        state.move = next_state.move
                        heapq.heapify(open_list)
                    break

            if not in_open:
                heapq.heappush(open_list, next_state)

    if step_count >= max_steps:
        print("搜索步数超过限制，未找到解！")
    else:
        print("未找到解！")

    return None

# 执行算法
if __name__ == "__main__":
    print("=" * 50)
    print("八数码问题A*算法求解")
    print("=" * 50)
    solution()`);

// 问答相关
const qaQuestion = ref("");
const qaList = ref([
  { id: 1, question: "差集和对称差集有什么区别？", answer: "差集A-B是属于A但不属于B的元素，对称差集是属于A或B但不同时属于两者的元素。" },
  { id: 2, question: "空集与任何集合的并集是什么？", answer: "空集与任何集合A的并集等于A本身。" }
]);

// 课程信息
const courseInfo = ref({
  id: "",
  title: "人工智能",
  coverImage: "https://gitee.com/dongyanxiao/hello-gitee/raw/master/dongImg/%E4%BA%BA%E5%B7%A5%E6%99%BA%E8%83%BD.png"
});

// 课程统计
const courseStats = ref({
  chapters: 7,
  units: 32,
  classExercises: 7,
  practiceCards: 32,
  homework: 14,
  videos: 32,
  learners: 8522,
  rating: 4,
  starRating: 4
});

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 4
});

// 实践关卡列表
const practiceLevels = ref([]);

const fetchPracticeLevels = async () => {
  try {
    const res = await getMyTrainings(route.params.id);
    const data = res.data;
    if (data) {
      practiceLevels.value = data.trainingList || data.records || data || [];
      pagination.total = data.total || practiceLevels.value.length;
      if (data.courseInfo) {
        courseInfo.value = {
          id: data.courseInfo.id || route.params.id,
          title: data.courseInfo.name || data.courseInfo.title || "",
          coverImage: data.courseInfo.coverImage || data.courseInfo.cover || ""
        };
      }
      if (data.courseStats) {
        courseStats.value = { ...courseStats.value, ...data.courseStats };
      }
    }
  } catch (error) {
    console.error("获取实践关卡列表失败:", error);
  }
};

// 获取状态类型
const getStatusType = status => {
  const map = {
    未开始: "info",
    进行中: "primary",
    已完成: "success",
    已截止: "danger"
  };
  return map[status] || "info";
};

// 分页大小改变
const handleSizeChange = val => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
  fetchPracticeLevels();
};

// 页码改变
const handleCurrentChange = val => {
  pagination.currentPage = val;
  fetchPracticeLevels();
};

// 格式化时间
const formatTime = seconds => {
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = seconds % 60;
  return `${h.toString().padStart(2, "0")}:${m.toString().padStart(2, "0")}:${s.toString().padStart(2, "0")}`;
};

// 打开关卡弹窗
const openLevelDialog = item => {
  currentLevel.value = item;
  levelDialogVisible.value = true;
  totalTime.value = 0;
  // 开始计时
  timerInterval = setInterval(() => {
    totalTime.value++;
  }, 1000);
};

// 关闭关卡弹窗
const closeLevelDialog = () => {
  levelDialogVisible.value = false;
  if (timerInterval) {
    clearInterval(timerInterval);
    timerInterval = null;
  }
};

// 配置与监控
const handleConfig = () => {
  ElMessage.info("配置与监控功能开发中");
};

// 资源中心
const handleResource = () => {
  ElMessage.info("资源中心功能开发中");
};

// 数据集
const handleDataset = () => {
  ElMessage.info("数据集功能开发中");
};

// 全屏切换
const toggleFullscreen = () => {
  isDialogFullscreen.value = !isDialogFullscreen.value;
};

// 获取难度类型
const getDifficultyType = difficulty => {
  const map = { 简单: "success", 中等: "warning", 困难: "danger" };
  return map[difficulty] || "info";
};

// 编辑器挂载
const handleEditorMount = editor => {
  editorInstance = editor;
};

// 运行代码
const runCode = () => {
  if (!currentCode.value) {
    ElMessage.warning("请先编写代码");
    return;
  }
  codeOutput.value = "代码运行中...\n";
  setTimeout(() => {
    codeOutput.value = "运行成功！\n>>> 测试用例1: union({1,2}, {2,3}) = {1,2,3} ✓\n>>> 测试用例2: intersection({1,2}, {2,3}) = {2} ✓";
    ElMessage.success("代码运行成功");
  }, 500);
};

// 提交代码
const submitCode = () => {
  if (!currentCode.value) {
    ElMessage.warning("请先编写代码");
    return;
  }
  ElMessage.success("代码提交成功！");
};

// 提交问题
const submitQuestion = () => {
  if (!qaQuestion.value.trim()) {
    ElMessage.warning("请输入问题");
    return;
  }
  qaList.value.unshift({
    id: Date.now(),
    question: qaQuestion.value,
    answer: "问题已收到，老师会尽快回复您。"
  });
  qaQuestion.value = "";
  ElMessage.success("问题提交成功");
};

onMounted(() => {
  courseInfo.value.id = route.params.id;
  fetchPracticeLevels();
});

onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval);
  }
});
</script>

<style lang="scss" scoped>
.practice-level-page {
  background: #f5f7fa;
  min-height: 100vh;
  padding: 20px 0;

  .container {
    max-width: calc(100vw - 200px);
    min-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .course-header-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;

    .breadcrumb-section {
      margin-bottom: 20px;
      padding-bottom: 15px;
      border-bottom: 1px solid #e4e7ed;
    }

    .course-info-section {
      display: flex;
      align-items: flex-start;
      gap: 30px;

      .course-left {
        display: flex;
        align-items: center;
        gap: 15px;

        .course-cover {
          width: 120px;
          height: 80px;
          object-fit: cover;
          border-radius: 6px;
        }

        .course-title-area {
          .tags-row {
            display: flex;
            gap: 8px;
            margin-bottom: 10px;
          }

          .course-title {
            font-size: 20px;
            font-weight: 600;
            color: #303133;
            margin: 0;
          }
        }
      }

      .course-stats {
        flex: 1;
        border-radius: 8px;
        padding: 15px 20px;
        background-color: #f5f5f5;

        :deep(.el-descriptions__body) {
          background-color: transparent;
        }

        :deep(.el-descriptions) {
          background-color: transparent;

          .el-descriptions__label {
            font-size: 12px;
            text-align: center;
          }

          .el-descriptions__content {
            font-size: 16px;
            text-align: center;
          }
        }
      }
    }
  }

  .practice-list-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;

    .empty-state {
      text-align: center;
      padding: 60px 0;

      .empty-img {
        width: 150px;
        height: auto;
        margin-bottom: 20px;
      }

      .empty-text {
        color: #999;
        margin-bottom: 20px;
      }
    }

    .practice-list {
      .practice-card {
        padding: 20px;
        margin-bottom: 15px;
        background: #fafafa;
        border-radius: 8px;
        border-left: 4px solid #00c185;

        &:last-of-type {
          margin-bottom: 0;
        }

        .practice-content {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .practice-info {
            display: flex;
            align-items: flex-start;
            gap: 20px;

            .practice-cover {
              width: 120px;
              height: 80px;
              border-radius: 8px;
              flex-shrink: 0;
            }

            .practice-detail {
              .practice-title {
                font-size: 16px;
                font-weight: bold;
                margin: 0 0 8px;
                color: #303133;

                .ml10 {
                  margin-left: 10px;
                }
              }

              .practice-meta {
                color: #909399;
                font-size: 14px;
                margin-bottom: 10px;
              }

              .practice-stats {
                display: flex;
                gap: 10px;
              }
            }
          }

          .practice-actions {
            flex-shrink: 0;
          }
        }
      }
    }

    .pagination-section {
      display: flex;
      justify-content: flex-start;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #e4e7ed;
    }
  }
}

// 关卡弹窗样式
.level-dialog {
  :deep(.el-dialog) {
    max-height: 90vh;
    display: flex;
    flex-direction: column;
  }

  :deep(.el-dialog.is-fullscreen) {
    max-height: 100vh;
    height: 100vh;
  }

  :deep(.el-dialog__header) {
    padding: 0;
    margin: 0;
    flex-shrink: 0;
  }

  :deep(.el-dialog__body) {
    padding: 0;
    flex: 1;
    overflow: auto;
  }

  .level-dialog-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 60px;
    padding: 0 10px;
    border-bottom: #00c185 1px solid;

    .header-left {
      display: flex;
      align-items: center;
      gap: 30px;

      .level-name {
        font-size: 18px;
        font-weight: 600;
      }

      .level-time {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        background: rgba(0, 193, 132, 0.446);
        padding: 6px 12px;
        border-radius: 4px;
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 5px;

      .menu-item {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 15px;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
        transition: background 0.3s;

        &:hover {
          background: rgba(255, 255, 255, 0.2);
        }

        &.close {
          background: rgba(255, 255, 255, 0.1);

          &:hover {
            background: #f56c6c;
          }
        }
      }
    }
  }

  .level-dialog-content {
    background: #f5f7fa;

    .left-panel {
      height: 100%;
      background: #fff;
      display: flex;
      flex-direction: column;

      .left-tabs {
        height: 100%;
        display: flex;
        flex-direction: column;

        :deep(.el-tabs__header) {
          margin: 0;
          padding: 0 15px;
          background: #fafafa;
        }

        :deep(.el-tabs__content) {
          flex: 1;
          overflow-y: auto;
          padding: 0;
        }

        .task-content {
          padding: 0px 20px 10px 20px;

          .task-header {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 20px;

            h3 {
              margin: 0;
              font-size: 18px;
              color: #303133;
            }
          }

          .task-desc,
          .task-steps,
          .task-tips {
            margin-bottom: 20px;

            h4 {
              margin: 0 0 10px;
              font-size: 14px;
              color: #303133;
              font-weight: 600;
            }

            p {
              margin: 0;
              font-size: 14px;
              color: #606266;
              line-height: 1.8;
            }

            ol,
            ul {
              margin: 0;
              padding-left: 20px;

              li {
                font-size: 14px;
                color: #606266;
                line-height: 2;
              }
            }
          }
        }

        .answer-content {
          padding: 20px;

          .answer-code {
            background: #1e1e1e;
            color: #d4d4d4;
            padding: 15px;
            border-radius: 6px;
            font-family: "Consolas", monospace;
            font-size: 13px;
            line-height: 1.6;
            overflow-x: auto;
          }
        }

        .qa-content {
          padding: 20px;

          .qa-input {
            margin-bottom: 10px;
          }

          .qa-list {
            .qa-item {
              margin-bottom: 20px;
              padding: 15px;
              background: #f9f9f9;
              border-radius: 6px;

              .qa-question {
                display: flex;
                align-items: flex-start;
                gap: 8px;
                margin-bottom: 10px;
                color: #303133;
                font-weight: 500;

                .el-icon {
                  color: #409eff;
                  margin-top: 3px;
                }
              }

              .qa-answer {
                display: flex;
                align-items: flex-start;
                gap: 8px;
                color: #606266;
                padding-left: 10px;

                .el-icon {
                  color: #67c23a;
                  margin-top: 3px;
                }
              }
            }
          }
        }
      }
    }

    .editor-panel {
      height: 100%;
      background: #fff;
      display: flex;
      flex-direction: column;

      .editor-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px 20px;
        border-bottom: 1px solid #e4e7ed;
        background: #fafafa;

        .header-left {
          .file-name {
            font-size: 14px;
            color: #606266;
          }
        }

        .header-actions {
          display: flex;
          gap: 10px;
        }
      }

      .monaco-editor-wrapper {
        flex: 1;
        min-height: 300px;
      }

      .code-output {
        padding: 15px 20px;
        background: #1e1e1e;
        max-height: 150px;
        overflow-y: auto;

        h4 {
          margin: 0 0 10px;
          font-size: 14px;
          color: #67c23a;
        }

        pre {
          margin: 0;
          font-family: "Consolas", monospace;
          font-size: 13px;
          color: #d4d4d4;
          white-space: pre-wrap;
        }
      }
    }
  }
}

.description-content {
  line-height: 1.8;
  color: #606266;
  text-align: justify;

  :deep(h4) {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 20px 0 10px;
  }

  :deep(p) {
    margin: 10px 0;
  }
  :deep(p::before) {
    content: "\00A0\00A0\00A0\00A0"; /* 两个空格 */
  }

  :deep(ul),
  :deep(ol) {
    padding-left: 20px;
    margin: 10px 0;

    li {
      margin: 8px 0;
    }
  }

  :deep(strong) {
    color: #303133;
    font-weight: 600;
  }
}
</style>
