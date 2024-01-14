package com.aakhramchuk.clientfx.objects;

public class DeserializedMessage {
    private boolean isSucess;
    private String message;
    private String opcode;
    private boolean isGameMessage;
    private String messageType;

    /**
     * Initializes a new DeserializedMessage with the provided attributes.
     *
     * @param isSucess       A boolean indicating the success of the message.
     * @param message        The content of the received message.
     * @param opcode         The opcode associated with the message.
     * @param isGameMessage  A boolean indicating whether the message is related to a game.
     */
    public DeserializedMessage(boolean isSucess, String message, String opcode, boolean isGameMessage) {
        this.isSucess = isSucess;
        this.message = message;
        this.opcode = opcode;
        this.isGameMessage = isGameMessage;
    }

    /**
     * Retrieves the message type associated with this deserialized message.
     *
     * @return The message type.
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Sets the message type for this deserialized message.
     *
     * @param messageType The message type to be set.
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }


    /**
     * Checks whether the message is related to a game.
     *
     * @return True if the message is a game-related message; otherwise, false.
     */
    public boolean isGameMessage() {
        return isGameMessage;
    }

    /**
     * Checks whether the message indicates success.
     *
     * @return True if the message indicates success; otherwise, false.
     */
    public boolean isSucess() {
        return isSucess;
    }


    /**
     * Retrieves the content of the received message.
     *
     * @return The message content.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the opcode associated with the message.
     *
     * @return The message opcode.
     */
    public String getOpcode() {
        return opcode;
    }

    /**
     * Sets the message content for this deserialized message.
     *
     * @param message The message content to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
