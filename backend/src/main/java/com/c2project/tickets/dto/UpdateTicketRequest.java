package com.c2project.tickets.dto;

import com.c2project.tickets.domain.TicketPriority;
import jakarta.validation.constraints.Size;

public record UpdateTicketRequest(
    @Size(max = 200) String title,
    @Size(max = 5000) String description,
    TicketPriority priority,
    @Size(max = 120) String assignee
) {}
