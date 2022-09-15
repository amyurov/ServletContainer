package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {

    private ConcurrentHashMap<Long, Post> storage; // Хранилище для постов
    private AtomicLong indexPointer; // Счетчик новых постов. Или указатель т.к. указывает на индекс для нового поста. Начинается с 1.

    public PostRepository() {
        this.storage = new ConcurrentHashMap<>();
        this.indexPointer = new AtomicLong(1);
    }


    public List<Post> all() {
        if (!storage.isEmpty()) {
            return new ArrayList<>(storage.values());
        }
        return Collections.emptyList();
    }

    public Optional<Post> getById(long id) throws NotFoundException {
        if (storage.containsKey(id)) {
            return Optional.of(storage.get(id));
        } else throw new NotFoundException("No post with id " + id);
    }

    /*
    Сохранение не срабатывает только при ошибке парсинга. Если запрос с id=0, то создается новый пост, id устанавливается
    автоматически.
    При запросе с указанием id - создается новый пост, если пост с указанным id уже существует, то контент обновляется.
     */
    public Post save(Post post) {
        // Сначала проверяем есть ли пост с таким id. Если есть, то просто обновляем контент.
        if (storage.containsKey(post.getId())) {
            storage.get(post.getId())
                    .setContent(post.getContent());
            return post;
        }

        // Если поста с таким id нет, то
        // Проверяем не находится ли указатель на занятом индексе, который мы могли добавить вне очереди (не по порядку)
        if (storage.containsKey(indexPointer.get())) {

            // Узнаем на сколько нужно сместить указатель.
            // Иными словами считаем сколько чисел идет по порядку, начиная от того которое совпало.
            long countMatches = 0L;
            for (long delta = indexPointer.get(); storage.containsKey(delta); delta++) {
                countMatches++;
            }
            indexPointer.addAndGet(countMatches); // Устанавливаем указатель на верную позицию
        }

        // id = 0 - значит создаем новый пост
        if (post.getId() == 0) {
            post.setId(indexPointer.getAndIncrement()); // Присваиваем посту свободный индекс
            storage.put(post.getId(), post);
            return post;
        }

        // Если в запросе указан id, и его не существует, то создаем его
        storage.put(post.getId(), post); // Добавляем пост в хранилище
        return post;
    }

    public void removeById(long id) throws NotFoundException {
        if (storage.containsKey(id)) {
            storage.remove(id);
        } else {
            throw new NullPointerException("No post with id " + id);
        }

    }
}
