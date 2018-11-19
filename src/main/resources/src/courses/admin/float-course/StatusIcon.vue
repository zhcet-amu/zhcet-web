<template>
  <i class="material-icons" style="cursor: pointer">{{ icon }}</i>
</template>

<script>
  export default {
    name: "StatusIcon",
    props: {
      type: {
        type: String,
        required: true
      }
    },
    computed: {
      icon() {
        switch (this.type) {
          case "WARNING":
            return 'warning';
          case "ERROR":
            return 'error';
          case "SUCCESS":
            return 'check_circle';
        }
      }
    }
  }
</script>
