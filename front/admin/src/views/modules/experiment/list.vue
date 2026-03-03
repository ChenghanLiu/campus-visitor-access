<template>
  <div>
    <el-card>

      <!-- 搜索栏 -->
      <div style="margin-bottom:16px; display:flex; gap:10px; flex-wrap:wrap;">
        <el-input
            v-model="filters.keyword"
            placeholder="关键词 keyword"
            style="width:220px;"
            clearable
            @keyup.enter.native="search"
        />
        <el-input
            v-model="filters.labId"
            placeholder="实验室ID labId"
            style="width:180px;"
            clearable
            @keyup.enter.native="search"
        />

        <el-button type="primary" @click="search">搜索</el-button>
        <el-button @click="reset">重置</el-button>

        <el-button
            v-if="canManage"
            type="success"
            @click="openCreate"
        >
          新建实验项目
        </el-button>

        <el-button
            v-if="isAdmin"
            @click="openStatistics"
        >
          统计（Admin）
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="list" border v-loading="loading">

        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="labName" label="实验室" />
        <el-table-column prop="name" label="项目名称" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />

        <el-table-column label="操作" width="260">
          <template slot-scope="scope">

            <el-button size="mini" @click="viewDetail(scope.row.id)">
              查看
            </el-button>

            <!-- student reserve -->
            <el-button
                v-if="isStudent"
                size="mini"
                type="primary"
                @click="openReserve(scope.row)"
            >
              预约
            </el-button>

            <!-- teacher/admin manage -->
            <el-button
                v-if="canManage"
                size="mini"
                type="success"
                @click="openEdit(scope.row)"
            >
              编辑
            </el-button>

            <el-button
                v-if="canManage"
                size="mini"
                type="danger"
                @click="remove(scope.row.id)"
            >
              删除
            </el-button>

          </template>
        </el-table-column>

      </el-table>

      <!-- 分页 -->
      <el-pagination
          style="margin-top:16px;"
          background
          layout="total, prev, pager, next, sizes"
          :total="total"
          :page-size="size"
          :page-sizes="[10, 20, 50]"
          @current-change="changePage"
          @size-change="changeSize"
      />
    </el-card>

    <!-- 新建/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px">
      <el-form :model="form" label-width="110px">

        <el-form-item label="实验室ID">
          <el-input v-model="form.labId" />
        </el-form-item>

        <el-form-item label="项目名称">
          <el-input v-model="form.name" />
        </el-form-item>

        <el-form-item label="实验目的">
          <el-input type="textarea" :rows="2" v-model="form.objective" />
        </el-form-item>

        <el-form-item label="实验内容">
          <el-input type="textarea" :rows="2" v-model="form.content" />
        </el-form-item>

        <el-form-item label="实验要求">
          <el-input type="textarea" :rows="2" v-model="form.requirements" />
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

      </el-form>

      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </span>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="实验项目详情" :visible.sync="detailVisible" width="700px">
      <el-descriptions :column="1" border v-loading="detailLoading">
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="实验室">{{ detail.labName }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="目的">{{ detail.objective }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ detail.content }}</el-descriptions-item>
        <el-descriptions-item label="要求">{{ detail.requirements }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detail.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detail.endTime }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createdAt }}</el-descriptions-item>
      </el-descriptions>

      <span slot="footer">
        <el-button @click="detailVisible=false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 学生预约弹窗 -->
    <el-dialog title="预约实验项目" :visible.sync="reserveVisible" width="700px">
      <el-form :model="reserveForm" label-width="110px">

        <el-form-item label="实验项目">
          <el-input v-model="reserveForm.experimentName" disabled />
        </el-form-item>

        <el-form-item label="实验室ID">
          <el-input v-model="reserveForm.labId" />
        </el-form-item>

        <el-form-item label="设备ID">
          <el-input v-model="reserveForm.equipmentId" />
        </el-form-item>

        <el-form-item label="开始时间">
          <el-date-picker
              v-model="reserveForm.startTime"
              type="datetime"
              value-format="yyyy-MM-ddTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="结束时间">
          <el-date-picker
              v-model="reserveForm.endTime"
              type="datetime"
              value-format="yyyy-MM-ddTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="用途">
          <el-input v-model="reserveForm.purpose" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="reserveForm.remark" />
        </el-form-item>

      </el-form>

      <span slot="footer">
        <el-button @click="reserveVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReserve">提交预约</el-button>
      </span>
    </el-dialog>

    <!-- 统计弹窗 -->
    <el-dialog title="实验项目统计（Admin）" :visible.sync="statsVisible" width="700px">
      <el-table :data="statsList" border v-loading="statsLoading">
        <el-table-column prop="experimentProjectId" label="ID" width="90" />
        <el-table-column prop="experimentProjectName" label="项目名称" />
        <el-table-column prop="reservationCount" label="预约数" width="120" />
      </el-table>

      <span slot="footer">
        <el-button @click="statsVisible=false">关闭</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import {
  listExperimentProjects,
  getExperimentProjectById,
  createExperimentProject,
  updateExperimentProject,
  deleteExperimentProject,
  reserveExperimentProject,
  getExperimentStatistics
} from "@/api/experiment";

