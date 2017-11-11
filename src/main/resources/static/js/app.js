var App = (function (Authentication) {

    Authentication.auth().init();

    return {
        logout: function () {
            Authentication.auth().signOut().then(function () {
                document.getElementById('logout-form').submit();
            })
        }
    }
}(Authentication));