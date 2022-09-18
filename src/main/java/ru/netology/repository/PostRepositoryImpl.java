package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
@Repository
public class PostRepositoryImpl implements PostRepository {

    private ConcurrentHashMap<Long, Post> storage; // Хранилище для постов
    private AtomicLong indexPointer; // Счетчик новых постов. Или указатель т.к. указывает на индекс для нового поста. Начинается с 1.

    public PostRepositoryImpl() {
        this.storage = new ConcurrentHashMap<>();
        this.indexPointer = new AtomicLong(1);
    }

    @Override
    public List<Post> all() {
        if (!storage.isEmpty()) {
            return new ArrayList<>(storage.values());
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<Post> getById(long id) throws NotFoundException {
        if (storage.containsKey(id)) {
            return Optional.of(storage.get(id));
        } else throw new NotFoundException("No post with id " + id);
    }

    /*
    Сохранение не срабатывает только при ошибке парсинга. Если запрос с id=0, то создается новый пост, id устанавливается
    автоматически.
    При запросе с указанием id - создается новый пост, если пост с указанным id уже существует, то 404
     */
    @Override
    public Post save(Post post) throws NotFoundException {
        // Сначала проверяем есть ли пост с таким id. Если есть, то NotFoundException => 404
        if (storage.containsKey(post.getId())) {
            throw new NotFoundException(post.getId() + " already exist");
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

    @Override
    public Post update(Post post) throws NotFoundException {
        if (storage.containsKey(post.getId())) {
            storage.get(post.getId()).setContent(post.getContent());
        return post;
        } else {
            throw new NotFoundException("Post with id " + post.getId() + " doesn't exist");
        }
    }

    @Override
    public void removeById(long id) throws NotFoundException {
        if (storage.containsKey(id)) {
            storage.remove(id);
        } else {
            throw new NotFoundException("No post with id " + id);
        }

    }
}
