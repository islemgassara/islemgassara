package com.poc.integrationhub.exception;

public class ConnectorNotFoundException extends RuntimeException {
    public ConnectorNotFoundException(String connectorId) {
        super("Connecteur inconnu: " + connectorId);
    }
}
