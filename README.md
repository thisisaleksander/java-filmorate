# java-filmorate
Template repository for Yandex Practicum Filmorate project.
Rest API with kinopoisk logic

Developer tasks:
Ekaterina -> add-search branch

	~ GET /fimls/search
	Возвращает список фильмов, отсортированных по популярности.

Konstantin -> add-feed

	~ GET /users/{id}/feed
	Возвращает ленту событий пользователя.

Andrey -> add-recommendations branch

	~ GET /users/{id}/recommendations
	Возвращает рекомендации по фильмам для просмотра.

Elena -> add-reviews branch

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