export default {
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      page: 1,
      size: 10,

      filters: {
        keyword: "",
        labId: ""
      },

      dialogVisible: false,
      dialogTitle: "",
      isEdit: false,
      editId: null,
      form: {
        labId: "",
        name: "",
        objective: "",
        content: "",
        requirements: "",
        startTime: "",
        endTime: ""
      },

      detailVisible: false,
      detailLoading: false,
      detail: {},

      reserveVisible: false,
      reserveExperimentId: null,
      reserveForm: {
        experimentName: "",
        labId: "",
        equipmentId: "",
        startTime: "",
        endTime: "",
        purpose: "",
        remark: ""
      },

      statsVisible: false,
      statsLoading: false,
      statsList: []
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
    },
    canManage() {
      return this.isTeacher || this.isAdmin;
    }
  },

  mounted() {
    this.load();
  },

  methods: {
    async load() {
      this.loading = true;
      try {
        const params = {
          page: this.page - 1,
          size: this.size,
          sort: "startTime,desc"
        };

        if (this.filters.keyword) params.keyword = this.filters.keyword;
        if (this.filters.labId) params.labId = this.filters.labId;

        const res = await listExperimentProjects(params);
        this.list = res.data.content || [];
        this.total = Number(res.data.totalElements || 0);

      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "加载失败";
        this.$message.error("加载实验项目失败：" + msg);
        this.list = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
    },

    search() {
      this.page = 1;
      this.load();
    },

    reset() {
      this.filters.keyword = "";
      this.filters.labId = "";
      this.page = 1;
      this.load();
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
      this.dialogTitle = "新建实验项目";
      this.isEdit = false;
      this.editId = null;
      this.form = {
        labId: "",
        name: "",
        objective: "",
        content: "",
        requirements: "",
        startTime: "",
        endTime: ""
      };
      this.dialogVisible = true;
    },

    openEdit(row) {
      this.dialogTitle = "编辑实验项目";
      this.isEdit = true;
      this.editId = row.id;
      this.form = {
        labId: row.labId,
        name: row.name,
        objective: row.objective,
        content: row.content,
        requirements: row.requirements,
        startTime: row.startTime,
        endTime: row.endTime
      };
      this.dialogVisible = true;
    },

    async submit() {
      try {
        if (this.isEdit) {
          await updateExperimentProject(this.editId, this.form);
          this.$message.success("更新成功");
        } else {
          await createExperimentProject(this.form);
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
        await deleteExperimentProject(id);
        this.$message.success("删除成功");
        this.load();
      } catch (e) {
        const status = e?.response?.status;
        const msg = e?.response?.data?.message || e?.message || "删除失败";
        // 后端如果没实现 DELETE，会是 405/404
        this.$message.error(`删除失败(${status || "-"})：${msg}`);
      }
    },

    async viewDetail(id) {
      this.detailVisible = true;
      this.detailLoading = true;
      try {
        const res = await getExperimentProjectById(id);
        this.detail = res.data || {};
      } catch (e) {
        this.$message.error("加载详情失败");
        this.detail = {};
      } finally {
        this.detailLoading = false;
      }
    },

    openReserve(row) {
      this.reserveExperimentId = row.id;
      this.reserveForm = {
        experimentName: row.name,
        labId: row.labId || "",
        equipmentId: "",
        startTime: row.startTime || "",
        endTime: row.endTime || "",
        purpose: "Course experiment",
        remark: ""
      };
      this.reserveVisible = true;
    },

    async submitReserve() {
      try {
        const params = {
          labId: this.reserveForm.labId,
          equipmentId: this.reserveForm.equipmentId,
          startTime: this.reserveForm.startTime,
          endTime: this.reserveForm.endTime,
          purpose: this.reserveForm.purpose,
          remark: this.reserveForm.remark
        };
        await reserveExperimentProject(this.reserveExperimentId, params);
        this.$message.success("预约已提交");
        this.reserveVisible = false;
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "预约失败";
        this.$message.error(msg);
      }
    },

    async openStatistics() {
      this.statsVisible = true;
      this.statsLoading = true;
      try {
        const res = await getExperimentStatistics();
        this.statsList = res.data || [];
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "获取统计失败";
        this.$message.error(msg);
        this.statsList = [];
      } finally {
        this.statsLoading = false;
      }
    }
  }
};
</script>
