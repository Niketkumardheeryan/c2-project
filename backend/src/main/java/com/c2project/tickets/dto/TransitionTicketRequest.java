package com.c2project.tickets.dto;

import com.c2project.tickets.domain.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record TransitionTicketRequest(@NotNull TicketStatus toStatus) {}
