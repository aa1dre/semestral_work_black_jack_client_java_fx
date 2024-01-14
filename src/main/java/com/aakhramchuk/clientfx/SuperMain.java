package com.aakhramchuk.clientfx;

import com.aakhramchuk.clientfx.BlackJackApplication;

public class SuperMain {

    /**
     * This is a workaround for the bug in javafx-maven-plugin.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        BlackJackApplication.main(args);
    }
}
