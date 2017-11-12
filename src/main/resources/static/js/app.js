var App = (function () {

    function initializeFirebase() {
        var config = {
            apiKey: "AIzaSyC11g0JvjdYajA9N_yA9DiqoxMhIuuJGzY",
            authDomain: "zhcet-backend.firebaseapp.com",
            databaseURL: "https://zhcet-backend.firebaseio.com",
            projectId: "zhcet-backend",
            storageBucket: "zhcet-backend.appspot.com",
            messagingSenderId: "591868576688"
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