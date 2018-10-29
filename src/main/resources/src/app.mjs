import './app/polyfill.js'

if (!!window.toastr) {
    toastr.options.progressBar = true;
    toastr.options.closeButton = true;
}
