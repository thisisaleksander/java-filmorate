package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;

    public List<Director> getAllDirectors() {
        return directorDbStorage.findAll();
    }

    public Director getDirectorById(int id) {
        return directorDbStorage.findDirectorById(id);
    }

    public Director save(Director director) {
        return directorDbStorage.save(director);
    }

    public Director update(Director director) {
        return directorDbStorage.update(director);
    }

    public void deleteDirector(int id) {
        directorDbStorage.deleteDirector(id);
    }
}
