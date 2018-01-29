/* Execute Immediately */

(function () {
    if ('Promise' in window && 'fetch' in window) {
        // Browser is modern
    } else {
        console.log('Browser is outdated. Loading polyfill');
        const s = document.createElement('script');
        s.src = 'https://cdn.polyfill.io/v2/polyfill.min.js';
        document.head.appendChild(s);
    }
}());

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

function loadScript(url, onload) {
    var async_load = function() {
        var first, s;
        s = document.createElement('script');
        s.src = url;
        s.type = 'text/javascript';
        s.async = true;
        s.onload = onload;
        first = document.getElementsByTagName('script')[0];
        return first.parentNode.insertBefore(s, first);
    };

    if (window.attachEvent) {
        window.attachEvent('onload', async_load);
    } else {
        window.addEventListener('load', async_load, false);
    }
}

var App = (function () {

    if (!!window.toastr) {
        toastr.options.progressBar = true;
        toastr.options.closeButton = true;
    }

    function blockUI(element, block) {
        if (block) {
            element.block({
                message: '<div class="icon-spinner9 icon-spin icon-lg"></div>',
                overlayCSS: {
                    backgroundColor: "#fff",
                    opacity: .8,
                    cursor: "wait"
                },
                css: {
                    border: 0,
                    padding: 0,
                    backgroundColor: "transparent"
                }
            });
        } else {
            element.unblock();
        }
    }

    function getCsrfTokens() {
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        return {
            header: header,
            token: token
        }
    }

    function postToServer(url, data) {
        return Promise.resolve($.ajax({
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            url: url,
            data: data,
            beforeSend: function (xhr) {
                var csrf = getCsrfTokens();
                xhr.setRequestHeader(csrf.header, csrf.token);
            }
        }));
    }

    return {
        postToServer: postToServer,
        blockUI: blockUI
    }
}());