/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace.ctrl;

import com.gemplace.Start;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author NCS
 */
public class Base implements Initializable {

    private ObjectProperty<Label> active = new SimpleObjectProperty<>();

    @FXML
    public AnchorPane contentPane, statusPane;

    @FXML
    public FlowPane loadingPane, navFlow;

    @FXML
    private Label statusLab;

    private Start app;

    public StringProperty status = new SimpleStringProperty();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contentPane.getChildren().remove(loadingPane);
        TranslateTransition statusTrans = new TranslateTransition(Duration.millis(500), statusPane);
        TranslateTransition statusTransOut = new TranslateTransition(Duration.millis(200), statusPane);

        statusTrans.setFromX(-600);
        statusTrans.setToX(0);

        statusTransOut.setFromX(0);
        statusTransOut.setToX(-600);

        statusTransOut.setDelay(Duration.seconds(5));
        statusTransOut.setOnFinished(a -> {
            statusPane.setOpacity(0);
        });
        statusTrans.setOnFinished(a -> statusTransOut.play());

        status.addListener((ob, ov, nv) -> {
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(() -> {
                    updateStatus(nv, statusTrans, statusTransOut);
                });
            } else {
                updateStatus(nv, statusTrans, statusTransOut);
            }
        });
    }

    private void updateStatus(String nv, TranslateTransition statusTrans, TranslateTransition statusTransOut) {
        if (nv.isEmpty()) {
            return;
        }
        statusPane.setOpacity(0);
        if (statusTrans.getStatus() == Animation.Status.RUNNING) {
            statusTrans.stop();
            statusTransOut.play();
        }
        if (statusTransOut.getStatus() == Animation.Status.RUNNING) {
            statusTransOut.stop();
        }
        statusLab.setText(nv);
        statusPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        statusPane.setOpacity(1);
        statusPane.toFront();
        statusTrans.playFromStart();
        status.setValue("");
    }

    public void construct(Start app) {
        this.app = app;
        if (!app.privilege.get()) {
            Set<Node> set = navFlow.lookupAll(".admin-menu");
            set.stream().filter((Node t) -> (t instanceof VBox)).forEach(item -> {
                VBox box = (VBox) item;
                box.getChildren().remove(0, 2);
            });
        }
    }

    @FXML
    void gotoMenuHandler(MouseEvent me) {
        Label label = (Label) me.getSource();
        active.setValue(label);
        switch (label.getText().toLowerCase()) {
            case "register product":
                gotoRegiterPage();
                break;
            case "stock new":
                gotoStockPage();
                break;
            case "make sales":
                gotoSalesPage();
                break;
            case "sales report":
                gotoSalesReportPage();
                break;
            case "stock report":
                gotoStockReportPage();
                break;
            case "logout":
                app.stage.close();
                app.showLoginPage();
                break;
            case "about":
                break;

        }
    }

    public void gotoRegiterPage() {
        showLoading();
        Platform.runLater(() -> contentPane.getChildren().setAll(app.getProductPage()));
    }

    private void gotoStockPage() {
        showLoading();
        Platform.runLater(() -> contentPane.getChildren().setAll(app.getStockPage()));
    }

    private void gotoSalesPage() {
        showLoading();
        Platform.runLater(() -> contentPane.getChildren().setAll(app.getSalesPage()));
    }

    private void showLoading() {
        if (!contentPane.getChildren().contains(loadingPane)) {
            contentPane.getChildren().add(loadingPane);
        } else {
            loadingPane.toFront();
        }
    }

    private void gotoSalesReportPage() {
        showLoading();
        Platform.runLater(() -> contentPane.getChildren().setAll(app.getSalesReportPage()));
    }

    private void gotoStockReportPage() {
        showLoading();
        Platform.runLater(() -> contentPane.getChildren().setAll(app.getStockReportPage()));
    }

}
