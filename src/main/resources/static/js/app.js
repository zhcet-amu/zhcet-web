var wait = function (ms) {
    return new Promise(function (resolve) { setInterval(resolve, ms); });
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