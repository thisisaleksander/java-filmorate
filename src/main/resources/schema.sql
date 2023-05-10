CREATE TABLE IF NOT EXISTS USERS (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email VARCHAR(50),
  login VARCHAR(25) NOT NULL,
  name VARCHAR(25),
  birthday DATE,
  deleted BOOl DEFAULT FALSE,
  constraint USER_PK primary key (id)
);

CREATE TABLE IF NOT EXISTS FILMS (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(50),
  description VARCHAR(200),
  release_date DATE,
  duration INTEGER,
  rate INTEGER,
  deleted BOOl DEFAULT FALSE,
  CONSTRAINT FILM_PK PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS STATUSES (
  status_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  status_name VARCHAR(25),
  CONSTRAINT STATUSES_PK PRIMARY KEY (status_id)
);

CREATE TABLE IF NOT EXISTS GENRES (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(25),
 CONSTRAINT GENRES_PK PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS MPA (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(25),
  CONSTRAINT MPA_PK PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS DIRECTORS (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(25),
  CONSTRAINT DIRECTORS_PK PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS FRIENDS (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  friend_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  status_id INTEGER REFERENCES STATUSES(status_id),
  CONSTRAINT FRIENDS_PK PRIMARY KEY (id),
  CONSTRAINT FK_FRIENDS_USER FOREIGN KEY (user_id) REFERENCES USERS,
  CONSTRAINT FK_FRIENDS_FRIEND FOREIGN KEY (friend_id) REFERENCES USERS,
  CONSTRAINT FK_FRIENDS_STATUS FOREIGN KEY (status_id) REFERENCES STATUSES
);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  film_id INTEGER NOT NULL REFERENCES FILMS(id) ON DELETE CASCADE,
  genre_id INTEGER NOT NULL REFERENCES GENRES(id) ON DELETE CASCADE,
  status_id INTEGER REFERENCES STATUSES(status_id),
  CONSTRAINT FILM_GENRE_PK primary key (id),
  CONSTRAINT FK_FILM_GENRE_FILM FOREIGN KEY (film_id) REFERENCES FILMS,
  CONSTRAINT FK_FILM_GENRE_GENRE FOREIGN KEY (genre_id) REFERENCES GENRES,
  CONSTRAINT FK_FILM_GENRE_STATUS FOREIGN KEY (status_id) REFERENCES STATUSES
);

CREATE TABLE IF NOT EXISTS FILM_MPA (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  film_id INTEGER NOT NULL REFERENCES FILMS(id) ON DELETE CASCADE,
  mpa_id INTEGER NOT NULL REFERENCES MPA(id) ON DELETE CASCADE,
  status_id INTEGER REFERENCES STATUSES(status_id),
  CONSTRAINT FILM_MPA_PK PRIMARY KEY (id),
  CONSTRAINT FK_FILM_MPA_FILM FOREIGN KEY (film_id) REFERENCES FILMS,
  CONSTRAINT FK_FILM_MPA_GENRE FOREIGN KEY (mpa_id) REFERENCES MPA,
  CONSTRAINT FK_FILM_MPA_STATUS FOREIGN KEY (status_id) REFERENCES STATUSES
);

CREATE TABLE IF NOT EXISTS FILM_DIRECTOR (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INTEGER NOT NULL REFERENCES FILMS(id) ON DELETE CASCADE,
    director_id INTEGER NOT NULL REFERENCES DIRECTORS(id) ON DELETE CASCADE,
    status_id INTEGER REFERENCES STATUSES(status_id),
    CONSTRAINT FILM_DIRECTOR_PK PRIMARY KEY (id),
    CONSTRAINT FK_FILM_DIRECTOR_FILM FOREIGN KEY (film_id) REFERENCES FILMS,
    CONSTRAINT FK_FILM_DIRECTOR_GENRE FOREIGN KEY (director_id) REFERENCES MPA,
    CONSTRAINT FK_FILM_DIRECTOR_STATUS FOREIGN KEY (status_id) REFERENCES STATUSES
    );

CREATE TABLE IF NOT EXISTS LIKES (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  film_id INTEGER NOT NULL REFERENCES FILMS(id) ON DELETE CASCADE,
  user_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  status_id INTEGER REFERENCES STATUSES(status_id),
  CONSTRAINT LIKES_PK PRIMARY KEY (id),
  CONSTRAINT FK_LIKES_FILM FOREIGN KEY (film_id) REFERENCES FILMS,
  CONSTRAINT FK_LIKES_GENRE FOREIGN KEY (user_id) REFERENCES USERS,
  CONSTRAINT FK_LIKES_STATUS FOREIGN KEY (status_id) REFERENCES STATUSES
);

CREATE TABLE IF NOT EXISTS REVIEWS (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  content VARCHAR(200),
  is_positive BOOLEAN,
  user_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  film_id INTEGER NOT NULL REFERENCES films(id) ON DELETE CASCADE ON UPDATE CASCADE,
  deleted BOOl DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS REVIEW_LIKES (
  review_id INTEGER REFERENCES reviews(id) ON DELETE CASCADE ON UPDATE CASCADE,
  user_id INTEGER REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  status_id INTEGER REFERENCES STATUSES(status_id),
  CONSTRAINT REVIEW_LIKES_PK PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS REVIEW_DISLIKES (
  review_id INTEGER REFERENCES reviews(id) ON DELETE CASCADE ON UPDATE CASCADE,
  user_id INTEGER REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  status_id INTEGER REFERENCES STATUSES(status_id),
  CONSTRAINT REVIEW_DISLIKES_PK PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS EVENT_TYPE (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  event_name VARCHAR(25)
);

CREATE TABLE IF NOT EXISTS OPERATION_TYPE (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  operation_name VARCHAR(25)
);

CREATE TABLE IF NOT EXISTS FEED (
  event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  film_id INTEGER,
  review_id INTEGER,
  friend_id INTEGER,
  user_id INTEGER NOT NULL REFERENCES USERS(id) ON DELETE CASCADE ON UPDATE CASCADE,
  event_type_id INTEGER NOT NULL REFERENCES event_type(id) ON DELETE NO ACTION ON UPDATE CASCADE,
  operation_id INTEGER NOT NULL REFERENCES operation_type(id) ON DELETE NO ACTION ON UPDATE CASCADE
);