package com.karl;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dddeb extends Application {

    public static class Product {
        private String name;
        private String code;

        public Product(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private TableView<Product> productTable = new TableView<Product>();

    @Override
    public void start(Stage stage) {

        Button refreshBtn = new Button("Refresh table");
        refreshBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                // You can get the new data from DB
                List<Product> newProducts = new ArrayList<Product>();
                newProducts.add(new Product("new product A", "1201"));
                newProducts.add(new Product("new product B", "1202"));
                newProducts.add(new Product("new product C", "1203"));
                newProducts.add(new Product("new product D", "1244"));

                productTable.getItems().clear();
                productTable.getItems().addAll(newProducts);
                //productTable.setItems(FXCollections.observableArrayList(newProducts));
            }
        });

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setMinWidth(100);
        nameCol.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));

        TableColumn codeCol = new TableColumn("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<Product, String>("code"));

        productTable.getColumns().addAll(nameCol, codeCol);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // You can get the data from DB
        List<Product> products = new ArrayList<Product>();
        products.add(new Product("product A", "0001"));
        products.add(new Product("product B", "0002"));
        products.add(new Product("product C", "0003"));

        //productTable.getItems().addAll(products);
        productTable.setItems(FXCollections.observableArrayList(products));

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.getChildren().addAll(productTable, refreshBtn);

        Scene scene = new Scene(new Group());
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setScene(scene);
        stage.setWidth(300);
        stage.setHeight(500);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}