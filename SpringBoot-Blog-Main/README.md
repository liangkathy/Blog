# Spring-Project

## Main Application
### User
**Functionalities/Endpoints**
1. Get all users  
    a. GET (“/users”)  
2. Get user by id  
    a. GET (“/user/{id}”)  
3. Get blogs by user id  
    a. GET (“/users/{id}/blogs”)  
    b. Details:  
       - Intended to mimic all blogs appearing on a user profile  
4. Get notifications by user id  
    a. GET (“/users/{id}/notifications”)  
    b. Details:  
       - Communicates with microservice method that gets notifications by userId  
5. Create a user  
    a. GET (“/users”)  
    b. Details:  
       - An instance of address is created when a user is created  
       - Verifies that username does not already exist in database  
6. Update user by id  
    a. GET (“/user/{id}”)  
7. Add new blog to user  
    a. GET (“/user/{id}/blogs”)   
    b. Details:  
       - Additional endpoint to add a blog to the user  
8. Delete user by id  
    a. GET (“/user/{id}”)  
    b. Details:  
       - Also deletes associated address and any associated blogs  

**Required fields in request body**
1. User RequestBody  
    a. Fields that cannot be null or blank: username, email, password, address  
    b. Address requires: street, city, state, zipcode, country  
    c. Used in user create method endpoint  
    c. Example User Request Body:  
>{  
    "username": "User123",  
    "password": "password123",  
    "email": "joe@example.com",  
    "address": {  
        "street": "123 1st Street",  
        "city": "Austin",  
        "state": "Texas",  
        "zipCode": "78701",  
        "country": "USA"  
    }  
>}  

### Blog
**Functionalities/Endpoints**
1. Get all blogs  
    a. GET (“/blogs”)  
2. Get blog by id  
    a. GET (“/blogs/{id}“)
3. Get blog(s) by keyword search  
    a. GET (“/blogs?keyword={string})  
    b. Details:  
       - Uses a JPQL to find any matching blogs from the database  
       - The JPQL is set up so the search is case insensitive  
       - Locates blogs with keyword mentioned in either the blog title or content  
       - Verifies that keyword parameter is not blank or empty  
4. Create a blog  
    a. POST (“/blogs”)  
    b. Details:  
       - The request accepts a blog DTO body that requires a user id and takes an optional list of tag ids  
       - The blog then gets associated to the specified user and any existing tags the user wants to add  
       - Verifies that user id and tag ids exist in database  
5. Update blog by id  
    a. PUT (“/blogs/{id}”)  
    b. Details:  
       -  The request accepts a blog body that requires title, content, and likes  
6. Add new comment to blog  
    a. PUT (“/blogs/{id}/comments”)  
    b. Details:  
       - Additional endpoint to add a comment to a blog id  
       - Sends notificationDTO to microservice to create a new notification  
7. Add new tag to blog    
    a. PUT (“/blogs/{id}/tags”)  
    b. Details:  
       - Additional endpoint to add tag to blog  
8. Remove tag from blog    
    a. PUT (“/blogs/{id}/tags/{tagId}”)   
9. Delete blog by id  
    a. DELETE (“/blogs/{id}”)  
    b. Details:  
       - Will also delete any associated comments  

**Required fields in request body**
1. BlogDTO RequestBody:  
    a. Fields that cannot be null or blank: title, content, userId  
    b. Adding existing tagIds is optional (can be blank or added as a list)  
    c. If likes for a blog is entered as null, the likes will be set to 0  
    d. Used in blog create method endpoint  
    e. Example BlogDTO Request Body:  
>{  
    "title": "Blog Name",  
    "content": "Random content",  
    "likes": 500,  
    "userId": 2,  
    "tagIds": [1, 2]  
>}  
  
2. Blog RequestBody  
    a. Fields that cannot be null or blank: title, content  
    b. If likes for a blog is entered as null, the likes will be set to 0  
    c. Used in blog update method and adding blog to user endpoint  
    d. Example Blog Request Body:  
>{  
    "title": "Updated Blog Name",  
    "content": "Random content updated",  
    "likes": 750  
>}  

### Comment
**Functionalities/Endpoints**
1. Get all comments  
    a. GET (“/comments”)  
2. Get comment by id  
    a. GET (“/comments/{id}”)  
3. Create a comment  
    a. POST (“/comments”) 
    b. Details:  
       - The request accepts a comment DTO body that requires a blog id  
       - The comment then gets associated to the specified blog  
       - Verifies that blog id and commenter username exists in user database  
       - Sends notificationDTO to microservice to create a new notification  
4. Update comment by id  
    a. PUT (“/comments/{id}”)  
    b. Details:  
       - By design, updating a comment will NOT change the commenter username or associate blog    
5. Delete comment by id  
    a. DELETE (“/comments/{id}”)  


**Required fields in request body**
1. CommentDTO RequestBody:  
    a. Fields that cannot be null or blank: text, commenterUsername, blogId  
    b. If likes for a comment is entered as null, the likes will be set to 0  
    c. Used in comment create method endpoint  
    d. Example CommentDTO Request Body:  
>{  
    "text": "Example comment",  
    "likes": 15,  
    "commenterUsername": "User456",  
    "blogId": 1  
>}  
  
2. Comment RequestBody  
    a. Fields that cannot be null or blank: text, commenterUsername  
    b. If likes for a comment is entered as null, the likes will be set to 0  
    c. Used in comment update method and adding comment to blog enpdpoint  
    d. Example Comment Request Body:  
>{  
    "title": "Updated Comment Name",  
    "content": "Random content",  
    "likes": 750  
>}  


