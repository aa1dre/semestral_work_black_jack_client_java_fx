package com.aakhramchuk.clientfx.objects;

import org.apache.commons.configuration2.Configuration;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class ConnectionObject {
    private PrintWriter writer;
    private BufferedReader reader;
    private Scanner scanner;
    private Configuration config;

    public ConnectionObject(PrintWriter writer, BufferedReader reader, Scanner scanner, Configuration config) {
        this.writer = writer;
        this.reader = reader;
        this.scanner = scanner;
        this.config = config;
    }

    public Configuration getConfig() {
        return config;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public Scanner getScanner() {
        return scanner;
    }
}
