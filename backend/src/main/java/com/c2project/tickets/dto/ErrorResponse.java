package com.c2project.tickets.dto;

import java.util.List;

public record ErrorResponse(
    int status,
    String message,
    List<FieldErrorDetail> fieldErrors
) {
  public record FieldErrorDetail(String field, String message) {}
}
