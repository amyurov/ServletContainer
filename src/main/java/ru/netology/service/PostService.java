package ru.netology.service;

import org.springframework.stereotype.Service;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.repository.PostRepositoryImpl;

import java.util.List;
@Service
public class PostService {
    private final PostRepositoryImpl repository;

    public PostService(PostRepositoryImpl repository) {
        this.repository = repository;
    }

    public List<Post> all() {
        if (repository.all().isEmpty()) {
            throw  new NotFoundException("No posts have been created yet");
        }
        return repository.all();
    }

    public Post getById(long id) {
        return repository.getById(id).orElseThrow(NotFoundException::new);
    }

    public Post save(Post post) {
        return repository.save(post);
    }

    public void removeById(long id) {
        try {
            repository.removeById(id);
        } catch (NullPointerException ex) {
            throw new NotFoundException("No post with id: " + id);
        }
    }

}
