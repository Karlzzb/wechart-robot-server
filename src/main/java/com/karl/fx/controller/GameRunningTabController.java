package com.karl.fx.controller;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
	private TableColumn<PlayerTraceModel, Long> optionsCol;

	@FXML
	private Button bulkApproveButton;
	@FXML
	private Button bulkRejectButton;

	private Thread traceFlushThread;

	private Task<Void> traceFlushTask;

	@Override
	public void initialize() {
		buildTraceTable();
		fillTraceTab();
	}

	private void buildTraceTable() {
		traceTab.setEditable(Boolean.TRUE);
		playerName.setEditable(Boolean.FALSE);
		playerName
				.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, String>(
						PlayerTraceModel.PLAYERNAMECOLKEY));

		playerName
				.setCellFactory(param -> new TableCell<PlayerTraceModel, String>() {
					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						setText(empty ? null : getString());
						setGraphic(null);
						TableRow<PlayerTraceModel> currentRow = getTableRow();
						PlayerTraceModel playerName = currentRow == null ? null : (PlayerTraceModel)currentRow.getItem();
						if (playerName != null && !playerName.getPlayerName().isEmpty()) {
							clearPriorityStyle();
							if (playerName.getPlayerName().equals(runtimeDomain
											.getBankerRemarkName())) {
								currentRow.setStyle("-fx-background-color: palevioletred");  
							}
						}
					}
					
			        private void clearPriorityStyle(){
			        	getTableRow().setStyle("");
			        }
			        private String getString() {
			            return getItem() == null ? "" : getItem().toString();
			        }
				});

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
				PlayerTraceModel traceModel = traceTab.getItems().get(cell
						.getTablePosition().getRow());
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
		flushDefiedCol();

	}

	private void flushDefiedCol() {
		optionsCol
				.setCellFactory(new Callback<TableColumn<PlayerTraceModel, Long>, TableCell<PlayerTraceModel, Long>>() {
					@Override
					public TableCell<PlayerTraceModel, Long> call(
							TableColumn<PlayerTraceModel, Long> applyId) {
						return new TraceDelCell();
					}
				});
	}

	private void deleteTrace(PlayerTraceModel traceModel) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("删除确认");
		alert.setContentText("确定删除玩家【" + traceModel.getPlayerName()
				+ "】 的这条下注信息？");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			if (gameService.deleteTraceById(traceModel.getTraceId())) {
				traceTab.getItems().remove(traceModel);
			}
			flushDefiedCol();
		}
	}

	private void fillTraceTab() {
		synchronized (this) {
			try {
			if (traceTab.getItems() != null) {
				traceTab.getItems().clear();
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

					traceTab.getItems().add(new PlayerTraceModel(trace.getTraceId(),
							trace.getPlayerId(), trace.getRemarkName(), pEntity
									.getPoints(), trace.getBetInfo(), trace
									.getLuckInfo().toString(), trace
									.getResultRuleName(), resultInfo, trace
									.getIsBanker()));
				}
			}
			flushDefiedCol();
			LOGGER.info("Trace table flush secess, table size={}!", traceTab.getItems().size());
			}catch(Exception e) {
				LOGGER.error("Trace table flush failed!", e);
			}
		}
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
									if (traceTab.getItems() == null) {
										return null;
									}
									if (traceTab.getItems().size() > 0) {
										traceTab.getItems().clear();
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
										traceTab.getItems()
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
																						.getResultPoint()),
														trace.getIsBanker()));
									}
								}
								flushDefiedCol();
							}
						} catch (Exception e) {
							LOGGER.error("player table auto change failed!", e);
						}
					}
				}
			};
			if (traceFlushThread == null) {
				 traceFlushThread = new Thread(traceFlushTask);
				 traceFlushThread.setDaemon(Boolean.TRUE);
				 traceFlushThread.start();
			}
			LOGGER.info("Auto trace flush Thread start");
		}
	}

	public void flushLuckInfo() {
		this.fillTraceTab();
		stageManager.popLuckInfoWindow(runtimeDomain);
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

	public void manuallyFlush() {
		this.fillTraceTab();
	}

	public void cleanCurrentTrace() {
		runtimeDomain.setCurrentGameId(Long.valueOf(0));
		if (traceTab.getItems() != null) {
			traceTab.getItems().clear();
		}
	}

	private class TraceDelCell extends TableCell<PlayerTraceModel, Long> {

		private GridPane gridPane;

		private Button traceDel;

		public TraceDelCell() {
			createButtons();
		}

		private void createButtons() {
			gridPane = new GridPane();
			gridPane.setAlignment(Pos.CENTER);

			traceDel = new Button();
			traceDel.setGraphic(new ImageView(ResouceUtils.IMAGENO));

			traceDel.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					int index = traceTab.getSelectionModel().getSelectedIndex();

					index = getIndex();
					if ((index != -1)) {
						deleteTrace(traceTab.getItems().get(index));
					}
				}
			});

			gridPane.add(traceDel, 0, 0);
		}

		@Override
		public void commitEdit(Long t) {
			super.commitEdit(t);
		}

		@Override
		public void updateItem(Long item, boolean empty) {
			super.updateItem(item, empty);
			final ObservableList<PlayerTraceModel> items = getTableView()
					.getItems();
			if (items != null) {
				if (getIndex() < items.size()) {
					setGraphic(gridPane);
				}
			}else {
				setGraphic(null);
			}
		}
	}
}
