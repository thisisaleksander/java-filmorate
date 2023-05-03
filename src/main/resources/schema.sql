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