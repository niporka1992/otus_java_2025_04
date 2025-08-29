package ru.otus.dto;

import java.util.List;

public record ClientDto(Long id, String name, String street, List<String> phones) {}
