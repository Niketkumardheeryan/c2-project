package com.c2project.tickets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
    @NotBlank @Size(max = 120) String author,
    @NotBlank @Size(max = 2000) String body
) {}
