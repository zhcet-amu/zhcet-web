import './app/polyfill.js'

if (!!window.toastr) {
    toastr.options.progressBar = true;
    toastr.options.closeButton = true;
}

if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        if (process.env.NODE_ENV === "production") {
            navigator.serviceWorker.register('/sw.js');
        }
    });
}
