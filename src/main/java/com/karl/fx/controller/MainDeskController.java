package com.karl.fx.controller;

import java.util.Map;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.db.domain.GameInfo;
import com.karl.db.domain.Player;
import com.karl.fx.FxmlView;
import com.karl.fx.model.ChatGroupModel;
import com.karl.fx.model.EditingCell;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.AppUtils;
import com.karl.utils.StringUtils;

@Component
@Lazy
public class MainDeskController extends FxmlController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MainDeskController.class);

	@FXML
	private ImageView imgLoad;

	@FXML
	private Button groupFlush;

	@FXML
	private Button syncPlayer;

	@FXML
	private ChoiceBox<ChatGroupModel> groupBox;

	@FXML
	private ChoiceBox<ChatGroupModel> groupBoxM;

	@FXML
	private Label groupSizeLable;

	@FXML
	private TableColumn<PlayerModel, Boolean> colBankerSgin;

	@FXML
	private TableColumn<PlayerModel, String> colPlayerName;

	@FXML
	private TableColumn<PlayerModel, String> colPlayerPoint;

	@FXML
	private TableView<PlayerModel> playerTab;

	private ObservableList<ChatGroupModel> groupList;

	private ObservableList<ChatGroupModel> groupListFiniance;

	private ObservableList<PlayerModel> playerList;

	@FXML
	private TextField bankerBetPoint;

	@FXML
	private TextField definedBet;

	@FXML
	private ToggleButton gameSingal;

	@FXML
	private Label bankerLabel;

	@FXML
	private Button openLotteryBut;

	@FXML
	private Button publishBut;

	@FXML
	private Button manualFlushBut;

	@FXML
	private Button cleanAllTraceBut;

	final ToggleGroup gameSingalGroup = new ToggleGroup();

	ToggleGroup playerGroup = new ToggleGroup();

	@FXML
	private ChoiceBox<String> gamekeyBox;

	@Autowired
	@Lazy
	private MessageController messageController;

	@Autowired
	@Lazy
	private GameRunningTabController gameRunningTabController;

	@FXML
	private TextField playerSearchText;

	@FXML
	private ProgressBar bar;

	private Thread playerFlushThread;

	private Task<Void> playerFlushTask;

	@FXML
	private Button undoGameButton;

	private Boolean autoPlayerFlushContinue;
	private Boolean isInitializing;

	@Override
	public void initialize() {
		imgLoad.setVisible(Boolean.TRUE);
		autoPlayerFlushContinue = Boolean.TRUE;
		isInitializing = Boolean.TRUE;
		buildGroupBox();
		buildPlayerTab();
		playerAutoFlush();
		buildGameKeyBox();
		buildGameQuicker();
		buildFilterPlayer();
		isInitializing = Boolean.FALSE;
		imgLoad.setVisible(Boolean.FALSE);
	}

	private void buildFilterPlayer() {
		playerSearchText
				.textProperty()
				.addListener(
						(ChangeListener<String>) (observable, oldVal, newVal) -> searchPlayer(
								oldVal, newVal));
	}

	@FXML
	private void confirmUndoGame(ActionEvent event) {
		if (runtimeDomain.getGlobalGameSignal()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("错误操作");
			alert.setContentText("请勿在开局过程中操作, 本操作用于恢复最近一次完成计算的游戏局！");
			alert.showAndWait();
			return;
		}

		if (runtimeDomain.getBeforeGameId() != null
				&& runtimeDomain.getBeforeGameId() > 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("作废 第【" + runtimeDomain.getBeforeGameId() + "】期");
			alert.setContentText("本操作用于恢复最近一次完成计算的游戏局！ 是否确定作废 第【"
					+ runtimeDomain.getBeforeGameId() + "】期？");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				GameInfo gameInfo = gameService.undoTheGame(runtimeDomain
						.getCurrentGameId());
				runtimeDomain.setBankerBetPoint(runtimeDomain
						.getBankerBetPoint() - gameInfo.getResultPoint());
				bankerBetPoint.setText(runtimeDomain.getBankerBetPoint()
						.toString());
				gameRunningTabController.cleanCurrentTrace();
			}
		}
	}

	private void searchPlayer(String oldVal, String newVal) {

		if (newVal == null || newVal.isEmpty()) {
			fillPlayerTab();
			return;
		}

		ObservableList<PlayerModel> filterPlayer = FXCollections
				.observableArrayList();
		for (PlayerModel playerModle : playerTab.getItems()) {
			if (playerModle.getPlayerName().matches(".*" + newVal + ".*")) {
				if (playerModle.getPlayerName().equals(
						runtimeDomain.getBankerRemarkName())) {
					playerModle.setIsBanker(Boolean.TRUE);
				}
				filterPlayer.add(playerModle);
			}
		}
		flushRadioCol();
		playerTab.setItems(filterPlayer);
	}

	private void buildGameQuicker() {
		gameSingal.setToggleGroup(gameSingalGroup);
		gameSingal.setUserData(Boolean.TRUE);
		gameSingal.setSelected(runtimeDomain.getGlobalGameSignal());
		if (runtimeDomain.getGlobalGameSignal()) {
			gameSingal.setText("结束");
		} else {
			gameSingal.setText("开局");
		}

		bankerBetPoint.setText(runtimeDomain.getBankerBetPoint().toString());
		bankerBetPoint.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!newValue.matches("\\d*")
						|| Long.valueOf(bankerBetPoint.getText()).compareTo(
								runtimeDomain
										.getRunningPlayeres()
										.get(runtimeDomain
												.getBankerRemarkName())
										.getPoints()) > 0) {
					bankerBetPoint.setText(oldValue);
				} else {
					bankerBetPoint.setText(newValue);
					runtimeDomain.setBankerBetPoint(Long.valueOf(bankerBetPoint
							.getText()));
				}
			}
		});

		setCurrentBankSign(runtimeDomain.getBankerRemarkName());
		gameSingalGroup.selectedToggleProperty().addListener(
				new ChangeListener<Toggle>() {
					public void changed(ObservableValue<? extends Toggle> ov,
							Toggle toggle, Toggle new_toggle) {
						if (isInitializing) {
							return;
						}

						Boolean selected = Boolean.FALSE;
						if (new_toggle != null) {
							selected = (Boolean) new_toggle.getUserData();
						}

						if (selected) {
							if (runtimeDomain.getBankerRemarkName() == null
									|| runtimeDomain.getBankerRemarkName()
											.isEmpty()) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("错误操作");
								alert.setContentText("请先选择庄家，再开局！");
								alert.showAndWait();
								gameSingal.setSelected(Boolean.FALSE);
								return;
							}
							if (runtimeDomain.getBankerBetPoint().compareTo(
									Long.valueOf(0)) <= 0) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("错误操作");
								alert.setContentText("请确认庄家的上庄积分大于0，再开局！");
								alert.showAndWait();
								gameSingal.setSelected(Boolean.FALSE);
								return;
							}
						}

						runtimeDomain.setGlobalGameSignal(selected);
						// start/end game, the view actions
						if (runtimeDomain.getGlobalGameSignal()) {
							gameService.ryncPlayersPoint(playerList);
							startGameViewAction();
							gameRunningTabController.gameStartFlush();
							gameSingal.setText("结束");
							openMessageBoard(gameService.declareGame());
						} else {
							gameSingal.setText("开局");
							if (runtimeDomain.getBankerRemarkName() == null
									|| runtimeDomain.getBankerRemarkName()
											.isEmpty()) {
								return;
							}
							if (runtimeDomain.getBankerBetPoint().compareTo(
									Long.valueOf(0)) <= 0) {
								return;
							}
							endGameViewAction();
							if (!AppUtils.PLAYLUCKWAY.equals(runtimeDomain
									.getCurrentGameKey())) {
								openMessageBoard(gameService.declareGame());
							}
						}

					}
				});

		definedBet.setText(runtimeDomain.getDefiendBet().toString());
		definedBet.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					definedBet.setText(oldValue);
				} else {
					definedBet.setText(newValue);
					runtimeDomain.setDefiendBet(Long.valueOf(definedBet
							.getText()));
				}
			}
		});
	}

	private void setCurrentBankSign(String bankerName) {
		bankerLabel.setText("当前庄家： 【 " + bankerName + "】");
	}

	private void buildGameKeyBox() {
		gamekeyBox.setItems(FXCollections
				.observableArrayList(AppUtils.PLAYLONG, AppUtils.PLAYLONGSPLIT,
						AppUtils.PLAYLUCKWAY));
		gamekeyBox.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(
							ObservableValue<? extends String> paramObservableValue,
							String paramT1, String newValue) {
						if (newValue != null && !newValue.isEmpty()) {
							runtimeDomain.setCurrentGameKey(newValue);
						}
					}
				});

		gamekeyBox.setTooltip(new Tooltip("请选择玩法"));
		for (int i = 0; i < gamekeyBox.getItems().size(); i++) {
			if (gamekeyBox.getItems().get(i)
					.equals(runtimeDomain.getCurrentGameKey())) {
				gamekeyBox.getSelectionModel().select(i);
				break;
			}
		}
	}

	private void buildGroupBox() {
		groupBox.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<ChatGroupModel>() {
					@Override
					public void changed(
							ObservableValue<? extends ChatGroupModel> observable,
							ChatGroupModel oldValue, ChatGroupModel newValue) {
						if (newValue != null
								&& !newValue.getGroupId().equals(
										String.valueOf(0))) {
							runtimeDomain.setCurrentGroupId(newValue
									.getGroupId());
							runtimeDomain.setCurrentGroupName(newValue
									.getGroupName());
							groupSizeLable.setText("群人数 :"
									+ String.valueOf(newValue.getGroupSize()));
							fillPlayerTab();
						}
					}
				});
		groupBoxM.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<ChatGroupModel>() {
					@Override
					public void changed(
							ObservableValue<? extends ChatGroupModel> observable,
							ChatGroupModel oldValue, ChatGroupModel newValue) {
						if (newValue != null
								&& !newValue.getGroupId().equals(
										String.valueOf(0))) {
							runtimeDomain.setCurrentMGroupId(newValue
									.getGroupId());
						}
					}
				});

		fillUpGroupBox();
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
		Callback<TableColumn<PlayerModel, String>, TableCell<PlayerModel, String>> cellFactory = new Callback<TableColumn<PlayerModel, String>, TableCell<PlayerModel, String>>() {
			public TableCell<PlayerModel, String> call(
					TableColumn<PlayerModel, String> p) {
				return new EditingCell<PlayerModel>();
			}
		};
		colPlayerPoint.setEditable(Boolean.TRUE);
		colPlayerPoint
				.setCellValueFactory(new PropertyValueFactory<PlayerModel, String>(
						PlayerModel.PLAYERPOINTCOLKEY));
		colPlayerPoint.setCellFactory(cellFactory);
		colPlayerPoint
				.setOnEditCommit(new EventHandler<CellEditEvent<PlayerModel, String>>() {
					@Override
					public void handle(CellEditEvent<PlayerModel, String> cell) {
						PlayerModel pModel = cell.getTableView().getItems()
								.get(cell.getTablePosition().getRow());
						if (!StringUtils.matchLong(cell.getNewValue())) {
							flushPlayerList();
							autoPlayerFlushContinue = Boolean.TRUE;
							return;
						}
						autoPlayerFlushContinue = Boolean.FALSE;
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
		fillPlayerTab();
	}

	private void fillPlayerTab() {
		if (playerList != null) {
			playerList.clear();
		}
		playerList = runtimeDomain.getPlayerList();
		runtimeDomain.getRunningPlayeres().clear();
		PlayerModel playerModle = null;
		Map<String, PlayerModel> currentPlayers = runtimeDomain
				.getCurrentPlayers();
		for (String remarkName : currentPlayers.keySet()) {
			playerModle = currentPlayers.get(remarkName);
			gameService.initialCurrentPlayer(playerModle);
			if (playerModle.getPlayerName().equals(
					runtimeDomain.getBankerRemarkName())) {
				playerModle.setIsBanker(Boolean.TRUE);
			}
			playerList.add(playerModle);
		}

		flushRadioCol();
		playerTab.setItems(playerList);

	}

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

	private void fillUpGroupBox() {
		if (groupList != null)
			groupList.clear();
		if (groupListFiniance != null) {
			groupListFiniance.clear();
		}

		groupList = runtimeDomain.getGroupList();
		groupListFiniance = runtimeDomain.getGroupListFiniance();
		ChatGroupModel groupModel = null;
		int i = 1;
		int selected = 0;
		int selectedM = 0;
		groupList.add(new ChatGroupModel(String.valueOf(0), "请选择玩家群", 0));
		groupListFiniance
				.add(new ChatGroupModel(String.valueOf(0), "请选择财务群", 0));
		for (String groupId : runtimeDomain.getGroupMap().keySet()) {
			groupModel = new ChatGroupModel(groupId, runtimeDomain
					.getGroupMap().get(groupId).getString("NickName")
					.replaceAll("</?[^>]+>", ""), runtimeDomain.getGroupMap()
					.get(groupId).getJSONArray("MemberList").size());
			if (groupId.equals(runtimeDomain.getCurrentGroupId())) {
				selected = i;
			}
			if (groupId.equals(runtimeDomain.getCurrentMGroupId())) {
				selectedM = i;
			}
			groupList.add(groupModel);
			groupListFiniance.add(groupModel);
			i++;
		}
		groupBox.setItems(groupList);
		groupBoxM.setItems(groupListFiniance);
		groupBox.getSelectionModel().select(selected);
		groupBoxM.getSelectionModel().select(selectedM);
	}

	@FXML
	private void flushGroup(ActionEvent event) {
		imgLoad.setVisible(true);
		try {
			webWechat.wxInit();
			webWechat.getContact();
			// webWechat.getGroupMembers();
			fillUpGroupBox();
		} catch (Exception e) {
			LOGGER.error("wechart Group flush failed!", e);
		} finally {
			imgLoad.setVisible(false);
		}
	}

	@FXML
	private void savePlayerPoint(ActionEvent event) {
		Service<Integer> service = new Service<Integer>() {
			@Override
			protected Task<Integer> createTask() {
				return new Task<Integer>() {
					@Override
					protected Integer call() throws Exception {
						gameService.ryncPlayersPoint(playerList);
						return Integer.valueOf("0");
					}
				};
			};
		};
		service.start();
		bar.progressProperty().bind(service.progressProperty());
		service.setOnRunning((WorkerStateEvent we) -> {
			imgLoad.setVisible(true);
		});
		service.setOnSucceeded((WorkerStateEvent we) -> {
			imgLoad.setVisible(false);
		});
	}

	@FXML
	private void openLottery(ActionEvent event) {
		if (runtimeDomain.getGlobalGameSignal()
				&& !AppUtils.PLAYLUCKWAY.equals(runtimeDomain
						.getCurrentGameKey())) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("错误操作");
			alert.setContentText("请先手动点击结束， 确认当前局数据收集完成！");
			alert.showAndWait();
			return;
		}
		if (runtimeDomain.getCurrentGameId() == null
				|| runtimeDomain.getCurrentGameId().compareTo(Long.valueOf(0)) <= 0) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("错误操作");
			alert.setContentText("无可用计算的数据，请确定是否已完成开局和包数据采集！");
			alert.showAndWait();
			return;
		}

		if (runtimeDomain.getBeforeGameId() != null
				&& runtimeDomain.getBeforeGameId().compareTo(
						runtimeDomain.getCurrentGameId()) == 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("重复操作");
			alert.setContentText("本局已完成计算，继续操作会导致重复扣分。是否继续？");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.CANCEL) {
				return;
			}
		}

		GameInfo gameInfo = gameService.getGameById(runtimeDomain
				.getCurrentGameId());
		if (gameInfo == null) {
			return;
		}
		if (gameInfo.getLuckTime() == null
				|| gameInfo.getLuckTime().compareTo(Long.valueOf(0)) <= 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("庄家无包信息");
			alert.setContentText("庄家无包信息，继续操作仅仅扣除管理费。是否继续？");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.CANCEL) {
				return;
			}
		}

		if (AppUtils.PLAYLUCKWAY.equals(runtimeDomain.getCurrentGameKey())) {
			gameSingal.setSelected(Boolean.FALSE);
		}

		// DO it
		String content = gameService.openLottery();
		openMessageBoard(content);
		bankerBetPoint
				.setText(runtimeDomain.getBankerBetPoint() > 0 ? runtimeDomain
						.getBankerBetPoint().toString() : "0");
		runtimeDomain.setBeforeGameId(runtimeDomain.getCurrentGameId());
		gameRunningTabController.flushResult();
	}

	@FXML
	private void publishRanks(ActionEvent event) {
		String content = gameService.publishPointRanks();
		if (content != null) {
			openMessageBoard(content);
		}
	}

	@FXML
	private void manuallyFlushTraceTab(ActionEvent event) {
		Service<Integer> service = new Service<Integer>() {
			@Override
			protected Task<Integer> createTask() {
				return new Task<Integer>() {
					@Override
					protected Integer call() throws Exception {
						gameRunningTabController.manuallyFlush();
						return Integer.valueOf("0");
					}
				};
			};
		};
		service.start();
		bar.progressProperty().bind(service.progressProperty());
		service.setOnRunning((WorkerStateEvent we) -> {
			imgLoad.setVisible(true);
		});
		service.setOnSucceeded((WorkerStateEvent we) -> {
			imgLoad.setVisible(false);
		});
	}

	@FXML
	private void cleanAllTrace(ActionEvent event) {
		if (!runtimeDomain.getShowManageFee()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("错误操作");
			alert.setContentText("仅能在开局过程中使用，用于清空本局的所有下注和包信息！");
			alert.showAndWait();
			return;
		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("确认操作");
		alert.setContentText("本操作会清空本局所有的下注和包信息。是否继续？");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.CANCEL) {
			return;
		}

		// clean player && banker
		gameService.cleanTraceInfo(runtimeDomain.getCurrentGameId());
		gameRunningTabController.flushLuckInfo();

		// clean time
		runtimeDomain.removeCurrentFirstPacageTime();
		runtimeDomain.removeCurrentLastPackegeTime();
	}

	private void openMessageBoard(String content) {
		if (isInitializing) {
			return;
		}
		runtimeDomain.setSentOutMessage(content);
		if (runtimeDomain.getMessageBoardCount() < 1) {
			popMessageWindow();
			runtimeDomain.setMessageBoardCount(1);
		} else {
			messageController.changeMessage();
		}
	}

	private void popMessageWindow() {
		Scene scene = new Scene(
				stageManager.loadViewNodeHierarchy(FxmlView.MESSAGE
						.getFxmlFile()));
		Stage newStage = new Stage();
		// newStage.initStyle(StageStyle.UNIFIED);
		newStage.setTitle(FxmlView.MESSAGE.getTitle());
		newStage.initModality(Modality.NONE);
		newStage.initOwner(stageManager.getPrimaryStage());
		newStage.setScene(scene);
		newStage.sizeToScene();
		newStage.centerOnScreen();
		try {
			newStage.show();
		} catch (Exception e) {
			LOGGER.error(
					"Uable to show scene for title "
							+ FxmlView.MESSAGE.getTitle(), e);
		}

		newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				runtimeDomain.setMessageBoardCount(0);
			}
		});
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
			LOGGER.info("Auto player flush Thread start");
		}
	}

	private void flushPlayerList() {
		synchronized (this) {
			PlayerModel pModel = null;
			Player pEntity = null;
			if (playerList == null || playerList.size() < 1) {
				return;
			}
			for (int i = 0; i < playerList.size(); i++) {
				pModel = playerList.get(i);
				pModel.getPlayerName();
				pEntity = runtimeDomain.getRunningPlayeres().get(
						pModel.getPlayerName());
				if (pEntity != null) {
					pModel.setPlayerPoint(String
							.valueOf(pEntity.getPoints() == null ? 0 : pEntity
									.getPoints()));
					pModel.setPlayerName(pEntity.getRemarkName());
				}
			}
		}
	}

	private void startGameViewAction() {
		PlayerModel pModel = null;
		for (int i = 0; i < playerList.size(); i++) {
			pModel = playerList.get(i);
			pModel.getPlayerName();
			pModel.setPlayerLatestBet(AppUtils.NONEBET);
		}
		groupBox.setDisable(runtimeDomain.getGlobalGameSignal());
		groupFlush.setDisable(runtimeDomain.getGlobalGameSignal());
		syncPlayer.setDisable(runtimeDomain.getGlobalGameSignal());
		bankerBetPoint.setEditable(Boolean.FALSE);
		definedBet.setEditable(Boolean.FALSE);
	}

	private void endGameViewAction() {
		groupBox.setDisable(runtimeDomain.getGlobalGameSignal());
		groupFlush.setDisable(runtimeDomain.getGlobalGameSignal());
		syncPlayer.setDisable(runtimeDomain.getGlobalGameSignal());
		bankerBetPoint.setEditable(Boolean.TRUE);
		definedBet.setEditable(Boolean.TRUE);
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
			radio.setToggleGroup(playerGroup);
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
					setCurrentBankSign(playerModel.getPlayerName());
					runtimeDomain.setBankerBetPoint(Long.valueOf(playerModel
							.getPlayerPoint()) * 2 / 3);
					bankerBetPoint.setText(runtimeDomain.getBankerBetPoint()
							.toString());
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

}
