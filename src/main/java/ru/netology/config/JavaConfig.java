package ru.netology.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.repository.PostRepositoryImpl;
import ru.netology.service.PostService;
import ru.netology.service.PostServiceImpl;

@Configuration
public class JavaConfig {

    @Bean
    public PostController postController(PostService postService) {
        return new PostController(postService);
    }

    @Bean
    public PostServiceImpl postService(PostRepository postRepository) {
        return new PostServiceImpl(postRepository);
    }

    @Bean
    public PostRepositoryImpl postRepository() {
        return new PostRepositoryImpl();
    }
}
