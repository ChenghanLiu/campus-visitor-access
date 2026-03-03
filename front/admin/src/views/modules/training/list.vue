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
          新建培训课程（Admin）
        </el-button>
      </div>

      <el-table :data="rows" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="title" label="课程标题" />

        <el-table-column label="操作" width="420">
          <template slot-scope="scope">
            <el-button size="mini" @click="openCourseDetail(scope.row.id)">
              查看详情
            </el-button>

            <el-button
                v-if="isStudent"
                size="mini"
                type="primary"
                @click="enroll(scope.row.id)"
            >
              报名
            </el-button>

            <el-button
                v-if="isStudent"
                size="mini"
                type="success"
                @click="openQuiz(scope.row.id)"
            >
              开始做题
            </el-button>

            <el-button
                v-if="isStudent"
                size="mini"
                @click="openHistory(scope.row.id)"
            >
              历史记录
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

    <!-- course detail -->
    <el-dialog title="课程详情" :visible.sync="detailVisible" width="760px" v-loading="detailLoading">
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="模式">{{ detail.mode }}</el-descriptions-item>
        <el-descriptions-item label="及格分">{{ detail.passingScore }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detail.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detail.endTime }}</el-descriptions-item>
        <el-descriptions-item label="时长(min)">{{ detail.durationMinutes }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">
          <div style="white-space: pre-wrap;">{{ detail.content }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <span slot="footer">
        <el-button @click="detailVisible=false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- quiz dialog -->
    <el-dialog
        title="安全培训考试"
        :visible.sync="quizVisible"
        width="860px"
        :close-on-click-modal="false"
        v-loading="quizLoading"
    >
      <div v-if="quizCourseId">
        <div style="margin-bottom: 10px;">
          课程ID：<b>{{ quizCourseId }}</b>
          <span style="margin-left: 12px;">AttemptID：<b>{{ quizAttemptId || '-' }}</b></span>
        </div>

        <div v-if="quizQuestions.length === 0 && !quizLoading" style="padding: 12px 0;">
          暂无题目（后端未配置题库或接口返回为空）
        </div>

        <div v-for="q in quizQuestions" :key="q.questionId" style="margin-bottom: 16px;">
          <div style="font-weight: 600; margin-bottom: 8px;">
            {{ q.questionId }}. {{ q.questionText }}
          </div>

          <el-radio-group v-model="quizAnswers[q.questionId]">
            <div style="display:flex; flex-direction:column; gap:6px;">
              <el-radio :label="'A'">A. {{ q.optionA }}</el-radio>
              <el-radio :label="'B'">B. {{ q.optionB }}</el-radio>
              <el-radio :label="'C'">C. {{ q.optionC }}</el-radio>
              <el-radio :label="'D'">D. {{ q.optionD }}</el-radio>
            </div>
          </el-radio-group>
        </div>
      </div>

      <span slot="footer">
        <el-button @click="closeQuiz">关闭</el-button>
        <el-button
            type="primary"
            :disabled="quizQuestions.length === 0 || !quizAttemptId"
            @click="submitQuiz"
        >
          提交答案
        </el-button>
      </span>
    </el-dialog>

    <!-- history -->
    <el-dialog title="考试历史" :visible.sync="historyVisible" width="760px">
      <el-table :data="historyList" border v-loading="historyLoading">
        <el-table-column prop="id" label="AttemptID" width="100" />
        <el-table-column prop="courseTitle" label="课程" />
        <el-table-column prop="startedAt" label="开始时间" width="180" />
        <el-table-column prop="submittedAt" label="提交时间" width="180" />
        <el-table-column prop="score" label="得分" width="90" />
        <el-table-column prop="passed" label="通过" width="90" />
      </el-table>

      <span slot="footer">
        <el-button @click="historyVisible=false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- create dialog（保留，不影响你UI习惯） -->
    <el-dialog title="新建培训课程（Admin）" :visible.sync="createVisible" width="720px">
      <el-form :model="createForm" label-width="120px">
        <el-form-item label="标题">
          <el-input v-model="createForm.title" />
        </el-form-item>

        <el-form-item label="内容">
          <el-input type="textarea" :rows="3" v-model="createForm.content" />
        </el-form-item>

        <el-form-item label="开始时间">
          <el-date-picker
              v-model="createForm.startTime"
              type="datetime"
              value-format="yyyy-MM-ddTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="结束时间">
          <el-date-picker
              v-model="createForm.endTime"
              type="datetime"
              value-format="yyyy-MM-ddTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="模式">
          <el-select v-model="createForm.mode" style="width: 220px;">
            <el-option label="ONLINE" value="ONLINE" />
            <el-option label="OFFLINE" value="OFFLINE" />
          </el-select>
        </el-form-item>

        <el-form-item label="及格分">
          <el-input v-model="createForm.passingScore" type="number" />
        </el-form-item>

        <el-form-item label="考试时长(min)">
          <el-input v-model="createForm.durationMinutes" type="number" />
        </el-form-item>
      </el-form>

      <span slot="footer">
        <el-button @click="createVisible=false">取消</el-button>
        <el-button type="primary" @click="submitCreate">提交</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import {
  listTrainingCourses,
  getTrainingCourse,
  createTrainingCourse,
  enrollCourse,
  getTrainingQuestions,
  startAttempt,
  submitAttempt,
  getAttemptHistory
} from "@/api/training";

export default {
  data() {
    return {
      loading: false,
      rows: [],
      total: 0,
      page: 1,
      size: 10,

      detailVisible: false,
      detailLoading: false,
      detail: null,

      quizVisible: false,
      quizLoading: false,
      quizCourseId: null,
      quizAttemptId: null,
      quizQuestions: [],
      quizAnswers: {},

      historyVisible: false,
      historyLoading: false,
      historyList: [],

      createVisible: false,
      createForm: {
        title: "",
        content: "",
        startTime: "",
        endTime: "",
        mode: "ONLINE",
        passingScore: 60,
        durationMinutes: 30
      }
    };
  },

  computed: {
    role() {
      return localStorage.getItem("Role") || "";
    },
    isAdmin() {
      return this.role === "ADMIN";
    },
    isStudent() {
      return this.role === "STUDENT";
    }
  },

  mounted() {
    this.load();
  },

  methods: {
    dash(v) {
      // keep UI unchanged; just avoid empty cells
      if (v === null || v === undefined) return "-";
      const s = String(v);
      return s.trim() === "" ? "-" : v;
    },

    normalizeCourse(raw) {
      const c = raw || {};
      // 兼容：后端可能只返回 id/title，或字段名不同
      return {
        id: this.dash(c.id),
        title: this.dash(c.title || c.name),

        mode: this.dash(c.mode),
        passingScore: this.dash(c.passingScore),

        startTime: this.dash(c.startTime),
        endTime: this.dash(c.endTime),

        durationMinutes: this.dash(c.durationMinutes),

        createdAt: this.dash(c.createdAt),

        content: this.dash(c.content || c.description || c.details)
      };
    },

    async load() {
      this.loading = true;
      try {
        const res = await listTrainingCourses({ page: this.page - 1, size: this.size });
        const data = res.data || {};
        this.rows = data.content || [];
        this.total = Number(data.totalElements || 0);
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "加载失败";
        this.$message.error("课程加载失败：" + msg);
        this.rows = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
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

    async openCourseDetail(courseId) {
      this.detailVisible = true;
      this.detailLoading = true;
      this.detail = null;

      try {
        const res = await getTrainingCourse(courseId);
        this.detail = this.normalizeCourse(res.data);
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "获取失败";
        this.$message.error(msg);
      } finally {
        this.detailLoading = false;
      }
    },

    async enroll(courseId) {
      try {
        await enrollCourse(courseId);
        this.$message.success("报名成功");
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "报名失败";
        this.$message.error(msg);
      }
    },

    async openQuiz(courseId) {
      this.quizVisible = true;
      this.quizLoading = true;

      this.quizCourseId = courseId;
      this.quizAttemptId = null;
      this.quizQuestions = [];
      this.quizAnswers = {};

      try {
        const qRes = await getTrainingQuestions(courseId);
        const list = Array.isArray(qRes.data) ? qRes.data : (qRes.data?.content || []);
        this.quizQuestions = (list || []).filter(x => x && x.questionId != null);

        const ans = {};
        this.quizQuestions.forEach(q => { ans[q.questionId] = ""; });
        this.quizAnswers = ans;

        const aRes = await startAttempt(courseId);
        this.quizAttemptId = aRes.data?.id;

      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "打开失败";
        this.$message.error("打开考试失败：" + msg);
      } finally {
        this.quizLoading = false;
      }
    },

    closeQuiz() {
      this.quizVisible = false;
      this.quizCourseId = null;
      this.quizAttemptId = null;
      this.quizQuestions = [];
      this.quizAnswers = {};
    },

    async submitQuiz() {
      if (!this.quizAttemptId) {
        this.$message.error("attemptId 为空");
        return;
      }

      const payload = {};
      this.quizQuestions.forEach(q => {
        const v = this.quizAnswers[q.questionId];
        if (v) payload[String(q.questionId)] = String(v).trim().toUpperCase();
      });

      try {
        await submitAttempt(this.quizAttemptId, payload);
        this.$message.success("提交成功");
        this.closeQuiz();
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "提交失败";
        this.$message.error(msg);
      }
    },

    async openHistory(courseId) {
      this.historyVisible = true;
      this.historyLoading = true;
      this.historyList = [];
      try {
        const res = await getAttemptHistory(courseId);
        this.historyList = res.data || [];
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "获取失败";
        this.$message.error(msg);
      } finally {
        this.historyLoading = false;
      }
    },

    openCreate() {
      this.createForm = {
        title: "",
        content: "",
        startTime: "",
        endTime: "",
        mode: "ONLINE",
        passingScore: 60,
        durationMinutes: 30
      };
      this.createVisible = true;
    },

    async submitCreate() {
      try {
        await createTrainingCourse(this.createForm);
        this.$message.success("创建成功");
        this.createVisible = false;
        this.load();
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "创建失败";
        this.$message.error(msg);
      }
    }
  }
};
</script>
