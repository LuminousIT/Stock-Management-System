/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace.ctrl;

import com.gemplace.Start;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author NCS
 */
public class Login implements Initializable {

    private Start app;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public Label statusLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void construct(Start aThis) {
        app = aThis;
    }

    @FXML
    void login(ActionEvent event) {
        Boolean exists = app.db.userExists(usernameField.getText(), passwordField.getText());
        if (exists == null) {
            return;
        }
        if (exists) {
            app.showMainApp();
        } else {
            statusLabel.setText("Access denied!");
        }
    }

    @FXML
    void exit(ActionEvent event) {
        Platform.exit();
    }

}
