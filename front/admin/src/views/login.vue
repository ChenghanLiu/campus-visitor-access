<template>
  <div class="login-page">
    <div class="login-card">
      <div class="title">智能高校实验室安全管理系统</div>

      <el-form
          ref="form"
          :model="form"
          :rules="rules"
          label-width="70px"
          @keyup.enter.native="onSubmit"
      >
        <el-form-item label="账号" prop="username">
          <el-input
              v-model.trim="form.username"
              placeholder="请输入账号"
              autocomplete="username"
              clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model.trim="form.password"
              placeholder="请输入密码"
              autocomplete="current-password"
              show-password
              clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="onSubmit"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="footer">
        <el-link type="primary" :underline="false" @click="goRegister">
          注册账号
        </el-link>
      </div>
    </div>
  </div>
</template>

<script>
import http from "@/utils/http";

export default {
  name: "Login",
  data() {
    return {
      loading: false,
      form: {
        username: "",
        password: ""
      },
      rules: {
        username: [{ required: true, message: "请输入账号", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }]
      }
    };
  },
  methods: {
    onSubmit() {
      this.$refs.form.validate((valid) => {
        if (!valid) return;
        this.doLogin().catch(() => {});
      });
    },

    async doLogin() {
      if (this.loading) return;
      this.loading = true;

      try {
        const res = await http({
          url: "/api/auth/login",
          method: "POST",
          data: {
            username: this.form.username,
            password: this.form.password
          }
        });

        const body = res && res.data !== undefined ? res.data : res;
        let token = "";

        if (typeof body === "string") {
          token = body;
        } else {
          token =
              (body && body.token) ||
              (body && body.accessToken) ||
              (body && body.jwt) ||
              (body && body.data && body.data.token) ||
              (body && body.data && body.data.accessToken) ||
              "";
        }

        if (!token) {
          this.$message.error("登录成功但未获取到Token，请检查后端返回字段");
          // eslint-disable-next-line no-console
          console.log("login response:", body);
          return;
        }

        localStorage.setItem("Token", token);
        this.$message.success("登录成功");
        this.$router.replace({ path: "/" });
      } catch (e) {
        const msg =
            e?.response?.data?.message ||
            e?.response?.data?.msg ||
            e?.message ||
            "登录失败";
        this.$message.error(msg);
      } finally {
        this.loading = false;
      }
    },

    goRegister() {
      this.$router.push({ path: "/register" });
    }
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 80px;
  background: #ffffff;
}

.login-card {
  width: 760px;
  padding: 48px 60px 32px;
  border-radius: 8px;
  box-sizing: border-box;
}

.title {
  text-align: center;
  font-size: 34px;
  font-weight: 700;
  color: #1677ff;
  margin-bottom: 40px;
}

.login-btn {
  width: 100%;
  height: 56px;
  font-size: 22px;
  font-weight: 700;
}

.footer {
  margin-top: 20px;
  text-align: left;
}
</style>
