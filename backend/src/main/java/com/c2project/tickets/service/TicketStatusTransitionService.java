package com.c2project.tickets.service;

import com.c2project.tickets.domain.TicketStatus;
import com.c2project.tickets.exception.InvalidTransitionException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class TicketStatusTransitionService {

  private final Map<TicketStatus, Set<TicketStatus>> allowed = new EnumMap<>(TicketStatus.class);

  public TicketStatusTransitionService() {
    allowed.put(TicketStatus.OPEN, EnumSet.of(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED));
    allowed.put(TicketStatus.IN_PROGRESS, EnumSet.of(TicketStatus.RESOLVED, TicketStatus.CANCELLED));
    allowed.put(TicketStatus.RESOLVED, EnumSet.of(TicketStatus.CLOSED));
    allowed.put(TicketStatus.CLOSED, EnumSet.noneOf(TicketStatus.class));
    allowed.put(TicketStatus.CANCELLED, EnumSet.noneOf(TicketStatus.class));
  }

  public void assertAllowed(TicketStatus from, TicketStatus to) {
    Set<TicketStatus> next = allowed.getOrDefault(from, Set.of());
    if (!next.contains(to)) {
      throw new InvalidTransitionException(
          "Invalid status transition: " + from + " → " + to);
    }
  }

  public Set<TicketStatus> allowedTargets(TicketStatus from) {
    return Set.copyOf(allowed.getOrDefault(from, Set.of()));
  }
}
