package ru.yandex.practicum.filmorate.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.Constants.STATUS_ACTIVE;
import static ru.yandex.practicum.filmorate.storage.Constants.STATUS_DELETED;

@Slf4j
@Component
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final DirectorDbStorage directorDbStorage;

    private static final String FILM_LOG = "FILM - {} : {}";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, DirectorDbStorage directorDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.directorDbStorage = directorDbStorage;
    }

    @Override
    public Film add(@NonNull Film film) {
        ValidateService.validateFilm(film);
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rate) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate()
        );
        log.info(FILM_LOG, LocalDateTime.now(), "added");
        List<Film> filmsList = jdbcTemplate.query("SELECT f.ID, f.name, description, release_date, duration, rate, deleted, " +
                        "fm.MPA_ID, m.NAME as mpa_name FROM films f " +
                        "LEFT JOIN (SELECT * FROM FILM_MPA WHERE status_id = 2) fm ON f.ID = fm.FILM_ID " +
                        "LEFT JOIN MPA m ON m.ID = fm.MPA_ID " +
                        "ORDER BY f.ID DESC LIMIT 1",
                new FilmMapper()
        );
        Film filmToReturn = filmsList.get(0);
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            addMpa(film.getMpa().getId(), filmToReturn.getId());
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> addGenre(genre.getId(), filmToReturn.getId()));
        }
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            film.getDirectors().forEach(director -> addDirector(director.getId(), filmToReturn.getId()));
        }
        return get(filmToReturn.getId());
    }

    @Override
    public Film update(@NonNull Integer id, @NonNull Film film) {
        get(id);
        ValidateService.validateFilm(film);
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate = ?, deleted = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getDeleted(),
                id
        );
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            addMpa(film.getMpa().getId(), film.getId());
        }
        removeAllGenres(id);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> addGenre(genre.getId(), id));
        }
        removeAllDirectors(id);
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            film.getDirectors().forEach(director -> addDirector(director.getId(), id));
        }
        log.info(FILM_LOG, LocalDateTime.now(), "updated");
        return get(id);
    }

    @Override
    public Film get(@NonNull Integer id) {
        String sql = "SELECT f.ID, f.name, description, release_date, duration, rate, deleted, " +
                "fm.MPA_ID, m.NAME as mpa_name FROM films f " +
                "LEFT JOIN (SELECT * FROM FILM_MPA WHERE status_id = 2) fm ON f.ID = fm.FILM_ID " +
                "LEFT JOIN MPA m ON m.ID = fm.MPA_ID " +
                "WHERE f.ID = ?";
        List<Film> filmsList = jdbcTemplate.query(sql, new FilmMapper(), id);
        if (filmsList.isEmpty()) {
            log.info("Film not found, id = {}", id);
            throw new NotFoundException("Film with id = " + id + " do not exist");
        }
        log.info("Found film with id = {}", id);
        Film film = filmsList.get(0);
        film.setGenres(genreDbStorage.getGenresOfFilm(id));
        film.setDirectors(findDirectorsByFilmId(film.getId()));
        return film;
    }

    @Override
    public Set<Film> getAll() {
        List<Film> filmsList = jdbcTemplate.query("SELECT f.ID, f.name, description, release_date, duration, rate, deleted, " +
                        "fm.MPA_ID, m.NAME as mpa_name FROM films f " +
                        "LEFT JOIN (SELECT * FROM FILM_MPA WHERE status_id = 2) fm ON f.ID = fm.FILM_ID " +
                        "LEFT JOIN MPA m ON m.ID = fm.MPA_ID " +
                        "ORDER BY f.ID",
                new FilmMapper()
        );
        if (filmsList.isEmpty()) {
            log.info("No films found in database");
        }
        log.info("Total films found: {}", Optional.of(filmsList.size()));
        filmsList.forEach(film -> film.setGenres(genreDbStorage.getGenresOfFilm(film.getId())));
        filmsList.forEach(film -> film.setDirectors(findDirectorsByFilmId(film.getId())));
        return filmsList.stream()
                .sorted(Film::getFilmIdToCompare)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void addGenre(Integer genreId, Integer filmId) {
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("SELECT * FROM film_genre WHERE (film_id = ? AND genre_id = ?) AND status_id = ?",
                filmId,
                genreId,
                STATUS_ACTIVE
        );
        if (resultSet.next()) {
            log.info("Genre already added to film with id = {}", filmId);
        } else {
            String sqlQuery = "INSERT INTO film_genre (film_id, genre_id, status_id) " +
                    "VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    genreId,
                    STATUS_ACTIVE
            );
            log.info("Add new genre to film with id = {}", filmId);
        }
    }

    @Override
    public void removeGenre(Integer genreId, Integer filmId) {
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("SELECT * FROM film_genre WHERE (film_id = ? AND genre_id = ?) AND status_id = ?",
                filmId,
                genreId,
                STATUS_ACTIVE
        );
        if (resultSet.next()) {
            String sqlQuery = "UPDATE film_genre SET status_id = ? WHERE (film_id = ? AND genre_id = ?)";
            jdbcTemplate.update(sqlQuery,
                    STATUS_DELETED,
                    filmId,
                    genreId
            );
            log.info("Genre was removed from film with id = {}", filmId);
        } else {
            log.info("Genre was not added to film with id = {}", filmId);
        }
    }

    @Override
    public void removeAllGenres(Integer filmId) {
        String sqlQuery = "UPDATE film_genre SET status_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                STATUS_DELETED,
                filmId
        );
        log.info("All genres was removed from film with id = {}", filmId);
    }

    public void addDirector(Integer directorId, Integer filmId) {
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "SELECT * FROM film_director WHERE (film_id = ? AND director_id = ?) AND status_id = ?",
                filmId,
                directorId,
                STATUS_ACTIVE
        );
        if (resultSet.next()) {
            log.info("Director already added to film with id = {}", filmId);
        } else {
            String sqlQuery = "INSERT INTO film_director (film_id, director_id, status_id) " +
                    "VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    directorId,
                    STATUS_ACTIVE
            );
            log.info("Add new director to film with id = {}", filmId);
        }
    }

    public void removeAllDirectors(Integer filmId) {
        String sqlQuery = "UPDATE film_director SET status_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                STATUS_DELETED,
                filmId
        );
        log.info("All directors was removed from film with id = {}", filmId);
    }

    @Override
    public void addMpa(Integer mpaId, Integer filmId) {
        removeMpa(filmId);
        String sqlQuery = "INSERT INTO film_mpa (film_id, mpa_id, status_id) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                filmId,
                mpaId,
                STATUS_ACTIVE
        );
        log.info("Add new mpa to film with id = {}", filmId);
    }

    @Override
    public void removeMpa(Integer filmId) {
        String sqlQuery = "UPDATE film_mpa SET status_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                STATUS_DELETED,
                filmId
        );
        log.info("Mpa was removed from film with id = {}", filmId);
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sql = "SELECT f.ID, f.name, description, release_date, duration, " +
                "rate, deleted, fm.MPA_ID, m.NAME as mpa_name " +
                "FROM films f " +
                "LEFT JOIN (SELECT * FROM FILM_MPA WHERE status_id = 2) fm ON f.ID = fm.FILM_ID " +
                "LEFT JOIN MPA m ON m.ID = fm.MPA_ID " +
                "LEFT JOIN (SELECT * FROM LIKES WHERE user_id = ? AND status_id = 2) l1 ON l1.film_id = f.ID " +
                "LEFT JOIN (SELECT * FROM LIKES WHERE user_id = ? AND status_id = 2) l2 ON l2.film_id = f.ID " +
                "WHERE l1.user_id IS NOT NULL AND l2.user_id IS NOT NULL " +
                "ORDER BY rate DESC";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper(), userId, friendId);
        if (films.isEmpty()) {
            log.info("No films found in database");
            return films;
        }
        films.forEach(film -> film.setGenres(genreDbStorage.getGenresOfFilm(film.getId())));
        films.forEach(film -> film.setDirectors(findDirectorsByFilmId(film.getId())));
        log.info("Total common films found: " + films.size());
        return films;
    }

    public List<Film> getSortedFilmsWithIdDirector(Integer id, String sortBy) {
        if (!jdbcTemplate.queryForRowSet("SELECT id FROM directors WHERE id = ?", id).next()) {
            throw new DirectorNotFoundException(String.format("Director with id %d not found", id));
        }
        if (sortBy.equalsIgnoreCase(Criteria.YEAR.toString())) {
            String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                    "f.duration, f.rate, f.deleted, m.id AS mpa_id, m.name AS mpa_name, " +
                    "d.id AS director_id, d.name AS director_name " +
                    "FROM films AS f " +
                    "LEFT JOIN film_genre AS fg ON fg.film_id = f.id " +
                    "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                    "LEFT JOIN film_mpa AS fm ON fm.film_id = f.id " +
                    "LEFT JOIN mpa AS m ON m.id = fm.mpa_id " +
                    "LEFT JOIN film_director AS fd ON fd.film_id = f.id " +
                    "LEFT JOIN directors AS d ON d.id = fd.director_id " +
                    "WHERE d.id = ? AND fm.status_id = 2 " +
                    "GROUP BY f.id " +
                    "ORDER BY f.release_date";
            return getGenresAndDirectorsForAllFilms(id, sqlQuery);
        } else if (sortBy.equalsIgnoreCase(Criteria.LIKES.toString())) {
            String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                    "f.duration, f.rate, f.deleted, m.id AS mpa_id, m.name AS mpa_name, " +
                    "d.id AS director_id, d.name AS director_name " +
                    "FROM films AS f " +
                    "LEFT JOIN film_genre AS fg ON fg.film_id = f.id " +
                    "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                    "LEFT JOIN film_mpa AS fm ON fm.film_id = f.id " +
                    "LEFT JOIN mpa AS m ON m.id = fm.mpa_id " +
                    "LEFT JOIN film_director AS fd ON fd.film_id = f.id " +
                    "LEFT JOIN directors AS d ON d.id = fd.director_id " +
                    "WHERE d.id = ? AND fm.status_id = 2 " +
                    "GROUP BY f.id " +
                    "ORDER BY f.rate DESC";
            return getGenresAndDirectorsForAllFilms(id, sqlQuery);
        } else {
            throw new FilmValidationException(String.format("Request could not be proceeded"));
        }
    }

    public List<Film> findFilmsByKeyWord(String query, String by) {
        String pattern = "\'%" + query.toLowerCase() + "%\'";
        String[] whereSearch = by.split(",");
        if (whereSearch.length > 1) {
            String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                    "           f.duration, f.rate, f.deleted, m.id AS mpa_id, m.name AS mpa_name, " +
                    "GROUP_CONCAT(DISTINCT d.name) AS director_name " +
                    "FROM films AS f " +
                    "LEFT JOIN film_genre AS fg ON fg.film_id = f.id " +
                    "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                    "LEFT JOIN film_mpa AS fm ON fm.film_id = f.id " +
                    "LEFT JOIN mpa AS m ON m.id = fm.mpa_id " +
                    "LEFT JOIN film_director AS fd ON fd.film_id = f.id " +
                    "LEFT JOIN directors AS d ON d.id = fd.director_id " +
                    "WHERE fm.status_id = 2 AND (lower(f.name) LIKE " + pattern +
                    "OR lower(d.name) LIKE " + pattern + ") " +
                    "GROUP BY f.id " +
                    "ORDER BY f.rate";
            return getGenresAndDirectorsForAllFilms(sqlQuery);
        }
        if (by.equalsIgnoreCase(Criteria.TITLE.toString())) {
            String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                    "           f.duration, f.rate, f.deleted, m.id AS mpa_id, m.name AS mpa_name, " +
                    "GROUP_CONCAT(DISTINCT d.name) AS director_name " +
                    "FROM films AS f " +
                    "LEFT JOIN film_genre AS fg ON fg.film_id = f.id " +
                    "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                    "LEFT JOIN film_mpa AS fm ON fm.film_id = f.id " +
                    "LEFT JOIN mpa AS m ON m.id = fm.mpa_id " +
                    "LEFT JOIN film_director AS fd ON fd.film_id = f.id " +
                    "LEFT JOIN directors AS d ON d.id = fd.director_id " +
                    "WHERE fm.status_id = 2 AND lower(f.name) LIKE " + pattern +
                    "GROUP BY f.id " +
                    "ORDER BY f.rate";
            return getGenresAndDirectorsForAllFilms(sqlQuery);
        }
        if (by.equalsIgnoreCase(Criteria.DIRECTOR.toString())) {
            String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                    "           f.duration, f.rate, f.deleted, m.id AS mpa_id, m.name AS mpa_name, " +
                    "GROUP_CONCAT(DISTINCT d.name) AS director_name " +
                    "FROM films AS f " +
                    "LEFT JOIN film_genre AS fg ON fg.film_id = f.id " +
                    "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                    "LEFT JOIN film_mpa AS fm ON fm.film_id = f.id " +
                    "LEFT JOIN mpa AS m ON m.id = fm.mpa_id " +
                    "LEFT JOIN film_director AS fd ON fd.film_id = f.id " +
                    "LEFT JOIN directors AS d ON d.id = fd.director_id " +
                    "WHERE fd.status_id = 2 AND lower(d.name) LIKE " + pattern +
                    "GROUP BY f.id " +
                    "ORDER BY f.rate";
            return getGenresAndDirectorsForAllFilms(sqlQuery);
        }
        throw new FilmValidationException(String.format(
                "Request could not be proceeded"));
    }

    public Set<Director> findDirectorsByFilmId(Integer id) {
        Set<Director> directors = new HashSet<>();
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT id FROM films WHERE id = ?", id);
        if (directorRows.next()) {
            String sqlQuery = "SELECT d.id, d.name " +
                    "FROM directors AS d " +
                    "JOIN film_director AS fd ON fd.director_id = d.id " +
                    "WHERE fd.film_id = ? AND fd.status_id = 2";
            directors.addAll(jdbcTemplate.query(sqlQuery, new DirectorMapper(), id));
            return directors;
        } else {
            throw new NotFoundException(String.format("Film with id %d not found", id));
        }
    }

    private List<Film> getGenresAndDirectorsForAllFilms(Integer id, String sqlQuery) {
        List<Film> films = jdbcTemplate.query(sqlQuery, new FilmMapper(), id);
        films.forEach(film -> film.setGenres(genreDbStorage.getGenresOfFilm(film.getId())));
        films.forEach(film -> film.setDirectors(findDirectorsByFilmId(film.getId())));
        return films;
    }

    private List<Film> getGenresAndDirectorsForAllFilms(String sqlQuery) {
        List<Film> films = jdbcTemplate.query(sqlQuery, new FilmMapper());
        films.forEach(film -> film.setGenres(genreDbStorage.getGenresOfFilm(film.getId())));
        films.forEach(film -> film.setDirectors(findDirectorsByFilmId(film.getId())));
        return films;
    }

    public List<Film> getMostPopularFilms(Integer count, Integer limit, Integer genreId, Integer year) {
        String param;
        String bound;
        String groupAndOrder = " group by f.ID order by count(l.user_id) desc ";
        String sql = "SELECT f.ID, f.name, description, release_date, duration, rate, deleted, " +
                "fm.MPA_ID, m.NAME as mpa_name FROM films f " +
                "LEFT JOIN (SELECT * FROM FILM_MPA WHERE status_id = 2) fm ON f.ID = fm.FILM_ID " +
                "LEFT JOIN (SELECT * FROM FILM_GENRE WHERE status_id = 2) fg ON f.ID = fg.FILM_ID " +
                "LEFT JOIN MPA m ON m.ID = fm.MPA_ID " +
                "LEFT JOIN LIKES l on f.ID = l.FILM_ID ";
        if (genreId > 0 && year > 0) {
            param = " WHERE fg.genre_id = " + genreId + " AND YEAR(f.release_date) = " + year + groupAndOrder;
        } else if (genreId > 0 && year == 0) {
            param = " WHERE fg.genre_id = " + genreId + groupAndOrder;
        } else if (genreId == 0 && year > 0) {
            param =  " WHERE YEAR(f.release_date) = " + year + groupAndOrder;
        } else {
            param = groupAndOrder;
        }
        if (limit >= 1 && (genreId > 0 || year > 0)) {
            bound = " LIMIT " + limit;
        } else {
            bound = " LIMIT " + count;
        }
        List<Film> filmsList = jdbcTemplate.query(sql + param + bound,
                new FilmMapper()
        );
        if (filmsList.isEmpty()) {
            log.info("No films found in database");
            return filmsList;
        }
        log.info("Total films found in database: " + filmsList.size());
        filmsList.forEach(film -> film.setGenres(genreDbStorage.getGenresOfFilm(film.getId())));
        filmsList.forEach(film -> film.setDirectors(findDirectorsByFilmId(film.getId())));
        return filmsList;
    }

    public Set<Film> delete(Integer id) {
        String sql = "DELETE FROM FILMS WHERE ID = " + id;
        jdbcTemplate.update(sql);
        log.info(String.format("Film with id %d was deleted", id));
        return getAll();
    }
}