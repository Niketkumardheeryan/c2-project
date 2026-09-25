package com.c2project.tickets.dto;

import com.c2project.tickets.domain.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
    @NotBlank @Size(max = 200) String title,
    @NotBlank @Size(max = 5000) String description,
    TicketPriority priority,
    @Size(max = 120) String assignee
) {}
