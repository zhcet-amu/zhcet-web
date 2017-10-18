(function () {
    $('.file-uploader').initUpload({
        error: function (message) {
            toastr.error(message);
        }
    });
}());