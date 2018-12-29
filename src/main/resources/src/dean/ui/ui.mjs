import DynamicUI from './DynamicUI.vue'
import { init } from "../../authentication/authentication";

init();

new Vue({
    el: '#app',
    components: {
        DynamicUI
    },
    template: '<DynamicUI />'
});
