/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace.domain;

/**
 *
 * @author gemdajs (+234-811-402-8137)
 */
public class CartData {

    private double price;
    private int quantity;
    private String name;
    private final SalesData data;
    private final int id;

    public CartData(SalesData data, int quantity) {
        this.data = data;
        name = data.getName();
        price = data.getPrice();
        this.quantity = quantity;
        id = data.getId();
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SalesData getSalesData() {
        return data;
    }

}
