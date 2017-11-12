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
                if (user.authenticated) signIn(user.token);
            }});
    }

    function setUp() {
        firebase.auth().onAuthStateChanged(function (user) {
            if (!user) attemptSignIn();
        });
    }

    function init() {
        $(document).ready(setUp);
    }

    function postToServer(url, idToken, func) {
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        $.ajax({
            type: 'POST',
            url: url,
            data: idToken,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: func
        });
    }

    function postToken(idToken) {
        postToServer("/login/api/token", idToken, function(action) {
            window.location = action;
        });
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
            callIfFunction(unauthenticated, !check[0], check[1]);
        }).catch(function(error) {
            callIfFunction(unauthenticated, true, error.message);
        });
    }

    function googleLogin() {
        firebase.auth().signInWithRedirect(googleProvider);
    }

    function callIfFunction(func, arg1, arg2) {
        if ($.isFunction(func))
            func(arg1, arg2);
    }

    function linkGoogle(linkCallback) {
        firebase.auth().currentUser.linkWithPopup(googleProvider)
            .then(function() {
                callIfFunction(linkCallback, true);
            }).catch(function(error) {
                if (error.code === 'auth/credential-already-in-use') {
                    var auth = firebase.auth();
                    var prevUser = auth.currentUser;
                    var credential = error.credential;
                    auth.signInWithCredential(credential).then(function(user) {
                        return user.delete().then(function() {
                            return prevUser.linkWithCredential(credential);
                        }).then(function() {
                            return auth.signInWithCredential(credential);
                        }).then(function () {
                            callIfFunction(linkCallback, true);
                        });
                    }).catch(function(error) {
                        callIfFunction(linkCallback, false, error);
                    });
                } else {
                    callIfFunction(linkCallback, false, error);
                }
            });
    }

    function unlinkGoogle(unlinkCallback) {
        firebase.auth().currentUser.unlink(googleProvider.providerId)
            .then(function () {
                if ($.isFunction(unlinkCallback))
                    unlinkCallback(true);
            }).catch(function (error) {
                if ($.isFunction(unlinkCallback))
                    unlinkCallback(true, error);
            });
    }

    function isProvider(providerData, providerId) {
        if (!providerData)
            return;

        for (var i = 0; i < providerData.length; i++)
            if (providerData[i].providerId === providerId)
                return true;

        return false;
    }

    function isGoogleLinked(linked) {
        firebase.auth().onAuthStateChanged(function (user) {
            if (!$.isFunction(linked))
                return;

            if (user) {
                linked(isProvider(user.providerData, googleProvider.providerId));
            } else {
                linked(); // Trigger hiding the button
            }
        });
    }

    return {
        normalFlow: init,
        loginFlow: loginListen,
        googleLogin: googleLogin,
        linkGoogle: linkGoogle,
        unlinkGoogle: unlinkGoogle,
        isGoogleLinked: isGoogleLinked,
        signOut: signOut
    }
}(jQuery));
