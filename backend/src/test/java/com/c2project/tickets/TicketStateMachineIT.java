package com.c2project.tickets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class TicketStateMachineIT {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @Test
  void happyPathOpenToClosedAndRejectsIllegalReopen() throws Exception {
    String id = createTicket("Lifecycle ticket", "walk the state machine");

    transition(id, "IN_PROGRESS");
    transition(id, "RESOLVED");
    transition(id, "CLOSED");

    mockMvc
        .perform(
            post("/api/v1/tickets/" + id + "/transitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toStatus\":\"OPEN\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid status transition: CLOSED → OPEN"));
  }

  @Test
  void openAndInProgressCanCancel_terminalCannotReopen() throws Exception {
    String openId = createTicket("Cancel from open", "desc");
    transition(openId, "CANCELLED");
    mockMvc
        .perform(
            post("/api/v1/tickets/" + openId + "/transitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toStatus\":\"OPEN\"}"))
        .andExpect(status().isBadRequest());

    String progressId = createTicket("Cancel from progress", "desc");
    transition(progressId, "IN_PROGRESS");
    transition(progressId, "CANCELLED");
    mockMvc
        .perform(
            post("/api/v1/tickets/" + progressId + "/transitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toStatus\":\"OPEN\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid status transition: CANCELLED → OPEN"));
  }

  @Test
  void resolvedCannotGoBackToOpen() throws Exception {
    String id = createTicket("Resolved ticket", "desc");
    transition(id, "IN_PROGRESS");
    transition(id, "RESOLVED");
    mockMvc
        .perform(
            post("/api/v1/tickets/" + id + "/transitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toStatus\":\"OPEN\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid status transition: RESOLVED → OPEN"));
  }

  @Test
  void validationSearchFilterUpdateAndComments() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"description\":\"x\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fieldErrors").isArray());

    String id = createTicket("Printer offline", "Cannot print invoices");
    createTicket("Network blip", "VPN drops");

    mockMvc
        .perform(get("/api/v1/tickets").param("q", "printer"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Printer offline"));

    mockMvc
        .perform(get("/api/v1/tickets").param("status", "OPEN"))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            patch("/api/v1/tickets/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"assignee\":\"alex@example.com\",\"priority\":\"HIGH\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.assignee").value("alex@example.com"))
        .andExpect(jsonPath("$.priority").value("HIGH"));

    mockMvc
        .perform(
            post("/api/v1/tickets/" + id + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"author\":\"alex\",\"body\":\"Looking into it\"}"))
        .andExpect(status().isCreated());

    MvcResult detail =
        mockMvc
            .perform(get("/api/v1/tickets/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.comments[0].body").value("Looking into it"))
            .andReturn();

    JsonNode node = objectMapper.readTree(detail.getResponse().getContentAsString());
    assertThat(node.get("status").asText()).isEqualTo("OPEN");
  }

  private String createTicket(String title, String description) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/tickets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"title":"%s","description":"%s","priority":"MEDIUM"}
                        """
                            .formatted(title, description)))
            .andExpect(status().isCreated())
            .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
  }

  private void transition(String id, String toStatus) throws Exception {
    mockMvc
        .perform(
            post("/api/v1/tickets/" + id + "/transitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toStatus\":\"" + toStatus + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(toStatus));
  }
}
