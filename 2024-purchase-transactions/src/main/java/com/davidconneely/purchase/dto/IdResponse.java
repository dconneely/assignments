package com.davidconneely.purchase.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record IdResponse(@NotNull UUID id) {
}