### Tag
**Functionalities/Endpoints**
1. Get all tags  
    a. GET (“/tags”)  
2. Get tag by id  
    a. GET (“/tags/{id}”)  
3. Get tag by name  
    a. GET (“/tags?name={string}”)  
    b. Details:  
       - Name match is designed to be case insensitive (all tag names converted to lowercase upon creation)  
       - Verifies that name paramater is not blank or empty  
4. Get blogs by tag id  
    a. GET (“/tags/{id}/blogs”)  
    b. Details:  
       - Intended to mimic search for blogs with a certain tag  
5. Create tag(s)  
    a. POST (“/tags”)  
    b. Details:  
       - The request accepts a list of tag DTOs that takes an optional list of existing blog ids  
       - Multiple tags can be created at once  
       - Any blog ids are verified and associated to the new tag  
       - Verifies that tag name doesn't exist already  
6. Update tag by id  
    a. PUT (“/tags/{id}”)  
    b. Details:  
       - Verifies that new tag name doesn't exist in the database already  
7. Delete tag by id  
    a. DELETE (“/tags/{id}”)  
    b. Details:  
       - Will remove tag from any associated blogs   


**Required fields in request body**
1. TagDTO RequestBody:  
    a. Fields that cannot be null or blank: name and description  
    b. Adding existing blogIds is optional (can be blank or added as a list)  
    c. Used in tag create method endpoint  
    d. Example TagDTO Request Body:  
>[  
    {  
        "name": "Travel",  
        "description": "tag for travel",  
        "blogIds": [1]  
    },  
    {  
        "name": "Photography",  
        "description": "tag for photography",  
        "blogIds": [1, 2]  
    },  
    {  
        "name": "Cooking",  
        "description": "tag for cooking",  
        "blogIds": []  
    }  
>]
  
2. Tag RequestBody  
    a. Fields that cannot be null or blank: name and description  
    b. Used in tag update method  
    c. Example Tag Request Body:  
>{  
    "name": "Updated tag name",  
    "description": "tag for update"  
>}  


### Address
**Functionalities/Endpoints**
1. Get all addresses  
    a. GET (“/addresses”)  
2. Get address by id  
    a. GET (“/addresses/{id}”)  
3. Create an address  
    a. POST (“/addresses”)  
4. Update address by id  
    a. PUT (“/addresses/{id}”)  
5. Delete address by id   
    a. DELETE (“/addresses/{id}”)  
    b. Details:  
       - Also deletes associated user  

Note: Basic CRUD added for project requirements and ideally I would have designed this without these endpoints. For example, adding address should not be used since address is added with user creation.   

**Required fields in request body**
1. Address RequestBody  
    a. Fields that cannot be null or blank: street, city, state, zipcode, country (all fields required)  
    b. Example of Address Request Body:  
>{  
    "street": "123 1st Street",  
    "city": "Austin",  
    "state": "Texas",  
    "zipCode": "78701",  
    "country": "USA",  
    "user": {  
        "username": "User123",  
        "password": "password123",  
        "email": "joe@example.com"  
    }  
>}  


## Testing
**Overall Coverage Breakdown**  
| Element  | Class % | Method % | Line % |
| ---- | ---- | ---- | ---- |
| Overall  | 100% | 100% | 89% |
| Service  | 100% | 100% | 95% |
| Controller | 100% | 100% | 79% |


## AOP  
**@Around**  
Info logging added to log trigger time before and after a service method runs  
**@AfterThrowing**  
Error logging added when a method throws an exception for tracking  

## Exception Handling/Validation
**Custom Exception Handler for Validation**
1. Validation added to each class for @RequestBody when specific fields cannot be null or blank  
2. Custom exception handler class created to handle exceptions  
    a. ExceptionHandler specifically for MethodArgumentNotValidException (thrown when validation fails)  
    b. When exception is thrown, Postman displays the details of the error including all error messages (if multiple fields needs correction)  
    c. All errors are also logged for reference  

Types of exceptions thrown:  
1. NullPointerException - when an object that should not be null is null  
    a. status = BAD_REQUEST  
2. IllegalArgumentException - when a field  
    a. status = BAD_REQUEST  
3. DuplicateKeyException - when the database already contains a version of that key (i.e. username, tag name)  
    a. status = CONFLICT  
4. Exception - catch-all, most commonly used when an object cannot be found  
    a. status = NOT_FOUND  
5. IllegalArgumentException - when a request parameter is blank/empty  
    a. status = BAD_REQUEST

## Microservice Application
### Notification
**Functionalities/Endpoints**
1. Get all notifications  
    a. GET (“/notifications”)  
2. Get notification by id  
    a. GET (“/notifications/{id}”)  
3. Get notification by user id (of blog post owner)  
    a. GET (“/notifications/users/{id}”)  
    b. Details:  
       - Main application calls this endpoint of the notification microservice to return notifications by userId  
4. Create a notification  
    a. POST (“/notifications”)  
    b. Details:  
       - Triggered when a comment is created in the main application  
       - Verifies that all fields are provided in order to successfully create a notification  
5. Delete by id  
    a. DELETE (“/notifications/{id}”)  

Note: Notification update functionality was intentionally left off as there isn’t a need to edit a notification. Also notification creation is triggered by a comment being added so the create method from the tag endpoint likely won't be used.  

**Required fields in request body**
1. NotificationDTO RequestBody  
    a. Required fields: commenterUsername, bloggerId (aka userId of blog poster), commentId, blogId  
    b. Used in notification create method  
    c. Example of NotificationDTO Request Body:  
>{  
    "commenterUsername": "User123",  
    "bloggerId": 1,  
    "commentId": 1,  
    "blogId": 1  
>}  