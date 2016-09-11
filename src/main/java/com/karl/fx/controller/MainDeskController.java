package com.karl.fx.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

@Component
@Lazy
public class MainDeskController extends FxmlController {
    @FXML
    private Button loginButton;

    @FXML
    private Button groupFlush;

    @FXML
    private TableView<ChatGroup> groupChoiseTab;

    private ObservableList<ChatGroup> groupList;

    @Override
    public void initialize() {
        assemableTable();
    }

    private ObservableList<ChatGroup> generateDataInMap() {
        if (groupList != null)
            groupList.clear();
        groupList = FXCollections.observableArrayList();
        for (String groupId : webWechat.getRuntimeDomain().getGroupMap().keySet()) {
            groupList.add(new ChatGroup(groupId, runtimeDomain.getGroupMap().get(groupId)
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

    @SuppressWarnings("unchecked")
    private void assemableTable() {
        groupChoiseTab.setEditable(true);
        groupChoiseTab.setItems(generateDataInMap());
        TableColumn<ChatGroup, Boolean> selectColumn = new TableColumn<>("选择");

        Callback<TableColumn<ChatGroup, Boolean>, TableCell<ChatGroup, Boolean>> cellFactory = new Callback<TableColumn<ChatGroup, Boolean>, TableCell<ChatGroup, Boolean>>() {
            @Override
            public TableCell<ChatGroup, Boolean> call(TableColumn<ChatGroup, Boolean> p) {
                return new RadioButtonCell();
            }
        };
        selectColumn.setCellFactory(cellFactory);

        TableColumn<ChatGroup, String> qunNameColumn = new TableColumn<>("群名称");
        TableColumn<ChatGroup, Integer> qunSizeColumn = new TableColumn<>("群人数");
        qunNameColumn.setCellValueFactory(new PropertyValueFactory<ChatGroup, String>(
                ChatGroup.groupNameColumnKey));
        qunSizeColumn.setCellValueFactory(new PropertyValueFactory<ChatGroup, Integer>(
                ChatGroup.groupSizeColumnKey));

        groupChoiseTab.getColumns().addAll(selectColumn, qunNameColumn, qunSizeColumn);
    }

    public class ChatGroup {
        public static final String selectorColumnKey = "selector";
        public static final String groupIDColumnKey = "groupId";
        public static final String groupNameColumnKey = "groupName";
        public static final String groupSizeColumnKey = "groupSize";
        private SimpleBooleanProperty selector;
        private final SimpleStringProperty groupId;
        private final SimpleStringProperty groupName;
        private final SimpleIntegerProperty groupSize;

        public ChatGroup(String groupId, String groupName, Integer groupSize) {
            super();
            this.selector = new SimpleBooleanProperty(Boolean.FALSE);
            this.groupId = new SimpleStringProperty(groupId);
            this.groupName = new SimpleStringProperty(groupName);
            this.groupSize = new SimpleIntegerProperty(groupSize);
        }

        public String getGroupId() {
            return groupId.getValue();
        }

        public String getGroupName() {
            return groupName.getValue();
        }

        public Integer getGroupSize() {
            return groupSize.getValue();
        }

        public Boolean getSelector() {
            return selector.getValue();
        }

        public void setSelector(Boolean selector) {
            this.selector = new SimpleBooleanProperty(selector);
        }
    }

    class RadioButtonCell extends TableCell<ChatGroup, Boolean> {

        final ToggleGroup group = new ToggleGroup();

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
            final ObservableList<ChatGroup> items = getTableView().getItems();
            for (int i = 0; i < items.size(); i++) {
                ChatGroup chatGroup = items.get(i);
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
            final ObservableList<ChatGroup> items = getTableView().getItems();
            if (items != null) {
                if (getIndex() < items.size()) {
                    setGraphic(radio);
                }
            }
        }
    }
}
