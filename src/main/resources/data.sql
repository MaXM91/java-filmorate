/*
INSERT INTO users (email, login, name, birthday)
    VALUES ('email1', 'login1', 'name1', '1990-01-01'),
           ('email2', 'login2', 'name2', '1990-01-02'),
           ('email3', 'login3', 'name3', '1990-01-03'),
           ('email4', 'login4', 'name4', '1990-01-04'),
           ('email5', 'login5', 'name5', '1990-01-05'),
           ('email6', 'login6', 'name6', '1990-01-06'),
           ('email7', 'login7', 'name7', '1990-01-07'),
           ('email8', 'login8', 'name8', '1990-01-08'),
           ('email9', 'login9', 'name9', '1990-01-09');

INSERT INTO films (name, description, releasedate, duration)
    VALUES ('name1', 'description1', '1990-02-01', 101),
           ('name2', 'description2', '1990-02-02', 102),
           ('name3', 'description3', '1990-02-03', 103),
           ('name4', 'description4', '1990-02-04', 104),
           ('name5', 'description5', '1990-02-05', 105),
           ('name6', 'description6', '1990-02-06', 106),
           ('name7', 'description7', '1990-02-07', 107),
           ('name8', 'description8', '1990-02-08', 108),
           ('name9', 'description9', '1990-02-09', 109),
           ('name10', 'description10', '1990-02-10', 110),
           ('name11', 'description11', '1990-02-11', 111),
           ('name12', 'description12', '1990-02-12', 112);

INSERT INTO public.genre (name)
    VALUES ('Комедия'),
           ('Драма'),
           ('Мультфильм'),
           ('Триллер'),
           ('Документальный'),
           ('Боевик');

INSERT INTO public.mpa (name)
    VALUES ('G'),
           ('PG'),
           ('PG-13'),
           ('R'),
           ('NC-17');

INSERT INTO film_genre
    VALUES (1, 1),
           (2, 2),
           (1, 3),
           (3, 3),
           (4, 4),
           (6, 3),
           (5, 5),
           (6, 6),
           (7, 6),
           (8, 5),
           (9, 4),
           (10, 3),
           (11, 2),
           (12, 1);

INSERT INTO film_mpa
    VALUES (1, 1),
           (2, 2),
           (3, 3),
           (4, 3),
           (5, 4),
           (6, 3),
           (7, 5),
           (8, 5),
           (9, 5),
           (10, 5),
           (11, 4),
           (12, 3);

INSERT INTO friends (user_id_from, user_id_to)
    VALUES (1, 2),
           (2, 3),
           (3, 2),
           (3, 4),
           (4, 5),
           (5, 6),
           (6, 5),
           (2, 7),
           (7, 8),
           (8, 1),
           (7, 1),
           (6, 2),
           (4, 7),
           (3, 1);

INSERT INTO likes
    VALUES (1, 1),
           (3, 1),
           (4, 1),
           (5, 2),
           (7, 3),
           (8, 4),
           (9, 5),
           (12, 5),
           (12, 6),
           (1, 7),
           (12, 8),
           (12, 3),
           (6, 7);
*/