package com.karl.fx.controller;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.db.domain.Player;
import com.karl.fx.model.EditingCell;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.AppUtils;

@Component
@Lazy
public class PlayerTableController extends FxmlController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PlayerTableController.class);

	@Autowired
	@Lazy
	private MainDeskController mainDeskController;

	@Autowired
	private MessageController messageController;
	
	@FXML
	private Label playerSizeLable;

	@FXML
	private Button singlePlayerInfoSend;

	@FXML
	private Button groupFlush;

	@FXML
	private Button syncPlayer;

	@FXML
	private TableColumn<PlayerModel, Boolean> colBankerSgin;

	@FXML
	private TableColumn<PlayerModel, String> colPlayerName;

	@FXML
	private TableColumn<PlayerModel, Long> colPlayerPoint;

	@FXML
	private TableView<PlayerModel> playerTab;

	@FXML
	private TextField playerSearchText;
	
	private Thread playerFlushThread;

	private Task<Void> playerFlushTask;

	private Boolean autoPlayerFlushContinue;
	private Boolean isInitializing;
	
	@Override
	public void initialize() {
		autoPlayerFlushContinue = Boolean.TRUE;
		isInitializing = Boolean.TRUE;
		buildPlayerTab();
		playerAutoFlush();
		buildFilterPlayer();
		isInitializing = Boolean.FALSE;
	}

	private void buildFilterPlayer() {
		playerSearchText
				.textProperty()
				.addListener(
						(ChangeListener<String>) (observable, oldVal, newVal) -> searchPlayer(
								oldVal, newVal));
	}

	private void searchPlayer(String oldVal, String newVal) {

		if (newVal == null || newVal.isEmpty()) {
			fillPlayerTab();
			return;
		}

		ObservableList<PlayerModel> filterPlayer = FXCollections
				.observableArrayList();
		List<Player> pEntityList = gameService.getAllPlayers();
		if (pEntityList != null && pEntityList.size() > 0) {
			PlayerModel playerModle = null;
			for (int i = 0; i < pEntityList.size(); i++) {
				if (pEntityList.get(i).getRemarkName()
						.matches(".*" + newVal + ".*")) {
					playerModle = new PlayerModel(i, pEntityList.get(i)
							.getRemarkName(), pEntityList.get(i).getPoints(),
							pEntityList.get(i).getWebchatId(), pEntityList.get(
									i).getWechatName());
					if (playerModle.getPlayerName().equals(
							runtimeDomain.getBankerRemarkName())) {
						playerModle.setIsBanker(Boolean.TRUE);
					}
					filterPlayer.add(playerModle);
				}
			}
			playerTab.setItems(filterPlayer);

			flushRadioCol();
		}
		this.flushRadioCol();
	}

	private void buildPlayerTab() {
		playerTab.setEditable(true);

		Callback<TableColumn<PlayerModel, Boolean>, TableCell<PlayerModel, Boolean>> radioFactory = new Callback<TableColumn<PlayerModel, Boolean>, TableCell<PlayerModel, Boolean>>() {
			@Override
			public TableCell<PlayerModel, Boolean> call(
					TableColumn<PlayerModel, Boolean> p) {
				return new RadioButtonCell();
			}
		};
		colBankerSgin.setCellFactory(radioFactory);
		colBankerSgin
				.setCellValueFactory(new PropertyValueFactory<PlayerModel, Boolean>(
						PlayerModel.ISBANKERCOLKEY));
		colPlayerName
				.setCellValueFactory(new PropertyValueFactory<PlayerModel, String>(
						PlayerModel.PLAYERNAMECOLKEY));
		colPlayerName.setEditable(Boolean.FALSE);

		// Editable col
		Callback<TableColumn<PlayerModel, Long>, TableCell<PlayerModel, Long>> cellFactory = new Callback<TableColumn<PlayerModel, Long>, TableCell<PlayerModel, Long>>() {
			public TableCell<PlayerModel, Long> call(
					TableColumn<PlayerModel, Long> p) {
				return new EditingCell<PlayerModel, Long>(0L);
			}
		};
		colPlayerPoint.setEditable(Boolean.TRUE);
		colPlayerPoint
				.setCellValueFactory(new PropertyValueFactory<PlayerModel, Long>(
						PlayerModel.PLAYERPOINTCOLKEY));
		colPlayerPoint.setCellFactory(cellFactory);
		colPlayerPoint.setSortable(Boolean.TRUE);
		colPlayerPoint.setSortType(TableColumn.SortType.DESCENDING);
		colPlayerPoint
				.setOnEditCommit(new EventHandler<CellEditEvent<PlayerModel, Long>>() {
					@Override
					public void handle(CellEditEvent<PlayerModel, Long> cell) {
						autoPlayerFlushContinue = Boolean.FALSE;
						PlayerModel pModel = cell.getTableView().getItems()
								.get(cell.getTablePosition().getRow());
						if (cell.getNewValue() == null) {
							flushPlayerList();
							autoPlayerFlushContinue = Boolean.TRUE;
							return;
						}
						try {
							pModel.setPlayerPoint(cell.getNewValue());
							gameService.ryncPlayersPoint(pModel);
						} catch (Exception e) {
							LOGGER.error("Player[" + pModel.getPlayerName()
									+ "] change point failed!", e);
						} finally {
							autoPlayerFlushContinue = Boolean.TRUE;
						}
					}
				});
		// Table select mode
		playerTab.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, selectedPModel) -> {
					if (selectedPModel != null) {
						singlePlayerInfoSend.setUserData(selectedPModel);
					}
				});
		fillPlayerTab();
		singlePlayerInfoSend.setUserData(null);
	}

	private void fillPlayerTab() {
		ObservableList<PlayerModel> playerTableList = FXCollections.observableArrayList();
		Map<String, Player> currentPlayers = runtimeDomain.getRunningPlayeres();

		if (currentPlayers != null && currentPlayers.size() > 0) {
			PlayerModel playerModle = null;
			Player pEntity = null;
			int i = 1;
			for (String remarkName : currentPlayers.keySet()) {
				pEntity = currentPlayers.get(remarkName);

				playerModle = new PlayerModel(i, pEntity.getRemarkName(),
						pEntity.getPoints(), pEntity.getWebchatId(),
						pEntity.getWechatName());
				if (playerModle.getPlayerName().equals(
						runtimeDomain.getBankerRemarkName())) {
					playerModle.setIsBanker(Boolean.TRUE);
				}
				playerTableList.add(playerModle);
				runtimeDomain.putRunningPlayeres(pEntity.getRemarkName(),
						pEntity);
				i++;
			}
			flushRadioCol();
			resortTable(playerTableList);
			playerSizeLableChange();
		}
		
	}

	private void resortTable(ObservableList<PlayerModel> playerTableList) {
		SortedList<PlayerModel> sortedList = new SortedList<>(
				playerTableList, (PlayerModel p1, PlayerModel p2) -> {
					if (p1.getPlayerPoint() < p2.getPlayerPoint()) {
						return 1;
					}
					if (p1.getPlayerPoint() > p2.getPlayerPoint()) {
						return -1;
					}
					return 0;
				});
//		sortedList.comparatorProperty().bind(playerTab.comparatorProperty());
		playerTab.setItems(sortedList);
	}

	public void addNewPlayer(Player pEntity) {
		PlayerModel playerModle = new PlayerModel(0, pEntity.getRemarkName(),
				pEntity.getPoints(), pEntity.getWebchatId(),
				pEntity.getWechatName());
		ObservableList<PlayerModel> observableList = FXCollections.observableArrayList(playerTab.getItems());
	    observableList.add(playerModle);
		SortedList<PlayerModel> sortedList = new SortedList<>(
				observableList, (PlayerModel p1, PlayerModel p2) -> {
					if (p1.getPlayerPoint() < p2.getPlayerPoint()) {
						return 1;
					}
					if (p1.getPlayerPoint() > p2.getPlayerPoint()) {
						return -1;
					}
					return 0;
				});
		playerTab.setItems(sortedList);
		flushRadioCol();
	}

	private void playerSizeLableChange() {
		Platform.setImplicitExit(false);
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
				playerSizeLable.setText("玩家人数 :"
						+ String.valueOf(playerTab.getItems().size()));
		    }
		});
	}

	// private void fillPlayerTab() {
	// playerTab.getItems().clear();
	// PlayerModel playerModle = null;
	// Map<String, PlayerModel> currentPlayers = gameService
	// .getCurrentPlayers();
	// for (String remarkName : currentPlayers.keySet()) {
	// playerModle = currentPlayers.get(remarkName);
	// gameService.initialCurrentPlayer(playerModle);
	// if (playerModle.getPlayerName().equals(
	// runtimeDomain.getBankerRemarkName())) {
	// playerModle.setIsBanker(Boolean.TRUE);
	// }
	// playerTab.getItems().add(playerModle);
	// }
	// flushRadioCol();
	// }

	private void flushRadioCol() {
		Callback<TableColumn<PlayerModel, Boolean>, TableCell<PlayerModel, Boolean>> radioFactory = new Callback<TableColumn<PlayerModel, Boolean>, TableCell<PlayerModel, Boolean>>() {
			@Override
			public TableCell<PlayerModel, Boolean> call(
					TableColumn<PlayerModel, Boolean> p) {
				return new RadioButtonCell();
			}
		};
		colBankerSgin.setCellFactory(radioFactory);
		colBankerSgin
				.setCellValueFactory(new PropertyValueFactory<PlayerModel, Boolean>(
						PlayerModel.ISBANKERCOLKEY));
	}

	@FXML
	private void singleInfoSentOut(ActionEvent event) {
		Object selectedRow = singlePlayerInfoSend.getUserData();
		if (selectedRow == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("错误操作");
			alert.setContentText("请先在列表中选择一个玩家！");
			alert.showAndWait();
			return;
		}

		if (selectedRow instanceof PlayerModel) {
			PlayerModel pMode = (PlayerModel) selectedRow;
			String content = MessageFormat.format(AppUtils.SINGLEPLAYERINFO,
					pMode.getWechatName(), pMode.getPlayerPoint());
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("信息发送");
			alert.setHeaderText("确认发送");
			alert.setContentText(content);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.CANCEL) {
				return;
			}
			webWechat.webwxsendmsg(content);
		}
	}

	@FXML
	private void flushGroup(ActionEvent event) {
		Service<Integer> service = new Service<Integer>() {
			@Override
			protected Task<Integer> createTask() {
				return new Task<Integer>() {
					@Override
					protected Integer call() throws Exception {
						try {
							//webWechat.wxInit();
							webWechat.getContact();
							// webWechat.getGroupMembers();
							fillPlayerTab();
						} catch (Exception e) {
							LOGGER.error("wechart Group flush failed!", e);
						}
						return Integer.valueOf("0");
					}
				};
			};
		};
		service.start();
		service.setOnRunning((WorkerStateEvent we) -> {
			mainDeskController.openImageLoad();
		});
		service.setOnSucceeded((WorkerStateEvent we) -> {
			mainDeskController.closeImageLoad();
		});
	}

	@FXML
	private void savePlayerPoint(ActionEvent event) {
		Service<Integer> service = new Service<Integer>() {
			@Override
			protected Task<Integer> createTask() {
				return new Task<Integer>() {
					@Override
					protected Integer call() throws Exception {
						gameService.ryncPlayersPoint(playerTab.getItems());
						return Integer.valueOf("0");
					}
				};
			};
		};
		service.start();
		service.setOnRunning((WorkerStateEvent we) -> {
			mainDeskController.openImageLoad();
		});
		service.setOnSucceeded((WorkerStateEvent we) -> {
			mainDeskController.closeImageLoad();
			mainDeskController.fillUpGroupBox();
		});
	}

	@FXML
	private void publishRanks(ActionEvent event) {
		String content = gameService.publishPointRanks();
		if (content != null) {
			openMessageBoard(content);
		}
	}

	private void openMessageBoard(String content) {
		if (isInitializing) {
			return;
		}
		runtimeDomain.setSentOutMessage(new String[] { content });
		if (runtimeDomain.getMessageBoardCount() < 1) {
			stageManager.popMessageWindow(runtimeDomain);
			runtimeDomain.setMessageBoardCount(1);
		} else {
			messageController.changeMessage();
		}
	}

	/**
	 * auto rync player table
	 */
	private void playerAutoFlush() {
		if (playerFlushTask == null) {
			playerFlushTask = new Task<Void>() {
				@Override
				public Void call() {
					while (true) {
						try {
							Thread.sleep(AppUtils.PLAYER_TAB_FLSH_TERVAL);
							if (autoPlayerFlushContinue) {
								flushPlayerList();
							}
						} catch (Exception e) {
							LOGGER.error("player table auto change failed!", e);
						}

					}
				}
			};
			if (playerFlushThread == null) {
				playerFlushThread = new Thread(playerFlushTask);
				playerFlushThread.setDaemon(Boolean.TRUE);
				playerFlushThread.start();
			}
			LOGGER.debug("Auto player flush Thread start");
		}
	}

	private void flushPlayerList() {
		PlayerModel pModel = null;
		Player pEntity = null;
		ObservableList<PlayerModel> playerList = playerTab.getItems();
		if (playerList == null || playerList.size() < 1) {
			return;
		}
		for (int i = 0; i < playerList.size(); i++) {
			pModel = playerList.get(i);
			pModel.getPlayerName();
			pEntity = runtimeDomain.getRunningPlayeres().get(
					pModel.getPlayerName());
			if (pEntity != null) {
				pModel.setPlayerPoint(pEntity.getPoints() == null ? 0L
						: pEntity.getPoints());
				pModel.setPlayerName(pEntity.getRemarkName());
			}
		}
		resortTable(playerList);
		playerSizeLableChange();
	}

	private class RadioButtonCell extends TableCell<PlayerModel, Boolean> {

		private RadioButton radio;

		public RadioButtonCell() {
			createRadioButton();
		}

		private void createRadioButton() {
			radio = new RadioButton();
			radio.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0,
						Boolean before, Boolean now) {
					// game time no change
					if (runtimeDomain.getGlobalGameSignal()) {
						radio.setSelected(before);
						return;
					}
					if (now) {
						commitEdit(radio.isSelected());
					}
				}
			});
			radio.setToggleGroup(mainDeskController.getPlayerGroup());
		}

		@Override
		public void commitEdit(Boolean t) {
			super.commitEdit(t);
			final ObservableList<PlayerModel> items = getTableView().getItems();
			for (int i = 0; i < items.size(); i++) {
				PlayerModel playerModel = items.get(i);
				if (i == getIndex()) {
					playerModel.setIsBanker(t);
					runtimeDomain.setBankerRemarkName(playerModel
							.getPlayerName());
					mainDeskController.setCurrentBankSign(playerModel
							.getPlayerName());
					runtimeDomain.setBankerBetPoint(Long.valueOf(playerModel
							.getPlayerPoint()) * 2 / 3);
					mainDeskController.setBankerBetPoint(runtimeDomain
							.getBankerBetPoint());
				} else {
					playerModel.setIsBanker(Boolean.FALSE);
				}
			}
		}

		@Override
		public void updateItem(Boolean item, boolean empty) {
			super.updateItem(item, empty);
			final ObservableList<PlayerModel> items = getTableView().getItems();
			if (items != null && getIndex() > -1) {
				if (getIndex() < items.size()) {
					radio.setSelected(items.get(getIndex()).getIsBanker());
					setGraphic(radio);
				}
			}
		}
	}

	public ObservableList<PlayerModel> getPlayerList() {
		return playerTab.getItems();
	}

}
