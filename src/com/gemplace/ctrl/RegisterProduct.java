/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace.ctrl;

import com.gemplace.Start;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author NCS
 */
public class RegisterProduct implements Initializable {

    private Start app;

    /**
     * Initializes the controller class.
     */
    @FXML
    private AnchorPane updateProductPane, createProductPane;

    @FXML
    private TextField priceField, editPriceField;

    @FXML
    private TextField nameField, editNameField;

    @FXML
    private TextArea descriptionField, editDescriptionField;

    @FXML
    private ListView<String> list;

    @FXML
    private TextField quantityField, editQuantityField;

    @FXML
    private TextField filterField;

    @FXML
    private Button resetButton, updateButton, deleteButton;

    @FXML
    private Button regButton;

    @FXML
    private FlowPane contentPane;

    private ObservableList<ObservableMap<String, Object>> result;
    private int selectedProductId;
    private long lastTime;
    private boolean deleteConfirmed;
    private Timer timer;
    private Timeline timeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contentPane.getChildren().remove(updateProductPane);
    }

    @FXML
    void regNewHandler(ActionEvent event) {
        if (contentPane.getChildren().contains(createProductPane)) {
            return;
        }
        switchView(createProductPane);
    }

    public void construct(Start aThis) {
        app = aThis;
        resetButton.setOnAction(ae -> resetFields());
        regButton.setOnAction(ae -> {
            String name = nameField.getText();
            if (name.trim().isEmpty()) {
                app.util.showMessage("Product name required!");
                return;
            }
            String desc = descriptionField.getText();
            double price;
            try {
                price = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                app.util.showMessage("Supply price in digit!");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException e) {
                app.util.showMessage("Supply quantity in digit!");
                return;
            }

            boolean regComplete = app.db.registerProduct(name, desc, quantity, price);
            if (regComplete) {
                resetFields();
                refreshList();
            }
            if (app.stockCtrl != null) {
                app.stockCtrl.refreshTable();
            }
        });

        deleteButton.setOnAction(ae -> {
            long now = System.currentTimeMillis();
            if (deleteConfirmed && now - lastTime <= 2000) {
                deleteConfirmed = false;
                if (app.db.deleteProductDetail(selectedProductId)) {
                    refreshList();
                    switchView(createProductPane);
                    app.util.showMessage("Product deleted!");
                } else {
                    app.util.showMessage("Unable to delete product, try again!");
                }
            } else {
                lastTime = now;
                deleteConfirmed = true;
                app.util.showMessage("Click again to delete product!");
            }
        });
        updateButton.setOnAction(ae -> {
            String name = editNameField.getText();
            if (name.trim().isEmpty()) {
                app.util.showMessage("Product name required!");
                return;
            }
            String desc = editDescriptionField.getText();
            double price;
            try {
                price = Double.parseDouble(editPriceField.getText());
            } catch (NumberFormatException e) {
                app.util.showMessage("Supply price in digit!");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(editQuantityField.getText());
            } catch (NumberFormatException e) {
                app.util.showMessage("Supply quantity in digit!");
                return;
            }

            boolean regComplete = app.db.updateProduct(name, desc, quantity, price, selectedProductId);
            if (regComplete) {
                refreshList();
            }
        });
        timeline = new Timeline(new KeyFrame(Duration.millis(250), (ActionEvent event) -> {
            refreshList();
        }));
        filterField.textProperty().addListener((ob, ov, nv) -> {
            if (timeline.getStatus() != Animation.Status.STOPPED) {
                timeline.stop();
            }
            timeline.playFromStart();
//            (timer = new Timer(true)).schedule(new TimerTask() {
//
//                @Override
//                public void run() {
//                    Platform.runLater(() -> {
//                        refreshList();
//                    });
//                }
//            }, 250);
        });
        list.setOnMouseClicked(mc -> {
            int count = mc.getClickCount();
            if (count == 2) {
                if (list.getSelectionModel().isEmpty()) {
                    return;
                }
                selectedProductId = (int) result.get(list.getSelectionModel().getSelectedIndex()).get("id");
                if (!contentPane.getChildren().contains(updateProductPane)) {
                    switchView(updateProductPane);
                }
                ObservableMap<String, Object> ret = app.db.getProductDetail(selectedProductId);
                if (ret != null) {
                    editDescriptionField.setText(ret.get("desc").toString());
                    editNameField.setText(ret.get("name").toString());
                    editQuantityField.setText(ret.get("quantity").toString());
                    editPriceField.setText(ret.get("price").toString());
                } else {
                    app.util.showMessage("Error encountered while fetching product detail!");
                    switchView(createProductPane);
                }
            }
        });
        refreshList();
    }

    public void refreshList() {
        result = app.db.getProducts(filterField.getText());
        list.getItems().clear();
        if (result != null) {
            result.stream().forEach(product -> {
                list.getItems().add((String) product.get("name"));
            });
        }

    }

    private void resetFields() {
        descriptionField.clear();
        nameField.clear();
        priceField.clear();
        quantityField.clear();
    }

    private void switchView(AnchorPane newView) {
        AnchorPane oldView = (AnchorPane) contentPane.getChildren().get(0);

        newView.setOpacity(0.);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), oldView);
        fadeOut.setToValue(0.);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), newView);
        fadeIn.setToValue(1.);

        fadeOut.setOnFinished((ae) -> {
            contentPane.getChildren().setAll(newView);
            fadeIn.play();
        });

        fadeOut.play();

//        SequentialTransition trans = new SequentialTransition(fadeOut, fadeIn);
////        ParallelTransition trans = new ParallelTransition(fadeOut, fadeIn);
//        trans.setOnFinished(ae -> contentPane.getChildren().remove(oldView));
//        trans.play();
    }

}
