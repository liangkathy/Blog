//LOCAL STORAGE
//username
function logUsername () {
    usernameInput = "";
    usernameInput = document.forms["login-input"]["username"].value;
    console.log(usernameInput);

    localStorage.setItem("username", usernameInput);

    return usernameInput;
}

signInButton.addEventListener("click", logUsername);


//theme
let darkMode = localStorage.getItem("darkMode");

function enableDarkMode () {
    body.classList.add("dark");
    localStorage.setItem("darkMode", "enabled");

    icons.forEach(icon => {
        icon.classList.add("icon");
    })
}

function disableDarkMode () {
    body.classList.remove("dark");
    localStorage.setItem("darkMode", "disabled");
}

if (darkMode === "enabled") {
    checkbox.checked = true;
    enableDarkMode();
}

darkModeCheckbox.addEventListener("change", (e) => {
    let darkMode = localStorage.getItem("darkMode");
    if (e.target.checked == true) {
        enableDarkMode();
    } else {
        disableDarkMode();
    }
})