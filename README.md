# java-filmorate

Repository for Yandex Practicum Filmorate project.
Rest API with kinopoisk logic

## Project functionality:

### Films:

    ~ GET /films
    GF-1
    Возвращает список всех фильмов

    ~ GET /films/{id}
    GF-2
    Возвращает фильм под номером {id}

    ~ GET /films/common?userId={userId}&friendId={friendId}
    GF-3
	Возвращает список фильмов, отсортированных по популярности.

    ~ GET /films/director/{directorId}?sortBy=[year,likes]
    GF-4
	Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.

    ~ GET /films/popular?count={limit}&genreId={genreId}&year={year}
    GF-5
	Возвращает список самых популярных фильмов указанного жанра за нужный год.

    ~ GET /films/search
    GF-6
	Возвращает список фильмов, отсортированных по популярности.

    Может искать фильмы по описанию или режиссёру, пример:
    GET /films/search?query=крад&by=director,title
    query — текст для поиска
    by — может принимать значения `director` (поиск по режиссёру),
    `title` (поиск по названию), либо оба значения через запятую при поиске одновременно и по режиссеру и по названию.

    ~ POST /films
    PF-1
    Создает фильм

    ~ PUT /films
    PF-2
    Обновляет ранее созданный фильм

    ~ PUT /films/{id}/like/{userId}
    PF-3
    Добавляет лайк от пользователя {userId} для фильма {id}

    ~ DELETE /films/{filmId}
    DF-1
	Удаляет фильм по идентификатору.

    ~ DELETE /films/{id}/like/{userId}
    DF-2
    Удаляет лайк от пользователя {userId} для фильма {id}

### Users:

    ~ GET /users
    GU-1
    Возвращает список всех пользователей

    ~ GET /users/{id}
    GU-2
    Возвращает пользователя под номером {id}
    
    ~ GET /users/{id}/friends
    GU-3
    Возвращает список друзей пользователя под номером {id}

    ~ GET /users/{id}/friends/common/{otherId}
    GU-4
    Возвращает общий список друзей пользователей под номером {id} и {otherId}

    ~ GET /users/{id}/recommendations
    GU-5
	Возвращает рекомендации фильмов для пользователя {id}

    ~ GET /users/{id}/feed
    GU-6
	Возвращает ленту событий пользователя {id}

    ~ POST /users
    PU-1    
    Создает нового пользователя

    ~ PUT /users
    PU-2
    Обновляет раннее созданого пользователя

    ~ PUT /users/{id}/friends/{friendId}
    PU-3
    Отправляет заявку в друзья от пользователя {id} к пользователю {friendId}

    ~ PUT /users/{id}/friends-accept/{friendId}
    PU-4
    Принимает заявку в друзья от пользователя {friendId} к пользователю {id}

    ~ DELETE /users/{userId}
    DU-1  
	Удаляет пользователя по идентификатору.    

    ~ DELETE /users/{id}/friends/{friendId}
    DU-2
    Удаляет пользователя {friendId} из списка друзей пользователя {id}


### Reviews:
    ~ GET /reviews/{id}
    GR-1
	Получение отзыва по идентификатору.

	~ GET /reviews?filmId={filmId}&count={count}
    GR-2
	Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.

	~ POST /reviews
    PR-1
	Добавление нового отзыва.

	~ PUT /reviews
    PR-2
	Редактирование уже имеющегося отзыва.

	~ PUT /reviews/{id}/like/{userId}
    PR-3
 	Пользователь ставит лайк отзыву.

	~ PUT /reviews/{id}/dislike/{userId}
    PR-4
	Пользователь ставит дизлайк отзыву.

    ~ DELETE /reviews/{id}
    DR-1
	Удаление уже имеющегося отзыва.

	~ DELETE /reviews/{id}/like/{userId}
    DR-2
	Пользователь удаляет лайк отзыву.

	~ DELETE /reviews/{id}/dislike/{userId}
    DR-3
	Пользователь удаляет дизлайк отзыву.

### Directors:
	~ GET /directors
    GDir-1
	Получение списка всех режиссёров

	~ GET /directors/{id}
    GDir-2
	Получение режиссёра по {id}

	~ POST /directors
    PDir-1
	Создание режиссёра

	~ PUT /directors
    PDir-2
	Изменение режиссёра
	
	~ DELETE /directors/{id}
    DDir-1
	Удаление режиссёра

### Genres:
	~ GET /genres
    GGen-1
	Получение списка всех жанров

	~ GET /genres/{id}
    GGen-2
	Получение жанра по {id}

	~ POST /genres
    PGen-1
	Создание жанра

### MPA:
	~ GET /mpa
    GM-1
	Получение списка всех mpa

	~ GET /mpa/{id}
    GM-2
	Получение mpa по {id}