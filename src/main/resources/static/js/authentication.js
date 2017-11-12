var Authentication = (function ($) {
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

    $(document).ready(setUp);

    return {
        signOut: signOut
    }
}(jQuery));
