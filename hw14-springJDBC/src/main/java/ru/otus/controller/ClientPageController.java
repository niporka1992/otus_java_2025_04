package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.service.ClientService;

@Controller
@RequiredArgsConstructor
public class ClientPageController {

    private final ClientService clientService;

    @GetMapping("/clients")
    public String clientsPage(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "client";
    }
}
