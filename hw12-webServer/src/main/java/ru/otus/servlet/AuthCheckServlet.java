package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class AuthCheckServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AuthCheckServlet.class);

    private final transient Gson gson;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        Map<String, Object> result = new HashMap<>();
        if (session != null && session.getAttribute("user") != null) {
            String userName = (String) session.getAttribute("user");
            result.put("isAuthenticated", true);
            result.put("userName", userName);
        } else {
            result.put("isAuthenticated", false);
        }

        try (PrintWriter writer = resp.getWriter()) {
            writer.println(gson.toJson(result));
        } catch (IOException e) {
            logger.error("Ошибка записи ответа", e);
            try {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка сервера");
            } catch (IOException ex) {
                logger.error("Ошибка при отправке статуса 500", ex);
            }
        }
    }
}
