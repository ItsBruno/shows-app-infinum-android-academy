# Shows app

The development of this app is currently in progress within the guidelines of 
the [Infinum Academy android course](https://infinum.academy/courses/android/).

## User interface
### Login
<div> 
    <div>
        <p>Upon launching the app, the user
            is greeted by the displayed UI
            containing a login form. The login button is initially disabled and gets updated according to every update of the form
        </p>
        <hr/>
        <br>
         <p>In the event that the user enters credentials that don't satisfy the criterion for a valid email or password, an error message will be displayed. Additionally a snackbar will appear upon the user entering an invalid email informing the user that the email is invalid.
        </p>
        <hr/>
        <br>
         <p>Once the user enters a valid email and password, the login button is enabled
        </p>
        <hr/>
        <img style="width : 216px; height: 444" src="screenshots/login_ui.png">
        <img style="width : 216px; height: 444;" src="screenshots/bad_login.png">
        <img style="width : 216px; height: 444;" src="screenshots/good_login.png">
</div>

### Shows

<div> 
    <div >
        <p>On successful login the user is met with a display of various shows. The user can (for now) navigate to a display that corresponds to a state where there are no shows to be displayed. Also, the user has the ability to log out which will bring the user back to the login screen.
        </p>
        <hr/>
    </div>
        <img style="width : 216px; height: 444" src="screenshots/shows.png">
        <img style="width : 216px; height: 444" src="screenshots/no_shows.png">
</div>

<hr/>

### Show details

<div> 
    <div >
        <p>Once the user taps on a show, he is greeted by the display of details for the corresponding show. The details include the title, fitting image, short description, average rating, and reviews. The user also has the ability to add a review. Upon clicking the "Write a review" button, a bottom sheet dialog will appear, prompting the user to add a rating of at least one star and write an optional review.
        </p>
        <hr/>
    </div>
        <img style="width : 216px; height: 444" src="screenshots/showDetails1.png">
        <img style="width : 216px; height: 444" src="screenshots/showDetails2.png">
        <img style="width : 216px; height: 444" src="screenshots/add_a_review.png">
</div>
<hr/>
Additional features will be implemented according to further instructions from the academy mentors.
