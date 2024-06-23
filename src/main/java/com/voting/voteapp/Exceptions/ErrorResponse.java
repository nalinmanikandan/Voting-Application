package com.voting.voteapp.Exceptions;

public class ErrorResponse {

    private int statusCode;
    private String reason;
    private String message;
    private String path;

    // Getters and setters
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ErrorResponse(int statusCode, String reason, String message, String path){
        this.message=message;
        this.path=path;
        this.reason=reason;
        this.statusCode=statusCode;
    }

}
