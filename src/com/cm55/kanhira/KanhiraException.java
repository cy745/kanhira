package com.cm55.kanhira;

public class KanhiraException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public KanhiraException(String message) {
    super(message);
  }
  public KanhiraException(Exception ex) {
    super(ex);
  }
}
