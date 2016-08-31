package com.karl.fx.controller;

import java.io.IOException;

import javafx.fxml.FXML;

import com.karl.App;

public class MainViewController {
    private App main;

    @FXML
    private void getConsole() throws IOException {
        main.showConsole();
    }

    @FXML
    private void goHome() throws IOException {
        main.homeMainView();
    }

}
