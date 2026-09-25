package com.c2project.tickets.controller;

import com.c2project.tickets.domain.TicketStatus;
import com.c2project.tickets.dto.CommentResponse;
import com.c2project.tickets.dto.CreateCommentRequest;
import com.c2project.tickets.dto.CreateTicketRequest;
import com.c2project.tickets.dto.TicketDetailResponse;
import com.c2project.tickets.dto.TicketResponse;
import com.c2project.tickets.dto.TransitionTicketRequest;
import com.c2project.tickets.dto.UpdateTicketRequest;
import com.c2project.tickets.service.TicketService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

  private final TicketService ticketService;

  public TicketController(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {
    return ticketService.create(request);
  }

  @GetMapping
  public List<TicketResponse> list(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) TicketStatus status) {
    return ticketService.list(q, status);
  }

  @GetMapping("/{id}")
  public TicketDetailResponse get(@PathVariable UUID id) {
    return ticketService.get(id);
  }

  @PatchMapping("/{id}")
  public TicketResponse update(
      @PathVariable UUID id, @Valid @RequestBody UpdateTicketRequest request) {
    return ticketService.update(id, request);
  }

  @PostMapping("/{id}/transitions")
  public TicketResponse transition(
      @PathVariable UUID id, @Valid @RequestBody TransitionTicketRequest request) {
    return ticketService.transition(id, request);
  }

  @PostMapping("/{id}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  public CommentResponse addComment(
      @PathVariable UUID id, @Valid @RequestBody CreateCommentRequest request) {
    return ticketService.addComment(id, request);
  }
}
