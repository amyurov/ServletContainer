package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private ConcurrentHashMap<Long, Post> storage; // Хранилище для постов
    private AtomicLong indexPointer; // Счетчик новых постов. Или указатель т.к. указывает на индекс для нового поста. Начинается с 1.
    private ConcurrentSkipListSet<Long> unorderedId; // Сет для индексов постов, которые добавлялись не по порядку
    private ConcurrentSkipListSet<Long> deletedId; // Сет для индексов постов, которые освободились после удаления

    public PostRepositoryImpl() {
        this.storage = new ConcurrentHashMap();
        this.indexPointer = new AtomicLong(1);
        this.unorderedId = new ConcurrentSkipListSet<>();
        this.deletedId = new ConcurrentSkipListSet<>();
    }


    public List<Post> all() {
        if (!storage.isEmpty()) {
            return new ArrayList<>(storage.values());
        }
        return Collections.emptyList();
    }

    public Optional<Post> getById(long id) {
        if (storage.containsKey(id)) {
            return Optional.of(storage.get(id));
        } else throw new NotFoundException("Поста с данным id нет");
    }

    /*
    Сохранение не срабатывает только при ошибке парсинга. Если запрос с id=0, то создается новый пост, id устанавливается
    автоматически - наименьший свободный.
    Свободный id это id за которым не закреплен пост => удаленные посты освобождают id для новых.
    При запросе с указанием id - создается новый пост, если пост с указанным id уже существует, то он обновляется.
     */
    public Post save(Post post) {
        // Сначала проверяем есть ли пост с таким id. Если есть, то просто обновляем контент.
        if (existingId(post.getId())) {
            storage.get(post.getId())
                    .setContent(post.getContent());
            return post;
        }

        // Если поста с таким id нет, то
        // Проверяем не находится ли указатель на занятом индексе, который мы могли добавить вне очереди (не по порядку)
        if (!unorderedId.isEmpty() && indexPointer.get() == unorderedId.first()) {
            long delta = 1; // Если указатель на существующем индексе, то его надо сместить минимум на 1

            // Узнаем на сколько нужно сместить указатель. (После инкремента указатель может снова попасть на занятой индекс)
            // Иными словами считаем сколько чисел идет по порядку, начиная от того которое совпало. Каждое такое число удаляется из сета.
            while ((unorderedId.size() > 1) && (unorderedId.higher(unorderedId.first()) - unorderedId.first()) == 1) {
                unorderedId.pollFirst();
                delta++;
            }
            unorderedId.pollFirst();
            indexPointer.addAndGet(delta); // Устанавливаем указатель на верную позицию
        }

        // id = 0 - значит создаем новый пост
        if (post.getId() == 0) {
            if (!deletedId.isEmpty()) {
                post.setId(freeIndexAfterDelete());
            } else {
                post.setId(indexPointer.getAndIncrement()); // Присваиваем посту наименьший свободный индекс
            }
            storage.put(post.getId(), post);
            return post;
        }

        // Если в запросе указан id, и его не существует, то создаем его
        unorderedId.add(post.getId()); // Добавляем этот id в список индексов занятых не по порядку.
        storage.put(post.getId(), post); // Добавляем пост в хранилище
        return post;
    }

    public void removeById(long id) {
        storage.remove(id);

        if (unorderedId.contains(id)) {
            unorderedId.remove(id);
        }

        deletedId.add(id);
    }

    private boolean existingId(long id) {
        return id <= indexPointer.get() && unorderedId.contains(id) && !deletedId.contains(id);
    }

    private Long freeIndexAfterDelete() {

        if (indexPointer.get() > deletedId.first()) {
            return deletedId.pollFirst();
        }

        if (indexPointer.get() == deletedId.first()) {
            indexPointer.incrementAndGet();
            return deletedId.pollFirst();
        }
        return indexPointer.getAndIncrement();
    }
}
