package com.anf.core.services.system;

/** Unchecked exception for errors concerning system users that can not be handled on runtime. */
public class ResourceResolverException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceResolverException(String message, Throwable cause) {
    super(message, cause);
  }
}
