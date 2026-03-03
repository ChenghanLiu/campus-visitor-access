<template>
  <div class="home">
    <el-card class="card" shadow="never" v-loading="loading">
      <div class="title">个人信息</div>

      <div v-if="user" class="content">
        <div class="welcome">
          欢迎你，
          <span class="strong">{{ user.username }}</span>
          <span class="role">（{{ roleText(user.role) }}）</span>
        </div>

        <el-divider></el-divider>

        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户ID">
            {{ user.id }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ user.username }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ user.email || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="角色">
            {{ roleText(user.role) }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="actions">
          <el-button type="primary" @click="goUpdatePassword">
            修改密码
          </el-button>

          <el-button @click="reload" :disabled="loading">
            刷新
          </el-button>

          <el-button type="danger" @click="logout">
            退出登录
          </el-button>
        </div>
      </div>

      <div v-else class="empty">
        <div class="empty-text">未获取到用户信息</div>
        <el-button type="danger" @click="logout">
          返回登录页
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import router from "@/router/router-static";
import http from "@/utils/http";

export default {
  name: "Home",
  data() {
    return {
      loading: false,
      user: null
    };
  },

  mounted() {
    this.init().catch(() => {});
  },

  computed: {
    role() {
      return (localStorage.getItem("Role") || "").trim().toUpperCase();
    },
    isAdmin() {
      return this.role === "ADMIN" || this.role === "ROLE_ADMIN";
    },
    isTeacher() {
      return this.role === "TEACHER" || this.role === "ROLE_TEACHER";
    },
    isStudent() {
      return this.role === "STUDENT" || this.role === "ROLE_STUDENT";
    }
  },


  methods: {
    async init() {
      const token = localStorage.getItem("Token");
      if (!token) {
        router.replace({ name: "login" });
        return;
      }
      await this.fetchSession();
    },

    async fetchSession() {
      this.loading = true;
      try {
        const res = await http({
          url: "/api/session",
          method: "GET"
        });

        const data = res && res.data ? res.data : null;
        this.user = data;

        // ✅ 关键：写入角色供菜单使用
        if (data && data.role) {
          localStorage.setItem("Role", data.role);
        }

      } catch (e) {
        localStorage.removeItem("Token");
        localStorage.removeItem("Role");
        router.replace({ name: "login" });
      } finally {
        this.loading = false;
      }
    },

    reload() {
      this.fetchSession().catch(() => {});
    },

    goUpdatePassword() {
      router.push({ path: "/updatePassword" });
    },

    logout() {
      localStorage.removeItem("Token");
      localStorage.removeItem("Role");
      this.user = null;
      router.replace({ name: "login" });
    },

    roleText(role) {
      const r = String(role || "").toUpperCase();
      if (r === "ADMIN") return "管理员";
      if (r === "TEACHER") return "教师";
      if (r === "STUDENT") return "学生";
      return role || "-";
    }
  }
};
</script>

<style scoped>
.home {
  padding: 16px;
}
.card {
  max-width: 820px;
}
.title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
}
.welcome {
  font-size: 16px;
  line-height: 24px;
}
.strong {
  font-weight: 700;
}
.role {
  color: #666;
  margin-left: 6px;
}
.actions {
  margin-top: 16px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.empty-text {
  margin-bottom: 12px;
  color: #999;
}
</style>
