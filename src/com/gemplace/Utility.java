/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.text.Font;

/**
 *
 * @author NCS
 */
public class Utility {

    private final Start app;

    public Font fa = Font.loadFont(Utility.class.getResourceAsStream("fa.ttf"), 14);

    public Utility(Start app) {
        this.app = app;
    }

    public enum AppConstant {

        FXMLPAGE, FXMLCTRL
    }

    public String toSentenceCase(String str) {
        ObservableList<String> exp = FXCollections.observableArrayList("of", "and", "in", "am");
        str = str.toLowerCase().replace("  ", " ");
        str = str.trim();
        String[] strArr = str.split(" ");
        if (strArr == null) {
            return "";
        }
        String newStr = "";
        for (String st : strArr) {
            if (exp.contains(st)) {
                newStr += " " + st;
            } else {
                newStr += " " + st.replaceFirst(String.valueOf(st.charAt(0)), String.valueOf(st.charAt(0)).toUpperCase());
            }
        }
        return newStr.replaceFirst(" ", "");
    }

    public HashMap<AppConstant, Object> createPage(String pageName) {
        HashMap<AppConstant, Object> ret = new HashMap<>();
        pageName = "view/" + pageName + ".fxml";
        FXMLLoader loader = new FXMLLoader(Start.class.getResource(pageName), null, new JavaFXBuilderFactory());
        try {
            ret.put(AppConstant.FXMLPAGE, loader.load(Start.class.getResourceAsStream(pageName)));
            ret.put(AppConstant.FXMLCTRL, loader.getController());
        } catch (IOException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public void showMessage(String message) {
        app.baseCtrl.status.setValue(message);
    }
}
