<template>
  <div class="status-bar row mb-2">
    <div class="col">
      <div v-if="loading" class="md-progress">
        <div class="indeterminate"></div>
      </div>
      <div class="float-right text-center">
        <button
          :class="['save-btn btn round btn-min-width', 'btn-outline-' + className]"
          :disabled="!valid || loading"
          @click="$emit('save')"
        >
          Save
        </button>
        <div v-if="type !== 'SUCCESS'" :class="['text-' + className]">{{ message }}</div>
      </div>
    </div>
  </div>
</template>

<script>
  import {getNormalizedErrorClass} from "./utils";

  export default {
    name: "StatusBar",
    props: {
      type: {
        type: String,
        required: true
      },
      valid: Boolean,
      loading: Boolean
    },
    computed: {
      className() {
        return getNormalizedErrorClass(this.type)
      },

      message() {
        return this.type === 'ERROR' ?
          'Resolve all errors to save' :
          'There are warnings, save at your own risk'
      }
    }
  }
</script>
