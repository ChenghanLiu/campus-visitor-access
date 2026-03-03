<template>
  <div>
    <el-card>

      <!-- 顶部筛选/操作 -->
      <div style="margin-bottom:16px; display:flex; gap:10px; flex-wrap:wrap; align-items:center;">
        <el-input
            v-model="filters.labId"
            placeholder="实验室ID（必填）"
            style="width: 200px;"
            clearable
        />
        <el-button type="primary" @click="search">加载</el-button>

        <el-button
            v-if="isAdmin"
            type="success"
            @click="openCreatePlan"
        >
          新建检查计划（Admin）
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="list" border v-loading="loading">
        <el-table-column prop="id" label="计划ID" width="90" />
        <el-table-column prop="labId" label="实验室ID" width="100" />
        <el-table-column prop="inspectorId" label="检查员ID" width="110" />
        <el-table-column prop="inspectionTime" label="检查时间" width="180" />
        <el-table-column prop="content" label="检查内容" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />

        <el-table-column label="操作" width="260">
          <template slot-scope="scope">
            <el-button size="mini" @click="openViewRecord(scope.row.id)">
              查看记录
            </el-button>

            <el-button
                v-if="canRecord(scope.row)"
                size="mini"
                type="primary"
                @click="openRecord(scope.row)"
            >
              录入结果
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 新建检查计划 -->
    <el-dialog title="新建检查计划（Admin）" :visible.sync="createVisible" width="700px">
      <el-form :model="createForm" label-width="110px">
        <el-form-item label="实验室ID">
          <el-input v-model="createForm.labId" />
        </el-form-item>

        <el-form-item label="检查员ID">
          <el-input v-model="createForm.inspectorId" />
        </el-form-item>

        <el-form-item label="检查时间">
          <el-date-picker
              v-model="createForm.inspectionTime"
              type="datetime"
              value-format="yyyy-MM-ddTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="检查内容">
          <el-input v-model="createForm.content" />
        </el-form-item>
      </el-form>

      <span slot="footer">
        <el-button @click="createVisible=false">取消</el-button>
        <el-button type="primary" @click="submitCreatePlan">提交</el-button>
      </span>
    </el-dialog>

    <!-- 录入检查结果 -->
    <el-dialog title="录入检查结果" :visible.sync="recordVisible" width="700px">
      <el-form :model="recordForm" label-width="110px">

        <el-form-item label="计划ID">
          <el-input v-model="recordForm.planId" disabled />
        </el-form-item>

        <el-form-item label="是否安全">
          <el-select v-model="recordForm.isSafe" placeholder="请选择" style="width: 200px;">
            <el-option :value="true" label="安全（true）" />
            <el-option :value="false" label="不安全（false）" />
          </el-select>
        </el-form-item>

        <el-form-item label="问题描述">
          <el-input v-model="recordForm.problemDescription" type="textarea" :rows="2" />
        </el-form-item>

        <el-form-item label="建议">
          <el-input v-model="recordForm.suggestion" type="textarea" :rows="2" />
        </el-form-item>

      </el-form>

      <span slot="footer">
        <el-button @click="recordVisible=false">取消</el-button>
        <el-button type="primary" @click="submitRecord">提交</el-button>
      </span>
    </el-dialog>

    <!-- 查看记录 -->
    <el-dialog title="检查记录" :visible.sync="viewVisible" width="700px">
      <el-descriptions :column="1" border v-loading="viewLoading">
        <el-descriptions-item label="记录ID">{{ viewRecord.id }}</el-descriptions-item>
        <el-descriptions-item label="计划ID">{{ viewRecord.planId }}</el-descriptions-item>
        <el-descriptions-item label="是否安全">{{ viewRecord.isSafe }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ viewRecord.problemDescription }}</el-descriptions-item>
        <el-descriptions-item label="建议">{{ viewRecord.suggestion }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewRecord.createdAt }}</el-descriptions-item>
      </el-descriptions>

      <span slot="footer">
        <el-button @click="viewVisible=false">关闭</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import {
  listInspectionPlansByLab,
  createInspectionPlan,
  recordInspection,
  getInspectionRecord
} from "@/api/inspection";

