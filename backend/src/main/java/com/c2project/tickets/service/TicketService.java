package com.c2project.tickets.service;

import com.c2project.tickets.domain.Comment;
import com.c2project.tickets.domain.Ticket;
import com.c2project.tickets.domain.TicketPriority;
import com.c2project.tickets.domain.TicketStatus;
import com.c2project.tickets.dto.CommentResponse;
import com.c2project.tickets.dto.CreateCommentRequest;
import com.c2project.tickets.dto.CreateTicketRequest;
import com.c2project.tickets.dto.TicketDetailResponse;
import com.c2project.tickets.dto.TicketResponse;
import com.c2project.tickets.dto.TransitionTicketRequest;
import com.c2project.tickets.dto.UpdateTicketRequest;
import com.c2project.tickets.exception.BadRequestException;
import com.c2project.tickets.exception.ResourceNotFoundException;
import com.c2project.tickets.repository.TicketRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

  private final TicketRepository ticketRepository;
  private final TicketStatusTransitionService transitionService;

  public TicketService(
      TicketRepository ticketRepository,
      TicketStatusTransitionService transitionService) {
    this.ticketRepository = ticketRepository;
    this.transitionService = transitionService;
  }

  @Transactional
  public TicketResponse create(CreateTicketRequest request) {
    Ticket ticket = new Ticket();
    ticket.setTitle(request.title().trim());
    ticket.setDescription(request.description().trim());
    ticket.setPriority(request.priority() != null ? request.priority() : TicketPriority.MEDIUM);
    ticket.setAssignee(blankToNull(request.assignee()));
    ticket.setStatus(TicketStatus.OPEN);
    return toResponse(ticketRepository.save(ticket));
  }

  @Transactional(readOnly = true)
  public List<TicketResponse> list(String q, TicketStatus status) {
    String keyword = (q == null || q.isBlank()) ? null : q.trim();
    return ticketRepository.search(keyword, status).stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public TicketDetailResponse get(UUID id) {
    Ticket ticket = findTicket(id);
    // touch comments inside transaction
    List<CommentResponse> comments = ticket.getComments().stream().map(this::toComment).toList();
    return new TicketDetailResponse(
        ticket.getId(),
        ticket.getTitle(),
        ticket.getDescription(),
        ticket.getStatus(),
        ticket.getPriority(),
        ticket.getAssignee(),
        ticket.getCreatedAt(),
        ticket.getUpdatedAt(),
        comments);
  }

  @Transactional
  public TicketResponse update(UUID id, UpdateTicketRequest request) {
    if (request.title() == null
        && request.description() == null
        && request.priority() == null
        && request.assignee() == null) {
      throw new BadRequestException("At least one field must be provided");
    }
    Ticket ticket = findTicket(id);
    if (request.title() != null) {
      if (request.title().isBlank()) {
        throw new BadRequestException("title must not be blank");
      }
      ticket.setTitle(request.title().trim());
    }
    if (request.description() != null) {
      if (request.description().isBlank()) {
        throw new BadRequestException("description must not be blank");
      }
      ticket.setDescription(request.description().trim());
    }
    if (request.priority() != null) {
      ticket.setPriority(request.priority());
    }
    if (request.assignee() != null) {
      ticket.setAssignee(blankToNull(request.assignee()));
    }
    return toResponse(ticketRepository.save(ticket));
  }

  @Transactional
  public TicketResponse transition(UUID id, TransitionTicketRequest request) {
    Ticket ticket = findTicket(id);
    transitionService.assertAllowed(ticket.getStatus(), request.toStatus());
    ticket.setStatus(request.toStatus());
    return toResponse(ticketRepository.save(ticket));
  }

  @Transactional
  public CommentResponse addComment(UUID id, CreateCommentRequest request) {
    Ticket ticket = findTicket(id);
    Comment comment = new Comment();
    comment.setAuthor(request.author().trim());
    comment.setBody(request.body().trim());
    ticket.addComment(comment);
    ticketRepository.save(ticket);
    return toComment(comment);
  }

  private Ticket findTicket(UUID id) {
    return ticketRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id));
  }

  private TicketResponse toResponse(Ticket ticket) {
    return new TicketResponse(
        ticket.getId(),
        ticket.getTitle(),
        ticket.getDescription(),
        ticket.getStatus(),
        ticket.getPriority(),
        ticket.getAssignee(),
        ticket.getCreatedAt(),
        ticket.getUpdatedAt());
  }

  private CommentResponse toComment(Comment comment) {
    return new CommentResponse(
        comment.getId(), comment.getAuthor(), comment.getBody(), comment.getCreatedAt());
  }

  private static String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
