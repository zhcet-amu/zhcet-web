export const wait = ms => {
    return new Promise(function (resolve) { setInterval(resolve, ms); });
};

export const loadImage = imageUrl => {
    return new Promise(function (resolve, reject) {
        const image = new Image();
        image.onload = resolve;
        image.onerror = reject;
        image.src = imageUrl;
    });
};

export const loadImages = images => {
    const promises = [];
    for (let i = 0; i < images.length; i++) {
        promises.push(loadImage(images[i]));
    }

    return Promise.all(promises);
};

export const truncateDecimals = function (number, digits) {
    const multiplier = Math.pow(10, digits),
        adjustedNum = number * multiplier,
        truncatedNum = Math[adjustedNum < 0 ? 'ceil' : 'floor'](adjustedNum);

    return truncatedNum / multiplier;
};

export function formatBytes(a,b) {
    if(0===a) return"0 Bytes";
    const c = 1024,
        d = b || 2,
        e = ["Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
        f = Math.floor(Math.log(a) / Math.log(c));
    return parseFloat((a/Math.pow(c,f)).toFixed(d))+" "+e[f]
}

export function blockUI(element) {
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
}

function getCsrfTokens() {
    const header = $("meta[name='_csrf_header']").attr("content");
    const token = $("meta[name='_csrf']").attr("content");

    return {
        header: header,
        token: token
    }
}

export function postToServer(url, data) {
    return Promise.resolve($.ajax({
        type: 'POST',
        contentType: 'application/json; charset=utf-8',
        url: url,
        data: data,
        beforeSend: function (xhr) {
            const csrf = getCsrfTokens();
            xhr.setRequestHeader(csrf.header, csrf.token);
        }
    }));
}
