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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author samth
 */
public class StockReport implements Initializable {

    private Start app;

    @FXML
    private TableColumn<StockData, Integer> snCol;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private TableView<StockData> productTable;

    @FXML
    private FlowPane newQuantityPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private TextField filterField;

    @FXML
    private TableColumn<?, ?> dateCol;

    @FXML
    private TableColumn<StockData, String> nameCol;

    @FXML
    private TableColumn<StockData, Integer> quantityCol;

    @FXML
    private HBox pricePane;

    @FXML
    private AnchorPane basePane;

    @FXML
    private Label priceLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        snCol.setCellValueFactory(new PropertyValueFactory("sn"));
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        dateCol.setCellValueFactory(new PropertyValueFactory("date"));
        quantityCol.setCellValueFactory(new PropertyValueFactory("quantity"));
    }

    public void construct(Start app) {
        this.app = app;
    }

}
