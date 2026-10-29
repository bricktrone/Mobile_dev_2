package ru.mirea.khrechko.foodscan.data.network;

/** A readable exception for network, HTTP and response-format failures. */
public class NetworkException extends RuntimeException {
    public NetworkException(String message) { super(message); }
    public NetworkException(String message, Throwable cause) { super(message, cause); }
}
