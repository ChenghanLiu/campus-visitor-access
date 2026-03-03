<template>
  <div>
    <el-card>
      <div style="margin-bottom:16px;">
        <el-button
            v-if="isAdmin"
            type="primary"
            @click="openCreate"
        >
          添加楼栋
        </el-button>
      </div>

      <!-- 卡片列表 -->
      <el-row :gutter="16" class="card-grid" v-loading="loading">
        <el-col
            v-for="item in list"
            :key="item.id"
            :xs="24" :sm="12" :md="8" :lg="6"
        >
          <div class="b-card" :style="cardBg(item)">
            <div class="b-overlay">

              <div>
                <div class="b-title">{{ item.name }}</div>
                <div class="b-sub">
                  {{ item.location || '未填写位置' }} · 容量 {{ item.capacity || '-' }}
                </div>
              </div>

              <div class="b-meta">
                <div class="meta-line">
                  开放时间：{{ item.openTime || '-' }} - {{ item.closeTime || '-' }}
                </div>
                <div class="meta-line">
                  负责人：{{ item.managerName || '-' }}
                </div>
              </div>

              <div class="b-actions">
                <el-button
                    size="mini"
                    type="success"
                    @click.stop="goDorms(item)"
                >
                  查看宿舍
                </el-button>

                <el-button
                    v-if="isAdmin"
                    size="mini"
                    @click.stop="openEdit(item)"
                >
                  编辑
                </el-button>

                <el-button
                    v-if="isAdmin"
                    size="mini"
                    type="danger"
                    @click.stop="remove(item.id)"
                >
                  删除
                </el-button>
              </div>

            </div>
          </div>
        </el-col>
      </el-row>

    </el-card>

    <!-- 对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px">
      <el-form :model="form" label-width="100px">

        <el-form-item label="名称">
          <el-input v-model="form.name"/>
        </el-form-item>

        <el-form-item label="类型">
          <el-input v-model="form.type"/>
        </el-form-item>

        <el-form-item label="位置">
          <el-input v-model="form.location"/>
        </el-form-item>

        <el-form-item label="容量">
          <el-input v-model="form.capacity" type="number"/>
        </el-form-item>

        <el-form-item label="开放时间">
          <el-input v-model="form.openTime"/>
        </el-form-item>

        <el-form-item label="关闭时间">
          <el-input v-model="form.closeTime"/>
        </el-form-item>

        <el-form-item label="负责人">
          <el-input v-model="form.managerName"/>
        </el-form-item>

        <el-form-item label="电话">
          <el-input v-model="form.managerPhone"/>
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="form.managerEmail"/>
        </el-form-item>

        <!-- 封面上传（纯前端绑定：localStorage） -->
        <el-form-item label="楼栋封面">
          <el-upload
              class="upload-demo"
              action="/api/admin/upload"
              :headers="uploadHeaders"
              name="file"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="onUploadSuccess"
              :on-error="onUploadError"
          >
            <el-button size="mini" type="primary">
              上传图片
            </el-button>
          </el-upload>

          <div class="hint">
            上传成功后会自动绑定到当前楼栋（仅本机有效，换电脑会丢）。
          </div>

          <div v-if="form._coverPreview" class="preview">
            <img :src="form._coverPreview" />
          </div>
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
  getAllLabs,
  createLab,
  updateLab,
  deleteLab
} from "@/api/lab";

