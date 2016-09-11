package com.karl.fx.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
        // webWechat.wxInit();
        webWechat.getContact();
        groupChoiseTab.setItems(generateDataInMap());
    }

    private class RadioButtonTableCell<S, T> extends TableCell<S, T> {
        private final RadioButton radio;
        private ObservableValue<T> ov;

        public RadioButtonTableCell() {
            this.radio = new RadioButton();
            setAlignment(Pos.CENTER);
            setGraphic(radio);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setGraphic(radio);
                if (ov instanceof SimpleBooleanProperty) {
                    radio.selectedProperty().unbindBidirectional((SimpleBooleanProperty) ov);
                }
                ov = getTableColumn().getCellObservableValue(getIndex());
                if (ov instanceof SimpleBooleanProperty) {
                    radio.selectedProperty().bindBidirectional((SimpleBooleanProperty) ov);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void assemableTable() {
        groupChoiseTab.setEditable(true);
        groupChoiseTab.setItems(generateDataInMap());
        TableColumn<ChatGroup, Boolean> selectColumn = new TableColumn<>("选择");
        selectColumn.setCellValueFactory(new PropertyValueFactory<ChatGroup, Boolean>(
                ChatGroup.selectorColumnKey));
        selectColumn
                .setCellFactory(new Callback<TableColumn<ChatGroup, Boolean>, TableCell<ChatGroup, Boolean>>() {
                    @Override
                    public TableCell<ChatGroup, Boolean> call(TableColumn<ChatGroup, Boolean> param) {
                        final RadioButtonTableCell<ChatGroup, Boolean> cell = new RadioButtonTableCell<>();
                        final RadioButton radio = (RadioButton) cell.getGraphic();
                        radio.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                for (ChatGroup group : groupChoiseTab.getItems()) {
                                    group.setSelector(false);
                                }
                                ChatGroup group = groupList.get(cell.getIndex());
                                group.setSelector(true);
                                runtimeDomain.setCurrentGroupId(group.getGroupId());
                            }
                        });
                        return cell;
                    }
                });

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
}
