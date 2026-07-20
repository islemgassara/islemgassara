package com.poc.integrationhub.exception;

public class N8nCallFailedException extends RuntimeException {
    public N8nCallFailedException(String message, Throwable cause) {
        super("Échec de l'appel n8n: " + message, cause);
    }
}
