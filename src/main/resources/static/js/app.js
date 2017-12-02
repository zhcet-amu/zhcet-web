var App = (function () {

    if (!!window.toastr) {
        toastr.options.progressBar = true;
        toastr.options.closeButton = true;
    }

    function postToServer(url, idToken, func) {
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        $.ajax({
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            url: url,
            data: idToken,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: func
        });
    }

    return {
        postToServer: postToServer
    }
}());