<template>
  <div>
    <v-layout class="px-2 pb-2">
      <v-flex>
        <v-btn color="info" @click="addBrand">新增品牌</v-btn>
      </v-flex>
      <v-flex class="xs4">
        <v-text-field label="搜索" hide-details append-icon="search" v-model="key"></v-text-field>
      </v-flex>
    </v-layout>
    <v-data-table
      :headers="headers"
      :items="brands"
      :pagination.sync="pagination"
      :total-items="totalBrands"
      :loading="loading"
      class="elevation-1"
    >
      <template slot="items" slot-scope="props">
        <tr>
          <td class="text-xs-center">{{ props.item.id }}</td>
          <td class="text-xs-center">{{ props.item.name }}</td>
          <td class="text-xs-center"><img :src="props.item.image" alt=""></td>
          <td class="text-xs-center">{{ props.item.letter }}</td>
          <td class="text-xs-center">
            <v-btn icon>
              <v-icon color="grey lighten-1">edit</v-icon>
            </v-btn>
            <v-btn icon>
              <v-icon color="grey lighten-1">delete</v-icon>
            </v-btn>
          </td>
        </tr>
      </template>
    </v-data-table>
    <v-dialog v-model="show" max-width="290">
    </v-dialog>
  </div>
</template>

<script>
  export default {
    name: "MyBrand",
    data(){
      return {
        totalBrands: 0,
        brands: [],
        loading: true,
        pagination: {},
        headers: [
          { text: '品牌id', align: 'center', sortable: true, value: 'id'},
          { text: '品牌名称', align: 'center', sortable: false, value: 'name'},
          { text: '品牌LOGO', align: 'center', sortable: false, value: 'image'},
          { text: '品牌首字母', align: 'center', sortable: true, value: 'letter'},
          { text: '操作', align: 'center',sortable: false, value:"name"}
        ],
        key: "",
        show: false
      }
    },
    created(){
      // 完成品牌数据加载
      this.loadData();
    },
    watch:{
      pagination:{
        deep:true,
        handler(){
          this.loadData();
        }
      },
      key(){
        this.loadData();
      }
    },
    methods:{
      loadData(){
        this.loading = true;
        // TODO 发起ajax，请求后台数据
        this.$http.get("/item/brand/page", {
          params:{
            page: this.pagination.page,
            rows: this.pagination.rowsPerPage,
            sortBy: this.pagination.sortBy,
            desc: this.pagination.descending,
            key: this.key
          }
        }).then(resp => {
          this.totalBrands = resp.data.total;
          this.brands = resp.data.items;
          this.loading = false;
        }).catch(error => {
          console.log(error);
          this.totalBrands = 0;
          this.brands = [];
          this.loading = false;
        });
      },
      addBrand(){
        this.show = true
      }
    }
  }
</script>

<style scoped>

</style>
