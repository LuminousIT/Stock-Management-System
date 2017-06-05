/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace;

import com.gemplace.ctrl.Base;
import com.gemplace.ctrl.Login;
import com.gemplace.ctrl.MakeSale;
import com.gemplace.ctrl.RegisterProduct;
import com.gemplace.ctrl.SalesReport;
import com.gemplace.ctrl.Stock;
import com.gemplace.ctrl.StockReport;
import com.gemplace.domain.DBHandler;
import java.util.HashMap;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author NCS
 */
public class Start extends Application {

    public Utility util;
    public Stage stage, loginStage;
    public Base baseCtrl;
    private Node regPage;
    private RegisterProduct regCtrl;
    public DBHandler db;
    public Stock stockCtrl;
    private Node stockPage;
    public MakeSale salesCtrl;
    private Node salesPage;
    private SalesReport salesReportCtrl;
    private Node salesReportPage;
    private StockReport stockReportCtrl;
    private Node stockReportPage;
    public Login loginCtrl;
    public BooleanProperty privilege = new SimpleBooleanProperty();

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        loginStage = new Stage();
        util = new Utility(this);
        db = new DBHandler(this);
        showLoginPage();
    }

    public void showLoginPage() {
        HashMap<Utility.AppConstant, Object> createPage = util.createPage("Login");
        Parent p = (Parent) createPage.get(Utility.AppConstant.FXMLPAGE);
        loginCtrl = (Login) createPage.get(Utility.AppConstant.FXMLCTRL);
        loginCtrl.construct(this);
        loginStage.setScene(new Scene(p));
        loginStage.show();
    }

    public void showMainApp() {
        loginStage.close();
        HashMap<Utility.AppConstant, Object> createPage = util.createPage("Base");
        Parent p = (Parent) createPage.get(Utility.AppConstant.FXMLPAGE);
        baseCtrl = (Base) createPage.get(Utility.AppConstant.FXMLCTRL);
        baseCtrl.construct(this);
        stage.setScene(new Scene(p));
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public Node getProductPage() {
        if (regCtrl == null) {
            HashMap<Utility.AppConstant, Object> createPage = util.createPage("RegisterProduct");
            regPage = (Node) createPage.get(Utility.AppConstant.FXMLPAGE);
            regCtrl = (RegisterProduct) createPage.get(Utility.AppConstant.FXMLCTRL);
            regCtrl.construct(this);
            fitContentToPage(regPage);
        }
        return regPage;
    }

    private void fitContentToPage(Node nde) {
        AnchorPane.setBottomAnchor(nde, 0.);
        AnchorPane.setTopAnchor(nde, 0.);
        AnchorPane.setLeftAnchor(nde, 0.);
        AnchorPane.setRightAnchor(nde, 0.);
    }

    public Node getStockPage() {
        if (stockCtrl == null) {
            HashMap<Utility.AppConstant, Object> createPage = util.createPage("Stock");
            stockPage = (Node) createPage.get(Utility.AppConstant.FXMLPAGE);
            stockCtrl = (Stock) createPage.get(Utility.AppConstant.FXMLCTRL);
            stockCtrl.construct(this);
            fitContentToPage(stockPage);
        }
        return stockPage;
    }

    public Node getSalesPage() {
        if (salesCtrl == null) {
            HashMap<Utility.AppConstant, Object> createPage = util.createPage("MakeSale");
            salesPage = (Node) createPage.get(Utility.AppConstant.FXMLPAGE);
            salesCtrl = (MakeSale) createPage.get(Utility.AppConstant.FXMLCTRL);
            salesCtrl.construct(this);
            fitContentToPage(salesPage);
        }
        return salesPage;
    }

    public Node getSalesReportPage() {
        if (salesReportCtrl == null) {
            HashMap<Utility.AppConstant, Object> createPage = util.createPage("SalesReport");
            salesReportPage = (Node) createPage.get(Utility.AppConstant.FXMLPAGE);
            salesReportCtrl = (SalesReport) createPage.get(Utility.AppConstant.FXMLCTRL);
            //here
            salesReportCtrl.construct(this);
            fitContentToPage(salesReportPage);
        }
        return salesReportPage;
    }

    public Node getStockReportPage() {
        if (stockReportCtrl == null) {
            HashMap<Utility.AppConstant, Object> createPage = util.createPage("StockReport");
            stockReportPage = (Node) createPage.get(Utility.AppConstant.FXMLPAGE);
            stockReportCtrl = (StockReport) createPage.get(Utility.AppConstant.FXMLCTRL);
            stockReportCtrl.construct(this);
            fitContentToPage(stockReportPage);
        }
        return stockReportPage;
    }

}
