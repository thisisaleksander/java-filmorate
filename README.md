# java-filmorate
Repository for Yandex Practicum Filmorate project.
Rest API with kinopoisk logic

Developer tasks:

Ekaterina -> add-search

	~ GET /fimls/search
	Возвращает список фильмов, отсортированных по популярности.

Konstantin -> add-feed

	~ GET /users/{id}/feed
	Возвращает ленту событий пользователя.

Andrey -> add-recommendations

	~ GET /users/{id}/recommendations
	Возвращает рекомендации по фильмам для просмотра.

Elena -> add-reviews

	~ POST /reviews
	Добавление нового отзыва.

	~ PUT /reviews
	Редактирование уже имеющегося отзыва.

	~ DELETE /reviews/{id}
	Удаление уже имеющегося отзыва.

	~ GET /reviews/{id}
	Получение отзыва по идентификатору.

	~ GET /reviews?filmId={filmId}&count={count} 
	Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.

	~ PUT /reviews/{id}/like/{userId}
 	Пользователь ставит лайк отзыву.

	~ PUT /reviews/{id}/dislike/{userId}
	Пользователь ставит дизлайк отзыву.

	~ DELETE /reviews/{id}/like/{userId}
	Пользователь удаляет лайк/дизлайк отзыву.

	~ DELETE /reviews/{id}/dislike/{userId}
	Пользователь удаляет дизлайк отзыву.

Alexander -> add-common-films

	~ GET /films/common?userId={userId}&friendId={friendId} 
	Возвращает список фильмов, отсортированных по популярности.
	
Ekaterina -> add-director

	~ GET /films/director/{directorId}?sortBy=[year,likes]
	Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.
	
	~ GET /directors
	Список всех режиссёров

	~ GET /directors/{id}
	Получение режиссёра по id

	~ POST /directors
	Создание режиссёра

	~ PUT /directors
	Изменение режиссёра
	
	~ DELETE /directors/{id}
	Удаление режиссёра

Alexander -> add-most-populars

	~ GET /films/popular?count={limit}&genreId={genreId}&year={year}
	Возвращает список самых популярных фильмов указанного жанра за нужный год.
	
Andrey -> add-remove-endpoint

	~ DELETE /users/{userId}
	Удаляет пользователя по идентификатору. 

	~ DELETE /films/{filmId} 
	Удаляет фильм по идентификатору.
