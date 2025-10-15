package com.inovapredial.exceptions;

public class BuildingDeletionBlockedException extends RuntimeException {
    
    public BuildingDeletionBlockedException(String message) {
        super(message);
    }
    
    public BuildingDeletionBlockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
