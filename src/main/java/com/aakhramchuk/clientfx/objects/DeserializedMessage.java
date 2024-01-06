package com.aakhramchuk.clientfx.objects;

public class DeserializedMessage {
    private boolean isSucess;
    private String message;
    private String opcode;
    private boolean isGameMessage;
    private String messageType;

    public DeserializedMessage(boolean isSucess, String message, String opcode, boolean isGameMessage) {
        this.isSucess = isSucess;
        this.message = message;
        this.opcode = opcode;
        this.isGameMessage = isGameMessage;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public boolean isGameMessage() {
        return isGameMessage;
    }

    public boolean isSucess() {
        return isSucess;
    }

    public String getMessage() {
        return message;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
