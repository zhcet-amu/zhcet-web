var Authentication = (function ($) {
    var googleProvider = new firebase.auth.GoogleAuthProvider();
    var userInformation;

    function signOut() {
        return firebase.auth().signOut();
    }

    function signIn(token) {
        return firebase.auth().signInWithCustomToken(token);
    }

    function attemptSignIn() {
        $.ajax({
            url: "/profile/api/token",
            success: function(user) {
                userInformation = user;

                if (user.authenticated)
                    signIn(user.token);
            }});
    }

    function setUp() {
        firebase.auth().onAuthStateChanged(function (user) {
            if (!user)
                attemptSignIn();
        });
    }

    function init() {
        $(document).ready(setUp);
    }

    function postToken(idToken) {
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        $.ajax({
            type: 'POST',
            url: "/login/api/token",
            data: idToken,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function(action) {
                window.location = action;
            }});
    }

    function checkUser(result) {
        if (!result || !result.user)
            return [false];

        result.user.getIdToken(false).then(postToken);

        return [true];
    }

    function loginListen(unauthenticated) {
        firebase.auth().getRedirectResult().then(function(result) {
            var check = checkUser(result);
            if ($.isFunction(unauthenticated))
                unauthenticated(!check[0], check[1]);
        }).catch(function(error) {
            if ($.isFunction(unauthenticated))
                unauthenticated(true, error.message);
        });
    }

    function googleLogin() {
        firebase.auth().signInWithRedirect(googleProvider);
    }

    return {
        normalFlow: init,
        loginFlow: loginListen,
        googleLogin: googleLogin,
        signOut: signOut
    }
}(jQuery));
