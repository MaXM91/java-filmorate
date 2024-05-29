# java-filmorate
### Preamble
A service that works with movies and user ratings, as well as returns  
popular movies recommended for viewing.
### Structure
#### java-filmorate.
#### Users:
- POST /users - add user
- PUT /users - update user
- DELETE /users/{id} - delete user by id
- GET /users/{id} - get user by id
- GET /users/{id}/friends - get friends user by id
- GET /users - get all users
- PUT /users/{id}/friends/{friendId} - add friend by user
- DELETE users//{id}/friends/{friendId} - delete friend by user
- GET /users/{id}/friends/common/{otherId} - get mutual friends
#### Films:
- POST /films - add film
- PUT /films - update film
- DELETE /films/{id} - delete film by id
- GET /films/{id} - get film by id
- GET /films - get all films
- PUT /films/{id}/like/{userId} - add like on film by user
- DELETE /films/{id}/like/{userId} - delete like on film by user
- GET /films//popular - get popular films, 10 by default
#### Genre:
- GET /genres/{id} - get genre by id
- GET /genres - get all genres
#### Mpa:
- GET /mpa/{id} - get mpa by id
- GET /mpa - get all mpa
### Launch
Change the H2 DB connection settings in the application.properties  
and deploy the project with the default profile.  
**java-filmorate** - [java_filmorate](https://github.com/MaXM91/java-filmorate)