<template>
  <main>
    <div class="card">
      <div class="card-content collapse show">
        <div v-if="state && state.csv" class="card-body">
          <ParseError
            v-for="error in state.csv.parseErrors"
            :error="error" />
          <div v-if="isParsedSuccessfully" class="successful-parse">
            <StatusBar
              :type="state.state.type"
              :valid="state.state.valid"
              :loading="loading"
              @save="$emit('save')"/>
            <PreviewList :items="state.items" />
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
  import StatusBar from './StatusBar.vue';
  import PreviewList from './PreviewList.vue';
  import ParseError from './ParseError.vue';

  export default {
    name: "UploadView",
    props: ['state', 'loading'],
    components: {
      StatusBar,
      PreviewList,
      ParseError
    },
    computed: {
      isParsedSuccessfully() {
        return this.state && this.state.parsed && this.state.csv.successful;
      }
    }
  }
</script>
