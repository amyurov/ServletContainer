package ru.netology.repository;

import ru.netology.model.Post;

import java.util.List;
import java.util.Optional;

public interface IPostRepository {

    List<Post> all();

    Optional<Post> getById(long id);

    Post save(Post post);

    Post update(Post post);

    void removeById(long id);
}
