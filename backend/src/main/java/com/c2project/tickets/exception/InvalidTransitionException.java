package com.c2project.tickets.exception;

public class InvalidTransitionException extends RuntimeException {
  public InvalidTransitionException(String message) {
    super(message);
  }
}
