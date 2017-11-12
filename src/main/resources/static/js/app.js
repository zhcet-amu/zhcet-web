var App = (function () {

    function initializeFirebase() {
        var config = {
            apiKey: "AIzaSyDPzDrlYgC1GlPooyvCmEOEBeQhLeJuggU",
            authDomain: "zhcet-web-amu.firebaseapp.com",
            databaseURL: "https://zhcet-web-amu.firebaseio.com",
            projectId: "zhcet-web-amu",
            storageBucket: "zhcet-web-amu.appspot.com",
            messagingSenderId: "632575632330"
        };

        firebase.initializeApp(config);
    }

    initializeFirebase();

    return {
        logout: function () {
            Authentication.signOut().then(function () {
                document.getElementById('logout-form').submit();
            })
        }
    }
}());