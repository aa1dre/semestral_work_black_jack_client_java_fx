package com.aakhramchuk.clientfx.objects;

import org.apache.commons.configuration2.Configuration;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionObject {
    private PrintWriter writer;
    private BufferedReader reader;
    private Scanner scanner;
    private Configuration config;
    private Socket socket;

    /**
     * Initializes a new ConnectionObject with the provided socket, writer, reader, scanner, and configuration.
     *
     * @param socket   The socket used for communication.
     * @param writer   The PrintWriter for sending data to server.
     * @param reader   The BufferedReader for receiving data from server.
     * @param scanner  The Scanner for reading input data.
     * @param config   The Configuration object containing settings.
     */
    public ConnectionObject(Socket socket, PrintWriter writer, BufferedReader reader, Scanner scanner, Configuration config) {
        this.socket = socket;
        this.writer = writer;
        this.reader = reader;
        this.scanner = scanner;
        this.config = config;
    }

    /**
     * Retrieves the socket associated with this connection.
     *
     * @return The socket used for communication.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Retrieves the Configuration object.
     *
     * @return The Configuration object.
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Retrieves the PrintWriter for sending data to server.
     *
     * @return The PrintWriter for writing data.
     */
    public PrintWriter getWriter() {
        return writer;
    }

    /**
     * Retrieves the BufferedReader for receiving data from server.
     *
     * @return The BufferedReader for reading data.
     */
    public BufferedReader getReader() {
        return reader;
    }

    /**
     * Retrieves the Scanner for reading input data.
     *
     * @return The Scanner for reading input.
     */
    public Scanner getScanner() {
        return scanner;
    }
}
