<template>
  <div>
    <el-card>

      <div style="margin-bottom:16px;">
        <el-button
            v-if="isAdmin"
            type="primary"
            @click="openCreate"
        >
          新建实验室
        </el-button>
      </div>

      <el-table :data="list" border v-loading="loading">

        <el-table-column prop="id" label="ID" width="70"/>
        <el-table-column prop="name" label="名称"/>
        <el-table-column prop="type" label="类型"/>
        <el-table-column prop="location" label="位置"/>
        <el-table-column prop="capacity" label="容量"/>
        <el-table-column prop="openTime" label="开放时间"/>
        <el-table-column prop="closeTime" label="关闭时间"/>
        <el-table-column prop="managerName" label="负责人"/>
        <el-table-column prop="managerPhone" label="电话"/>
        <el-table-column prop="managerEmail" label="邮箱"/>

        <el-table-column
            v-if="isAdmin"
            label="操作"
            width="180"
        >
          <template slot-scope="scope">
            <el-button
                size="mini"
                type="primary"
                @click="openEdit(scope.row)"
            >
              编辑
            </el-button>

            <el-button
                size="mini"
                type="danger"
                @click="remove(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>

      </el-table>
    </el-card>

    <!-- 对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible">
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

        <el-form-item label="详情">
          <el-input v-model="form.details"/>
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
        details: ""
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
        const res = await getAllLabs();
        this.list = res.data || [];
      } catch (e) {
        this.$message.error("加载实验室失败");
      } finally {
        this.loading = false;
      }
    },

    openCreate() {
      this.dialogTitle = "新建实验室";
      this.isEdit = false;
      this.dialogVisible = true;
      this.form = {};
    },

    openEdit(row) {
      this.dialogTitle = "编辑实验室";
      this.isEdit = true;
      this.editId = row.id;
      this.form = { ...row };
      this.dialogVisible = true;
    },

    async submit() {
      try {
        if (this.isEdit) {
          await updateLab(this.editId, this.form);
          this.$message.success("更新成功");
        } else {
          await createLab(this.form);
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
        this.load();
      } catch (e) {
        this.$message.error("删除失败");
      }
    }
  }
};
</script>
