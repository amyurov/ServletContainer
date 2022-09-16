package ru.netology.controller;

import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
@Controller
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
        Post post;
        try {
            post = service.getById(id);
            response.getWriter().print(gson.toJson(post));
        } catch (NotFoundException ex) {
            response.setStatus(SC_NOT_FOUND);
        }
    }

    public void save(Reader body, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var gson = new Gson();
        final var post = gson.fromJson(body, Post.class);
        try {
            final var data = service.save(post);
            response.getWriter().print("New post:\n" + gson.toJson(data));
        } catch (NotFoundException ex) {
            response.setStatus(SC_NOT_FOUND);
        }
    }

    public void update(Reader body, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var gson = new Gson();
        final var post = gson.fromJson(body, Post.class);
        try {
            final var data = service.update(post);
            response.getWriter().print("Updated\n" + gson.toJson(data));
        } catch (NotFoundException ex) {
            response.setStatus(SC_NOT_FOUND);
        }
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