export default {
  data() {
    return {
      loading: false,
      list: [],
      dialogVisible: false,
      dialogTitle: "",
      isEdit: false,
      editId: null,

      // 当前编辑中的楼栋 id（用于把上传的图片绑定到它）
      currentEditLabId: null,

      form: {
        name: "",
        type: "",
        location: "",
        capacity: "",
        openTime: "",
        closeTime: "",
        managerName: "",
        managerPhone: "",
        managerEmail: "",
        details: "",

        // 仅前端预览，不提交后端
        _coverPreview: ""
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
    uploadHeaders() {
      // ⚠️ 如果你 token 存的 key 不是 Token，改这里
      const token = localStorage.getItem("Token") || localStorage.getItem("token") || "";
      return token ? { Authorization: "Bearer " + token } : {};
    }
  },

  mounted() {
    this.load();
  },

  methods: {
    async load() {
      this.loading = true;
      try {
        const res = await getAllLabs();
        this.list = res.data || [];
      } catch (e) {
        this.$message.error("加载楼栋失败");
      } finally {
        this.loading = false;
      }
    },

    // --- 纯前端封面：localStorage ---
    getCoverMap() {
      try {
        return JSON.parse(localStorage.getItem("labImageMap") || "{}");
      } catch (e) {
        return {};
      }
    },
    setCoverForLab(labId, url) {
      const map = this.getCoverMap();
      map[String(labId)] = url; // 保存 /uploads/xxx.png
      localStorage.setItem("labImageMap", JSON.stringify(map));
    },
    getCoverUrl(labId) {
      const map = this.getCoverMap();
      return map[String(labId)] || "";
    },

    // 卡片背景（不写死端口）
    cardBg(item) {
      const url = this.getCoverUrl(item.id);
      if (!url) {
        return {
          background: "linear-gradient(135deg, #f2f4f8, #e9edf5)"
        };
      }
      return {
        backgroundImage: `url(${url})`,
        backgroundSize: "cover",
        backgroundPosition: "center"
      };
    },

    beforeUpload(file) {
      const okType = file.type && file.type.startsWith("image/");
      const okSize = file.size / 1024 / 1024 < 5;
      if (!okType) this.$message.error("只能上传图片文件");
      if (!okSize) this.$message.error("图片不能超过 5MB");
      return okType && okSize;
    },

    onUploadSuccess(res) {
      if (!this.currentEditLabId) {
        this.$message.warning("请先点击编辑某个楼栋，再上传封面");
        return;
      }
      if (!res || !res.url) {
        this.$message.error("上传返回数据异常");
        return;
      }

      // ✅ 关键：把 /uploads/... 变成绝对地址，避免 8082 吃掉路径跳首页
      const apiBase =
          process.env.VUE_APP_API_BASE ||
          window.location.origin.replace(":8082", ":8081"); // 你本地后端就是 8081

      const fullUrl = res.url.startsWith("http")
          ? res.url
          : apiBase + res.url;

      this.setCoverForLab(this.currentEditLabId, fullUrl);
      this.form._coverPreview = fullUrl;
      this.$message.success("封面绑定成功（仅本机有效）");
      this.$forceUpdate();
    },

    onUploadError() {
      this.$message.error("上传失败");
    },

    openCreate() {
      this.dialogTitle = "添加楼栋";
      this.isEdit = false;
      this.editId = null;
      this.currentEditLabId = null;
      this.dialogVisible = true;
      this.form = {
        name: "",
        type: "",
        location: "",
        capacity: "",
        openTime: "",
        closeTime: "",
        managerName: "",
        managerPhone: "",
        managerEmail: "",
        details: "",
        _coverPreview: ""
      };
    },

    openEdit(row) {
      this.dialogTitle = "编辑楼栋";
      this.isEdit = true;
      this.editId = row.id;
      this.currentEditLabId = row.id;
      this.form = { ...row, _coverPreview: this.getCoverUrl(row.id) || "" };
      this.dialogVisible = true;
    },

    goDorms(row) {
      this.$router.push({
        path: "/equipment",
        query: { labId: row.id, labName: row.name }
      });
    },

    async submit() {
      try {
        // 不把 _coverPreview 提交后端
        const payload = { ...this.form };
        delete payload._coverPreview;

        if (this.isEdit) {
          await updateLab(this.editId, payload);
          this.$message.success("更新成功");
        } else {
          await createLab(payload);
          this.$message.success("创建成功");
        }

        this.dialogVisible = false;
        this.load();
      } catch (e) {
        this.$message.error("操作失败");
      }
    },

    async remove(id) {
      try {
        await deleteLab(id);
        this.$message.success("删除成功");

        // 顺便把本机绑定的封面也删掉（可选）
        const map = this.getCoverMap();
        delete map[String(id)];
        localStorage.setItem("labImageMap", JSON.stringify(map));

        this.load();
      } catch (e) {
        this.$message.error("删除失败");
      }
    }
  }
};
</script>

<style scoped>
.card-grid {
  margin-top: 8px;
}

.b-card {
  height: 200px;
  border-radius: 14px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.10);
  cursor: pointer;
  transition: transform .15s ease;
}
.b-card:hover {
  transform: translateY(-2px);
}

.b-overlay {
  height: 100%;
  padding: 14px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: linear-gradient(
      180deg,
      rgba(0,0,0,0.20) 0%,
      rgba(0,0,0,0.55) 55%,
      rgba(0,0,0,0.70) 100%
  );
  color: #fff;
}

.b-title {
  font-size: 16px;
  font-weight: 700;
  line-height: 20px;
}

.b-sub {
  font-size: 12px;
  opacity: .9;
  margin-top: 6px;
}

.b-meta {
  font-size: 12px;
  opacity: .95;
}
.meta-line {
  margin-top: 6px;
}

.b-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.hint {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.preview {
  margin-top: 10px;
}
.preview img {
  width: 100%;
  max-width: 260px;
  height: 140px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #eee;
}
</style>