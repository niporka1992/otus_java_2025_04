package ru.otus.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.dto.ClientDto;
import ru.otus.service.ClientService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clients")
public class ClientApiController {

    private final ClientService clientService;

    @GetMapping
    public List<ClientDto> getClients() {
        return clientService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto createClient(@RequestBody ClientDto dto) {
        return clientService.save(dto);
    }
}
