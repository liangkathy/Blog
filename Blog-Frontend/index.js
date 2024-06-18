console.log("connected");

//variables
const body = document.querySelector("body");
const darkModeCheckbox = document.querySelector(".switch");
const checkbox = document.getElementById("switch");
const icons = document.querySelectorAll(".fa");

const signInButton = document.querySelector(".sign-in-button");
const loginErrorMessage = document.querySelector(".login-error-message");

let usernameInput = ""
let passwordInput = ""

//functions
function formValidation () {
    let usernameValue = document.forms["login-input"]["username"].value;
    if (usernameValue == "" || usernameValue == null) {
        loginErrorMessage.textContent = "Username must be filled out";
        return false;
    } 
    let passwordValue = document.forms["login-input"]["password"].value;
    if (passwordValue == "" || passwordValue == null) {
        loginErrorMessage.textContent = "Password must be filled out";
        return false;
    }
}

