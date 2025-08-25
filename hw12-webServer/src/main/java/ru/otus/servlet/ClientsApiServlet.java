package ru.otus.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.dao.ClientDao;
import ru.otus.dto.ClientDto;
import ru.otus.model.Client;

@RequiredArgsConstructor
public class ClientsApiServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ClientsApiServlet.class);

    private final transient ClientDao clientDao;
    private final transient Gson gson;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<ClientDto> clients =
                    clientDao.findAll().stream().map(ClientDto::fromEntity).toList();

            resp.setStatus(HttpServletResponse.SC_OK);
            writeResponse(resp, gson.toJson(clients));
        } catch (Exception e) {
            logger.error("Ошибка при обработке запроса", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        try (BufferedReader reader = req.getReader()) {
            String body = reader.lines().collect(Collectors.joining());

            ClientDto clientDto = parseClientDto(body, resp);
            if (clientDto == null) {
                return;
            }

            Client savedClient = clientDao.save(clientDto.toEntity());
            ClientDto savedDto = ClientDto.fromEntity(savedClient);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            writeResponse(resp, gson.toJson(savedDto));

        } catch (IOException e) {
            logger.error("Ошибка чтения или записи", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при сохранении клиента", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }

    private ClientDto parseClientDto(String body, HttpServletResponse resp) {
        try {
            return gson.fromJson(body, ClientDto.class);
        } catch (JsonSyntaxException e) {
            logger.warn("Некорректный JSON: {}", body, e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(resp, "{\"error\": \"Неверный формат JSON\"}");
            return null;
        } catch (Exception e) {
            logger.error("Ошибка парсинга JSON", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ошибка парсинга данных");
            return null;
        }
    }

    private void writeResponse(HttpServletResponse resp, String content) {
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(content);
        } catch (IOException e) {
            logger.error("Ошибка записи ответа", e);
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) {
        resp.setStatus(status);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write("{\"error\": \"" + message + "\"}");
        } catch (IOException e) {
            logger.error("Ошибка при отправке ошибки", e);
        }
    }
}
