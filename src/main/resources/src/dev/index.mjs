const app = new Vue({
    el: '#status-app',
    data: {
        loaded: true,
        status: null,
        error: null
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
            fetch('/actuator/services-status')
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
});
