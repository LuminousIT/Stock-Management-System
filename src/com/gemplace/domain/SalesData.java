/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace.domain;

import com.gemplace.Start;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import javafx.collections.ObservableMap;

/**
 *
 * @author gemdajs (+234 811 402 8137)
 */
public class SalesData {

    private String name;
    private Double price;
    private Integer quantity, sn;

    private final int id;

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

    public int getId() {
        return id;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public SalesData(Start app, int sn, ObservableMap<String, Object> data) {
        this.name = (String) data.get("name");
        this.price = (Double) data.get("price");;
        this.quantity = (Integer) data.get("quantity");
        this.sn = sn;
        this.id = (int) data.get("id");
        
        if(data.get("date") != null) {
            this.date = ((Timestamp)data.get("date")).toLocalDateTime().format(DateTimeFormatter.ISO_DATE);
        }
    }
    public String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
