package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.otus.services.TemplateProcessor;

@SuppressWarnings({"java:S1989"})
public class ClientsServlet extends HttpServlet {

    private static final String CLIENT_PAGE_TEMPLATE = "client.html";
    private final transient TemplateProcessor templateProcessor;

    public ClientsServlet(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        var sessionUser = request.getSession().getAttribute("user");

        Map<String, Object> params = new HashMap<>();
        params.put("user", sessionUser);
        params.put("clients", List.of());
        response.getWriter().println(templateProcessor.getPage(CLIENT_PAGE_TEMPLATE, params));
    }
}
