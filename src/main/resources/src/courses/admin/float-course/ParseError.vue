<template>
    <div class="alert alert-danger">
      <span>
        <strong>{{ error.errorType}}</strong>: {{ error.message }}
      </span>
      <span class="badge badge-danger">
        Line: {{ error.lineNumber}}
      </span>
      <span class="badge badge-danger">
        Position: {{ error.linePos }}
      </span>
    </div>
</template>

<script>
    export default {
      props: {
        error: {
          required: true
        }
      }
    }
</script>
