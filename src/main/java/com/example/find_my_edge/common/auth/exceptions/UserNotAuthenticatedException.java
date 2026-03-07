package com.example.find_my_edge.common.auth.exceptions;

public class UserNotAuthenticatedException extends RuntimeException {

  public UserNotAuthenticatedException() {
    super("User not authenticated");
  }
}
