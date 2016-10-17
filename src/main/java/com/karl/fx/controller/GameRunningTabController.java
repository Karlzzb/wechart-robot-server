package com.karl.fx.controller;

import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.db.domain.GameInfo;
import com.karl.db.domain.Player;
import com.karl.db.domain.PlayerTrace;
import com.karl.fx.model.EditingCell;
import com.karl.fx.model.PlayerTraceModel;
import com.karl.utils.AppUtils;
import com.karl.utils.ResouceUtils;
import com.karl.utils.StringUtils;

@Component
@Lazy
public class GameRunningTabController extends FxmlController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GameRunningTabController.class);

	@FXML
	private TableView<PlayerTraceModel> traceTab;

	@FXML
	private TableColumn<PlayerTraceModel, String> playerName;

	@FXML
	private TableColumn<PlayerTraceModel, Long> playerPoint;

	@FXML
	private TableColumn<PlayerTraceModel, String> betInfo;

	@FXML
	private TableColumn<PlayerTraceModel, String> luckInfo;

	@FXML
	private TableColumn<PlayerTraceModel, String> resultRuleName;

	@FXML
	private TableColumn<PlayerTraceModel, String> resultInfo;

	@FXML
	private TableColumn<PlayerTraceModel, PlayerTraceModel> optionsCol;

	private ObservableList<PlayerTraceModel> traceModeList;

	@FXML
	private Button bulkApproveButton;
	@FXML
	private Button bulkRejectButton;

	private Thread traceFlushThread;

	private Task<Void> traceFlushTask;

	@Override
	public void initialize() {
		buidApprovalTab();
		fillTraceTab();
		traceAutoFlush();
	}

	private void buidApprovalTab() {
		traceTab.setEditable(Boolean.TRUE);
		traceModeList = FXCollections.observableArrayList();
		playerName.setEditable(Boolean.FALSE);
		playerName
				.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, String>(
						PlayerTraceModel.PLAYERNAMECOLKEY));
		playerPoint.setEditable(Boolean.FALSE);
		playerPoint
				.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, Long>(
						PlayerTraceModel.PLAYERPOINTCOLKEY));
		resultRuleName.setEditable(Boolean.FALSE);
		resultRuleName
				.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, String>(
						PlayerTraceModel.RESULTRULENAMEKEY));
		resultInfo.setEditable(Boolean.FALSE);
		resultInfo
				.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, String>(
						PlayerTraceModel.RESULTINFOKEY));

		// editable col
		Callback<TableColumn<PlayerTraceModel, String>, TableCell<PlayerTraceModel, String>> cellFactory = new Callback<TableColumn<PlayerTraceModel, String>, TableCell<PlayerTraceModel, String>>() {
			public TableCell<PlayerTraceModel, String> call(
					TableColumn<PlayerTraceModel, String> p) {
				return new EditingCell<PlayerTraceModel>();
			}
		};
		betInfo.setEditable(Boolean.TRUE);
		betInfo.setCellFactory(cellFactory);
		betInfo.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, String>(
				PlayerTraceModel.BETINFOKEY));
		betInfo.setOnEditCommit(new EventHandler<CellEditEvent<PlayerTraceModel, String>>() {
			@Override
			public void handle(CellEditEvent<PlayerTraceModel, String> cell) {
				PlayerTraceModel traceModel = cell.getTableView().getItems()
						.get(cell.getTablePosition().getRow());
				if (!StringUtils.matchLongSplit(cell.getNewValue())) {
					fillTraceTab();
					return;
				}
				try {
					PlayerTrace trace = gameService.updatePlayerTraceBetInfo(
							traceModel.getTraceId(), cell.getNewValue());
					if (trace != null) {
						traceModel.setBetInfo(trace.getBetInfo());
						return;
					}
				} catch (Exception e) {
					LOGGER.error("PlayTrace[" + traceModel.getPlayerName()
							+ "] change betInfo failed!", e);
				} finally {
				}

				traceModel.setBetInfo(cell.getNewValue());
			}
		});
		luckInfo.setEditable(Boolean.TRUE);
		luckInfo.setCellFactory(cellFactory);
		luckInfo.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, String>(
				PlayerTraceModel.LUCKINFOKEY));
		luckInfo.setOnEditCommit(new EventHandler<CellEditEvent<PlayerTraceModel, String>>() {
			@Override
			public void handle(CellEditEvent<PlayerTraceModel, String> cell) {
				PlayerTraceModel traceModel = traceModeList.get(cell.getTablePosition().getRow());
				if (!StringUtils.matchDouble(cell.getNewValue())) {
					fillTraceTab();
					return;
				}
				try {
					PlayerTrace trace = gameService.updatePlayerTraceLuckInfo(
							traceModel.getTraceId(),
							Double.valueOf(cell.getNewValue()));
					if (trace != null) {
						traceModel.setLuckInfo(trace.getLuckInfo().toString());
						traceModel.setResultRuleName(trace.getResultRuleName());
						return;
					}
				} catch (Exception e) {
					LOGGER.error("PlayTrace[" + traceModel.getPlayerName()
							+ "] change luckInfo failed!", e);
				} finally {
				}
				traceModel.setLuckInfo(cell.getOldValue());
			}
		});

		// button col
		optionsCol
				.setCellFactory(param -> new TableCell<PlayerTraceModel, PlayerTraceModel>() {
					private final Button deleteButton = new Button("",
							new ImageView(ResouceUtils.IMAGENO));

					@Override
					protected void updateItem(PlayerTraceModel traceModel,
							boolean empty) {
						super.updateItem(traceModel, empty);
						if (traceModel == null) {
							setGraphic(null);
							return;
						}
						setGraphic(deleteButton);
						deleteButton.setOnAction(event -> delete(traceModel));
					}

					private void delete(PlayerTraceModel traceModel) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("删除确认");
						alert.setContentText("确定删除玩家【"
								+ traceModel.getPlayerName() + "】 的这条下注信息？");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK) {
							if (gameService.deleteTraceById(traceModel
									.getTraceId())) {
								getTableView().getItems().remove(traceModel);
							}
						}
					}
				});
	}

	private synchronized void fillTraceTab() {
		if (traceModeList != null) {
			traceModeList.clear();
		}
		GameInfo gameInfo = gameService.getGameById(runtimeDomain
				.getCurrentGameId());
		if (gameInfo == null || gameInfo.getIsUndo()) {
			return;
		}
		List<PlayerTrace> traceList = gameService.getCurrentPlayTrace();
		if (traceList != null) {
			PlayerTrace trace = null;
			Player pEntity = null;
			String resultInfo;
			for (int i = 0; i < traceList.size(); i++) {
				trace = traceList.get(i);
				pEntity = runtimeDomain.getRunningPlayeres().get(
						trace.getRemarkName());
				if (pEntity == null) {
					continue;
				}
				resultInfo = "";
				if (trace.getResultPoint() != null) {
					resultInfo = trace.getResultPoint() > 0 ? "赢"
							+ trace.getResultPoint() : "输"
							+ Math.abs(trace.getResultPoint());
				}

				traceModeList.add(new PlayerTraceModel(trace.getTraceId(),
						trace.getPlayerId(), trace.getRemarkName(), pEntity
								.getPoints(), trace.getBetInfo(), trace
								.getLuckInfo().toString(), trace
								.getResultRuleName(), resultInfo));
			}
		}
		traceTab.setItems(traceModeList);
	}

	/**
	 * auto rync player table
	 */
	private void traceAutoFlush() {
		if (traceFlushTask == null) {
			traceFlushTask = new Task<Void>() {
				@Override
				public Void call() {
					while (true) {
						try {
							Thread.sleep(AppUtils.TRACE_TAB_FLSH_TERVAL);
							if (runtimeDomain.getGlobalGameSignal()) {
								List<PlayerTrace> traceList = gameService
										.getCurrentPlayTrace();
								if (traceList != null) {
									if (traceModeList == null) {
										return null;
									}
									if (traceModeList.size() > 0) {
										traceModeList.clear();
									}
									GameInfo gameInfo = gameService
											.getGameById(runtimeDomain
													.getCurrentGameId());
									if (gameInfo == null
											|| gameInfo.getIsUndo()) {
										continue;
									}
									PlayerTrace trace = null;
									for (int i = 0; i < traceList.size(); i++) {
										trace = traceList.get(i);
										traceModeList
												.add(new PlayerTraceModel(
														trace.getTraceId(),
														trace.getPlayerId(),
														trace.getRemarkName(),
														runtimeDomain
																.getRunningPlayeres()
																.get(trace
																		.getRemarkName())
																.getPoints(),
														trace.getBetInfo(),
														trace.getLuckInfo()
																.toString(),
														trace.getResultRuleName() == null ? ""
																: trace.getResultRuleName(),
														trace.getResultPoint() == null ? ""
																: trace.getResultPoint() > 0 ? "赢"
																		+ trace.getResultPoint()
																		: "输"
																				+ Math.abs(trace
																						.getResultPoint())));
									}
								}
							}
						} catch (Exception e) {
							LOGGER.error("player table auto change failed!", e);
						}
					}
				}
			};
			if (traceFlushThread == null) {
				// traceFlushThread = new Thread(traceFlushTask);
				// traceFlushThread.setDaemon(Boolean.TRUE);
				// traceFlushThread.start();
			}
			LOGGER.info("Auto trace flush Thread start");
		}
	}

	public void flushLuckInfo() {
		this.fillTraceTab();
	}

	public void flushBetInfo() {
		this.fillTraceTab();
	}

	public void flushResult() {
		this.fillTraceTab();
	}

	public void gameStartFlush() {
		this.fillTraceTab();
	}

	public void cleanCurrentTrace() {
		runtimeDomain.setCurrentGameId(Long.valueOf(0));
		if (traceModeList != null) {
			traceModeList.clear();
		}
		traceTab.setItems(traceModeList);
	}

}
