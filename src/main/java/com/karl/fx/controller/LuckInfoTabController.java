package com.karl.fx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.fx.model.LuckInfoModel;

@Component
@Lazy
public class LuckInfoTabController extends FxmlController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LuckInfoTabController.class);

	@FXML
	private TableView<LuckInfoModel> luckInfoTable;

	@FXML
	private TableColumn<LuckInfoModel, Integer> packageOrderCol;

	@FXML
	private TableColumn<LuckInfoModel, String> playerNameCol;

	@FXML
	private TableColumn<LuckInfoModel, String> packageInfoCol;

	@FXML
	private TableColumn<LuckInfoModel, String> packageTimeCol;

	@FXML
	private TableColumn<LuckInfoModel, String> playerRoleCol;

	@Override
	public void initialize() {
		buildLuckTable();
		if (luckInfoTable.getItems() != null) {
			luckInfoTable.getItems().clear();
		}
		luckInfoTable.setItems(runtimeDomain.getLuckInfoModeList());
	}

	private void buildLuckTable() {
		luckInfoTable.setEditable(Boolean.FALSE);
		packageOrderCol
				.setCellValueFactory(new PropertyValueFactory<LuckInfoModel, Integer>(
						LuckInfoModel.PACKAGEORDERCOL));
		playerNameCol
				.setCellValueFactory(new PropertyValueFactory<LuckInfoModel, String>(
						LuckInfoModel.PLAYERNAMECOL));
		packageInfoCol
				.setCellValueFactory(new PropertyValueFactory<LuckInfoModel, String>(
						LuckInfoModel.PACKAGEINFOCOL));
		packageTimeCol
				.setCellValueFactory(new PropertyValueFactory<LuckInfoModel, String>(
						LuckInfoModel.PACKAGETIMECOL));
		playerRoleCol
				.setCellValueFactory(new PropertyValueFactory<LuckInfoModel, String>(
						LuckInfoModel.PLAYERROLECOL));

		playerRoleCol
				.setCellFactory(param -> new TableCell<LuckInfoModel, String>() {
					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						setText(empty ? null : getString());
						setGraphic(null);
						TableRow<LuckInfoModel> currentRow = getTableRow();
						LuckInfoModel luckInfoModel = currentRow == null ? null
								: currentRow.getItem();
						if (luckInfoModel != null
								&& luckInfoModel.getPlayerRole() != null) {
							clearPriorityStyle();
							if (luckInfoModel.getPlayerName().equals(
									runtimeDomain.getBankerRemarkName())) {
								currentRow
										.setStyle("-fx-background-color: palevioletred");
							}
							if (luckInfoModel.getPlayerRole().equals(
									LuckInfoModel.PLAYERROLENOPOINT)) {
								// no point
								currentRow
										.setStyle("-fx-background-color: skyblue");

							} else if (luckInfoModel.getPlayerRole().equals(
									LuckInfoModel.PLAYERROLENOBET)) {
								// no bet
								currentRow
										.setStyle("-fx-background-color: palegreen");

							} else if (luckInfoModel.getPlayerRole().equals(
									LuckInfoModel.PLAYERROLENONE)) {
								// no friend
								currentRow
										.setStyle("-fx-background-color: yellow");

							} else if (luckInfoModel.getPlayerRole().equals(
									LuckInfoModel.PLAYERROLENOMAL)) {
								// normal
							}

						}
					}

					private void clearPriorityStyle() {
						getTableRow().setStyle("");
					}

					private String getString() {
						return getItem() == null ? "" : getItem().toString();
					}
				});
	}
}
