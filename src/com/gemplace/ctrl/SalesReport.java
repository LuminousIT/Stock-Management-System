/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace.ctrl;

import com.gemplace.Start;
import com.gemplace.domain.SalesData;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author samth
 */
public class SalesReport implements Initializable {

    private Start app;

    @FXML
    private DatePicker fromDatePicker, toDatePicker;
    @FXML
    private TableColumn<SalesData, Integer> snCol;

    @FXML
    private TableView<SalesData> productTable;

    @FXML
    private TextField filterField;

    @FXML
    private TableColumn<SalesData, Double> priceCol;

    @FXML
    private Label priceLabel;

    @FXML
    private TableColumn<SalesData, String> nameCol, dateCol;

    @FXML
    private TableColumn<SalesData, Integer> quantityCol;

    private ObservableList<ObservableMap<String, Object>> result;
    private int x;

    @FXML
    private HBox pricePane;
    private TranslateTransition infoIn;
    private TranslateTransition infoOut;
    private double total;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        snCol.setCellValueFactory(new PropertyValueFactory("sn"));
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        dateCol.setCellValueFactory(new PropertyValueFactory("date"));
        quantityCol.setCellValueFactory(new PropertyValueFactory("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory("price"));

        infoIn = new TranslateTransition(Duration.millis(400), pricePane.getParent());
        infoIn.setToY(0);
        infoIn.setInterpolator(Interpolator.EASE_OUT);

        infoOut = new TranslateTransition(Duration.millis(400), pricePane.getParent());
        infoOut.setToY(50);
        infoOut.setInterpolator(Interpolator.EASE_IN);
    }

    public void construct(Start app) {
        this.app = app;
        fromDatePicker.setValue(LocalDate.now().minusYears(1));
        toDatePicker.setValue(LocalDate.now());

        fromDatePicker.valueProperty().addListener((ob, ov, nv) -> refreshTable());
        toDatePicker.valueProperty().addListener((ob, ov, nv) -> refreshTable());
        filterField.textProperty().addListener((ob, ov, nv) -> refreshTable());

        pricePane.setOnMouseEntered(me -> {
            System.out.println("Mouse entered!");
            infoIn.play();
        });

        pricePane.setOnMouseExited(me -> {
            System.out.println("Mouse exited!");
            infoOut.play();
        });

        refreshTable();

    }

    public void refreshTable() {
        Timestamp fromDate = Timestamp.valueOf(LocalDateTime.of(fromDatePicker.getValue(), LocalTime.MIN));

        Timestamp toDate = Timestamp.valueOf(LocalDateTime.of(toDatePicker.getValue(), LocalTime.MAX));
        result = app.db.getSalesReportData(filterField.getText().trim(), fromDate, toDate);
        productTable.getItems().clear();
        if (result != null) {
            x = 1;
            result.stream().forEach(product -> {
                productTable.getItems().add(new SalesData(app, x++, product));
            });
        }
        infoIn.play();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), (ActionEvent event) -> {
            infoOut.play();
        }));
        timeline.play();
        recomputePrice();
    }

    public void recomputePrice() {
        total = 0D;
        productTable.getItems().stream().forEach((SalesData itm) -> {
            total += itm.getPrice();
        });
        priceLabel.setText(String.format("# %.2f", total));
    }
}
