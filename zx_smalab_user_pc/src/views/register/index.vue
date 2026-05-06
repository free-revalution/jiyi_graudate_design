<template>
  <div class="register-page">
    <div class="logo-container">
      <div class="system-logo-text">实验系统</div>
    </div>

    <div class="left-section">
      <div class="illustration">
        <img :src="loginBgUrl" alt="illustration" class="illustration-img" />
      </div>
    </div>

    <div class="right-section">
      <div class="register-card">
        <div class="register-header">
          <h2>用户注册</h2>
          <p>创建您的账号</p>
        </div>

        <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="register-form">
          <el-form-item prop="school">
            <el-select v-model="registerForm.school" placeholder="请选择学校" class="full-width">
              <el-option label="河南大学" value="henu" />
              <el-option label="郑州大学" value="zzu" />
              <el-option label="河南师范大学" value="htu" />
            </el-select>
          </el-form-item>

          <el-form-item prop="userType">
            <el-select v-model="registerForm.userType" placeholder="学生/老师" class="full-width">
              <el-option label="学生" value="student" />
              <el-option label="老师" value="teacher" />
            </el-select>
          </el-form-item>

          <el-form-item prop="username">
            <el-input v-model="registerForm.username" placeholder="请输入学号/工号" :prefix-icon="User" />
          </el-form-item>

          <el-form-item prop="phone">
            <el-input v-model="registerForm.phone" placeholder="请输入手机号" :prefix-icon="Phone" />
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" :prefix-icon="Lock" show-password />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="agreeTerms">我已阅读并同意<a href="#" class="terms-link">服务协议</a></el-checkbox>
          </div>

          <el-button type="primary" class="register-btn" :loading="loading" @click="handleRegister"> 注册 </el-button>

          <div class="login-link">
            已有账号？<router-link to="/login" class="link">立即登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { User, Lock, Phone } from "@element-plus/icons-vue";
import { ElMessage, ElNotification } from "element-plus";
import loginBgUrl from "@/assets/loginBG.svg";
import request from "@/api";

const router = useRouter();
const registerFormRef = ref();
const loading = ref(false);
const agreeTerms = ref(false);

const registerForm = reactive({
  school: "",
  userType: "",
  username: "",
  phone: "",
  password: "",
  confirmPassword: ""
});

const registerRules = {
  school: [{ required: true, message: "请选择学校", trigger: "change" }],
  userType: [{ required: true, message: "请选择用户类型", trigger: "change" }],
  username: [{ required: true, message: "请输入学号/工号", trigger: "blur" }],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号格式", trigger: "blur" }
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度至少为6位", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ]
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  if (!agreeTerms.value) {
    ElMessage.error("请先同意服务协议");
    return;
  }

  registerFormRef.value.validate(async valid => {
    if (!valid) return;
    
    loading.value = true;
    try {
      const response = await request.post(
        "/user_permiss/auth/register",
        {
          school: registerForm.school,
          userType: registerForm.userType,
          username: registerForm.username,
          phone: registerForm.phone,
          password: registerForm.password
        }
      );

      if (response.code === 200) {
        ElNotification({
          title: "注册成功",
          message: "注册成功，请登录",
          type: "success",
          duration: 3000
        });
        router.push("/login");
      } else {
        ElMessage.error(response.msg || "注册失败，请重试");
      }
    } catch (error) {
      const msg = error?.msg || error?.message || "注册失败，请检查网络";
      ElMessage.error(msg);
    } finally {
      loading.value = false;
    }
  });
};

</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #eef2f7 0%, #d8e2f0 100%);
  display: flex;
  position: relative;
  overflow: hidden;

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
  width: 700px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.register-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 48px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;

  h2 {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 14px;
    color: #999;
    margin: 0;
  }
}

.register-form {
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
  margin-bottom: 24px;

  .terms-link {
    color: #1e88e5;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.register-btn {
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

.login-link {
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
</style>