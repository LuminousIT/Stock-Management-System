/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace.domain;

import com.gemplace.Start;
import java.util.Scanner;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.layout.HBox;

/**
 *
 * @author gemdajs (+234-811-402-8137)
 */
public class StockData {

    private String name;
    private Double price;
    private Integer quantity, sn;

    private Node action;
    private final int id;
    private final Button newQuantityButton;
    private final Button DeleteButton;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public Node getAction() {
        return action;
    }

    public void setAction(Node action) {
        this.action = action;
    }

    public StockData(Start app, int sn, ObservableMap<String, Object> data) {
        this.name = (String) data.get("name");
        this.price = (Double) data.get("price");;
        this.quantity = (Integer) data.get("quantity");
        this.sn = sn;
        this.id = (int) data.get("id");

        action = new HBox(5,
                newQuantityButton = ButtonBuilder.create().font(app.util.fa).text(String.valueOf('\uf217')).styleClass("green-button").onAction(ae -> {
                    app.stockCtrl.showInfo(sn - 1);
                }).build(),
                DeleteButton = ButtonBuilder.create().font(app.util.fa).text(String.valueOf('\uf1F8')).styleClass("red-button").onAction(ae -> {
                    //
                }).build()
        );
        action.setStyle("-fx-alignment: center");
    }
    
}