export default {
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      page: 1,
      size: 10,

      filters: {
        labId: "4" // 你默认常用 4，想空就删掉
      },

      createVisible: false,
      createForm: {
        labId: "",
        inspectorId: "",
        inspectionTime: "",
        content: ""
      },

      recordVisible: false,
      recordForm: {
        planId: null,
        isSafe: false,
        problemDescription: "",
        suggestion: ""
      },

      viewVisible: false,
      viewLoading: false,
      viewRecord: {}
    };
  },

  computed: {
    role() {
      return localStorage.getItem("Role") || "";
    },
    isAdmin() {
      return this.role === "ADMIN";
    },
    // 这里先简单：TEACHER 也允许录入（如果你后端只允许 inspector，本按钮不会影响安全性）
    isTeacher() {
      return this.role === "TEACHER";
    },
    canRecordAny() {
      return this.isAdmin || this.isTeacher;
    }
  },

  mounted() {
    if (this.filters.labId) this.load();
  },

  methods: {
    async load() {
      const labId = String(this.filters.labId || "").trim();
      if (!labId) {
        this.$message.warning("请先输入实验室ID");
        return;
      }

      this.loading = true;
      try {
        const res = await listInspectionPlansByLab(labId, {
          page: this.page - 1,
          size: this.size
        });

        const data = res.data || {};
        this.list = data.content || [];
        this.total = Number(data.totalElements || 0);

      } catch (e) {
        const status = e?.response?.status;
        const msg = e?.response?.data?.message || e?.message || "加载失败";
        console.error("[inspection] list ERROR", status, e?.response?.data || e);
        this.$message.error("加载检查计划失败：" + msg);
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

    changePage(p) {
      this.page = p;
      this.load();
    },

    changeSize(s) {
      this.size = s;
      this.page = 1;
      this.load();
    },

    openCreatePlan() {
      this.createForm = {
        labId: String(this.filters.labId || "").trim(),
        inspectorId: "",
        inspectionTime: "",
        content: ""
      };
      this.createVisible = true;
    },

    async submitCreatePlan() {
      try {
        const params = {
          labId: this.createForm.labId,
          inspectorId: this.createForm.inspectorId,
          inspectionTime: this.createForm.inspectionTime,
          content: this.createForm.content
        };

        await createInspectionPlan(params);
        this.$message.success("创建成功");
        this.createVisible = false;
        this.load();

      } catch (e) {
        const status = e?.response?.status;
        const msg = e?.response?.data?.message || e?.message || "创建失败";
        console.error("[inspection] create ERROR", status, e?.response?.data || e);
        this.$message.error(msg);
      }
    },

    // ✅ 是否显示“录入结果”按钮：
    // - Admin 总能看到
    // - Teacher 也能看到（如果你想严格 inspectorId == 当前用户ID，需要你后端返回 username 或前端存 userId）
    // - 只有 SCHEDULED 才给录入（避免重复录）
    canRecord(row) {
      if (!row) return false;
      if (!(this.isAdmin || this.isTeacher)) return false;
      return row.status === "SCHEDULED";
    },

    openRecord(row) {
      this.recordForm = {
        planId: row.id,
        isSafe: false,
        problemDescription: "",
        suggestion: ""
      };
      this.recordVisible = true;
    },

    async submitRecord() {
      try {
        const params = {
          isSafe: this.recordForm.isSafe,
          problemDescription: this.recordForm.problemDescription,
          suggestion: this.recordForm.suggestion
        };

        await recordInspection(this.recordForm.planId, params);
        this.$message.success("提交成功");
        this.recordVisible = false;
        this.load();

      } catch (e) {
        const status = e?.response?.status;
        const msg = e?.response?.data?.message || e?.message || "提交失败";
        console.error("[inspection] record ERROR", status, e?.response?.data || e);
        this.$message.error(msg);
      }
    },

    async openViewRecord(planId) {
      this.viewVisible = true;
      this.viewLoading = true;
      this.viewRecord = {};

      try {
        const res = await getInspectionRecord(planId);
        this.viewRecord = res.data || {};
      } catch (e) {
        const status = e?.response?.status;
        // 记录可能不存在（比如还没录入），后端可能返回 404/500
        const msg = e?.response?.data?.message || e?.message || "暂无记录";
        console.error("[inspection] getRecord ERROR", status, e?.response?.data || e);
        this.$message.error(msg);
      } finally {
        this.viewLoading = false;
      }
    }
  }
};
</script>
