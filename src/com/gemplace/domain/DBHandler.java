/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemplace.domain;

import com.gemplace.Start;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author Gemdajs
 */
public class DBHandler {

    private String dbName = "gemmie_sms",
            host = "localhost",
            password = "",
            username = "root",
            port = "3306";

    private Start app;
    private FadeTransition fadeTrans;
    public BooleanProperty firstTimeSetup = new SimpleBooleanProperty(false);
    private Connection con;
    private int attempts = 0, trials = 1;

    public DBHandler(Start app) {
        this.app = app;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            int code = getConnection();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getConnection() {
        if (con != null) {
            return -1;
        }
        try {
            String conString = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
            System.out.println("con: " + conString);
            con = DriverManager.getConnection(conString, username, password);
            return -1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            int code = ex.getErrorCode();
            System.out.println("Code: " + code);
            switch (code) {
                case 1049:
                    return createDefaultDB();
            }
            return code;
        }
    }

    public ImageView getRoundedImageView(ImageView img, int width, boolean putShadow) {
        Rectangle clip = new Rectangle(width, width);

        clip.setArcWidth(width);
        clip.setArcHeight(width);
        img.setClip(clip);

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        Platform.runLater(() -> {
            WritableImage image = img.snapshot(parameters, null);
            img.setClip(null);
            // apply a shadow effect.
            if (putShadow) {
                img.setEffect(new DropShadow(5, Color.web("#a05", .5)));
            }
            // store the rounded image in the img.
            img.setImage(image);
        });

        // remove the rounding clip so that our effect can show through.
        return img;
    }

    public void fadeView(boolean show, Node pane) {
        if (fadeTrans != null && fadeTrans.getNode() == pane && fadeTrans.getStatus() == Animation.Status.RUNNING) {
            fadeTrans.stop();
        }

        fadeTrans = new FadeTransition(Duration.millis(show ? 1200 : 400), pane);
        fadeTrans.setToValue(show ? 1 : 0);

        fadeTrans.play();
    }

    private int createDefaultDB() {
        try {
            String conString = "jdbc:mysql://" + host + ":" + port;
            con = DriverManager.getConnection(conString, username, password);
            PreparedStatement stmt = con.prepareStatement("create database " + dbName);
            stmt.executeUpdate();

            con = DriverManager.getConnection(conString + "/" + dbName, username, password);
            stmt = con.prepareStatement("CREATE TABLE products ( id INT(6) NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(250) NOT NULL , description text, price double(11, 2) default 0.00)");
            stmt.executeUpdate();

            stmt = con.prepareStatement("CREATE TABLE stock_report ( id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, p_id INT(6), quantity INT(11) , date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            stmt.executeUpdate();
//
            stmt = con.prepareStatement("CREATE TABLE sales_report ( id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, p_id INT(6), price double(10, 2), quantity INT(11), u_id int(11) default -1, date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            stmt.executeUpdate();
//
            stmt = con.prepareStatement("CREATE TABLE users ( id INT(6) NOT NULL AUTO_INCREMENT PRIMARY KEY, username varchar(20) NOT NULL , password varchar(20) NOT NULL , privilege boolean default true)");
            stmt.executeUpdate();

            stmt = con.prepareStatement("INSERT INTO users (username, password, privilege) VALUES ('admin', 'adminpass', 1)");
            stmt.executeUpdate();
            return -1;
        } catch (SQLException ex) {
            return ex.getErrorCode();
        }
    }

    public boolean registerProduct(String name, String desc, int quantity, double price) {
        if (con == null) {
            getConnection();
        }
        try {
            PreparedStatement prep = con.prepareStatement("select * from products where name = ?");
            prep.setString(1, name);
            ResultSet rst = prep.executeQuery();
            if (rst.next()) {
                app.util.showMessage("Product with same name already exist");
                return false;
            } else {
                prep = con.prepareStatement("insert into products (name, description, price) values (?, ?, ?)");
                prep.setString(1, name);
                prep.setString(2, desc);
                prep.setDouble(3, price);
                int x = prep.executeUpdate();

                if (quantity > 0 && x >= 0) {
                    prep = con.prepareStatement("select id from products where name = ? and description = ? and price = ? limit 1");
                    prep.setString(1, name);
                    prep.setString(2, desc);
                    prep.setDouble(3, price);
                    rst = prep.executeQuery();
                    if (rst.next()) {
                        return addNewQuantity(rst.getInt(1), quantity);
                    } else {
                        return false;
                    }
                } else {
                    return x > -1;
                }
            }
        } catch (SQLException ex) {
            app.util.showMessage(ex.getMessage());
            return false;
        } catch (NullPointerException ex) {
            app.util.showMessage("Network problem!");
            return false;
        }
    }

    public boolean updateProduct(String name, String desc, int quantity, double price, int id) {
        if (con == null) {
            getConnection();
        }
        try {
            PreparedStatement prep = con.prepareStatement("update products set name = ?, description = ?, price = ? where id = ?");
            prep.setString(1, name);
            prep.setString(2, desc);
            prep.setDouble(3, price);
            prep.setInt(4, id);
            return prep.executeUpdate() > -1;
        } catch (SQLException ex) {
            app.util.showMessage(ex.getMessage());
            return false;
        } catch (NullPointerException ex) {
            app.util.showMessage("Network problem!");
            return false;
        }
    }

    public ObservableList<ObservableMap<String, Object>> getProducts(String text) {
        if (con == null) {
            getConnection();
        }
        ObservableList<ObservableMap<String, Object>> list = FXCollections.observableArrayList();
        try {
            PreparedStatement stmt = con.prepareStatement("select id, name from products where name like ? order by name asc");
            stmt.setString(1, "%" + text + "%");
            ResultSet rst = stmt.executeQuery();
            while (rst.next()) {
                ObservableMap<String, Object> obj = FXCollections.observableHashMap();
                obj.put("name", rst.getString(2));
                obj.put("id", rst.getInt(1));
                list.add(obj);
            }
            attempts = 0;
            return list;
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case 0:
                    if (getConnection() != -1) {
                        app.util.showMessage("Network problem");
                        return null;
                    } else if (++attempts < trials) {
                        app.util.showMessage("reattempting to get product...");
                        return getProducts(text);
                    } else {
                        attempts = 0;
                        return null;
                    }
                default:
                    app.util.showMessage(ex.getMessage());
                    return null;
            }
        } catch (NullPointerException exception) {
            app.util.showMessage("Network problem");
            return null;
        }
    }

    public ObservableList<ObservableMap<String, Object>> getStockData() {
        if (con == null) {
            getConnection();
        }
        ObservableList<ObservableMap<String, Object>> list = FXCollections.observableArrayList();
        try {
            PreparedStatement stmt = con.prepareStatement("select * from products order by name asc");
            ResultSet rst = stmt.executeQuery();
            while (rst.next()) {
                ObservableMap<String, Object> obj = FXCollections.observableHashMap();
                obj.put("name", rst.getString("name"));
                obj.put("desc", rst.getString("description"));
                obj.put("id", rst.getInt("id"));
                obj.put("price", rst.getDouble("price"));
                obj.put("quantity", getQuantity(rst.getInt("id")));
                list.add(obj);
            }
            attempts = 0;
            return list;
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case 0:
                    if (getConnection() != -1) {
                        app.util.showMessage("Network problem");
                        return null;
                    } else if (++attempts < trials) {
                        app.util.showMessage("reattempting to get stock data...");
                        return getStockData();
                    } else {
                        attempts = 0;
                        return null;
                    }
                default:
                    app.util.showMessage(ex.getMessage());
                    return null;
            }
        } catch (NullPointerException exception) {
            app.util.showMessage("Network problem");
            return null;
        }
    }

    public ObservableList<ObservableMap<String, Object>> getStockData(String filter) {
        if (con == null) {
            getConnection();
        }
        ObservableList<ObservableMap<String, Object>> list = FXCollections.observableArrayList();
        try {
            PreparedStatement stmt = con.prepareStatement("select * from products where name like ? order by name asc");
            stmt.setString(1, "%" + filter + "%");
            ResultSet rst = stmt.executeQuery();
            while (rst.next()) {
                ObservableMap<String, Object> obj = FXCollections.observableHashMap();
                obj.put("name", rst.getString("name"));
                obj.put("desc", rst.getString("description"));
                obj.put("id", rst.getInt("id"));
                obj.put("price", rst.getDouble("price"));

                obj.put("quantity", getQuantity(rst.getInt("id")));
                list.add(obj);
            }
            attempts = 0;
            return list;
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case 0:
                    if (getConnection() != -1) {
                        app.util.showMessage("Network problem");
                        return null;
                    } else if (++attempts < trials) {
                        app.util.showMessage("reattempting to get stock data...");
                        return getStockData();
                    } else {
                        attempts = 0;
                        return null;
                    }
                default:
                    app.util.showMessage(ex.getMessage());
                    return null;
            }
        } catch (NullPointerException exception) {
            app.util.showMessage("Network problem");
            return null;
        }
    }

