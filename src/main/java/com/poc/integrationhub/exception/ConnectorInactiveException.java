package com.poc.integrationhub.exception;

public class ConnectorInactiveException extends RuntimeException {
    public ConnectorInactiveException(String connectorId) {
        super("Connecteur inactif: " + connectorId);
    }
}
