// Fetch API
const asideSection = document.querySelector(".aside-container");
const triviaDiv = document.querySelector(".trivia-div");
const question = document.getElementById("question");
const choices = document.querySelector(".answer-choices");
const _correctScore = document.getElementById("correct-score");
const _totalQuestion = document.getElementById("total-questions");
const checkButton = document.getElementById("check-answer");
const playAgain = document.getElementById("play-again");
const result = document.querySelector(".result");

let trivia = null;
let correctAnswer = "", correctScore = askedCount = 0, totalQuestion = 5; //starting data

//functions

function loadTrivia() {
    const apiURL ="https://opentdb.com/api.php?amount=1";

    fetch(`${apiURL}`)
    .then(response => {
        return response.json()
    })
    .then(data => {
        trivia = data.results[0];
        result.innerHTML = ""; //reset results section
        showQuestion(trivia);
        delayClick(); //prevent early click to avoid error
    })
    .catch(err => {
        console.log("There's an error!", err);
        alert("Loading error, try again in 5 seconds.");
    });
}

//get question and randomize answer choices - input into aside elements
function showQuestion(trivia) {
    checkButton.disabled = false;

    correctAnswer = trivia.correct_answer;
    let incorrectAnswers = trivia.incorrect_answers;
    let optionsList = incorrectAnswers;
    optionsList.splice(Math.floor(Math.random()* (incorrectAnswers.length + 1)), 0, correctAnswer); //insert correct answer in random position

    //add API content to HTML elements in aside (with template literals)
    question.innerHTML = `${trivia.question} <br> <span class="category">${trivia.category}</span>`;
    choices.innerHTML = `${optionsList.map((option) => 
        `<li> <span> ${option} </span> </li>`).join("")}`;

    selectOption();
}

//choice selection (changes button color via class when clicked)
function selectOption() {
    choices.querySelectorAll('li').forEach((option) => {
        option.addEventListener("click",() => {
            if (choices.querySelector(".selected")) {
                const activeOption = choices.querySelector(".selected");
                activeOption.classList.remove("selected");
            }
            option.classList.add("selected");
        });
    });
    console.log(correctAnswer);
}

//check if answer is correct/incorrect
function checkAnswer() {
    checkButton.disabled = true;

    if(choices.querySelector(".selected")) {
        let selectedAnswer = choices.querySelector(".selected span").textContent;
        if(selectedAnswer.trim() === HTMLDecode(correctAnswer)){
            correctScore++;
            result.innerHTML = `<p> <i class="fa fa-check"></i>Correct Answer!</p>`
        } else {
            result.innerHTML = `<p> <i class="fa fa-times"></i>Incorrect Answer!</p> <p><small><b>Correct Answer: </b> ${correctAnswer}</small></p>`
        }
        checkCount();
    } else {
        result.innerHTML = `<p><i class="fa-solid fa-question"></i><small>Please make a selection.</small></p>`;
        checkButton.disabled = false;
    }
}

//added delay to click button because API can only give a response every 5 seconds
function delayClick() {
    checkButton.disabled = true;
    
    setTimeout(() => {
        checkButton.disabled = false;
    }, 3900);
}

//convert HTML entities into normal text of correct answer (i.e. &amp; = &)
function HTMLDecode(textString) {
    let doc = new DOMParser().parseFromString(textString, "text/html");
    return doc.documentElement.textContent;
}

//add count total questions that have been asked after answer is submitted
function checkCount() {
    askedCount++;
    setCount();
    if(askedCount == totalQuestion) {
        result.innerHTML = `<p>Your score is ${correctScore}.</p>`
        playAgain.style.display = "block"; //end of game, allows reset
        checkButton.style.display = "none";
    } else {
        setTimeout(() => {
            loadTrivia();
        }, 1100); //gives time before new question loads
    }
}
//update counter score
function setCount(){
    _totalQuestion.textContent = totalQuestion;
    _correctScore.textContent = correctScore;
}

//reset everything for a new round of questions
function restartQuiz() {
    correctScore = askedCount = 0;
    playAgain.style.display = "none";
    checkButton.style.display = "block";
    checkButton.disabled = false;
    setCount();
    loadTrivia();
}
//events
document.addEventListener("DOMContentLoaded", () => {
    loadTrivia();
    _totalQuestion.textContent = totalQuestion;
    _correctScore.textContent = correctScore;
});


checkButton.addEventListener("click", checkAnswer);

playAgain.addEventListener("click", restartQuiz);
