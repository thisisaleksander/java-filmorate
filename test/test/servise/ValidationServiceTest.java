package test.servise;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmValidationFailedException;
import ru.yandex.practicum.filmorate.exception.UserValidationFailedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.ValidateService;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDate;

public class ValidationServiceTest {
    static User testUser;
    static Film testFilm;

    @BeforeAll
    public static void init() {

        testUser = new User(
                1,
                "test@yandex.ru",
                "login",
                "Alexander",
                LocalDate.of(2002, 3, 29)
        );
        testFilm = new Film(
                1,
                "Avatar",
                "Film description",
                LocalDate.of(2020, 10, 9),
                Duration.ofMinutes(120)
        );
    }
    @Test
    public void shouldNotThrowOnUserValidationTest() {
        ValidateService.validateUser(testUser);
        assertTrue(ValidateService.isValid);
    }

    @Test
    public void shouldThrowOnUserWhenInvalidEmailTest() {
        testUser.setEmail("strange email");
        assertThrows(UserValidationFailedException.class,
                    () -> ValidateService.validateUser(testUser)
                );
        assertFalse(ValidateService.isValid);
        testUser.setEmail("test@yandex.ru");
    }

    @Test
    public void shouldThrowOnUserWhenEmailDoNotContainsDogTest() {
        testUser.setEmail("testyandex.ru");
        assertThrows(UserValidationFailedException.class,
                () -> ValidateService.validateUser(testUser)
        );
        assertFalse(ValidateService.isValid);
        testUser.setEmail("test@yandex.ru");
    }

    @Test
    public void shouldThrowOnUserWhenInvalidBirthdayTest() {
        testUser.setBirthday(LocalDate.of(2025, 3, 29));
        assertThrows(UserValidationFailedException.class,
                () -> ValidateService.validateUser(testUser)
        );
        assertFalse(ValidateService.isValid);
        testUser.setBirthday(LocalDate.of(2002, 3, 29));
    }

    @Test
    public void shouldChangeNameToLoginInUserTest() {
        testUser.setName("");
        ValidateService.validateUser(testUser);
        assertTrue(ValidateService.isValid);
        assertEquals(testUser.getName(), "login");
        testUser.setName("Alexander");
    }

    @Test
    public void shouldNotThrowOnFilmValidationTest() {
        ValidateService.validateFilm(testFilm);
        assertTrue(ValidateService.isValid);
    }

    @Test
    public void shouldThrowOnFilmWhenNameIsBlankTest() {
        testFilm.setName("");
        assertThrows(FilmValidationFailedException.class,
                () -> ValidateService.validateFilm(testFilm)
        );
        assertFalse(ValidateService.isValid);
        testFilm.setName("Avatar");
    }

    @Test
    public void shouldThrowOnFilmWhenDescriptionIsTooLongTest() {
        testFilm.setDescription("desc".repeat(60));
        assertThrows(FilmValidationFailedException.class,
                () -> ValidateService.validateFilm(testFilm)
        );
        assertFalse(ValidateService.isValid);
        testFilm.setDescription("Test description");
    }

    @Test
    public void shouldThrowOnFilmWhenIncorrectReleaseDateTest() {
        testFilm.setReleaseDate(LocalDate.of(1800, 1, 1));
        assertThrows(FilmValidationFailedException.class,
                () -> ValidateService.validateFilm(testFilm)
        );
        assertFalse(ValidateService.isValid);
        testFilm.setReleaseDate(LocalDate.of(2020, 10, 9));
    }

    @Test
    public void shouldThrowOnFilmWhenIncorrectDurationTest() {
        testFilm.setDuration(Duration.ofMinutes(-120));
        assertThrows(FilmValidationFailedException.class,
                () -> ValidateService.validateFilm(testFilm)
        );
        assertFalse(ValidateService.isValid);
        testFilm.setDuration(Duration.ofMinutes(120));
    }
}
