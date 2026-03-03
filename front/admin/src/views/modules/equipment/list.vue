<template>
  <div>
    <el-card>

      <!-- 筛选栏 -->
      <div style="margin-bottom:16px; display:flex; gap:10px; flex-wrap:wrap; align-items:center;">
        <el-select
            v-model="filters.category"
            placeholder="类别"
            clearable
            style="width: 200px;"
            @change="onSearch"
        >
          <el-option
              v-for="c in categoryOptions"
              :key="c"
              :label="c"
              :value="c"
          />
        </el-select>

        <el-input
            v-model="filters.keyword"
            placeholder="关键字（楼栋名称/型号/规格/描述）"
            clearable
            style="width: 280px;"
            @keyup.enter.native="onSearch"
        />
        <el-button type="text" @click="goBack">
          ← 返回楼栋列表
        </el-button>
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="onReset">重置</el-button>

        <el-button
            v-if="isAdmin"
            type="success"
            @click="openCreate"
        >
          新增宿舍
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table
          :data="displayRows"
          border
          v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="70" />


        <el-table-column prop="labName" label="楼栋名称" />
        <el-table-column prop="category" label="宿舍类型" width="140" />
        <el-table-column prop="name" label="宿舍房间号" />
        <el-table-column prop="model" label="房间型" width="140" />
        <el-table-column prop="specification" label="规格" />
        <el-table-column prop="quantity" label="床位数量" width="90" />
        <el-table-column prop="purchaseDate" label="起租日期" width="130" />
        <el-table-column prop="storageLocation" label="楼层" />
        <el-table-column label="状态" width="120">
          <template slot-scope="scope">
            {{ formatStatus(scope.row.status) }}
          </template>
        </el-table-column>
        <!-- 预约列：所有人可见 -->
        <!-- 预约列：所有人可见 -->
        <el-table-column label="预约" width="110" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="success" @click="openReserve(scope.row)">
              预约
            </el-button>
          </template>
        </el-table-column>


        <!-- 管理员操作列 -->
        <el-table-column v-if="isAdmin" label="操作" width="200">
          <template slot-scope="scope">

            <el-button size="mini" type="primary" @click="openEdit(scope.row)">
              编辑
            </el-button>
            <el-button size="mini" type="danger" @click="remove(scope.row.id)">
              删除
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
          @current-change="onPageChange"
          @size-change="onSizeChange"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="720px">
      <el-form :model="form" label-width="110px">

        <el-form-item label="楼栋号">
          <el-input v-model="form.labId" />
        </el-form-item>

        <el-form-item label="类别">
          <el-select v-model="form.category" placeholder="选择类别" style="width: 240px;">
            <el-option
                v-for="c in categoryOptions"
                :key="c"
                :label="c"
                :value="c"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>

        <el-form-item label="型号">
          <el-input v-model="form.model" />
        </el-form-item>

        <el-form-item label="规格">
          <el-input v-model="form.specification" />
        </el-form-item>

        <el-form-item label="数量">
          <el-input v-model="form.quantity" type="number" />
        </el-form-item>

        <el-form-item label="购置日期">
          <el-date-picker
              v-model="form.purchaseDate"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期"
          />
        </el-form-item>

        <el-form-item label="存放位置">
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
    <el-dialog title="新建预约" :visible.sync="reserveDialogVisible" width="600px">
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
        <el-button @click="reserveDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReserve">提交预约</el-button>
      </span>
    </el-dialog>


  </div>
</template>

