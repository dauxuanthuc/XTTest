package com.example.demo.payload;

public class MessageResponse {
    private String message;
    private Object data;

    public MessageResponse(String message) {
        this.message = message;
        this.data = null;
    }

    public MessageResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}

