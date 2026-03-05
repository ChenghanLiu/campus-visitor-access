

<template>
  <div class="layout">
    <el-container style="height: 100vh">

      <!-- 左侧菜单 -->
      <el-aside width="220px">
        <el-menu
            :default-active="$route.path"
            class="el-menu-vertical-demo"
            router
        >

          <el-menu-item index="/">
            系统首页
          </el-menu-item>

          <el-menu-item index="/users" v-if="isAdmin">用户管理</el-menu-item>

          <el-menu-item index="/labs">
            楼栋信息
          </el-menu-item>




          <el-menu-item index="/reservations">
            预约管理
          </el-menu-item>



          <el-menu-item index="/analytics" v-if="isAdmin">统计分析</el-menu-item>

          <el-menu-item @click="logout">
            退出登录
          </el-menu-item>

        </el-menu>
      </el-aside>

      <!-- 主体 -->
      <el-container>
        <el-header style="background: #fff;">
          <div class="header-right">
            高校宿舍访客预约管理系统
          </div>
        </el-header>

        <el-main style="background: #f5f7fa;">
          <router-view />
        </el-main>
      </el-container>

    </el-container>
  </div>
</template>

<script>

export default {
  name: "Layout",

  data() {
    return {
      role: (localStorage.getItem("Role") || "").trim().toUpperCase()
    };
  },

  computed: {
    isAdmin() {
      return this.role === "ADMIN";
    }
  },

  created() {
    // 进入 Layout 时重新读一次，确保登录跳转后菜单立即正确
    this.role = (localStorage.getItem("Role") || "").trim().toUpperCase();
  },

  watch: {
    // 每次路由变化都刷新一次 role（解决“登录后不刷新”的核心问题）
    $route() {
      this.role = (localStorage.getItem("Role") || "").trim().toUpperCase();
    }
  },

  methods: {
    logout() {
      localStorage.removeItem("Token");
      localStorage.removeItem("Role");
      this.$router.replace({ name: "login" });
    }
  }
};


</script>

<style scoped>
.layout {
  height: 100%;
}

.header-right {
  float: right;
  font-weight: 500;
}
</style>
