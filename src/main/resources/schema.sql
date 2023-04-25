CREATE TABLE IF NOT EXISTS USERS (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email VARCHAR(50),
  login VARCHAR(25) NOT NULL,
  name VARCHAR(25),
  birthday DATE,
  deleted  bool default false,
  constraint USER_PK primary key (id)
);

CREATE TABLE IF NOT EXISTS FILMS (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(50),
  description VARCHAR(200),
  release_date DATE,
  duration INTEGER,
  rate INTEGER,
  deleted  bool default false,
  constraint FILM_PK primary key (id)
);

CREATE TABLE IF NOT EXISTS FRIENDS (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_id INTEGER NOT NULL,
  friend_id INTEGER NOT NULL,
  status_id INTEGER,
  constraint FRIENDS_PK primary key (id)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  film_id INTEGER NOT NULL,
  genre_id INTEGER NOT NULL,
  status_id INTEGER,
  constraint FILM_GENRE_PK primary key (id)
);

CREATE TABLE IF NOT EXISTS FILM_MPA (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  film_id INTEGER NOT NULL,
  mpa_id INTEGER NOT NULL,
  status_id INTEGER,
  constraint FILM_MPA_PK primary key (id)
);

CREATE TABLE IF NOT EXISTS GENRES (
  genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  genre_name VARCHAR(25),
  constraint GENRES_PK primary key (genre_id)
);

CREATE TABLE IF NOT EXISTS MPA (
  mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  mpa_name VARCHAR(25),
  constraint MPA_PK primary key (mpa_id)
);

CREATE TABLE IF NOT EXISTS LIKES (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  film_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  status_id INTEGER,
  constraint LIKES_PK primary key (id)
);

CREATE TABLE IF NOT EXISTS STATUSES (
  status_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  status_name VARCHAR(25),
  constraint STATUSES_PK primary key (status_id)
);