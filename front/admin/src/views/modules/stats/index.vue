<template>
  <div>
    <el-card>

      <div style="margin-bottom:16px; display:flex; gap:10px; flex-wrap:wrap; align-items:center;">
        <el-button type="primary" @click="loadAll">刷新全部</el-button>
      </div>

      <el-tabs v-model="activeTab">

        <!-- Reservation -->
        <el-tab-pane label="预约统计" name="reservation">
          <el-descriptions :column="3" border v-loading="loading.reservation">
            <el-descriptions-item label="待审核">{{ reservationStats.pending }}</el-descriptions-item>
            <el-descriptions-item label="已获批">{{ reservationStats.approved }}</el-descriptions-item>
            <el-descriptions-item label="已拒绝">{{ reservationStats.rejected }}</el-descriptions-item>
            <el-descriptions-item label="已取消">{{ reservationStats.cancelled }}</el-descriptions-item>
            <el-descriptions-item label="总共">{{ reservationStats.total }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- Experiment -->
        <el-tab-pane label="实验项目统计" name="experiment">
          <el-table :data="experimentStats" border v-loading="loading.experiment">
            <el-table-column prop="experimentProjectId" label="项目ID" width="120" />
            <el-table-column prop="experimentProjectName" label="项目名称" />
            <el-table-column prop="reservationCount" label="预约数" width="120" />
          </el-table>
        </el-tab-pane>

        <!-- Training -->
        <el-tab-pane label="培训统计" name="training">
          <el-descriptions :column="3" border v-loading="loading.trainingOverview" style="margin-bottom:12px;">
            <el-descriptions-item label="总课程">{{ trainingOverview.totalCourses }}</el-descriptions-item>
            <el-descriptions-item label="总报名">{{ trainingOverview.totalEnrollments }}</el-descriptions-item>
            <el-descriptions-item label="已提交">{{ trainingOverview.submittedAttempts }}</el-descriptions-item>
            <el-descriptions-item label="通过">{{ trainingOverview.passedAttempts }}</el-descriptions-item>
            <el-descriptions-item label="通过率">{{ trainingOverview.passRate }}</el-descriptions-item>
          </el-descriptions>

          <el-table :data="trainingCourseStats" border v-loading="loading.trainingCourses">
            <el-table-column prop="courseId" label="课程ID" width="120" />
            <el-table-column prop="courseTitle" label="课程标题" />
            <el-table-column prop="submittedAttempts" label="已提交" width="120" />
            <el-table-column prop="passedAttempts" label="通过" width="120" />
            <el-table-column prop="passRate" label="通过率" width="120" />
          </el-table>

          <el-pagination
              style="margin-top:16px;"
              background
              layout="total, sizes, prev, pager, next"
              :total="trainingTotal"
              :page-size="trainingSize"
              :page-sizes="[10, 20, 50]"
              @current-change="changeTrainingPage"
              @size-change="changeTrainingSize"
          />
        </el-tab-pane>

        <!-- Lab -->
        <el-tab-pane label="实验室统计" name="lab">
          <el-descriptions :column="3" border v-loading="loading.labOverview" style="margin-bottom:12px;">
            <el-descriptions-item label="实验室总数">{{ labOverview.totalLabs }}</el-descriptions-item>
            <el-descriptions-item label="总容量">{{ labOverview.totalCapacity }}</el-descriptions-item>
            <el-descriptions-item label="设备总数">{{ labOverview.totalEquipment }}</el-descriptions-item>
            <el-descriptions-item label="预约总数">{{ labOverview.totalReservations }}</el-descriptions-item>
            <el-descriptions-item label="活跃预约">{{ labOverview.activeReservations }}</el-descriptions-item>
            <el-descriptions-item label="检查计划">{{ labOverview.totalInspectionPlans }}</el-descriptions-item>
            <el-descriptions-item label="不安全次数">{{ labOverview.unsafeInspectionCount }}</el-descriptions-item>
          </el-descriptions>

          <el-table :data="labStatsByLab" border v-loading="loading.labByLab">
            <el-table-column prop="labId" label="实验室ID" width="120" />
            <el-table-column prop="labName" label="实验室名称" />
            <el-table-column prop="capacity" label="容量" width="100" />
            <el-table-column prop="equipmentCount" label="设备数" width="110" />
            <el-table-column prop="totalReservations" label="总预约" width="110" />
            <el-table-column prop="activeReservations" label="活跃预约" width="110" />
            <el-table-column prop="inspectionPlans" label="检查计划" width="110" />
            <el-table-column prop="unsafeInspections" label="不安全" width="110" />
          </el-table>

          <el-pagination
              style="margin-top:16px;"
              background
              layout="total, sizes, prev, pager, next"
              :total="labTotal"
              :page-size="labSize"
              :page-sizes="[10, 20, 50]"
              @current-change="changeLabPage"
              @size-change="changeLabSize"
          />
        </el-tab-pane>

        <!-- Inspection -->
        <el-tab-pane label="检查统计" name="inspection">
          <el-descriptions :column="3" border v-loading="loading.inspectionOverview" style="margin-bottom:12px;">
            <el-descriptions-item label="计划总数">{{ inspectionOverview.totalPlans }}</el-descriptions-item>
            <el-descriptions-item label="完成计划">{{ inspectionOverview.completedPlans }}</el-descriptions-item>
            <el-descriptions-item label="记录总数">{{ inspectionOverview.totalRecords }}</el-descriptions-item>
            <el-descriptions-item label="不安全">{{ inspectionOverview.unsafeCount }}</el-descriptions-item>
            <el-descriptions-item label="安全率">{{ inspectionOverview.safeRate }}</el-descriptions-item>
          </el-descriptions>

          <el-table :data="inspectionStatsByLab" border v-loading="loading.inspectionByLab">
            <el-table-column prop="labId" label="实验室ID" width="120" />
            <el-table-column prop="labName" label="实验室名称" />
            <el-table-column prop="totalPlans" label="计划总数" width="110" />
            <el-table-column prop="completedPlans" label="完成计划" width="110" />
            <el-table-column prop="totalRecords" label="记录总数" width="110" />
            <el-table-column prop="unsafeCount" label="不安全" width="110" />
            <el-table-column prop="safeRate" label="安全率" width="110" />
          </el-table>

          <el-pagination
              style="margin-top:16px;"
              background
              layout="total, sizes, prev, pager, next"
              :total="inspectionTotal"
              :page-size="inspectionSize"
              :page-sizes="[10, 20, 50]"
              @current-change="changeInspectionPage"
              @size-change="changeInspectionSize"
          />
        </el-tab-pane>

      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import {
  getReservationStats,
  getExperimentStats,
  getTrainingOverviewStats,
  getTrainingCourseStats,
  getLabOverviewStats,
  getLabStatsByLab,
  getInspectionOverviewStats,
  getInspectionStatsByLab
} from "@/api/stats";

export default {
  data() {
    return {
      activeTab: "reservation",

      loading: {
        reservation: false,
        experiment: false,
        trainingOverview: false,
        trainingCourses: false,
        labOverview: false,
        labByLab: false,
        inspectionOverview: false,
        inspectionByLab: false
      },

      reservationStats: { pending: 0, approved: 0, rejected: 0, cancelled: 0, total: 0 },
      experimentStats: [],

      trainingOverview: { totalCourses: 0, totalEnrollments: 0, submittedAttempts: 0, passedAttempts: 0, passRate: 0 },
      trainingCourseStats: [],
      trainingPage: 1,
      trainingSize: 10,
      trainingTotal: 0,

      labOverview: {
        totalLabs: 0,
        totalCapacity: 0,
        totalEquipment: 0,
        totalReservations: 0,
        activeReservations: 0,
        totalInspectionPlans: 0,
        unsafeInspectionCount: 0
      },
      labStatsByLab: [],
      labPage: 1,
      labSize: 10,
      labTotal: 0,

      inspectionOverview: { totalPlans: 0, completedPlans: 0, totalRecords: 0, unsafeCount: 0, safeRate: 0 },
      inspectionStatsByLab: [],
      inspectionPage: 1,
      inspectionSize: 10,
      inspectionTotal: 0
    };
  },

  mounted() {
    this.loadAll();
  },

  methods: {
    async loadAll() {
      await Promise.all([
        this.loadReservation(),
        this.loadExperiment(),
        this.loadTrainingOverview(),
        this.loadTrainingCourses(),
        this.loadLabOverview(),
        this.loadLabByLab(),
        this.loadInspectionOverview(),
        this.loadInspectionByLab()
      ]);
    },

    async loadReservation() {
      this.loading.reservation = true;
      try {
        const res = await getReservationStats();
        this.reservationStats = res.data || this.reservationStats;
      } catch (e) {
        this.$message.error("预约统计加载失败");
      } finally {
        this.loading.reservation = false;
      }
    },

    async loadExperiment() {
      this.loading.experiment = true;
      try {
        const res = await getExperimentStats();
        this.experimentStats = res.data || [];
      } catch (e) {
        this.$message.error("实验项目统计加载失败");
      } finally {
        this.loading.experiment = false;
      }
    },

    async loadTrainingOverview() {
      this.loading.trainingOverview = true;
      try {
        const res = await getTrainingOverviewStats();
        this.trainingOverview = res.data || this.trainingOverview;
      } catch (e) {
        this.$message.error("培训总览统计加载失败");
      } finally {
        this.loading.trainingOverview = false;
      }
    },

    async loadTrainingCourses() {
      this.loading.trainingCourses = true;
      try {
        const res = await getTrainingCourseStats({
          page: this.trainingPage - 1,
          size: this.trainingSize
        });
        const data = res.data || {};
        this.trainingCourseStats = data.content || [];
        this.trainingTotal = Number(data.totalElements || 0);
      } catch (e) {
        this.$message.error("培训课程统计加载失败");
        this.trainingCourseStats = [];
        this.trainingTotal = 0;
      } finally {
        this.loading.trainingCourses = false;
      }
    },

    changeTrainingPage(p) {
      this.trainingPage = p;
      this.loadTrainingCourses();
    },

    changeTrainingSize(s) {
      this.trainingSize = s;
      this.trainingPage = 1;
      this.loadTrainingCourses();
    },

    async loadLabOverview() {
      this.loading.labOverview = true;
      try {
        const res = await getLabOverviewStats();
        this.labOverview = res.data || this.labOverview;
      } catch (e) {
        this.$message.error("实验室总览统计加载失败");
      } finally {
        this.loading.labOverview = false;
      }
    },

    async loadLabByLab() {
      this.loading.labByLab = true;
      try {
        const res = await getLabStatsByLab({
          page: this.labPage - 1,
          size: this.labSize
        });
        const data = res.data || {};
        this.labStatsByLab = data.content || [];
        this.labTotal = Number(data.totalElements || 0);
      } catch (e) {
        this.$message.error("实验室分实验室统计加载失败");
        this.labStatsByLab = [];
        this.labTotal = 0;
      } finally {
        this.loading.labByLab = false;
      }
    },

    changeLabPage(p) {
      this.labPage = p;
      this.loadLabByLab();
    },

    changeLabSize(s) {
      this.labSize = s;
      this.labPage = 1;
      this.loadLabByLab();
    },

    async loadInspectionOverview() {
      this.loading.inspectionOverview = true;
      try {
        const res = await getInspectionOverviewStats();
        this.inspectionOverview = res.data || this.inspectionOverview;
      } catch (e) {
        this.$message.error("检查总览统计加载失败");
      } finally {
        this.loading.inspectionOverview = false;
      }
    },

    async loadInspectionByLab() {
      this.loading.inspectionByLab = true;
      try {
        const res = await getInspectionStatsByLab({
          page: this.inspectionPage - 1,
          size: this.inspectionSize
        });
        const data = res.data || {};
        this.inspectionStatsByLab = data.content || [];
        this.inspectionTotal = Number(data.totalElements || 0);
      } catch (e) {
        this.$message.error("检查分实验室统计加载失败");
        this.inspectionStatsByLab = [];
        this.inspectionTotal = 0;
      } finally {
        this.loading.inspectionByLab = false;
      }
    },

    changeInspectionPage(p) {
      this.inspectionPage = p;
      this.loadInspectionByLab();
    },

    changeInspectionSize(s) {
      this.inspectionSize = s;
      this.inspectionPage = 1;
      this.loadInspectionByLab();
    }
  }
};
</script>
