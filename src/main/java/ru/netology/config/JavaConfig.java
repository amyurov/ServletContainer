package ru.netology.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.controller.PostController;
import ru.netology.repository.IPostRepository;
import ru.netology.repository.PostRepository;
import ru.netology.service.IPostService;
import ru.netology.service.PostService;

@Configuration
public class JavaConfig {

    @Bean
    public PostController postController(IPostService postService) {
        return new PostController(postService);
    }

    @Bean
    public PostService postService(IPostRepository postRepository) {
        return new PostService(postRepository);
    }

    @Bean
    public PostRepository postRepository() {
        return new PostRepository();
    }
}
