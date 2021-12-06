package br.com.alura.ecommerce.dispatcher;

public class DeadLetterWriteException extends RuntimeException {

    public DeadLetterWriteException(Throwable cause) {
        super(cause);
    }
}
