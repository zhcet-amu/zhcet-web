import './app/polyfill.js'

if (!!window.toastr) {
    toastr.options.progressBar = true;
    toastr.options.closeButton = true;
}

if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('/sw.js');
    });
}
