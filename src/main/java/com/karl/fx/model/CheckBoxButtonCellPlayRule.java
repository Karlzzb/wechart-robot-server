package com.karl.fx.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;

public class CheckBoxButtonCellPlayRule extends TableCell<PlayRule, Boolean> {

    private CheckBox checkBox;

    public CheckBoxButtonCellPlayRule() {
        createRadioButton();
    }

    private void createRadioButton() {
        checkBox = new CheckBox();
        checkBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean before,
                    Boolean now) {
                if (now) {
                    commitEdit(checkBox.isSelected());
                }
            }
        });
    }

    @Override
    public void commitEdit(Boolean value) {
        super.commitEdit(value);
        final ObservableList<PlayRule> items = getTableView().getItems();
        for (int i = 0; i < items.size(); i++) {
            PlayRule playRule = items.get(i);
            if (i == getIndex()) {
            	playRule.setRuleCheck(value);
            }
        }
    }

    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        final ObservableList<PlayRule> items = getTableView().getItems();
        if (items != null) {
            if (getIndex() < items.size() && getIndex() > -1) {
                checkBox.setSelected(items.get(getIndex()).getRuleCheck());
                setGraphic(checkBox);
            }
        }

    }
}