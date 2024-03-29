INSERT INTO PUBLIC.STATUSES
(STATUS_NAME)
VALUES('REQUEST');

INSERT INTO PUBLIC.STATUSES
(STATUS_NAME)
VALUES('ACTIVE');

INSERT INTO PUBLIC.STATUSES
(STATUS_NAME)
VALUES('DELETED');

INSERT INTO PUBLIC.MPA
(name)
VALUES('G');

INSERT INTO PUBLIC.MPA
(name)
VALUES('PG');

INSERT INTO PUBLIC.MPA
(name)
VALUES('PG-13');

INSERT INTO PUBLIC.MPA
(name)
VALUES('R');

INSERT INTO PUBLIC.MPA
(name)
VALUES('NC-17');

INSERT INTO PUBLIC.GENRES
(name)
VALUES('Комедия');

INSERT INTO PUBLIC.GENRES
(name)
VALUES('Драма');

INSERT INTO PUBLIC.GENRES
(name)
VALUES('Мультфильм');

INSERT INTO PUBLIC.GENRES
(name)
VALUES('Триллер');

INSERT INTO PUBLIC.GENRES
(name)
VALUES('Документальный');

INSERT INTO PUBLIC.GENRES
(name)
VALUES('Боевик');

INSERT INTO PUBLIC.EVENT_TYPE (event_name)
    VALUES ('LIKE'), ('REVIEW'), ('FRIEND');

INSERT INTO PUBLIC.OPERATION_TYPE (operation_name)
    VALUES ('REMOVE'), ('ADD'), ('UPDATE');