var App = (function () {

    Authentication.normalFlow();

    return {
        logout: function () {
            Authentication.signOut().then(function () {
                document.getElementById('logout-form').submit();
            })
        }
    }
}());