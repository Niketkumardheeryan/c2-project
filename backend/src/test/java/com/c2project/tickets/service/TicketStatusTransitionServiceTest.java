package com.c2project.tickets.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.c2project.tickets.domain.TicketStatus;
import com.c2project.tickets.exception.InvalidTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TicketStatusTransitionServiceTest {

  private TicketStatusTransitionService service;

  @BeforeEach
  void setUp() {
    service = new TicketStatusTransitionService();
  }

  @ParameterizedTest
  @CsvSource({
      "OPEN,IN_PROGRESS",
      "OPEN,CANCELLED",
      "IN_PROGRESS,RESOLVED",
      "IN_PROGRESS,CANCELLED",
      "RESOLVED,CLOSED"
  })
  void allowsValidTransitions(TicketStatus from, TicketStatus to) {
    service.assertAllowed(from, to);
  }

  @ParameterizedTest
  @CsvSource({
      "CLOSED,OPEN",
      "RESOLVED,OPEN",
      "CANCELLED,OPEN",
      "OPEN,RESOLVED",
      "OPEN,CLOSED",
      "IN_PROGRESS,CLOSED",
      "RESOLVED,CANCELLED",
      "CLOSED,IN_PROGRESS",
      "OPEN,OPEN"
  })
  void rejectsInvalidTransitions(TicketStatus from, TicketStatus to) {
    assertThatThrownBy(() -> service.assertAllowed(from, to))
        .isInstanceOf(InvalidTransitionException.class)
        .hasMessageContaining(from.name())
        .hasMessageContaining(to.name());
  }

  @Test
  void allowedTargetsForOpen() {
    assertThat(service.allowedTargets(TicketStatus.OPEN))
        .containsExactlyInAnyOrder(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED);
  }
}
