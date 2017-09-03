function formatBytes(a,b){if(0==a)return"0 Bytes";var c=1024,d=b||2,e=["Bytes","KB","MB","GB","TB","PB","EB","ZB","YB"],f=Math.floor(Math.log(a)/Math.log(c));return parseFloat((a/Math.pow(c,f)).toFixed(d))+" "+e[f]}

function setText(element, text) {
    element.html(text);
    element.removeClass('hidden');
}

function fileUploaded(event) {
    console.log(event);
    var types = event.accept.split(',').map(function(item) {
        return item.trim();
    });

    var files = event.files;

    if (files.length !== 1) {
        toastr.error('You must select a file to upload!');
        return;
    }

    var file = files[0];

    var uploader = $(event).parentsUntil('.file-uploader').parent();
    setText(uploader.find('.file-name'), file.name);
    setText(uploader.find('.file-size'), formatBytes(file.size));

    if (types.indexOf(file.type) === -1) {
        toastr.error('The type of file must be CSV');
        uploader.find('.upload-btn').addClass('hidden');
        return;
    }

    uploader.find('.upload-btn').removeClass('hidden')
}