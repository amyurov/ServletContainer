package ru.netology.service;

import org.springframework.stereotype.Service;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.repository.IPostRepository;

import java.util.List;
@Service
public class PostService implements IPostService {
    private final IPostRepository repository;

    public PostService(IPostRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Post> all() {
        if (repository.all().isEmpty()) {
            throw new NotFoundException("No posts have been created yet");
        }
        return repository.all();
    }

    @Override
    public Post getById(long id) {
        return repository.getById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Post save(Post post) {
        return repository.save(post);
    }

    @Override
    public Post update(Post post) {
        return repository.update(post);
    }

    @Override
    public void removeById(long id) {
        repository.removeById(id);
    }

}
