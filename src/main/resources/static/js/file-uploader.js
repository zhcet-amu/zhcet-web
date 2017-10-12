function formatBytes(a,b){if(0===a)return"0 Bytes";var c=1024,d=b||2,e=["Bytes","KB","MB","GB","TB","PB","EB","ZB","YB"],f=Math.floor(Math.log(a)/Math.log(c));return parseFloat((a/Math.pow(c,f)).toFixed(d))+" "+e[f]}

function setText(element, text) {
    element.html(text);
    element.removeClass('hidden');
}

var typesById = {};
var sizeById = {};

function setTypes(id, types) {
    typesById[id] = types;
}

function setMaxSize(id, size) {
    sizeById[id] = size;
}

function fileUploaded(event) {
    var types = (typesById[event.id] ? typesById[event.id] : event.accept).split(',').map(function(item) {
        return item.trim();
    });

    var files = event.files;

    if (files.length !== 1) {
        toastr.error('You must select a file to upload!');
        return;
    }

    var file = files[0];

    var uploader = $(event).parentsUntil('.file-uploader').parent();
    var uploadBtn = uploader.find('.upload-btn');
    var fileName = uploader.find('.file-name');
    var fileSize = uploader.find('.file-size');
    setText(fileName, file.name);
    setText(fileSize, formatBytes(file.size));

    uploadBtn.addClass('hidden');

    console.log(file.type);
    if (types.indexOf(file.type) !== -1) {
        fileName.removeClass('bg-danger');
        var maxSize = sizeById[event.id] ? sizeById[event.id] : 5*1024*1024;

        if (file.size > maxSize) {
            toastr.error('The file size must be under ' + formatBytes(maxSize));
            fileSize.addClass('bg-danger');
            return;
        } else {
            fileSize.removeClass('bg-danger');
            fileSize.addClass('bg-success');
        }

        uploadBtn.removeClass('hidden');
        return;
    }

    toastr.error('The type of file must be either of ' + types + '. Was ' + file.type);
    fileName.addClass('bg-danger');
}