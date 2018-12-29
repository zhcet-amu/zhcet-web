<template>
    <div>
        <div class="loading" v-if="mounting">
            <div class="md-progress">
                <div class="indeterminate"></div>
            </div>
        </div>
        <div class="loaded" style="display: none" v-show="!mounting">
            <div class="row">
                <div class="col-12 col-lg-3">
                    <div class="list-group">
                        <a v-for="section in sections" :href="'#' + section.title"
                           class="list-group-item list-group-item-action"
                           :class="[ { active: section.active } ]">
                            <div>{{ section.title }}</div>
                            <small>{{ section.description }}</small>
                        </a>
                    </div>
                </div>
                <div class="col-12 col-lg-9">
                    <div class="mui-panel text-center">
                        <div v-if="imagesLoading">
                            <div class="md-progress">
                                <div class="indeterminate"></div>
                            </div>
                        </div>
                        <span v-for="image in images" :key="image.key" class="mr-1">
                                <img height="150px" width="150px" :src="image['.value']" alt="" class="img-thumbnail hover-cursor" @click="showImage(image)">
                            </span>
                        <div class="file-container">
                            <label class="mui-btn mui-btn--raised mui-btn--primary">
                                <input :disabled="imageUpload.uploading" @change="filesChange($event.target.files)"
                                       class="upload-input" type="file" name="file" style="display: none" accept="image/*">
                                <span>Upload Image</span>
                            </label>
                        </div>
                        <div v-if="uploadedFile">
                            <div class="alert alert-dismissible alert-success">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <strong>Success!</strong>
                                <span>Image uploaded successfully</span>
                            </div>
                            <img height="200px" :src="uploadedFile" /><br>
                            <button @click="save"
                                    class="mui-btn mui-btn--fab mui-btn--primary">
                                <i class="material-icons md-24 md-light">save</i>
                            </button>
                        </div>
                        <div v-if="file">
                            <div v-if="!imageUri">
                                Loading...
                            </div>
                            <div v-else>
                                <img height="200px" :src="imageUri" /><br>
                                <button v-if="!imageUpload.uploading" @click="upload"
                                        class="mui-btn mui-btn--fab mui-btn--primary">
                                    <i class="material-icons md-24 md-light">file_upload</i>
                                </button>
                            </div>
                            <div v-if="imageUpload.uploading">
                                <span v-if="imageUpload.uploadTask">
                                    <button v-if="!imageUpload.paused" @click="pause"
                                            class="mui-btn mui-btn--primary mui-btn--fab">
                                        <i class="material-icons md-18 md-light">pause</i>
                                    </button>
                                    <button v-if="imageUpload.paused" @click="resume"
                                            class="mui-btn mui-btn--primary mui-btn--fab">
                                        <i class="material-icons md-18 md-light">play_arrow</i>
                                    </button>
                                    <button @click="cancel"
                                            class="mui-btn mui-btn--primary mui-btn--fab">
                                        <i class="material-icons md-18 md-light">stop</i>
                                    </button>
                                </span>
                                <span>{{ progress }} %</span>
                                <span :hidden="imageUpload.paused" class="md-progress">
                                    <span class="indeterminate"></span>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal" role="dialog">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-body">
                            <img :src="imageToShow" alt="Image" style="width: 100%;">
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-outline-danger" @click="deleteImage">Delete</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    const storageRef = firebase.storage().ref();
    const slidesRef = storageRef.child('login/slides/');

    function round(value, decimals) {
        return Number(Math.round(value+'e'+decimals)+'e-'+decimals);
    }

    function cloneImageUpload() {
        return {
            paused: false,
            uploading: false,
            uploadTask: null,
            uploadedBytes: 0,
            totalBytes: 0
        }
    }

    export default {
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
        data() {
            return {
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
            }
        },
        computed: {
            progress: function () {
                return this.imageUpload.totalBytes === 0 ? 0 :
                    round(this.imageUpload.uploadedBytes / this.imageUpload.totalBytes * 100, 2);
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
                const newImages = this.images
                    .map(image => image['.value']);

                newImages.push(this.uploadedFile);

                this.$firebaseRefs.images.set(newImages)
                    .then(() => {
                        toastr.success('Saved Images');
                        this.reset();
                        this.uploadedFile = null;
                    })
                    .catch(error => {
                        console.error(error);
                        toastr.error('Error saving images');
                        this.reset();
                        this.uploadedFile = null;
                    })
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

                this.imageUpload.uploadTask.on('state_changed', snapshot => {
                    this.imageUpload.totalBytes = snapshot.totalBytes;
                    this.imageUpload.uploadedBytes = snapshot.bytesTransferred;
                    this.imageUpload.paused = snapshot.state === firebase.storage.TaskState.PAUSED;
                }, error => {
                    this.reset();
                    console.log('Failed' + error)
                }, () => {
                    this.uploadedFile = this.imageUpload.uploadTask.snapshot.downloadURL;
                    this.reset();
                })
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
                for (let image of this.images) {
                    image = image['.value'];
                    if (image !== this.imageToShow)
                        newImages.push(image);
                }
                newImages.push(this.uploadedFile);
                this.$firebaseRefs.images.set(newImages)
                    .then(() => {
                        toastr.success('Image Deleted');
                    })
                    .catch(error => {
                        console.log(error);
                        toastr.error('Error deleting image');
                    })

            }
        },
        mounted: function() {
            this.reset();
        }
    }
</script>