    private int getQuantity(int id) {
        try {
            PreparedStatement prep = con.prepareStatement("select sum(quantity) from sales_report where p_id = ?");
            prep.setInt(1, id);
            ResultSet rst = prep.executeQuery();
            int totalSold;
            if (rst.next()) {
                totalSold = rst.getInt(1);
            } else {
                totalSold = 0;
            }

            prep = con.prepareStatement("select sum(quantity) from stock_report where p_id = ?");
            prep.setInt(1, id);
            rst = prep.executeQuery();
            int totalStocked;
            if (rst.next()) {
                totalStocked = rst.getInt(1);
            } else {
                totalStocked = 0;
            }

            return (totalStocked - totalSold) < 0 ? 0 : (totalStocked - totalSold);
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean makeSale(int id, int quantity, double price) {
        if (con == null) {
            getConnection();
        }
        ObservableList<ObservableMap<String, Object>> list = FXCollections.observableArrayList();
        try {
            PreparedStatement stmt = con.prepareStatement("insert into sales_report (p_id, price, quantity) values (?, ?, ?)");
            stmt.setInt(1, id);
            stmt.setDouble(2, price);
            stmt.setInt(3, quantity);
            return stmt.executeUpdate() > -1;
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case 0:
                    if (getConnection() != -1) {
                        app.util.showMessage("Network problem");
                        return false;
                    } else if (++attempts < trials) {
                        app.util.showMessage("reattempting to get stock data...");
                        return makeSale(id, quantity, price);
                    } else {
                        attempts = 0;
                        return false;
                    }
                default:
                    app.util.showMessage(ex.getMessage());
                    return false;
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            app.util.showMessage("Network problem");
            return false;
        }
    }

    public ObservableMap<String, Object> getProductDetail(int id) {
        if (con == null) {
            getConnection();
        }
        try {
            PreparedStatement stmt = con.prepareStatement("select * from products where id = ?");
            stmt.setInt(1, id);
            ResultSet rst = stmt.executeQuery();
            attempts = 0;
            if (rst.next()) {
                ObservableMap<String, Object> obj = FXCollections.observableHashMap();
                obj.put("name", rst.getString("name"));
                obj.put("desc", rst.getString("description"));
                obj.put("quantity", getQuantity(rst.getInt("id")));
                obj.put("price", rst.getDouble("price"));
                return obj;
            }
            return null;

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            switch (ex.getErrorCode()) {
                case 0:
                    if (getConnection() != -1) {
                        app.util.showMessage("Network problem");
                        return null;
                    } else if (++attempts < trials) {
                        app.util.showMessage("reattempting to get product...");
                        return getProductDetail(id);
                    } else {
                        attempts = 0;
                        return null;
                    }
                default:
                    app.util.showMessage(ex.getMessage());
                    return null;
            }
        } catch (NullPointerException exception) {
            app.util.showMessage("Network problem");
            exception.printStackTrace();
            return null;
        }
    }

    public boolean deleteProductDetail(int id) {
        if (con == null) {
            getConnection();
        }
        try {
            PreparedStatement stmt = con.prepareStatement("delete from products where id = ?");
            stmt.setInt(1, id);
            attempts = 0;
            return stmt.executeUpdate() > -1;
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case 0:
                    if (getConnection() != -1) {
                        app.util.showMessage("Network problem");
                        return false;
                    } else if (++attempts < trials) {
                        app.util.showMessage("reattempting to get product...");
                        return deleteProductDetail(id);
                    } else {
                        attempts = 0;
                        return false;
                    }
                default:
                    app.util.showMessage(ex.getMessage());
                    return false;
            }
        } catch (NullPointerException exception) {
            app.util.showMessage("Network problem");
            exception.printStackTrace();
            return false;
        }
    }

    public boolean addNewQuantity(int currentId, int quantity) {
        if (con == null) {
            getConnection();
        }
        try {
            PreparedStatement stmt = con.prepareStatement("insert into stock_report (p_id, quantity) values (?, ?)");
            stmt.setInt(1, currentId);
            stmt.setInt(2, quantity);
            attempts = 0;
            return stmt.executeUpdate() > -1;
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case 0:
                    if (getConnection() != -1) {
                        app.util.showMessage("Network problem");
                        return false;
                    } else if (++attempts < trials) {
                        app.util.showMessage("reattempting to get product...");
                        return addNewQuantity(currentId, quantity);
                    } else {
                        attempts = 0;
                        return false;
                    }
                default:
                    app.util.showMessage(ex.getMessage());
                    return false;
            }
        } catch (NullPointerException exception) {
            app.util.showMessage("Network problem");
            exception.printStackTrace();
            return false;
        }
    }

