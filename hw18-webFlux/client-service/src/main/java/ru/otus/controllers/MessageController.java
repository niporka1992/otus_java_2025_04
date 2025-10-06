package ru.otus.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.domain.Message;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private static final String TOPIC_TEMPLATE = "/topic/response.";
    private static final long ADMIN_ROOM = 1408;
    private final WebClient datastoreClient;
    private final SimpMessagingTemplate template;

    public MessageController(WebClient datastoreClient, SimpMessagingTemplate template) {
        this.datastoreClient = datastoreClient;
        this.template = template;
    }

    @MessageMapping("/message.{roomId}")
    public void getMessage(@DestinationVariable("roomId") String roomId, Message message) {
        long room = Long.parseLong(roomId);
        logger.info("[Controller] get message:{}, roomId:{}", message, room);

        if (checkAdminRoom(room)) {
            logger.info("[Controller] Message skipped for admin room, roomId:{}", room);
            return;
        }

        saveMessage(roomId, message)
                .doOnNext(msgId -> logger.info("[Controller] message saved with id:{}", msgId))
                .doOnError(err -> logger.error("[Controller] saveMessage error", err))
                .subscribe();

        logger.info("[Controller] Sending message to room: {}", room);
        template.convertAndSend(
                String.format("%s%s", TOPIC_TEMPLATE, room), new Message(HtmlUtils.htmlEscape(message.messageStr())));

        logger.info("[Controller] Duplicating message to admin room: {}", ADMIN_ROOM);
        template.convertAndSend(
                String.format("%s%s", TOPIC_TEMPLATE, ADMIN_ROOM),
                new Message(HtmlUtils.htmlEscape(message.messageStr())));
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        var genericMessage = (GenericMessage<byte[]>) event.getMessage();
        var simpDestination = (String) genericMessage.getHeaders().get("simpDestination");
        if (simpDestination == null || !simpDestination.startsWith(TOPIC_TEMPLATE)) return;

        var roomId = parseRoomId(simpDestination);
        var principal = event.getUser();
        if (principal == null) return;

        logger.info("[Subscription] user:{} subscribes to {}, roomId:{}", principal.getName(), simpDestination, roomId);

        if (checkAdminRoom(roomId)) {
            getAllRoomIds()
                    .map(Long::parseLong)
                    .flatMap(rId -> getMessagesByRoomId(rId).doOnNext(msg -> {
                        logger.info("[Admin] Sending message from room {}: {}", rId, msg);
                        template.convertAndSendToUser(principal.getName(), simpDestination, msg);
                    }))
                    .subscribe(
                            null,
                            error -> logger.error("Error sending messages to admin", error),
                            () -> logger.info("[Admin] All messages sent to admin user: {}", principal.getName()));

        } else {
            getMessagesByRoomId(roomId)
                    .doOnSubscribe(s -> logger.info("[User] Loading messages for room: {}", roomId))
                    .doOnNext(msg -> {
                        logger.info("[User] Sending message: {}", msg);
                        template.convertAndSendToUser(principal.getName(), simpDestination, msg);
                    })
                    .doOnComplete(() -> logger.info("[User] Finished sending messages for room: {}", roomId))
                    .doOnError(err -> logger.error("[User] Error loading messages for room: {}", roomId, err))
                    .subscribe();
        }
    }

    private boolean checkAdminRoom(long roomId) {
        return roomId == ADMIN_ROOM;
    }

    private Flux<String> getAllRoomIds() {
        logger.info("[Controller] getAllRoomIds called");
        return WebClient.create("http://localhost:8081")
                .get()
                .uri("/rooms")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(roomId -> logger.info("[Controller] got roomId from WebClient: {}", roomId))
                .doOnComplete(() -> logger.info("[Controller] Completed fetching all roomIds"))
                .doOnError(err -> logger.error("[Controller] Error fetching roomIds", err));
    }

    private long parseRoomId(String simpDestination) {
        try {
            int idxRoom = simpDestination.lastIndexOf(TOPIC_TEMPLATE);
            return Long.parseLong(simpDestination.substring(idxRoom + TOPIC_TEMPLATE.length()));
        } catch (Exception ex) {
            logger.error("Can not get roomId", ex);
            throw new ChatException("Can not get roomId");
        }
    }

    private Mono<Long> saveMessage(String roomId, Message message) {
        return datastoreClient
                .post()
                .uri(String.format("/msg/%s", roomId))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .exchangeToMono(response -> response.bodyToMono(Long.class));
    }

    private Flux<Message> getMessagesByRoomId(long roomId) {
        return datastoreClient
                .get()
                .uri(String.format("/msg/%s", roomId))
                .accept(MediaType.APPLICATION_NDJSON)
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToFlux(Message.class);
                    } else {
                        return response.createException().flatMapMany(Mono::error);
                    }
                });
    }
}
