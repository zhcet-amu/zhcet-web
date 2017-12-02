var wait = function (ms) {
    return new Promise(function (resolve) { setInterval(resolve, ms); });
};

var loadImage = function (imageUrl) {
    return new Promise(function (resolve, reject) {
        var image = new Image();
        image.onload = resolve;
        image.onerror = reject;
        image.src = imageUrl;
    });
};

var loadImages = function (images) {
    var promises = [];
    for (var i = 0; i < images.length; i++) {
        promises.push(loadImage(images[i]));
    }

    return Promise.all(promises);
};

var App = (function () {

    if (!!window.toastr) {
        toastr.options.progressBar = true;
        toastr.options.closeButton = true;
    }

    function postToServer(url, data) {
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        return Promise.resolve($.ajax({
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            url: url,
            data: data,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            }
        }));
    }

    return {
        postToServer: postToServer
    }
}());