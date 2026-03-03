<template>
  <div>
    <el-card>

      <div style="margin-bottom:16px; display:flex; gap:10px; flex-wrap:wrap; align-items:center;">
        <el-button type="primary" @click="load">刷新</el-button>

        <el-button
            v-if="isAdmin"
            type="success"
            @click="openCreate"
        >
          新增用户（Admin）
        </el-button>
      </div>

      <el-table :data="rows" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="角色">
          <template slot-scope="scope">
            {{ formatRole(scope.row.role) }}
          </template>
        </el-table-column>


        <el-table-column v-if="isAdmin" label="操作" width="220">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="openEdit(scope.row.id)">
              编辑
            </el-button>
            <el-button size="mini" type="danger" @click="remove(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          style="margin-top:16px;"
          background
          layout="total, sizes, prev, pager, next"
          :total="total"
          :page-size="size"
          :page-sizes="[10, 20, 50]"
          @current-change="changePage"
          @size-change="changeSize"
      />
    </el-card>

    <!-- create/edit dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px">
      <el-form :model="form" label-width="110px">

        <el-form-item label="用户名" v-if="!isEdit">
          <el-input v-model="form.username" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" show-password />
        </el-form-item>

        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 220px;">
            <el-option label="STUDENT" value="STUDENT" />
            <el-option label="TEACHER" value="TEACHER" />
            <el-option label="ADMIN" value="ADMIN" />
          </el-select>
        </el-form-item>

      </el-form>

      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import {
  listUsers,
  getUserById,
  createUser,
  updateUser,
  deleteUser
} from "@/api/user";

export default {
  data() {
    return {
      loading: false,
      rows: [],
      total: 0,
      page: 1,
      size: 10,

      dialogVisible: false,
      dialogTitle: "",
      isEdit: false,
      editId: null,

      form: {
        username: "",
        email: "",
        password: "",
        role: "STUDENT"
      }
    };
  },

  computed: {
    role() {
      return localStorage.getItem("Role") || "";
    },
    isAdmin() {
      return this.role === "ADMIN";
    }
  },

  mounted() {
    this.load();
  },

  methods: {
    async load() {
      this.loading = true;
      try {
        const res = await listUsers({
          page: this.page - 1,
          size: this.size
        });

        const data = res.data || {};
        this.rows = data.content || [];
        this.total = Number(data.totalElements || 0);

      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "加载失败";
        this.$message.error("用户列表加载失败：" + msg);
        this.rows = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
    },
    formatRole(role) {
      const map = {
        ADMIN: "管理员",
        TEACHER: "教师",
        STUDENT: "学生"
      };
      return map[role] || role;
    },


    changePage(p) {
      this.page = p;
      this.load();
    },

    changeSize(s) {
      this.size = s;
      this.page = 1;
      this.load();
    },

    openCreate() {
      this.dialogTitle = "新增用户（Admin）";
      this.isEdit = false;
      this.editId = null;
      this.form = {
        username: "",
        email: "",
        password: "",
        role: "STUDENT"
      };
      this.dialogVisible = true;
    },

    async openEdit(id) {
      this.dialogTitle = "编辑用户（Admin）";
      this.isEdit = true;
      this.editId = id;
      this.dialogVisible = true;

      try {
        const res = await getUserById(id);
        const u = res.data || {};
        this.form = {
          username: u.username || "", // 编辑时不改 username，但保留显示用
          email: u.email || "",
          password: "",
          role: u.role || "STUDENT"
        };
      } catch (e) {
        this.$message.error("加载用户信息失败");
      }
    },

    async submit() {
      try {
        if (!this.isAdmin) {
          this.$message.error("无权限");
          return;
        }

        if (this.isEdit) {
          // 按你后端：put body 可包含 email/role/password
          const payload = {
            email: this.form.email,
            role: this.form.role,
            password: this.form.password
          };
          await updateUser(this.editId, payload);
          this.$message.success("更新成功");
        } else {
          await createUser({
            username: this.form.username,
            email: this.form.email,
            password: this.form.password,
            role: this.form.role
          });
          this.$message.success("创建成功");
        }

        this.dialogVisible = false;
        this.load();

      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "操作失败";
        this.$message.error(msg);
      }
    },

    async remove(id) {
      try {
        if (!this.isAdmin) {
          this.$message.error("无权限");
          return;
        }

        await this.$confirm("确定删除该用户？", "提示", { type: "warning" });
        await deleteUser(id);
        this.$message.success("删除成功");
        this.load();
      } catch (e) {
        if (e === "cancel") return;
        const msg = e?.response?.data?.message || e?.message || "删除失败";
        this.$message.error(msg);
      }
    }
  }
};
</script>
