package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.controller.PostController;
import ru.netology.service.PostServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";

    private PostController controller;

    @Override
    public void init() throws ServletException {
        final var context = new AnnotationConfigApplicationContext(JavaConfig.class);

        final var repository = context.getBean("postRepository");
        final var service = context.getBean(PostServiceImpl.class);
        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            // primitive routing
            if(method.equals(GET) && path.equals("/api/posts")) {
                controller.all(resp);
                return;
            }

            if (method.equals(GET) && path.matches("/api/posts/\\d+")) {
                // easy way
                final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                controller.getById(id, resp);
                return;
            }

            if (method.equals(POST) && path.equals("/api/posts")) {
                controller.save(req.getReader(), resp);
                return;
            }

            if (method.equals(PUT) && path.equals("/api/posts")) {
                controller.update(req.getReader(), resp);
                return;
            }

            if (method.equals(DELETE) && path.matches("/api/posts/\\d+")) {
                // easy way
                final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
