package com.karl.fx.controller;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.db.domain.ApplyPoints;
import com.karl.fx.model.CheckBoxButtonCellApply;
import com.karl.fx.model.PlayerApply;
import com.karl.utils.AppUtils;

@Component
@Lazy
public class ApprovalTabController extends FxmlController {

	@FXML
	private TableView<PlayerApply> approvalTab;

	@FXML
	private TableColumn<PlayerApply, Boolean> appCheck;

	@FXML
	private TableColumn<PlayerApply, String> playerName;

	@FXML
	private TableColumn<PlayerApply, String> applyInfo;
	@FXML
	private TableColumn<PlayerApply, Long> approvalOption;

	private ObservableList<PlayerApply> applyList;
	
	
	@FXML private Button bulkApproveButton;
	@FXML private Button bulkRejectButton;

	@Override
	public void initialize() {
		buidApprovalTab();
	}

	private void buidApprovalTab() {
		approvalTab.setEditable(false);
		appCheck.setCellFactory(new Callback<TableColumn<PlayerApply, Boolean>, TableCell<PlayerApply, Boolean>>() {
			@Override
			public TableCell<PlayerApply, Boolean> call(
					TableColumn<PlayerApply, Boolean> arg0) {
				return new CheckBoxButtonCellApply();
			}

		});

		approvalOption
				.setCellFactory(new Callback<TableColumn<PlayerApply, Long>, TableCell<PlayerApply, Long>>() {
					@Override
					public TableCell<PlayerApply, Long> call(
							TableColumn<PlayerApply, Long> arg0) {
						return new MultiButtonCellApply();
					}
				});

		playerName
				.setCellValueFactory(new PropertyValueFactory<PlayerApply, String>(
						PlayerApply.PLAYERNAMEKEY));
		applyInfo
				.setCellValueFactory(new PropertyValueFactory<PlayerApply, String>(
						PlayerApply.APPLYINFOKEY));
		fillApplyTab();
	}

	private void fillApplyTab() {
		if (applyList != null) {
			applyList.clear();
		}
		applyList = runtimeDomain.getApplyList();
		List<ApplyPoints> applyEntityList = gameService.getUncheckedApplyList();
		if (applyEntityList != null && applyEntityList.size() > 0) {
			ApplyPoints applyEntity = null;
			for (int i = 0; i < applyEntityList.size(); i++) {
				applyEntity = applyEntityList.get(i);
				applyList.add(new PlayerApply(Boolean.FALSE, applyEntity
						.getApplyId(), applyEntity.getPlayerId(), applyEntity
						.getRemarkName(), applyEntity.getApplyType(),
						applyEntity.getPoints(), applyEntity
								.getApprovalStatus()));
			}
		}
		approvalTab.setItems(applyList);
	}
	
	public void addApply(ApplyPoints applyEntity) {
		if (applyEntity == null) {
			return;
		}
		approvalTab.getItems().add(new PlayerApply(Boolean.FALSE, applyEntity
				.getApplyId(), applyEntity.getPlayerId(), applyEntity
				.getRemarkName(), applyEntity.getApplyType(),
				applyEntity.getPoints(), applyEntity
						.getApprovalStatus()));
	}
	
	
	@FXML
	private void bulkApprove(ActionEvent event) {
		for (int i = 0; i < approvalTab.getItems().size(); i++) {
			if(approvalTab.getItems().get(i).getApplyCheck()) {
				changeApply(i, AppUtils.APPROVALYES);
			}
		}
		
	}
	
	@FXML
	private void bulkReject(ActionEvent event) {
		for (int i = 0; i < applyList.size(); i++) {
			if(applyList.get(i).getApplyCheck()) {
				changeApply(i, AppUtils.APPROVALNO);
			}
		}
		
	}
	
	private void changeApply(int index, int approvalStatus) {
		if (gameService.approvalPlayer(applyList.get(index)
				.getApplyId(), applyList.get(index)
				.getPlayerId(), approvalStatus, applyList
				.get(index).getApplyPoint())) {
			applyList.remove(index);
			approvalTab.getSelectionModel().clearSelection();
		}
	}

	private class MultiButtonCellApply extends TableCell<PlayerApply, Long> {

		private Button pass;
		private Button deny;

		public MultiButtonCellApply() {
			createButtons();
		}

		private void createButtons() {
			pass = new Button();
			deny = new Button();

			pass.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					int index = approvalTab.getSelectionModel()
							.getSelectedIndex();
					if ((index != -1)) {
						changeApply(index, AppUtils.APPROVALYES);
					}
				}
			});

			deny.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					int index = approvalTab.getSelectionModel()
							.getSelectedIndex();
					if ((index != -1)) {
						changeApply(index, AppUtils.APPROVALNO);
					}
				}
			});
		}

		@Override
		public void commitEdit(Long t) {
			super.commitEdit(t);
			final ObservableList<PlayerApply> items = getTableView().getItems();
			for (int i = 0; i < items.size(); i++) {
				if (i == getIndex()) {
					applyList.remove(i);
				}
			}
		}

		@Override
		public void updateItem(Long item, boolean empty) {
			super.updateItem(item, empty);
			final ObservableList<PlayerApply> items = getTableView().getItems();
			if (items != null) {
				if (getIndex() < items.size()) {
					setGraphic(pass);
					setGraphic(deny);
				}
			}
		}
	}
}
