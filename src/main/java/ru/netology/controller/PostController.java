package ru.netology.controller;

import com.google.gson.Gson;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class PostController {
    public static final String APPLICATION_JSON = "application/json";
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    public void all(HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var gson = new Gson();
        List<Post> data = null;
        try {
            data = service.all();
        } catch (NotFoundException ex) {
            response.setStatus(SC_NOT_FOUND);
        }
        response.getWriter().print("All posts:\n" + gson.toJson(data));
    }

    public void getById(long id, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var gson = new Gson();
        Post post = null;
        try {
            post = service.getById(id);
        } catch (NotFoundException ex) {
            response.setStatus(SC_NOT_FOUND);
        }
        response.getWriter().print(gson.toJson(post));
    }

    public void save(Reader body, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var gson = new Gson();
        final var post = gson.fromJson(body, Post.class);
        final var data = service.save(post);
        response.getWriter().print("New post:\n" + gson.toJson(data));
    }

    public void removeById(long id, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            service.removeById(id);
            response.getWriter().print("Post with id: " + id + "has been removed");
        } catch (NotFoundException ex) {
            response.setStatus(SC_NOT_FOUND);
        }
    }
}
