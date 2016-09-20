package com.karl.fx.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.ToggleGroup;

class RadioButtonCell extends TableCell<PlayerModel, Boolean> {

    private RadioButton radio;

    public RadioButtonCell(ToggleGroup group) {
        createRadioButton(group);
    }

    private void createRadioButton(ToggleGroup group) {
        radio = new RadioButton();
        radio.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean before,
                    Boolean now) {
                if (now) {
                    commitEdit(radio.isSelected());
                }
            }
        });
        radio.setToggleGroup(group);
    }

    @Override
    public void commitEdit(Boolean t) {
        super.commitEdit(t);
        final ObservableList<PlayerModel> items = getTableView().getItems();
        for (int i = 0; i < items.size(); i++) {
//            PlayerModel chatGroup = items.get(i);
            if (i == getIndex()) {
//                chatGroup.setSelector(t);
            } else {
//                chatGroup.setSelector(Boolean.FALSE);
            }
        }
    }

    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        final ObservableList<PlayerModel> items = getTableView().getItems();
        if (items != null) {
            if (getIndex() < items.size()) {
                radio.setSelected(items.get(getIndex()).getIsBanker());
                setGraphic(radio);
            }
        }

    }
}