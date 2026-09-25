package com.c2project.tickets.repository;

import com.c2project.tickets.domain.Ticket;
import com.c2project.tickets.domain.TicketStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

  @Query("""
      SELECT t FROM Ticket t
      WHERE (:status IS NULL OR t.status = :status)
        AND (
          :q IS NULL OR :q = ''
          OR LOWER(t.title) LIKE LOWER(CONCAT('%', :q, '%'))
          OR LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%'))
        )
      ORDER BY t.updatedAt DESC
      """)
  List<Ticket> search(@Param("q") String q, @Param("status") TicketStatus status);
}
