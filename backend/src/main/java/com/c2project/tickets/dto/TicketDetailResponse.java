package com.c2project.tickets.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TicketDetailResponse(
    UUID id,
    String title,
    String description,
    com.c2project.tickets.domain.TicketStatus status,
    com.c2project.tickets.domain.TicketPriority priority,
    String assignee,
    Instant createdAt,
    Instant updatedAt,
    List<CommentResponse> comments
) {}
