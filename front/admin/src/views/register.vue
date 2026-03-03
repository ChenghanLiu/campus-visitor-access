<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="title">用户注册</h2>

      <el-form :model="form" ref="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              show-password
          />
        </el-form-item>

        <el-form-item label="角色">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="学生" value="STUDENT" />
            <el-option label="教师" value="TEACHER" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width:100%" @click="submit">
            注册
          </el-button>
        </el-form-item>

        <div class="login-link">
          已有账号？
          <span @click="$router.push('/login')">去登录</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { postAuthRegister } from "@/api/auth";

export default {
  name: "Register",

  data() {
    return {
      form: {
        username: "",
        email: "",
        password: "",
        role: "STUDENT"
      }
    };
  },

  methods: {
    async submit() {
      try {
        await postAuthRegister(this.form);
        this.$message.success("注册成功，请登录");
        this.$router.push("/login");
      } catch (e) {
        const msg =
            e?.response?.data?.message ||
            e?.response?.data ||
            "注册失败";
        this.$message.error(msg);
      }
    }
  }
};
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409eff, #66b1ff);
}

.register-box {
  width: 360px;
  padding: 30px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  margin-bottom: 20px;
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.login-link span {
  color: #409eff;
  cursor: pointer;
}
</style>
