 <template>
  <div class="login-page">
    <!-- 左上角 Logo 文字：实验系统 -->
    <div class="logo-container">
      <div class="system-logo-text">实验系统</div>
    </div>

    <!-- 左侧插画区域 -->
    <div class="left-section">
      <div class="illustration">
        <img :src="loginBgUrl" alt="illustration" class="illustration-img" />
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="right-section">
      <div class="login-card">
        <!-- 登录方式切换 -->
        <div class="login-tabs">
          <span :class="['tab', { active: loginType === 'account' }]" @click="loginType = 'account'"> 用户登录 </span>
          <span :class="['tab', { active: loginType === 'scan' }]" @click="loginType = 'scan'"> 扫码登录 </span>
        </div>

        <!-- 账号登录表单 -->
        <el-form v-if="loginType === 'account'" ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="school">
            <el-select v-model="loginForm.school" placeholder="请选择学校" class="full-width">
              <el-option label="河南大学" value="henu" />
              <el-option label="郑州大学" value="zzu" />
              <el-option label="河南师范大学" value="htu" />
            </el-select>
          </el-form-item>

          <el-form-item prop="userType">
            <el-select v-model="loginForm.userType" placeholder="学生/老师" class="full-width">
              <el-option label="学生" value="student" />
              <el-option label="老师" value="teacher" />
            </el-select>
          </el-form-item>

          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入学号/工号" :prefix-icon="User" />
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
            <span class="forgot-link" @click.prevent>忘记密码?</span>
          </div>

          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin"> 登录 </el-button>

          <div class="register-link">
            还没有账号？<router-link to="/register" class="link">立即注册</router-link>
          </div>
        </el-form>

        <!-- 扫码登录 -->
        <div v-else class="scan-login">
          <div class="qrcode-placeholder">
            <el-icon :size="120" color="#ddd"><Iphone /></el-icon>
            <p>请使用手机APP扫码登录</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { User, Lock, Iphone } from "@element-plus/icons-vue";
import { ElMessage, ElNotification } from "element-plus";
import loginBgUrl from "@/assets/loginBG.svg";
import request from "@/api";

const router = useRouter();
const loginFormRef = ref();
const loginType = ref("account");
const rememberMe = ref(false);
const loading = ref(false);

const loginForm = reactive({
  school: "",
  userType: "",
  username: "",
  password: ""
});

const loginRules = {
  school: [{ required: true, message: "请选择学校", trigger: "change" }],
  userType: [{ required: true, message: "请选择用户类型", trigger: "change" }],
  username: [{ required: true, message: "请输入学号/工号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }]
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  loginFormRef.value.validate(async valid => {
    if (!valid) return;
    
    loading.value = true;
    try {
      const response = await request.post(
        "/user_permiss/auth/login",
        {
          phone: loginForm.username,
          password: loginForm.password,
          userType: loginForm.userType,
          school: loginForm.school
        }
      );

      if (response.code === 200) {
        const { data } = response;
        localStorage.setItem("token", data.access_token);
        localStorage.setItem("userId", data.user_id);
        localStorage.setItem("userType", data.identity_type);
        
        ElNotification({
          title: "登录成功",
          message: "欢迎回来！",
          type: "success",
          duration: 3000
        });
        
        router.push("/");
      } else {
        // 拦截器已弹出错误信息
      }
    } catch (error) {
      // interceptor already shows error message via ElMessage.error
    } finally {
      loading.value = false;
    }
  });
};

</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  /* 换成干净柔和新背景 */
  background: linear-gradient(135deg, #eef2f7 0%, #d8e2f0 100%);
  display: flex;
  position: relative;
  overflow: hidden;

  // 背景装饰圆圈保留
  &::before {
    content: "";
    position: absolute;
    width: 600px;
    height: 600px;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 50%;
    top: -200px;
    left: -200px;
  }

  &::after {
    content: "";
    position: absolute;
    width: 400px;
    height: 400px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    bottom: -100px;
    right: 30%;
  }
}

.logo-container {
  position: absolute;
  top: 30px;
  left: 40px;
  z-index: 10;

  /* 左上角实验系统文字样式 */
  .system-logo-text {
    font-size: 24px;
    font-weight: 600;
    color: #1e5bb8;
  }
}

.left-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;

  .illustration {
    max-width: 600px;
    width: 100%;

    .illustration-img {
      width: 100%;
      height: auto;
      filter: drop-shadow(0 20px 40px rgba(0, 0, 0, 0.2));
    }
  }
}

.right-section {
  width:700px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: #fff;
  border-radius: 12px;
  padding: 40px 36px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.login-tabs {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 32px;

  .tab {
    font-size: 16px;
    color: #999;
    cursor: pointer;
    padding-bottom: 8px;
    border-bottom: 2px solid transparent;
    transition: all 0.3s;

    &.active {
      color: #333;
      border-bottom-color: #1e88e5;
    }

    &:hover {
      color: #333;
    }
  }
}

.login-form {
  .full-width {
    width: 100%;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper) {
    height: 44px;
    border-radius: 6px;
  }

  :deep(.el-form-item) {
    margin-bottom: 20px;
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .forgot-link {
    color: #1e88e5;
    font-size: 14px;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  border-radius: 6px;
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  border: none;

  &:hover {
    background: linear-gradient(135deg, #1976d2 0%, #0d47a1 100%);
  }
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;

  .link {
    color: #1e88e5;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.scan-login {
  .qrcode-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40px 0;
    color: #999;

    p {
      margin-top: 16px;
      font-size: 14px;
    }
  }
}
</style>