package com.karl.fx.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.fx.FxmlView;
import com.karl.fx.model.ChatGroupModel;

@Component
@Lazy
public class MainDeskController extends FxmlController {
    @FXML
    private Button loginButton;

    @FXML
    private Button groupFlush;

    @FXML
    private TableColumn<ChatGroupModel, Boolean> selectColumn;

    @FXML
    private TableColumn<ChatGroupModel, String> qunNameColumn;

    @FXML
    TableColumn<ChatGroupModel, Integer> qunSizeColumn;

    @FXML
    private TableView<ChatGroupModel> groupChoiseTab;

    final ToggleGroup group = new ToggleGroup();

    private ObservableList<ChatGroupModel> groupList;

    @Override
    public void initialize() {
        assemableTable();
    }

    private ObservableList<ChatGroupModel> generateDataInMap() {
        if (groupList != null)
            groupList.clear();
        groupList = FXCollections.observableArrayList();
        for (String groupId : webWechat.getRuntimeDomain().getGroupMap().keySet()) {
            groupList.add(new ChatGroupModel(groupId, runtimeDomain.getGroupMap().get(groupId)
                    .getString("NickName").replaceAll("</?[^>]+>", ""), runtimeDomain.getGroupMap()
                    .get(groupId).getJSONArray("MemberList").size()));
        }
        return groupList;
    }

    @FXML
    private void wechatLogin(ActionEvent event) {
        stageManager.switchScene(FxmlView.LOGIN);
    }

    @FXML
    private void flushGroup(ActionEvent event) {
        webWechat.wxInit();
        // webWechat.getContact();
        groupChoiseTab.setItems(generateDataInMap());
    }

    private void assemableTable() {
        groupChoiseTab.setEditable(true);
        groupChoiseTab.setItems(generateDataInMap());
        Callback<TableColumn<ChatGroupModel, Boolean>, TableCell<ChatGroupModel, Boolean>> cellFactory = new Callback<TableColumn<ChatGroupModel, Boolean>, TableCell<ChatGroupModel, Boolean>>() {
            @Override
            public TableCell<ChatGroupModel, Boolean> call(TableColumn<ChatGroupModel, Boolean> p) {
                return new RadioButtonCell();
            }
        };
        selectColumn.setCellFactory(cellFactory);

        qunNameColumn.setCellValueFactory(new PropertyValueFactory<ChatGroupModel, String>(
                ChatGroupModel.groupNameColumnKey));
        qunSizeColumn.setCellValueFactory(new PropertyValueFactory<ChatGroupModel, Integer>(
                ChatGroupModel.groupSizeColumnKey));
    }

    class RadioButtonCell extends TableCell<ChatGroupModel, Boolean> {

        private RadioButton radio;

        public RadioButtonCell() {
            createRadioButton();
        }

        private void createRadioButton() {
            radio = new RadioButton();
            radio.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1,
                        Boolean arg2) {
                    if (!arg2) {
                        commitEdit(radio.isSelected());
                    }
                }
            });
            radio.setToggleGroup(group);
        }

        @Override
        public void commitEdit(Boolean t) {
            super.commitEdit(t);
            final ObservableList<ChatGroupModel> items = getTableView().getItems();
            for (int i = 0; i < items.size(); i++) {
                ChatGroupModel chatGroup = items.get(i);
                if (i == getIndex()) {
                    chatGroup.setSelector(t);
                    runtimeDomain.setCurrentGroupId(chatGroup.getGroupId());
                } else {
                    chatGroup.setSelector(Boolean.FALSE);
                }
            }
        }

        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            final ObservableList<ChatGroupModel> items = getTableView().getItems();
            if (items != null) {
                if (getIndex() < items.size()) {
                    setGraphic(radio);
                }
            }
        }
    }
}
