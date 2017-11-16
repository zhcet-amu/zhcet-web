var App = (function (Authentication) {

    config.initialize();
    Authentication.auth().init();

    toastr.options.progressBar = true;
    toastr.options.closeButton = true;

    return {
        logout: function () {
            Authentication.auth().signOut().then(function () {
                document.getElementById('logout-form').submit();
            })
        }
    }
}(Authentication));