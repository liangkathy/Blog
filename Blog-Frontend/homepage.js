//variables
const postHistorySection = document.querySelector(".post-history");

const oldCommentSection = document.createElement("div");
const newPostButton = document.querySelector(".new-post-button");
const textAreas = document.querySelectorAll("textarea");

//get username data from local storage
const hiUserField = document.querySelector(".hi-user");
let username = localStorage.getItem("username");
hiUserField.innerHTML = "Hi, " + username;

//get dark mode setting choice from local storage
darkMode = localStorage.getItem("darkMode");
if (darkMode === "enabled") {
    body.classList.add("dark");
    textAreas.forEach(element => {
        element.classList.add("dark-text");
    });
    icons.forEach(icon => {
        icon.classList.add("icon");
    });
}

function commentDarkMode (commentTextArea) {
    if (darkMode === "enabled") {
        console.log(commentTextArea);
        commentTextArea.classList.add("dark-text");
    }
}

const sortOptions = document.getElementById("sort-options");
const searchInput = document.querySelector(".search-bar");
const searchButton = document.querySelector(".search-button");
const searchForm = document.querySelector(".search-container");

let allText = [];

//functions

//add post
function addNewPost() {
    const postContent = document.getElementById("new-post").value;
    const postTitle = document.getElementById("new-post-title").value;
    const newPostElement = document.createElement("div");
    const newPostTitleElement = document.createElement("div");
    newPostElement.classList.add("new-post-text");
    newPostTitleElement.classList.add("new-post-title");

    newPostElement.textContent = postContent;
    newPostTitleElement.textContent = postTitle;

    const newPostDiv = document.createElement("div");
    newPostDiv.classList.add("new-post-div");

    const commentSection = document.createElement("div");
    commentSection.classList.add("comment-section")

    postHistorySection.appendChild(newPostDiv);
    newPostDiv.appendChild(newPostTitleElement);
    newPostDiv.appendChild(newPostElement);
    newPostDiv.appendChild(commentSection);

    addDeleteButton(newPostDiv);
    addCommentSection(commentSection);
    addLikeButton(newPostElement);
    sortOptionSelect(newPostDiv);
    runSearch(newPostElement, newPostTitleElement);
    clearPostTextArea();
}

//clear post title & text area
function clearPostTextArea() {
    document.getElementById("new-post").value = "";
    document.getElementById("new-post-title").value = "";
}

//search for value
function runSearch(newPostElement, newPostTitleElement) {
    let postText = {element: newPostElement, text: newPostElement.textContent, title: newPostTitleElement.textContent}
    
    allText.push(postText);

    console.log(allText);

    searchButton.addEventListener("click", () => {
        const value = searchInput.value.toLowerCase();
    
        allText.forEach(object  => {
        const isVisible = object.text.toLowerCase().includes(value);
        const isTitleVisible = object.title.toLowerCase().includes(value);

        if (!isVisible && !isTitleVisible) {
            object.element.parentElement.classList.add("hide");
        } else {
            object.element.parentElement.classList.remove("hide");
        }
    })
})
}

//add delete button
function addDeleteButton(element) {
    const deleteButton = document.createElement("button");
    deleteButton.textContent = "x"
    deleteButton.classList.add("delete-button");

    deleteButton.addEventListener("click", () => {
        element.remove();
    })

    element.appendChild(deleteButton);
    return deleteButton;
}

//add like button to each post
function addLikeButton(newPostElement) {
    const likeButton = document.createElement("div");
    likeButton.setAttribute("id","heart");
    likeButton.textContent = " ♥︎"

    const counterDisplay = document.createElement("div");
    counterDisplay.classList.add("counter");

    newPostElement.appendChild(likeButton);
    newPostElement.appendChild(counterDisplay);
    let count = 0

    likeButton.addEventListener("click", () => {
        count++;
        likeButton.style.color = "red";
        updateCounterDisplay(counterDisplay, count);
    })
}

//update like counter
function updateCounterDisplay(counterDisplay, count) {
    counterDisplay.innerHTML = count;
}

//add comment section to each post
function addCommentSection(commentSection) {
    const newCommentForm = document.createElement("form")
    newCommentForm.classList.add("comment-form");
    
    const commentTextArea = document.createElement("textarea");
    commentTextArea.classList.add("new-comment")
    commentTextArea.setAttribute("placeholder", "New comment");
    commentTextArea.setAttribute("rows", "5");

    const commentPostButton = document.createElement("input");
    commentPostButton.setAttribute("type","submit");
    commentPostButton.setAttribute("value", "Add comment");
    commentPostButton.classList.add("button", "new-comment-button");
    
    commentSection.appendChild(newCommentForm);
    newCommentForm.appendChild(commentTextArea);
    newCommentForm.appendChild(commentPostButton);

    newCommentForm.addEventListener("submit", preventRefreshForm);

    newCommentForm.addEventListener("submit", addNewComment);

    commentDarkMode(commentTextArea);
}

//add new comment to each comment section
function addNewComment(e) {
    const commentContent = e.target[0].value;
    const newCommentElement = document.createElement("div");
    newCommentElement.classList.add("comment-text");

    newCommentElement.textContent = commentContent;

    e.target.parentElement.appendChild(newCommentElement);

    addDeleteButton(newCommentElement);
    clearCommentTextArea(e);
}

//clear comment text area 
function clearCommentTextArea(e) {
    e.target[0].value = "";
}

//add timestamp to each post and create an array of all posts
function sortOptionSelect (newPostDiv) {
    newPostDiv.timeStamp = Date.now();

    let postChildren = [...postHistorySection.children]
    
    postChildren.sort(sortCompare); //sort by time

    postHistorySection.innerHTML = ""; //clear post history section

    for(let i = 0; i<postChildren.length; i++) {
        postHistorySection.appendChild(postChildren[i]); //re-add each post child div to history section by time sort
    }
}


//order by new to old or old to new by timestamp
function sortCompare(a,b) {
    if (sortOptions.value == "old2New") {
        return a.timeStamp - b.timeStamp;
    } else if (sortOptions.value == "new2Old") {
        return b.timeStamp - a.timeStamp;
    }
}

//events
newPostButton.addEventListener("click", addNewPost);
sortOptions.addEventListener("change", sortOptionSelect);


// hamburger menu functions
const navBar = document.querySelector(".navbar");
const hamburgerElement = document.querySelector(".hamburger");
const navLink = document.querySelectorAll(".nav-link");

hamburgerElement.addEventListener("click", () => {
    navBar.classList.toggle("nav-open"); //toggle - add and remove class with click
    hamburgerElement.classList.toggle("hamburger-open");
})

 //close menu by clicking on nav items
navLink.forEach(element => {
    element.addEventListener("click", () => {
        navBar.classList.remove("nav-open"); 
        hamburgerElement.classList.remove("hamburger-open");
    })
});


// prevent page refresh on form submission
const newPostForm = document.getElementById("new-post-form");

function preventRefreshForm(e) {
    e.preventDefault();
}

newPostForm.addEventListener("submit", preventRefreshForm); //prevent refresh on post submit

searchForm.addEventListener("submit", preventRefreshForm); //prevent refresh on search submit