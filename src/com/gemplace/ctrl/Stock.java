/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace.ctrl;

import com.gemplace.Start;
import com.gemplace.domain.StockData;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author NCS
 */
public class Stock implements Initializable {

    private Start app;

    @FXML
    private TableColumn<StockData, Integer> snCol;

    @FXML
    private TableColumn<StockData, String> nameCol;

    @FXML
    private TableColumn<StockData, Integer> quantityCol;

    @FXML
    private TableView<StockData> productTable;

    @FXML
    private TableColumn<StockData, Node> actionCol;

    @FXML
    private TableColumn<StockData, Double> priceCol;

    @FXML
    private AnchorPane infoPane;

    @FXML
    private Label nameLab, descriptionLab, quantityLab, priceLab;

    @FXML
    private TextField newQuantityField;

    private ObservableList<ObservableMap<String, Object>> result;
    private int x;
    private TranslateTransition infoIn, infoOut;
    private int currentId;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        snCol.setCellValueFactory(new PropertyValueFactory("sn"));
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        quantityCol.setCellValueFactory(new PropertyValueFactory("quantity"));
        actionCol.setCellValueFactory(new PropertyValueFactory("action"));
        priceCol.setCellValueFactory(new PropertyValueFactory("price"));

        infoIn = new TranslateTransition(Duration.millis(400), infoPane);
        infoIn.setToX(0);
        infoIn.setInterpolator(Interpolator.EASE_OUT);

        infoIn.currentTimeProperty().addListener((ob, ov, nv) -> {
            if (nv.lessThanOrEqualTo(Duration.millis(100))) {
                infoPane.setDisable(false);
            }
        });

        infoOut = new TranslateTransition(Duration.millis(400), infoPane);
        infoOut.setToX(300);
        infoOut.setInterpolator(Interpolator.EASE_IN);
        infoOut.setOnFinished(e -> infoPane.setDisable(true));
        productTable.setOnMouseClicked(me -> {
            hideInfo();
            me.consume();
        });
    }

    public void construct(Start aThis) {
        this.app = aThis;
        refreshTable();
    }

    public void refreshTable() {
        result = app.db.getStockData();
        productTable.getItems().clear();
        if (result != null) {
            x = 1;
            result.stream().forEach(product -> {
                productTable.getItems().add(new StockData(app, x++, product));
            });
        }
        infoOut.play();
    }

    public void showInfo(int x) {
        if (Animation.Status.STOPPED != infoOut.getStatus()) {
            infoOut.stop();
        }

        if (result.size() > x) {
            ObservableMap<String, Object> get = result.get(x);
            nameLab.setText(get.get("name").toString());
            descriptionLab.setText(get.get("desc").toString());
            priceLab.setText("Price: " + get.get("price").toString());
            quantityLab.setText("Quanity: " + get.get("quantity").toString());
            currentId = (int) get.get("id");
        } else {
            return;
        }
        infoIn.play();
    }

    public void hideInfo() {
        if (Animation.Status.STOPPED != infoIn.getStatus()) {
            infoIn.stop();
        }
        infoOut.play();
    }

    @FXML
    void addNewQuantityHandler(ActionEvent ae) {
        try {
            int quantity = Integer.parseInt(newQuantityField.getText());
            if (quantity < 1) {
                app.util.showMessage("Quantity must be greater zero!");
                return;
            }

            if (app.db.addNewQuantity(currentId, quantity)) {
                app.util.showMessage("New quantity added to '" + nameLab.getText() + "'!");
                newQuantityField.clear();
                refreshTable();
            } else {
                app.util.showMessage("Unable to complete request, please try again!");
            }
        } catch (Exception e) {
            app.util.showMessage("Quantity required!");
        }

    }

}
