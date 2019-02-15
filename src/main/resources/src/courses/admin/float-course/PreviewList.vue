<template>
  <table class="table display table-striped table-bordered" ref="table">
    <thead>
      <tr>
        <th v-for="header in headers" class="text-capitalize">{{ header }}</th>
        <th width="10px"></th>
      </tr>
    </thead>
    <tbody>
      <PreviewItem
        v-for="item in items"
        :item="item"
        :headers="headers" />
    </tbody>
  </table>
</template>

<script>
  import PreviewItem from './PreviewItem.vue'
  import {extractHeaders} from "./utils";

  export default {
    name: "PreviewList",
    components: {
      PreviewItem
    },
    props: {
      items: {
        type: Array
      }
    },
    computed: {
      headers() {
        if (!this.items)
          return [];

        return Array.from(extractHeaders(this.items));
      }
    },
    mounted() {
      $(this.$refs.table).DataTable({
        order: [],
        drawCallback: function () {
          $('[data-toggle="tooltip"]').tooltip()
        }
      })
    }
  }
</script>
