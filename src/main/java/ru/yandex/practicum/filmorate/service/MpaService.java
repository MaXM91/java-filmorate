package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public Mpa getMpaById(Integer id) {
        log.info("MpaService/getMpaById: mpa Id - {} founded", id);
        return mpaDbStorage.found(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaDbStorage.get();
    }

}