<script>
import {
  listEquipment,
  createEquipment,
  updateEquipment,
  deleteEquipment
} from "@/api/equipment";
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

      filters: {
        category: "",
        keyword: ""
      },

      categoryOptions: [
        "显微镜",
        "玻璃器皿",
        "安全器材",
        "化学试剂",
        "电子设备",
        "实验室器材",
        "其他"
      ],

      dialogVisible: false,
      dialogTitle: "",
      isEdit: false,
      editId: null,

      // ✅ 宿舍新增/编辑表单（原来的）
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

      // ✅ 预约弹窗：必须放在根级别（和 form 同级）
      reserveDialogVisible: false,
      reserveForm: {
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
    isAdmin() {
      return this.role === "ADMIN";
    },

    // 前端兜底过滤（后端不支持 keyword/category 也能在当前页筛）
    displayRows() {
      const cat = (this.filters.category || "").trim();
      const kw = (this.filters.keyword || "").trim().toLowerCase();

      return (this.rows || []).filter(r => {
        const okCat = !cat || String(r.category || "") === cat;
        if (!kw) return okCat;

        const hay = [
          r.name,
          r.model,
          r.specification,
          r.description,
          r.labName,
          r.category
        ].map(x => String(x || "").toLowerCase()).join(" ");

        return okCat && hay.includes(kw);
      });
    }
  },


  mounted() {
    this.fixedLabId = this.$route.query.labId ? Number(this.$route.query.labId) : null;
    this.fixedLabName = this.$route.query.labName || "";
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
    // =========================
    // 图片静态映射（不改后端）
    // 图片放在：public/equipment/ 下
    // =========================
    _img(fileName) {
      // 处理中文文件名路径
      return encodeURI(`/equipment/${fileName}`);
    },
    getEquipmentImage(row) {
      const category = String(row?.category || "").trim();
      const name = String(row?.name || "").trim();

      // 1) 先按名称关键字（更精准）
      if (name.includes("电子显微镜")) return this._img("电子显微镜.png");
      if (name.includes("光学显微镜") || name.includes("显微镜")) return this._img("光学显微镜.png");
      if (name.includes("灭火器")) return this._img("二氧化碳灭火器.png");
      if (name.includes("烧杯")) return this._img("250ml烧杯.png");
      if (name.includes("氢化钠")) return this._img("氢化钠.png");

      // 2) 再按类别（兜底）
      const map = {
        "显微镜": "光学显微镜.png",
        "电子设备": "电子显微镜.png",
        "玻璃器皿": "250ml烧杯.png",
        "安全器材": "二氧化碳灭火器.png",
        "化学试剂": "氯化钠.png",
        "实验室器材": "250ml烧杯.png"
      };

      if (map[category]) return this._img(map[category]);

      // 3) 最后兜底：用已有的一张图当默认（你当前目录没有 default.png）
      return this._img("250ml烧杯.png");
    },

    async load() {
      this.loading = true;

      const params = {
        page: this.page - 1,
        size: this.size
      };
      if (this.fixedLabId) params.labId = this.fixedLabId;
      if (this.fixedLabId) this.form.labId = this.fixedLabId;

      // 如果后端未来支持 category/keyword，这里会自动生效；不支持也不会影响
      if (this.filters.category) params.category = this.filters.category;
      if (this.filters.keyword) params.keyword = this.filters.keyword;

      try {
        const res = await listEquipment(params);
        const data = res && res.data ? res.data : {};

        this.rows = data.content || [];
        this.total = Number(data.totalElements || 0);

        // 动态补全类别（你未来扩库不用改前端）
        const set = new Set(this.categoryOptions);
        (this.rows || []).forEach(r => {
          if (r && r.category) set.add(String(r.category));
        });
        this.categoryOptions = Array.from(set);

      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "加载失败";
        this.$message.error("设备列表加载失败：" + msg);
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
      this.$router.push('/labs')   // 不是 /lab，是你 router 里的 /labs
    },
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
        remark: ""
      };

      this.reserveDialogVisible = true;
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

    onPageChange(p) {
      this.page = p;
      this.load();
    },

    onSizeChange(s) {
      this.size = s;
      this.page = 1;
      this.load();
    },

    openCreate() {
      this.dialogTitle = "新增设备";
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
      this.dialogTitle = "编辑设备";
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
        await this.$confirm("确定删除该设备？", "提示", { type: "warning" });
        await deleteEquipment(id);
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
