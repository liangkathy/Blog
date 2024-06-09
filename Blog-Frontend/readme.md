ABOUT MY PROJECT

Part 1: Login Page
1. Header: 
    a. Contains site name and dark mode toggle
2. Dark mode toggle button:
    a. CSS coded to look like a toggle button
    b. Dark mode setting is saved in local storage so the set theme carries over to homepage
3. Login body:
    a. Username and password field must be filled out to redirect to homepage. User receives an error message if either one is missing value
    b. Username value is saved in local storage so username carries over to homepage header

Part 2: Homepage
1. Header:
    a. Contains site name, navigation menu links, greeting for user, profile picture
    b. Position is fixed and will stay at the top of the page when scrolling
2. Mobile first approach:
    a. Website coded first for mobile version (screen size under 768px)
    b. Default (mobile) view has hamburger menu, hides profile picture, and hides API aside
    c. Hamburger menu: three bars turns into an X when the menu is opened, clicking the X or any link on the menu will close the hamburger menu view
3. Desktop view:
    a. Media query added for screen sizes larger than 768px
    b. API aside and profile picture appear
    c. The hamburger menu format transitions to a regular horizontal menu across the header and a scroll bar appears for the post section
4. Post section:
    a. The first section shown by default is the new post text area and submit button to post
    b. Posted content appears below when the new post form is submitted along with the ability to add comments on each post
    c. Posts can be individually deleted with the X button
    d. Each post can be liked by clicking the heart icon which turns red on the first click and displays a counter for each click
    e. Upon post submission, the post textarea is reset to the default placeholder text to make it easy to create a new post
5. Comment section:
    a. The comment sections are shown with each existing post
    b. When a comment is submitted, the content is displayed below each comment area
    c. Individual comments can be deleted with the X button
    d. Each comment section has a scroll bar added when content is longer than 500px (~4 comments)
    e. Upon adding a comment, the comment textarea is reset to the default placeholder text to easily add a new comment
6. Search bar:
    a. When a value is searched for with the search input, it yields posts with matching text and hides all other posts
    b. Different searches can be performed and posts will update to reflect the new search input
    c. If an empty search is performed, all posts will return to the screen
7. Sort drop-down menu:
    a. Gives option to sort by newest to oldest (default) or oldest to newest
    b. The sort is done based on the timestamp of when the post was created
    c. Any new posts added will follow the selected sort settings
8. Footer:
    a. Contains site name and Made by: Kathy
    b. Position is fixed so the footer remains at the bottom when scrolling


Part 3: Fetch API Aside
1. Used trivia API to load a 10 question interactive quiz
2. Each set of answer choices is randomized so the correct answer is in a random location each time
3. Answer selection:
    a. If the correct answer is selected, display will show that it was correct
    b. If a wrong answer is selected, display will show it was incorrect
    c. If no answer is selected when hitting "Check answer", display will advise user to choose an option
4. When an answer is selected and submitted, the next question will populate
    a. A new question will not populate if an answer choice was not selected
5. A counter tracks the number of right answers out of the 10 total questions
6. Once 10 questions are completed, the display shows the total score, hides the "Check Answer" button, and the button to "Play Again" appears
7. This API is limited to one response every 5 seconds so I set a delay between questions before the "Check Answer" button can be clicked again. The page will also alert a loading error if the page is refreshed within 5 seconds as this will cause the trivia aside to appear empty.

