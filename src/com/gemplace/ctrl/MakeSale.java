/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace.ctrl;

import com.gemplace.Start;
import com.gemplace.domain.CartData;
import com.gemplace.domain.SalesData;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author NCS
 */
public class MakeSale implements Initializable {

    private Start app;

    @FXML
    private TableColumn<SalesData, Integer> snCol;

    @FXML
    private Button checkOutButton;

    @FXML
    private TableColumn<CartData, Integer> cartQuantityCol;

    @FXML
    private TableView<SalesData> productTable;

    @FXML
    private TextField filterField, newQuantityField, priceField;

    @FXML
    private TableView<CartData> cart;

    @FXML
    private TableColumn<SalesData, Double> priceCol;

    @FXML
    private Button clearButton;

    @FXML
    private TableColumn<CartData, String> cartNameCol;

    @FXML
    private TableColumn<SalesData, String> nameCol;

    @FXML
    private TableColumn<SalesData, Integer> quantityCol;

    @FXML
    private Button removeButton;

    @FXML
    private FlowPane newQuantityPane;

    @FXML
    private AnchorPane basePane, contentPane;

    private ObservableList<ObservableMap<String, Object>> result;
    private int x;
    private SalesData selectedSaleData;
    private ScaleTransition quantityIn, quantityOut;
    private boolean found;
    private double total;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        snCol.setCellValueFactory(new PropertyValueFactory("sn"));
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        quantityCol.setCellValueFactory(new PropertyValueFactory("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory("price"));

        cartNameCol.setCellValueFactory(new PropertyValueFactory("name"));
        cartQuantityCol.setCellValueFactory(new PropertyValueFactory("quantity"));

        quantityIn = new ScaleTransition(Duration.millis(400), newQuantityPane);
        quantityIn.setToX(1);
        quantityIn.setToY(1);

        quantityIn.currentTimeProperty().addListener((ob) -> contentPane.setDisable(true));

        quantityOut = new ScaleTransition(Duration.millis(400), newQuantityPane);
        quantityOut.setToX(.1);
        quantityOut.setToY(.1);

        quantityOut.setOnFinished((e) -> {
            basePane.getChildren().remove(newQuantityPane);
            contentPane.setDisable(false);
        });

        newQuantityPane.setOnMouseClicked(me -> quantityOut.play());

        newQuantityPane.getChildren().get(0).setOnMouseClicked(me -> me.consume());

        basePane.getChildren().remove(newQuantityPane);
//        newQuantityField.setOnKeyTyped(kp -> {
//            String ch = kp.getCharacter();
//            System.out.println(ch);
//        });

        cart.getItems().addListener((ListChangeListener.Change<? extends CartData> c) -> {
            clearButton.setDisable(cart.getItems().isEmpty());
            checkOutButton.setDisable(cart.getItems().isEmpty());
        });

        cart.setOnKeyPressed(kp -> {
            if (kp.getCode() == KeyCode.DELETE) {
                if (!cart.getSelectionModel().isEmpty()) {
                    removeButton.fire();
                }
            }
        });

        cart.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        removeButton.disableProperty().bind(cart.getSelectionModel().selectedItemProperty().isNull());

        removeButton.setOnAction((ae) -> {
            ObservableList<CartData> data = cart.getSelectionModel().getSelectedItems();
            cart.getItems().removeAll(data);
            recomputePrice();
        });

        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ae) {
                cart.getItems().clear();
                recomputePrice();
            }
        });
    }

    public void construct(Start aThis) {
        this.app = aThis;
        filterField.textProperty().addListener((ob, ov, nv) -> refreshTable());
        refreshTable();

        checkOutButton.setOnAction(ae -> {
            cart.getItems().stream().forEach(itm -> {
                app.db.makeSale(itm.getSalesData().getId(), itm.getQuantity(), itm.getQuantity() * itm.getPrice());
            });

            String invoice = "Ajike Pharmacy LTD.+\n"
                    + "No 21, Ajobiewe strt., Shomolu, Lagos\n"
                    + "\n\n"
                    + stofw("item", 50) + stofw("Price", 20) + stofw("Quantity", 20);
            for (CartData item : cart.getItems()) {
                invoice += "\n" + stofw(item.getName(), 50) + stofw(item.getPrice(), 20) + stofw(item.getQuantity(), 20);
            }
            invoice += "\n" + stofwr("", 50) + stofw("Total", 20) + stofw(priceField.getText(), 20);
            System.out.println(invoice);
            PrinterJob pj = PrinterJob.createPrinterJob(Printer.getDefaultPrinter());
            TextArea area = new TextArea(invoice);

            pj.jobStatusProperty().addListener(new ChangeListener<PrinterJob.JobStatus>() {

                @Override
                public void changed(ObservableValue<? extends PrinterJob.JobStatus> observable, PrinterJob.JobStatus oldValue, PrinterJob.JobStatus newValue) {
                    app.util.showMessage(newValue.toString());
                }
            });
            if (pj.showPageSetupDialog(app.stage)) {
                if (pj.showPrintDialog(app.stage)) {
                    if (pj.printPage(area)) {
                        pj.endJob();

                    }
                }
            }

            clearButton.fire();
            app.util.showMessage("Sales completed successfully");
            refreshTable();

        });

        productTable.setOnMouseClicked(me -> {
            if (!productTable.getItems().isEmpty() && me.getClickCount() == 2) {
                selectedSaleData = productTable.getSelectionModel().getSelectedItem();
                basePane.getChildren().add(newQuantityPane);
                quantityIn.play();
            }
        });
    }

    private String stofw(Object ob, int width) {
        String str = ob.toString();
        if (str.length() > width) {
            return str.substring(0, width);
        }

        for (int i = str.length(); i < width; i++, str += " ") {
            ;
        }
        return str;
    }

    private String stofwr(Object ob, int width) {
        String str = ob.toString();
        if (str.length() > width) {
            return str.substring(0, width);
        }

        for (int i = str.length(); i < width; i++, str = " " + str) {
            ;
        }
        return str;
    }

    public void refreshTable() {
        result = app.db.getStockData(filterField.getText().trim());
        productTable.getItems().clear();
        if (result != null) {
            x = 1;
            result.stream().forEach(product -> {
                productTable.getItems().add(new SalesData(app, x++, product));
            });
        }
    }

    @FXML
    void addQToCartHandler(ActionEvent event) {
        String q = newQuantityField.getText();
        try {
            int newQuantity = Integer.parseInt(q);

            if (newQuantity > selectedSaleData.getQuantity()) {
                app.util.showMessage("Quantity available for " + selectedSaleData.getName() + " is: " + selectedSaleData.getQuantity());
                return;
            }

            addToCart(newQuantity);
        } catch (Exception e) {
            app.util.showMessage("Invalid quantity, supply value in digit");
        }
    }

    @FXML
    void cancelAddQHandler(ActionEvent event) {
        quantityOut.play();
        newQuantityField.clear();
    }

    private void addToCart(int q) {
        found = false;
        if (!cart.getItems().isEmpty()) {
            cart.getItems().stream().forEach(itm -> {
                if (itm.getSalesData().getId() == selectedSaleData.getId()) {
                    cart.getItems().set(cart.getItems().indexOf(itm), new CartData(selectedSaleData, q));
                    found = true;
                }
            });
        }

        if (!found) {
            cart.getItems().add(new CartData(selectedSaleData, q));
        }
        recomputePrice();
        cancelAddQHandler(null);
    }

    public void recomputePrice() {
        total = 0D;
        cart.getItems().stream().forEach(itm -> {
            total += itm.getPrice() * itm.getQuantity();
        });
        priceField.setText(String.format("# %.2f", total));
    }

}
