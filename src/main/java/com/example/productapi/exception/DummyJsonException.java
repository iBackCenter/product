package com.example.productapi.exception;

public class DummyJsonException extends RuntimeException {

    public DummyJsonException(String message) {
        super(message);
    }

    public DummyJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class NotFoundException extends DummyJsonException {
        public NotFoundException(String message) {
            super(message);
        }
        public NotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class BadRequestException extends DummyJsonException {
        public BadRequestException(String message) {
            super(message);
        }
        public BadRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class BadGatewayException extends DummyJsonException {
        public BadGatewayException(String message) {
            super(message);
        }
        public BadGatewayException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class GatewayTimeoutException extends DummyJsonException {
        public GatewayTimeoutException(String message) {
            super(message);
        }
        public GatewayTimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InternalServerException extends DummyJsonException {
        public InternalServerException(String message) {
            super(message);
        }
        public InternalServerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
