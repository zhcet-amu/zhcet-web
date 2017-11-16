var config = (function (firebase) {
    /* Initialize Firebase */
    var config = {
        apiKey: "AIzaSyDPzDrlYgC1GlPooyvCmEOEBeQhLeJuggU",
        authDomain: "zhcet-web-amu.firebaseapp.com",
        databaseURL: "https://zhcet-web-amu.firebaseio.com",
        projectId: "zhcet-web-amu",
        storageBucket: "zhcet-web-amu.appspot.com",
        messagingSenderId: "632575632330"
    };

    return {
        messagingSenderId: config.messagingSenderId,
        initialize: function () {
            firebase.initializeApp(config);
        }
    }
}(firebase));