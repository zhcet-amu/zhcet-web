(function ($) {
    function formatBytes(a,b){if(0===a)return"0 Bytes";var c=1024,d=b||2,e=["Bytes","KB","MB","GB","TB","PB","EB","ZB","YB"],f=Math.floor(Math.log(a)/Math.log(c));return parseFloat((a/Math.pow(c,f)).toFixed(d))+" "+e[f]}

    function setText(element, text) {
        element.html(text);
        element.removeClass('hidden');
    }

    $.fn.initUpload = function ( options ) {
        var settings = $.extend({
            fileTypes: ["text/csv", "application/vnd.ms-excel", "text/comma-separated-values"],
            maxFileSize: 5*1024*1024,
            success: null,
            error: null
        }, options);

        function showError(text) {
            if ($.isFunction(settings.error))
                settings.error(text);
        }

        return this.each(function () {
            var uploader = $(this);
            var input = $(this).find('.upload-input');

            input.off('change.file');
            input.on('change.file', function (event) {
                var files = event.target.files;

                if (files.length !== 1) {
                    showError('You must select a file to upload!');
                    return;
                }

                var file = files[0];
                var uploadBtn = uploader.find('.upload-btn');
                var fileName = uploader.find('.file-name');
                var fileSize = uploader.find('.file-size');
                setText(fileName, file.name);
                setText(fileSize, formatBytes(file.size));

                uploadBtn.addClass('hidden');

                if (settings.fileTypes.indexOf(file.type) !== -1) {
                    fileName.removeClass('bg-danger');
                    var maxSize = settings.maxFileSize;

                    if (file.size > maxSize) {
                        showError('The file size must be under ' + formatBytes(maxSize));
                        fileSize.addClass('bg-danger');
                        return;
                    } else {
                        fileSize.removeClass('bg-danger');
                        fileSize.addClass('bg-success');
                    }

                    uploadBtn.removeClass('hidden');
                    if ($.isFunction(settings.success))
                        settings.success('Successfully added file');
                    return;
                }

                showError('The type of file must be either of ' + settings.fileTypes + '. Was ' + file.type);
                fileName.addClass('bg-danger');
            });
        });
    }
}(jQuery));