    public ObservableList<ObservableMap<String, Object>> getSalesReportData(String filter, Timestamp fromDate, Timestamp toDate) {
        if (con == null) {
            getConnection();
        }
        ObservableList<ObservableMap<String, Object>> list = FXCollections.observableArrayList();
        try {
            PreparedStatement stmt = con.prepareStatement("select * from sales_report where date between ? and ?  order by date asc");
            stmt.setTimestamp(1, fromDate);
            stmt.setTimestamp(2, toDate);
            ResultSet rst = stmt.executeQuery(), rst2;
            while (rst.next()) {
                int id = rst.getInt("p_id");
                PreparedStatement prep = con.prepareStatement("select name, description from products where name like ? and id = ?");
                prep.setString(1, "%" + filter + "%");
                prep.setInt(2, id);
                rst2 = prep.executeQuery();
                if (rst2.next()) {
                    ObservableMap<String, Object> obj = FXCollections.observableHashMap();
                    obj.put("name", rst2.getString(1));
                    obj.put("desc", rst2.getString(2));
                    obj.put("id", rst.getInt("id"));
                    obj.put("price", rst.getDouble("price"));
                    obj.put("quantity", rst.getInt("quantity"));
                    obj.put("date", rst.getTimestamp("date"));
                    list.add(obj);
                }

            }
            attempts = 0;
            return list;
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case 0:
                    if (getConnection() != -1) {
                        app.util.showMessage("Network problem");
                        return null;
                    } else if (++attempts < trials) {
                        app.util.showMessage("reattempting to get stock data...");
                        return getStockData();
                    } else {
                        attempts = 0;
                        return null;
                    }
                default:
                    app.util.showMessage(ex.getMessage());
                    return null;
            }
        } catch (NullPointerException exception) {
            app.util.showMessage("Network problem");
            return null;
        }
    }

    public Boolean userExists(String username, String password) {
        if (con == null) {
            getConnection();
        }
        ObservableList<ObservableMap<String, Object>> list = FXCollections.observableArrayList();
        try {
            PreparedStatement stmt = con.prepareStatement("select * from users where username = ?");
            stmt.setString(1, username);
            ResultSet rst = stmt.executeQuery();
            if (rst.next() && rst.getString("password").equals(password)) {
                app.privilege.setValue(rst.getBoolean("privilege"));
                return true;
            }
            return false;
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case 0:
                    if (getConnection() != -1) {
                        app.loginCtrl.statusLabel.setText("Network problem");
                        return null;
                    } else if (++attempts < trials) {
                        app.loginCtrl.statusLabel.setText("reattempting to get stock data...");
                        return null;
                    } else {
                        attempts = 0;
                        return null;
                    }
                default:
                    app.loginCtrl.statusLabel.setText(ex.getMessage());
                    return null;
            }
        } catch (NullPointerException exception) {
            app.loginCtrl.statusLabel.setText("Network problem");
            return null;
        }
    }
}
