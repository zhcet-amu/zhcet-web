import UploadView from './float-course/UploadView.vue';

const header = $("meta[name='_csrf_header']").attr("content");
const token = $("meta[name='_csrf']").attr("content");

const uploaderApp = new Vue({
    el: '#app',
    components: {
      UploadView
    },
    data() {
        return {
            loading: false,
            state: null
        }
    },
    template: `
    <UploadView 
      :state="state"
      :loading="loading"
      @save="save"/>`,
    methods: {
        setState(state) {
            this.state = state;
        },
        save() {
            const items = this.state.items.map(item => item.item.code);

            const data = {
                items: items,
                state: this.state.state
            };

            this.loading = true;
            $.ajax({
                type: 'POST',
                url: '/admin/dean/api/float/confirm',
                contentType: 'application/json',
                data: JSON.stringify(data),
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(header, token)
                },
                success: (response) => {
                    this.loading = false;
                    if (response.success) {
                        toastr.success(response.floated.length + " " + response.message)
                        this.state = null
                    } else {
                        toastr.error(response.message)
                    }
                },
                error: (err) => {
                    this.loading = false;
                    console.error(err)
                }
            })
        }
    }
});

const uploaderForm = $(document.getElementById('float_course_uploader').form);
uploaderForm.submit(function (e) {
    e.preventDefault();
    const formData = new FormData(this);
    $.ajax({
        type: uploaderForm.attr('method'),
        url: uploaderForm.attr('action'),
        contentType: false,
        processData: false,
        data: formData,
        success: function (result) {
            uploaderApp.setState(result);
        }
    });
    return false;
});
