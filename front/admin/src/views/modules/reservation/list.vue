<template>
  <div>
    <el-card>

      <div style="margin-bottom: 16px;">
        <el-button v-if="isStudent" type="primary" @click="openCreateDialog">
          新建预约
        </el-button>
      </div>

      <el-table :data="list" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="labName" label="实验室" />
        <el-table-column prop="equipmentName" label="设备" />
        <el-table-column prop="studentUsername" label="学生" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column prop="endTime" label="结束时间" />
        <el-table-column prop="purpose" label="用途" />

        <el-table-column label="操作" width="220">
          <template slot-scope="scope">
            <template v-if="isTeacher && scope.row.status === 'PENDING'">
              <el-button size="mini" type="success" @click="approve(scope.row.id)">通过</el-button>
              <el-button size="mini" type="danger" @click="reject(scope.row.id)">拒绝</el-button>
            </template>

            <template v-if="isStudent && scope.row.status === 'PENDING'">
              <el-button size="mini" type="danger" @click="cancel(scope.row.id)">取消</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          style="margin-top:16px;"
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="size"
          @current-change="changePage"
      />
    </el-card>

    <el-dialog title="新建预约" :visible.sync="dialogVisible">
      <el-form :model="form">
        <el-form-item label="实验室ID">
          <el-input v-model="form.labId" />
        </el-form-item>

        <el-form-item label="设备ID">
          <el-input v-model="form.equipmentId" />
        </el-form-item>

        <el-form-item label="开始时间">
          <el-date-picker
              v-model="form.startTime"
              type="datetime"
              value-format="yyyy-MM-ddTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="结束时间">
          <el-date-picker
              v-model="form.endTime"
              type="datetime"
              value-format="yyyy-MM-ddTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="用途">
          <el-input v-model="form.purpose" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" />
        </el-form-item>
      </el-form>

      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import http from "@/utils/http";
import {
  createReservation,
  approveReservation,
  rejectReservation,
  cancelReservation
} from "@/api/reservation";

export default {
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      page: 1,
      size: 10,
      dialogVisible: false,
      form: {
        labId: "",
        equipmentId: "",
        startTime: "",
        endTime: "",
        purpose: "",
        remark: ""
      }
    };
  },

  computed: {
    role() {
      return localStorage.getItem("Role") || "";
    },
    isStudent() {
      return this.role === "STUDENT";
    },
    isTeacher() {
      return this.role === "TEACHER";
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

      // ✅ 按角色选对接口
      const params = { page: this.page - 1, size: this.size };
      let url = "/api/reservations";

      if (this.isStudent) url = "/api/reservations/my";
      else if (this.isTeacher) url = "/api/reservations/pending";
      else if (this.isAdmin) url = "/api/reservations";

      try {
        // ✅ 打印出来你就知道到底打到哪个接口了
        console.log("[reservation] GET", url, params, "role=", this.role);

        const res = await http({ url, method: "GET", params });

        const data = res && res.data ? res.data : {};
        this.list = data.content || [];
        this.total = Number(data.totalElements || 0);

      } catch (e) {
        const status = e?.response?.status;
        const msg = e?.response?.data?.message || e?.message || "请求失败";

        console.error("[reservation] ERROR", status, e?.response?.data || e);

        if (status === 401) this.$message.error("登录过期，请重新登录");
        else if (status === 403) this.$message.error("无权限访问预约列表");
        else this.$message.error("预约列表加载失败：" + msg);

        // 失败也要把数据清一下，避免旧数据误导
        this.list = [];
        this.total = 0;

      } finally {
        // ✅ 无论成功失败都关闭 loading，避免“卡住”
        this.loading = false;
      }
    },

    changePage(p) {
      this.page = p;
      this.load();
    },

    openCreateDialog() {
      this.dialogVisible = true;
    },

    async submit() {
      try {
        await createReservation(this.form);
        this.$message.success("预约已提交");
        this.dialogVisible = false;
        this.load();
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "提交失败";
        this.$message.error("提交失败：" + msg);
      }
    },

    async approve(id) {
      try {
        await approveReservation(id, { decisionNote: "" });
        this.$message.success("已通过");
        this.load();
      } catch (e) {
        const status = e?.response?.status;
        const msg = e?.response?.data?.message || e?.message || "审批失败";
        console.error("[approve] ERROR", status, e?.response?.data || e);
        this.$message.error(msg);
      }
    },

    async reject(id) {
      try {
        await rejectReservation(id, { decisionNote: "" });
        this.$message.success("已拒绝");
        this.load();
      } catch (e) {
        const status = e?.response?.status;
        const msg = e?.response?.data?.message || e?.message || "拒绝失败";
        console.error("[reject] ERROR", status, e?.response?.data || e);
        this.$message.error(msg);
      }
    },

    async cancel(id) {
      try {
        await cancelReservation(id);
        this.$message.success("已取消");
        this.load();
      } catch (e) {
        const status = e?.response?.status;
        const msg = e?.response?.data?.message || e?.message || "取消失败";
        console.error("[cancel] ERROR", status, e?.response?.data || e);
        this.$message.error(msg);
      }
    }

  }
};
</script>
