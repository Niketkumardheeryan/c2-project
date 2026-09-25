package com.c2project.tickets.dto;

import com.c2project.tickets.domain.TicketPriority;
import com.c2project.tickets.domain.TicketStatus;
import java.time.Instant;
import java.util.UUID;

public record TicketResponse(
    UUID id,
    String title,
    String description,
    TicketStatus status,
    TicketPriority priority,
    String assignee,
    Instant createdAt,
    Instant updatedAt
) {}
