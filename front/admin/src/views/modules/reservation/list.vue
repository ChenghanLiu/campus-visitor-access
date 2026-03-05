<template>
  <div class="page">
    <!-- 顶部信息条 -->
    <div class="topbar">
      <div class="title-area">
        <div class="h1">预约管理</div>
        <div class="sub">
          当前角色：<b>{{ roleLabel }}</b>
          <span class="dot">·</span>
          后端：<span class="mono">{{ apiBase }}</span>
        </div>
      </div>

      <div class="actions">
        <el-button
            v-if="isStudent"
            type="primary"
            icon="el-icon-plus"
            @click="openCreate"
        >在线预约</el-button>

        <el-button icon="el-icon-refresh" @click="load">刷新</el-button>
      </div>
    </div>

    <!-- 筛选区 -->
    <el-card class="card" shadow="never">
      <div class="filters">
        <el-input
            v-model="filters.keyword"
            placeholder="搜索：楼栋/宿舍/用途/备注/状态"
            clearable
            prefix-icon="el-icon-search"
            style="width: 320px;"
            @keyup.enter.native="onSearch"
        />

        <el-select
            v-model="filters.status"
            placeholder="状态"
            clearable
            style="width: 180px;"
            @change="onSearch"
        >
          <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
        </el-select>

        <div class="spacer"></div>

        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </div>
    </el-card>

    <!-- 列表区 -->
    <el-card class="card" shadow="never">
      <el-table
          :data="displayRows"
          border
          v-loading="loading"
          class="table"
      >
        <el-table-column prop="id" label="ID" width="80" />

        <el-table-column label="楼栋/宿舍" min-width="220">
          <template slot-scope="scope">
            <div class="cell-main">
              <div class="cell-title">
                {{ scope.row.labName || ('楼栋#' + scope.row.labId) }}
                <span class="slash">/</span>
                {{ scope.row.equipmentName || ('宿舍#' + scope.row.equipmentId) }}
              </div>
              <div class="cell-sub">
                时间：{{ fmt(scope.row.startTime) }} → {{ fmt(scope.row.endTime) }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="140" align="center">
          <template slot-scope="scope">
            <el-tag :type="tagType(scope.row.status)" effect="dark">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="用途/备注" min-width="240">
          <template slot-scope="scope">
            <div class="text">
              <div class="line"><b>用途：</b>{{ scope.row.purpose || "-" }}</div>
              <div class="line"><b>备注：</b>{{ scope.row.remark || "-" }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="提交人/审核" min-width="220">
          <template slot-scope="scope">
            <div class="text">
              <div class="line">
                <b>访客：</b>{{ scope.row.studentUsername || ('#' + scope.row.studentId) }}
              </div>
              <div class="line">
                <b>宿管：</b>{{ scope.row.approverUsername || "-" }}
              </div>
              <div class="line">
                <b>审核时间：</b>{{ scope.row.decidedAt ? fmt(scope.row.decidedAt) : "-" }}
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 访客：查看自己上传的照片（如果有） -->
        <el-table-column v-if="isStudent" label="核验资料" width="150" align="center">
          <template slot-scope="scope">
            <el-button
                v-if="scope.row.status === 'APPROVED'"
                size="mini"
                type="primary"
                @click="openPhotos(scope.row)"
            >
              查看照片
            </el-button>
          </template>
        </el-table-column>

        <!-- 宿管：审核/核检 -->
        <el-table-column v-if="isTeacher" label="操作" fixed="right" width="320">
          <template slot-scope="scope">
            <div class="op">
              <el-button
                  v-if="scope.row.status === 'PENDING'"
                  size="mini"
                  type="success"
                  @click="approve(scope.row)"
              >通过</el-button>

              <el-button
                  v-if="scope.row.status === 'PENDING'"
                  size="mini"
                  type="danger"
                  @click="reject(scope.row)"
              >驳回</el-button>

              <el-button
                  v-if="scope.row.status === 'APPROVED'"
                  size="mini"
                  type="primary"
                  @click="openPhotos(scope.row)"
              >
                查看照片
              </el-button>

              <el-button
                  v-if="scope.row.status === 'APPROVED'"
                  size="mini"
                  type="primary"
                  @click="checkIn(scope.row)"
              >核检放行</el-button>

              <el-button
                  v-if="scope.row.status === 'CHECKED_IN'"
                  size="mini"
                  type="warning"
                  @click="checkOut(scope.row)"
              >签退</el-button>
            </div>
          </template>
        </el-table-column>

        <!-- 访客：撤销 -->
        <el-table-column v-if="isStudent" label="操作" fixed="right" width="120" align="center">
          <template slot-scope="scope">
            <el-button
                size="mini"
                type="danger"
                plain
                :disabled="!canCancel(scope.row)"
                @click="cancel(scope.row)"
            >撤销</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next"
            :total="total"
            :page-size="size"
            :page-sizes="[10, 20, 50]"
            @current-change="onPageChange"
            @size-change="onSizeChange"
        />
      </div>
    </el-card>

    <!-- 新建预约（访客端） -->
    <el-dialog title="在线预约" :visible.sync="createVisible" width="860px">
      <div class="dialog-grid">
        <el-card class="dialog-card" shadow="never">
          <div class="section-title">
            <i class="el-icon-edit-outline"></i>
            <span>预约信息</span>
          </div>

          <el-form :model="createForm" label-width="110px" class="form">
            <el-form-item label="楼栋号">
              <el-input v-model="createForm.labId" placeholder="例如：1" />
            </el-form-item>

            <el-form-item label="宿舍号">
              <el-input v-model="createForm.equipmentId" placeholder="例如：101" />
            </el-form-item>

            <el-form-item label="开始时间">
              <el-date-picker
                  v-model="createForm.startTime"
                  type="datetime"
                  value-format="yyyy-MM-ddTHH:mm:ss"
                  placeholder="选择开始时间"
                  style="width: 100%;"
              />
            </el-form-item>

            <el-form-item label="结束时间">
              <el-date-picker
                  v-model="createForm.endTime"
                  type="datetime"
                  value-format="yyyy-MM-ddTHH:mm:ss"
                  placeholder="选择结束时间"
                  style="width: 100%;"
              />
            </el-form-item>

            <el-form-item label="用途">
              <el-input v-model="createForm.purpose" placeholder="来访目的" />
            </el-form-item>

            <el-form-item label="备注">
              <el-input v-model="createForm.remark" type="textarea" :rows="2" placeholder="补充说明（可选）" />
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="dialog-card" shadow="never">
          <div class="section-title">
            <i class="el-icon-picture-outline"></i>
            <span>身份核验资料（上传后自动带入本预约）</span>
          </div>

          <!-- 身份证 -->
          <!-- ✅ Upload: id card photo -->
          <!-- 身份证照片 -->
          <!-- 身份证照片 -->
          <div class="upload-block">
            <div class="upload-label">身份证照片</div>
            <el-upload
                ref="idCardUp"
                class="uploader"
                drag
                action="http://localhost:8081/api/student/upload"
                name="file"
                :headers="uploadHeaders"
                accept="image/*"
                :show-file-list="false"
                :http-request="uploadIdCard"
            >
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">拖拽到这里 / 点击上传</div>
            </el-upload>

            <div v-if="createForm.idCardPhotoUrl" class="preview">
              <el-image
                  style="width: 140px; height: 140px; border-radius: 10px;"
                  :src="fileUrl(createForm.idCardPhotoUrl)"
                  :preview-src-list="[fileUrl(createForm.idCardPhotoUrl)]"
                  fit="cover"
              />
              <div class="preview-sub">{{ createForm.idCardPhotoUrl }}</div>
            </div>
          </div>

          <!-- 人脸照片 -->
          <div class="upload-block">
            <div class="upload-label">人脸照片</div>
            <el-upload
                ref="faceUp"
                class="uploader"
                drag
                action="http://localhost:8081/api/student/upload"
                name="file"
                :headers="uploadHeaders"
                accept="image/*"
                :show-file-list="false"
                :http-request="uploadFace"
            >
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">拖拽到这里 / 点击上传</div>
            </el-upload>

            <div v-if="createForm.facePhotoUrl" class="preview">
              <el-image
                  style="width: 140px; height: 140px; border-radius: 10px;"
                  :src="fileUrl(createForm.facePhotoUrl)"
                  :preview-src-list="[fileUrl(createForm.facePhotoUrl)]"
                  fit="cover"
              />
              <div class="preview-sub">{{ createForm.facePhotoUrl }}</div>
            </div>
          </div>


            <div v-if="createForm.facePhotoUrl" style="margin-top:10px;">
              <el-image
                  style="width: 140px; height: 140px; border-radius: 8px;"
                  :src="fileUrl(createForm.facePhotoUrl)"
                  :preview-src-list="[fileUrl(createForm.facePhotoUrl)]"
                  fit="cover"
              />
            </div>

        </el-card>
      </div>

      <span slot="footer">
        <el-button @click="createVisible=false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreate">提交预约</el-button>
      </span>
    </el-dialog>

    <!-- 查看照片（宿管/访客都用） -->
    <el-dialog title="访客核验资料" :visible.sync="photosVisible" width="720px">
      <div class="photo-grid">
        <div class="photo-item">
          <div class="photo-title">身份证照片</div>
          <el-empty v-if="!photoRow.idCardPhotoUrl" description="未上传" />
          <el-image
              v-else
              :src="fileUrl(photoRow.idCardPhotoUrl)"
              :preview-src-list="[fileUrl(photoRow.idCardPhotoUrl)]"
              fit="cover"
              style="width: 100%; height: 260px; border-radius: 12px;"
          />
        </div>

        <div class="photo-item">
          <div class="photo-title">人脸照片</div>
          <el-empty v-if="!photoRow.facePhotoUrl" description="未上传" />
          <el-image
              v-else
              :src="fileUrl(photoRow.facePhotoUrl)"
              :preview-src-list="[fileUrl(photoRow.facePhotoUrl)]"
              fit="cover"
              style="width: 100%; height: 260px; border-radius: 12px;"
          />
        </div>
      </div>

      <div class="photo-note" v-if="isTeacher && photoRow.status === 'APPROVED'">
        <el-alert
            type="info"
            show-icon
            :closable="false"
            title="宿管建议流程：查看照片 → 确认无误 → 点击「核检放行」(CHECKED_IN)；离开时点击「签退」(CHECKED_OUT)。"
        />
      </div>

      <span slot="footer">
        <el-button @click="photosVisible=false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import http from "@/utils/http";
import {
  listReservationsByRole,
  createReservation,
  approveReservation,
  rejectReservation,
  cancelReservation
} from "@/api/reservation";

export default {
  name: "ReservationList",

  data() {
    return {
      loading: false,
      submitting: false,

      rows: [],
      total: 0,
      page: 1,
      size: 10,

      filters: {
        keyword: "",
        status: ""
      },

      statusOptions: [
        "PENDING",
        "APPROVED",
        "REJECTED",
        "CANCELLED",
        "CHECKED_IN",
        "CHECKED_OUT"
      ],

      // dialogs
      createVisible: false,
      photosVisible: false,
      photoRow: {},

      createForm: {
        labId: "",
        equipmentId: "",
        startTime: "",
        endTime: "",
        purpose: "",
        remark: "",
        idCardPhotoUrl: "",
        facePhotoUrl: ""
      },
      uploadAction:
          (process.env.VUE_APP_API_BASE ||
              window.location.origin.replace(":8082", ":8081")) + "/api/student/upload",
    };
  },

  computed: {
    uploadHeaders() {
      const token = localStorage.getItem("Token") || "";
      return token ? { Authorization: `Bearer ${token}` } : {};
    },
    role() {
      return localStorage.getItem("Role") || "";
    },
    isStudent() {
      return this.role === "STUDENT";
    },
    isTeacher() {
      return this.role === "TEACHER";
    },
    roleLabel() {
      if (this.isStudent) return "访客（STUDENT）";
      if (this.isTeacher) return "宿舍管理员（TEACHER）";
      return this.role || "UNKNOWN";
    },

    apiBase() {
      // ✅ 你说的老办法：8082 -> 8081
      return (
          process.env.VUE_APP_API_BASE ||
          window.location.origin.replace(":8082", ":8081")
      );
    },

    displayRows() {
      const kw = (this.filters.keyword || "").trim().toLowerCase();
      const st = (this.filters.status || "").trim();

      return (this.rows || []).filter(r => {
        const okSt = !st || String(r.status || "") === st;
        if (!kw) return okSt;

        const hay = [
          r.labName, r.labId,
          r.equipmentName, r.equipmentId,
          r.purpose, r.remark,
          r.status,
          r.studentUsername, r.approverUsername
        ].map(x => String(x || "").toLowerCase()).join(" ");

        return okSt && hay.includes(kw);
      });
    }
  },

  mounted() {
    if (this.role === "TEACHER") {
      this.filters.status = "APPROVED";
    }
    this.load();


  },

  methods: {
    // =========================
    // URL 拼接：/uploads/xxx -> http://localhost:8081/uploads/xxx
    // =========================
    fileUrl(path) {
      if (!path) return "";
      if (String(path).startsWith("http")) return path;
      return "http://localhost:8081" + path;
    },

    fmt(v) {
      if (!v) return "-";
      return String(v).replace("T", " ").slice(0, 19);
    },

    tagType(status) {
      const s = String(status || "");
      if (s === "PENDING") return "warning";
      if (s === "APPROVED") return "success";
      if (s === "REJECTED") return "danger";
      if (s === "CANCELLED") return "info";
      if (s === "CHECKED_IN") return "primary";
      if (s === "CHECKED_OUT") return "";
      return "info";
    },

    hasAnyPhoto(row) {
      return !!(row && (row.idCardPhotoUrl || row.facePhotoUrl));
    },

    canCancel(row) {
      // 访客：未审核前可以撤销
      return row && (row.status === "PENDING" || row.status === "APPROVED");
    },

    async load() {
      this.loading = true;
      try {
        const params = { page: this.page - 1, size: this.size };
        if (this.filters.status) params.status = this.filters.status;
        // 这里不强依赖后端过滤，前端 displayRows 会兜底筛
        const res = await listReservationsByRole(this.role, params);
        const data = res && res.data ? res.data : {};
        this.rows = data.content || data || [];
        this.total = Number(data.totalElements || (Array.isArray(this.rows) ? this.rows.length : 0));
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "加载失败";
        this.$message.error("预约列表加载失败：" + msg);
      } finally {
        this.loading = false;
      }
    },

    onSearch() {
      this.page = 1;
      this.load();
    },

    onReset() {
      this.filters.keyword = "";
      this.filters.status = "";
      this.page = 1;
      this.load();
    },

    onPageChange(p) {
      this.page = p;
      this.load();
    },

    onSizeChange(s) {
      this.size = s;
      this.page = 1;
      this.load();
    },

    // =========================
    // 访客：新建预约 + 上传照片
    // =========================
    openCreate() {
      this.createVisible = true;
      this.createForm = {
        labId: "",
        equipmentId: "",
        startTime: "",
        endTime: "",
        purpose: "",
        remark: "",
        idCardPhotoUrl: "",
        facePhotoUrl: ""
      };
    },

    beforeUpload(file) {
      const okType = file.type && file.type.startsWith("image/");
      const okSize = file.size / 1024 / 1024 < 10; // 10MB
      if (!okType) this.$message.error("只能上传图片文件");
      if (!okSize) this.$message.error("图片不能超过 10MB");
      return okType && okSize;
    },


    async uploadCommon(file) {
      // ✅ 不用 el-upload 的 action，避免 action=undefined 警告
      // ✅ 用你的后端：/api/student/upload
      const fd = new FormData();
      fd.append("file", file);

      const res = await http({
        url: "/api/student/upload",
        method: "POST",
        data: fd,
        headers: { "Content-Type": "multipart/form-data" }
      });

      const url = res?.data?.url;
      if (!url) throw new Error("上传成功但未返回 url");
      return url;
    },

    async uploadIdCard(req) {
      try {
        const url = await this.uploadCommon(req.file);
        this.createForm.idCardPhotoUrl = url;
        this.$message.success("身份证照片上传成功");
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "上传失败";
        this.$message.error("身份证上传失败：" + msg);
      }
    },

    async uploadFace(req) {
      try {
        const url = await this.uploadCommon(req.file);
        this.createForm.facePhotoUrl = url;
        this.$message.success("人脸照片上传成功");
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "上传失败";
        this.$message.error("人脸上传失败：" + msg);
      }
    },

    async submitCreate() {
      // 简单校验
      if (!this.createForm.labId || !this.createForm.equipmentId) {
        this.$message.error("请填写楼栋号与宿舍号");
        return;
      }
      if (!this.createForm.startTime || !this.createForm.endTime) {
        this.$message.error("请选择开始/结束时间");
        return;
      }

      this.submitting = true;
      try {
        await createReservation(this.createForm);
        this.$message.success("预约提交成功（等待宿管审核）");
        this.createVisible = false;
        this.load();
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "提交失败";
        this.$message.error(msg);
      } finally {
        this.submitting = false;
      }
    },

    // =========================
    // 宿管：审批 + 核检放行/签退
    // =========================
    async approve(row) {
      try {
        await approveReservation(row.id, { decisionNote: "通过" });
        this.$message.success("已通过");
        this.load();
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "操作失败";
        this.$message.error(msg);
      }
    },

    async reject(row) {
      try {
        await rejectReservation(row.id, { decisionNote: "驳回" });
        this.$message.success("已驳回");
        this.load();
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "操作失败";
        this.$message.error(msg);
      }
    },
    onIdCardSuccess(res) {
      // 后端返回：{ filename, url }
      if (!res || !res.url) {
        this.$message.error("上传成功但未返回 url");
        return;
      }
      this.createForm.idCardPhotoUrl = res.url; // 形如 /uploads/xxx.png
      this.$message.success("身份证照片上传成功");
    },
    onFaceSuccess(res) {
      if (!res || !res.url) {
        this.$message.error("上传成功但未返回 url");
        return;
      }
      this.createForm.facePhotoUrl = res.url;
      this.$message.success("人脸照片上传成功");
    },

    onUploadError() {
      this.$message.error("上传失败（可能是403/接口不通）");
    },


    openPhotos(row) {
      this.photoRow = row ? { ...row } : {};
      this.photosVisible = true;
      console.log("idCard raw:", row.idCardPhotoUrl, "final:", this.imgSrc(row.idCardPhotoUrl));
    },
    imgSrc(path) {
      return this.fileUrl(path);
    },

    async checkIn(row) {
      try {
        await http({
          url: `/api/reservations/${row.id}/check-in`,
          method: "PUT"
        });
        this.$message.success("核检放行（CHECKED_IN）");
        this.load();
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "操作失败";
        this.$message.error(msg);
      }
    },

    async checkOut(row) {
      try {
        await http({
          url: `/api/reservations/${row.id}/check-out`,
          method: "PUT"
        });
        this.$message.success("已签退（CHECKED_OUT）");
        this.load();
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "操作失败";
        this.$message.error(msg);
      }
    },

    // =========================
    // 访客：撤销
    // =========================
    async cancel(row) {
      try {
        await this.$confirm("确定撤销该预约？", "提示", { type: "warning" });
        await cancelReservation(row.id, {});
        this.$message.success("已撤销");
        this.load();
      } catch (e) {
        if (e === "cancel") return;
        const msg = e?.response?.data?.message || e?.message || "操作失败";
        this.$message.error(msg);
      }
    }
  }
};
</script>

<style scoped>
.page {
  padding: 14px;
  background: #f6f7fb;
  min-height: calc(100vh - 50px);
}

.topbar {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 12px;
}

.title-area .h1 {
  font-size: 22px;
  font-weight: 800;
  color: #1f2d3d;
  line-height: 1.2;
}

.title-area .sub {
  margin-top: 6px;
  font-size: 12px;
  color: #6b7280;
}

.dot {
  margin: 0 8px;
  color: #c0c4cc;
}

.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", monospace;
}

.actions {
  display: flex;
  gap: 10px;
}

.card {
  border-radius: 14px;
  border: 1px solid #ebeef5;
  margin-bottom: 12px;
}

.filters {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.spacer {
  flex: 1;
}

.table {
  border-radius: 12px;
  overflow: hidden;
}

.cell-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cell-title {
  font-weight: 700;
  color: #111827;
}

.slash {
  margin: 0 6px;
  color: #9ca3af;
}

.cell-sub {
  font-size: 12px;
  color: #6b7280;
}

.text .line {
  font-size: 12px;
  color: #374151;
  margin: 2px 0;
}

.op {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.pager {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}

.dialog-grid {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 12px;
}

@media (max-width: 960px) {
  .dialog-grid {
    grid-template-columns: 1fr;
  }
}

.dialog-card {
  border-radius: 14px;
  border: 1px solid #ebeef5;
}

.section-title {
  font-weight: 800;
  color: #111827;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.upload-block {
  margin-bottom: 14px;
}

.upload-label {
  font-size: 13px;
  font-weight: 700;
  color: #374151;
  margin-bottom: 8px;
}

.preview {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.preview-sub {
  font-size: 12px;
  color: #6b7280;
  word-break: break-all;
}

.photo-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

@media (max-width: 720px) {
  .photo-grid {
    grid-template-columns: 1fr;
  }
}

.photo-item {
  border: 1px solid #ebeef5;
  border-radius: 14px;
  padding: 10px;
  background: #fafafa;
}

.photo-title {
  font-weight: 800;
  margin-bottom: 8px;
  color: #111827;
}

.photo-note {
  margin-top: 12px;
}
</style>