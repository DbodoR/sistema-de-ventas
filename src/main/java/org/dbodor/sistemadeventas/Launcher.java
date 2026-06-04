package org.dbodor.sistemadeventas;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        System.setProperty("glass.win.uiScale", "1.0");
        System.setProperty("glass.gtk.uiScale", "1.0");
        System.setProperty("prism.allowhidpi", "false");
        Application.launch(HelloApplication.class, args);
    }
}
