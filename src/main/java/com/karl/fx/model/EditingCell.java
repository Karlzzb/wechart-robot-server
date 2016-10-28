package com.karl.fx.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class EditingCell<T, S> extends TableCell<T, S> {
	private TextField textField;

	private S testData;

	public EditingCell(S testDate) {
		testData = testDate;
	}

	@Override
	public void startEdit() {
		if (!isEmpty()) {
			super.startEdit();
			createTextField();
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(String.valueOf(getItem()));
		setGraphic(null);
	}

	@Override
	public void updateItem(S item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
				}
				setText(getString());
				setGraphic(textField);
			} else {
				setText(getString());
				setGraphic(null);
			}
		}
	}

	private void createTextField() {
		textField = new TextField(getString());

		if (testData instanceof Integer || testData instanceof Long) {
			textField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> ov,
						String oldValue, String newValue) {
					try {
						if (newValue.matches("\\d*")) {
							textField.setText(newValue);
						} else {
							textField.setText(oldValue);
						}
					} catch (Exception e) {
						textField.setText(oldValue);
					}
				}
			});
		}

		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@SuppressWarnings("unchecked")
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				if (!arg2) {
					if (testData instanceof String) {
						commitEdit((S) textField.getText());
					} else if (testData instanceof Integer) {
						commitEdit((S) Integer.valueOf(textField.getText()));
					} else if (testData instanceof Long) {
						commitEdit((S) Long.valueOf(textField.getText()));
					}
				}
			}
		});
	}

	private String getString() {

		return getItem() == null ? "" : getItem().toString();
	}

}
