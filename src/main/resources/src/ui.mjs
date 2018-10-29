import { init } from "./authentication/authentication";

init();

const storageRef = firebase.storage().ref();
const slidesRef = storageRef.child('login/slides/');

function cloneImageUpload() {
    return {
        paused: false,
        uploading: false,
        uploadTask: null,
        uploadedBytes: 0,
        totalBytes: 0
    }
}

new Vue({
    el: '#app',
    firebase: {
        images: {
            source: firebase.database().ref('login/slides'),
            readyCallback: function () {
                this.imagesLoading = false;
            },
            cancelCallback: function () {
                this.imagesLoading = false;
            }
        }
    },
    data: {
        mounting: false,
        imagesLoading: true,
        uploadedFile: null,
        imageUpload: cloneImageUpload(),
        imageToShow: null,
        file: null,
        imageUri: null,
        sections: [{
            title: 'Slides',
            description: 'Change the images in slides on login page',
            active: true
        }]
    },
    computed: {
        progress: function () {
            return this.imageUpload.totalBytes === 0 ? 0 : this.imageUpload.uploadedBytes / this.imageUpload.totalBytes * 100;
        }
    },
    methods: {
        reset: function () {
            // reset form to initial state
            this.imageUpload = cloneImageUpload();
            this.file = null;
            this.imageUri = null;
        },
        save: function () {
            const newImages = [];
            for (let i = 0; i < this.images.length; i++)
                newImages.push(this.images[i]['.value']);
            newImages.push(this.uploadedFile);
            this.$firebaseRefs.images.set(newImages)
                .then(function () {
                    toastr.success('Saved Images');
                    this.reset();
                    this.uploadedFile = null;
                }.bind(this)).catch(function (error) {
                console.log(error);
                toastr.error('Error saving images');
                this.reset();
                this.uploadedFile = null;
            }.bind(this))
        },
        filesChange: function (fileList) {
            if (!fileList.length) return;

            this.file = fileList[0];
            this.imageUri = URL.createObjectURL(this.file);
        },
        upload: function () {
            this.imageUpload.uploading = true;

            const fileRef = slidesRef.child(new Date().toISOString() + this.file.name);
            this.imageUpload.uploadTask = fileRef.put(this.file);

            this.imageUpload.uploadTask.on('state_changed', function (snapshot) {
                this.imageUpload.totalBytes = snapshot.totalBytes;
                this.imageUpload.uploadedBytes = snapshot.bytesTransferred;
                this.imageUpload.paused = snapshot.state === firebase.storage.TaskState.PAUSED;
            }.bind(this), function (error) {
                this.reset();
                console.log('Failed' + error)
            }.bind(this), function () {
                this.uploadedFile = this.imageUpload.uploadTask.snapshot.downloadURL;
                this.reset();
            }.bind(this))
        },
        pause: function () {
            this.imageUpload.uploadTask.pause()
        },
        cancel: function () {
            this.imageUpload.uploadTask.cancel()
        },
        resume: function () {
            this.imageUpload.uploadTask.resume()
        },

        showImage: function (imageObj) {
            this.imageToShow = imageObj['.value'];
            $('.modal').modal();
        },

        deleteImage: function () {
            const newImages = [];
            for (let i = 0; i < this.images.length; i++) {
                const image = this.images[i]['.value'];
                if (image !== this.imageToShow)
                    newImages.push(image);
            }
            newImages.push(this.uploadedFile);
            this.$firebaseRefs.images.set(newImages)
                .then(function () {
                    toastr.success('Image Deleted');
                }.bind(this)).catch(function (error) {
                console.log(error);
                toastr.error('Error deleting image');
            }.bind(this))

        }
    },
    mounted: function() {
        this.reset();
    }
});