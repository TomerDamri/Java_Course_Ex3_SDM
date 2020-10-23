
$(function() { // onload...do
    //add a function to the submit event
    window.localStorage.clear();
    $("#loginForm").submit(function() {
        $.ajax({
            data: $(this).serialize(),
            url: this.action,
            timeout: 2000,
            error: function(errorObject) {
                console.error("Failed to login !");
                $("#error-placeholder").append(errorObject.responseText)
            },
            /* response = {
        nextPageURL = nextPageURL,
        userId = userId,
        username = username,
        userType = userType}*/
            success: function(response) {
                window.localStorage.setItem('userType', response.userType);
                window.localStorage.setItem('username', response.username);
                window.localStorage.setItem('userId', response.userId);

                window.location.replace(response.nextPageURL);
            }
        });

        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});