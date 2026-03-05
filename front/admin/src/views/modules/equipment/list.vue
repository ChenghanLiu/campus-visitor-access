<template>
  <div class="page">
    <el-card class="card" shadow="never">
      <div class="topbar">
        <div class="title">
          <div class="h1">宿舍列表</div>
          <div class="sub">
            楼栋：<span class="mono">{{ fixedLabName || fixedLabId || "-" }}</span>
          </div>
        </div>
        <div class="actions">
          <el-button @click="goBack">返回楼栋</el-button>
          <el-button v-if="isAdmin" type="primary" @click="openCreate">新增宿舍</el-button>
        </div>
      </div>

      <div class="filters">
        <el-select v-model="filters.category" placeholder="类别" clearable style="width: 180px;">
          <el-option v-for="c in categoryOptions" :key="c" :label="c" :value="c" />
        </el-select>

        <el-input
            v-model="filters.keyword"
            placeholder="关键词（名称/描述）"
            style="width: 260px;"
            clearable
            @keyup.enter.native="onSearch"
        />

        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="onReset">重置</el-button>

        <div class="spacer"></div>
      </div>
    </el-card>

    <el-card class="card" shadow="never">
      <el-table :data="displayRows" v-loading="loading" class="table" stripe>


        <el-table-column label="宿舍信息" min-width="240">
          <template slot-scope="scope">
            <div class="cell-main">
              <div class="cell-title">
                {{ scope.row.name || "-" }}
                <span class="slash">/</span>
                <span class="mono">ID: {{ scope.row.id }}</span>
              </div>
              <div class="cell-sub">
                类别：{{ scope.row.category || "-" }} |
                型号：{{ scope.row.model || "-" }} |
                数量：{{ scope.row.quantity ?? "-" }}
              </div>
              <div class="cell-sub">
                位置：{{ scope.row.storageLocation || "-" }} |
                状态：{{ formatStatus(scope.row.status) }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="规格/描述" min-width="220">
          <template slot-scope="scope">
            <div class="text">
              <div class="line">规格：{{ scope.row.specification || "-" }}</div>
              <div class="line">描述：{{ scope.row.description || "-" }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- ✅ 访客：一键预约 -->
        <el-table-column label="预约" width="110" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="success" @click="openReserve(scope.row)">预约</el-button>
          </template>
        </el-table-column>

        <!-- ✅ 管理员：编辑/删除 -->
        <el-table-column v-if="isAdmin" label="管理" width="150" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button size="mini" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="remove(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          class="pager"
          background
          layout="total, sizes, prev, pager, next"
          :total="total"
          :page-size="size"
          :page-sizes="[10, 20, 50]"
          @current-change="onPageChange"
          @size-change="onSizeChange"
      />
    </el-card>

    <!-- ✅ 新增/编辑宿舍 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="720px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="楼栋号">
          <el-input v-model="form.labId" />
        </el-form-item>

        <el-form-item label="类别">
          <el-select v-model="form.category" placeholder="选择类别" style="width: 240px;">
            <el-option v-for="c in categoryOptions" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>

        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>

        <el-form-item label="最大容纳">
          <el-input v-model="form.model" />
        </el-form-item>

        <el-form-item label="规格">
          <el-input v-model="form.specification" />
        </el-form-item>

        <el-form-item label="数量">
          <el-input v-model="form.quantity" type="number" />
        </el-form-item>

        <el-form-item label="开放日期">
          <el-date-picker
              v-model="form.purchaseDate"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期"
          />
        </el-form-item>

        <el-form-item label="宿舍位置">
          <el-input v-model="form.storageLocation" />
        </el-form-item>

        <el-form-item label="状态">
          <el-input v-model="form.status" placeholder="例如 AVAILABLE" />
        </el-form-item>

        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>

      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </span>
    </el-dialog>

    <!-- ✅ 一键预约弹窗：对齐 ReservationList 的上传 -->
    <el-dialog title="新建预约" :visible.sync="reserveDialogVisible" width="860px">
      <div class="dialog-grid">
        <el-card class="dialog-card" shadow="never">
          <div class="section-title">
            <i class="el-icon-edit-outline"></i>
            <span>预约信息</span>
          </div>

          <el-form :model="reserveForm" label-width="110px">
            <el-form-item label="楼栋号">
              <el-input v-model="reserveForm.labId" disabled />
            </el-form-item>

            <el-form-item label="宿舍号">
              <el-input v-model="reserveForm.equipmentId" disabled />
            </el-form-item>

            <el-form-item label="开始时间">
              <el-date-picker
                  v-model="reserveForm.startTime"
                  type="datetime"
                  value-format="yyyy-MM-ddTHH:mm:ss"
                  style="width: 100%;"
              />
            </el-form-item>

            <el-form-item label="结束时间">
              <el-date-picker
                  v-model="reserveForm.endTime"
                  type="datetime"
                  value-format="yyyy-MM-ddTHH:mm:ss"
                  style="width: 100%;"
              />
            </el-form-item>

            <el-form-item label="预约说明">
              <el-input v-model="reserveForm.purpose" />
            </el-form-item>

            <el-form-item label="备注">
              <el-input v-model="reserveForm.remark" type="textarea" :rows="2" />
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="dialog-card" shadow="never">
          <div class="section-title">
            <i class="el-icon-picture-outline"></i>
            <span>身份核验资料</span>
          </div>

          <div class="upload-block">
            <div class="upload-label">身份证照片）</div>
            <el-upload
                drag
                action="http://localhost:8081/api/student/upload"
                :headers="uploadHeaders"
                :http-request="uploadReserveIdCard"
                :show-file-list="false"
                name="file"
                accept="image/*"
                :before-upload="beforeUpload"
            >
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">拖拽/点击上传</div>
              <div class="el-upload__tip" slot="tip">支持 jpg/png/webp 等图片格式</div>
            </el-upload>

            <div v-if="reserveForm.idCardPhotoUrl" class="preview">
              <el-image
                  style="width: 150px; height: 150px; border-radius: 12px;"
                  :src="fileUrl(reserveForm.idCardPhotoUrl)"
                  :preview-src-list="[fileUrl(reserveForm.idCardPhotoUrl)]"
                  fit="cover"
              />
              <div class="preview-sub">{{ reserveForm.idCardPhotoUrl }}</div>
            </div>
          </div>

          <div class="upload-block">
            <div class="upload-label">人脸照片</div>
            <el-upload
                drag
                action="http://localhost:8081/api/student/upload"
                :headers="uploadHeaders"
                :http-request="uploadReserveFace"
                :show-file-list="false"
                name="file"
                accept="image/*"
                :before-upload="beforeUpload"
            >
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">拖拽/点击上传</div>
            </el-upload>

            <div v-if="reserveForm.facePhotoUrl" class="preview">
              <el-image
                  style="width: 150px; height: 150px; border-radius: 12px;"
                  :src="fileUrl(reserveForm.facePhotoUrl)"
                  :preview-src-list="[fileUrl(reserveForm.facePhotoUrl)]"
                  fit="cover"
              />
              <div class="preview-sub">{{ reserveForm.facePhotoUrl }}</div>
            </div>
          </div>
        </el-card>
      </div>

      <span slot="footer">
        <el-button @click="reserveDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReserve">提交预约</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import http from "@/utils/http";
import { listEquipment, createEquipment, updateEquipment, deleteEquipment } from "@/api/equipment";
import { createReservation } from "@/api/reservation";

export default {
  data() {
    return {
      fixedLabId: null,
      fixedLabName: "",
      loading: false,

      rows: [],
      total: 0,
      page: 1,
      size: 10,

      filters: { category: "", keyword: "" },

      categoryOptions: [
        "东区",
        "西区",
        "南区",
        "北区",
        "其他"
      ],

      dialogVisible: false,
      dialogTitle: "",
      isEdit: false,
      editId: null,

      form: {
        labId: "",
        category: "",
        name: "",
        model: "",
        specification: "",
        quantity: "",
        purchaseDate: "",
        storageLocation: "",
        status: "",
        description: ""
      },

      // ✅ 一键预约表单：加上两张照片字段
      reserveDialogVisible: false,
      reserveForm: {
        labId: "",
        equipmentId: "",
        startTime: "",
        endTime: "",
        purpose: "",
        remark: "",
        idCardPhotoUrl: "",
        facePhotoUrl: ""
      },
      uploadHeaders: {
        Authorization: "Bearer " + (localStorage.getItem("Token") || localStorage.getItem("token") || "")
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
    apiBase() {
      // ✅ 你之前用过的老办法：8082 -> 8081
      return process.env.VUE_APP_API_BASE || window.location.origin.replace(":8082", ":8081");
    },
    displayRows() {
      const cat = (this.filters.category || "").trim();
      const kw = (this.filters.keyword || "").trim().toLowerCase();

      return (this.rows || []).filter(r => {
        const okCat = !cat || String(r.category || "") === cat;
        if (!kw) return okCat;

        const hay = [
          r.name, r.model, r.specification, r.description, r.labName, r.category
        ].map(x => String(x || "").toLowerCase()).join(" ");

        return okCat && hay.includes(kw);
      });
    }
  },

  mounted() {
    this.fixedLabId = this.$route.query.labId ? Number(this.$route.query.labId) : null;
    this.fixedLabName = this.$route.query.labName || "";
    this.uploadHeaders = {
      Authorization: "Bearer " + (localStorage.getItem("Token") || localStorage.getItem("token") || "")
    };
    this.load();
  },

  watch: {
    "$route.query.labId"() {
      this.fixedLabId = this.$route.query.labId ? Number(this.$route.query.labId) : null;
      this.fixedLabName = this.$route.query.labName || "";
      this.load();
    }
  },

  methods: {
    // ✅ /uploads/xxx -> http://localhost:8081/uploads/xxx
    fileUrl(path) {
      if (!path) return "";
      if (String(path).startsWith("http")) return path;
      return this.apiBase + path;
    },



    // =========================
    // 静态图片映射（原逻辑）
    // =========================
    _img(fileName) {
      return encodeURI(`/equipment/${fileName}`);
    },


    async load() {
      this.loading = true;
      const params = { page: this.page - 1, size: this.size };
      if (this.fixedLabId) params.labId = this.fixedLabId;
      if (this.fixedLabId) this.form.labId = this.fixedLabId;
      if (this.filters.category) params.category = this.filters.category;
      if (this.filters.keyword) params.keyword = this.filters.keyword;

      try {
        const res = await listEquipment(params);
        const data = res && res.data ? res.data : {};
        this.rows = data.content || [];
        this.total = Number(data.totalElements || 0);

        const set = new Set(this.categoryOptions);
        (this.rows || []).forEach(r => r?.category && set.add(String(r.category)));
        this.categoryOptions = Array.from(set);
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "加载失败";
        this.$message.error("宿舍列表加载失败：" + msg);
        this.rows = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
    },

    onSearch() {
      this.page = 1;
      this.load();
    },
    onReset() {
      this.filters.category = "";
      this.filters.keyword = "";
      this.page = 1;
      this.load();
    },
    goBack() {
      this.$router.push("/labs");
    },

    // =========================
    // ✅ 一键预约 + 上传（对齐 ReservationList 成功逻辑）
    // =========================
    openReserve(row) {
      const labId = this.$route.query.labId;
      if (!labId) {
        this.$message.error("缺少楼栋ID，请从楼栋页面进入宿舍列表");
        return;
      }

      this.reserveForm = {
        labId: Number(labId),
        equipmentId: row.id,
        startTime: "",
        endTime: "",
        purpose: "",
        remark: "",
        idCardPhotoUrl: "",
        facePhotoUrl: ""
      };
      this.reserveDialogVisible = true;
    },

    beforeUpload(file) {
      const okType = file && file.type && file.type.startsWith("image/");
      const okSize = file.size / 1024 / 1024 <= 8; // 8MB
      if (!okType) this.$message.error("只能上传图片文件");
      if (!okSize) this.$message.error("图片不能超过 8MB");
      return okType && okSize;
    },

    async uploadCommon(file) {
      const fd = new FormData();
      fd.append("file", file);

      // ✅ 这里走你已经打通的接口（token 由 http 拦截器自动带）
      const res = await http({
        url: "http://localhost:8081/api/student/upload",
        method: "POST",
        data: fd,
        headers: { "Content-Type": "multipart/form-data" }
      });

      const url = res?.data?.url;
      if (!url) throw new Error("上传成功但未返回 url");
      return url;
    },

    async uploadReserveIdCard(req) {
      try {
        const url = await this.uploadCommon(req.file);
        this.reserveForm.idCardPhotoUrl = url;
        this.$message.success("身份证照片上传成功");
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "上传失败";
        this.$message.error("身份证上传失败：" + msg);
      }
    },

    async uploadReserveFace(req) {
      try {
        const url = await this.uploadCommon(req.file);
        this.reserveForm.facePhotoUrl = url;
        this.$message.success("人脸照片上传成功");
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "上传失败";
        this.$message.error("人脸上传失败：" + msg);
      }
    },

    async submitReserve() {
      try {
        await createReservation(this.reserveForm);
        this.$message.success("预约提交成功");
        this.reserveDialogVisible = false;
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "预约失败";
        this.$message.error(msg);
      }
    },

    // =========================
    // 管理员 CRUD（原逻辑）
    // =========================
    openCreate() {
      this.dialogTitle = "新增宿舍";
      this.isEdit = false;
      this.editId = null;
      this.form = {
        labId: "",
        category: "",
        name: "",
        model: "",
        specification: "",
        quantity: "",
        purchaseDate: "",
        storageLocation: "",
        status: "",
        description: ""
      };
      this.dialogVisible = true;
    },

    formatStatus(status) {
      const map = {
        AVAILABLE: "可用",
        UNAVAILABLE: "不可用",
        IN_USE: "使用中",
        MAINTENANCE: "维护中"
      };
      return map[status] || status;
    },

    openEdit(row) {
      this.dialogTitle = "编辑宿舍";
      this.isEdit = true;
      this.editId = row.id;
      this.form = {
        labId: row.labId,
        category: row.category,
        name: row.name,
        model: row.model,
        specification: row.specification,
        quantity: row.quantity,
        purchaseDate: row.purchaseDate,
        storageLocation: row.storageLocation,
        status: row.status,
        description: row.description
      };
      this.dialogVisible = true;
    },

    async submit() {
      try {
        if (this.isEdit) {
          await updateEquipment(this.editId, this.form);
          this.$message.success("更新成功");
        } else {
          await createEquipment(this.form);
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
        await this.$confirm("确定删除该宿舍？", "提示", { type: "warning" });
        await deleteEquipment(id);
        this.$message.success("删除成功");
        this.load();
      } catch (e) {
        if (e === "cancel") return;
        const msg = e?.response?.data?.message || e?.message || "删除失败";
        this.$message.error(msg);
      }
    },

    onPageChange(p) {
      this.page = p;
      this.load();
    },
    onSizeChange(s) {
      this.size = s;
      this.page = 1;
      this.load();
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

.title .h1 {
  font-size: 22px;
  font-weight: 800;
  color: #1f2d3d;
  line-height: 1.2;
}

.title .sub {
  margin-top: 6px;
  font-size: 12px;
  color: #6b7280;
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
  font-weight: 800;
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
  font-weight: 800;
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
</style>