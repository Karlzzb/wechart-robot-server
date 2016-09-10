package com.karl.fx.controller;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.MapValueFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.fx.FxmlView;
import com.karl.fx.StageManager;

@Component
@Lazy
public class MainDeskController extends FxmlController {
    @FXML
    private Button loginButton;

    @SuppressWarnings("rawtypes")
    @FXML
    private TableView groupChoiseTab;

    public static final String selectColumnKey = "selectColumnKey";
    public static final String qunNameColumnKey = "qunNameColumnKey";
    public static final String qunSizeColumnKey = "qunSizeColumnKey";

    @Autowired
    @Lazy(value = true)
    public MainDeskController(StageManager stageManager) {
        super();
        this.stageManager = stageManager;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void initialize() {
        groupChoiseTab.setItems(generateDataInMap());

        TableColumn<Object, String> selectColumn = new TableColumn<>("选择");
        TableColumn<Map<String, String>, String> qunNameColumn = new TableColumn<>("群名称");
        TableColumn<Map<String, String>, String> qunSizeColumn = new TableColumn<>("群人数");

        selectColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections
                .observableArrayList(runtimeDomain.getGroupMap().keySet())));
        // selectColumn.setCellValueFactory(new
        // MapValueFactory(selectColumnKey));
        qunNameColumn.setCellValueFactory(new MapValueFactory(qunNameColumnKey));
        qunSizeColumn.setCellValueFactory(new MapValueFactory(qunSizeColumnKey));

        groupChoiseTab.getColumns().setAll(selectColumn, qunNameColumn, qunSizeColumn);
    }

    private ObservableList<Map<String, String>> generateDataInMap() {
        ObservableList<Map<String, String>> allData = FXCollections.observableArrayList();

        for (String groupId : runtimeDomain.getGroupMap().keySet()) {
            Map<String, String> dataRow = new HashMap<>();
            dataRow.put(selectColumnKey, groupId);
            dataRow.put(qunNameColumnKey,
                    runtimeDomain.getGroupMap().get(groupId).getString("NickName"));
            dataRow.put(
                    qunSizeColumnKey,
                    String.valueOf(runtimeDomain.getGroupMap().get(groupId)
                            .getJSONArray("MemberList").size()));
            allData.add(dataRow);
        }
        return allData;
    }

    @FXML
    private void wechatLogin(ActionEvent event) {
        stageManager.switchScene(FxmlView.LOGIN);
    }
}
