<template>
  <tr :class="['lighten', backgroundColor]">
    <td v-for="header in headers">{{ item.item[header] }}</td>
    <td v-if="item.message" data-toggle="tooltip" :data-original-title="item.message.message" style="cursor:pointer">
      <StatusIcon :type="item.message.type" />
    </td>
    <td v-else></td>
  </tr>
</template>

<script>
  import {getNormalizedErrorClass} from "./utils";
  import StatusIcon from './StatusIcon.vue';

  export default {
    name: "PreviewItem",
    components: {StatusIcon},
    props: {
      item: {
        type: Object,
        required: true
      },
      headers: {
        type: Array,
        required: true
      }
    },
    computed: {
      backgroundColor() {
        if (!this.item.message) {
          return '';
        }
        return `bg-${getNormalizedErrorClass(this.item.message.type)}`;
      }
    }
  }
</script>

