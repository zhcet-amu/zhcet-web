<template>
    <div>
        <div v-if="!loaded">
            <div class="md-progress">
                <div class="indeterminate"></div>
            </div>
        </div>
        <div v-show="loaded" style="display: none">
            <div v-if="status">
                <button @click="reload" class="btn btn-outline-success float-right">Reload</button>
                <div v-for="(serviceStatus, service) in status">
                    <div v-if="typeof(serviceStatus) === 'boolean'">
                        <span><strong>{{service | normalize}}</strong></span>
                        <span v-if="serviceStatus" class="material-icons">check_circle</span>
                        <span v-else class="material-icons text-danger">clear</span>
                    </div>
                    <div v-else>
                        <span><strong>{{service | normalize}}</strong></span>
                        <div v-for="(serviceStatus, service) in serviceStatus">
                            <div v-if="typeof(serviceStatus) === 'boolean'" class="pl-2">
                                <span>{{service | normalize}}</span>&nbsp;&nbsp;
                                <span v-if="serviceStatus" class="material-icons text-success">check_circle</span>
                                <span v-else class="material-icons text-danger">clear</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div v-else>
                <div class="md-progress">
                    <div class="indeterminate"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            loaded: true,
            status: null,
            error: null
        }
    },
    filters: {
        normalize(value) {
            const split = value.replace(/([a-z](?=[A-Z]))/g, '$1 ')
            return split[0].toUpperCase() + split.slice(1)
        }
    },
    methods: {
        reload() {
            this.status = null
            fetch('/actuator/servicestatus')
                .then(result => result.json())
                .then(json => {
                    this.status = {
                        ...json,
                        error: false
                    }
                })
                .catch(error => {
                    console.error(error);
                    this.error = error
                })
        }
    },
    created() {
        this.reload()
    }
}
</script>
