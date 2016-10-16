package com.karl.fx.controller;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
	private ToggleButton gameStart;

	@FXML
	private ToggleButton gameEnd;

	@FXML
	private Label bankerLabel;

	@FXML
	private Button openLotteryBut;

	@FXML
	private Button publishBut;

	final ToggleGroup group = new ToggleGroup();

	ToggleGroup playerGroup = new ToggleGroup();

	@FXML
	private ChoiceBox<String> gamekeyBox;

	@Autowired
	@Lazy
	private MessageController messageController;

	@FXML
	private TextField playerSearchText;

	@FXML
	private ProgressBar bar;

	private Thread playerFlushThread;

	private Task<Void> playerFlushTask;

	private Boolean autoPlayerFlushContinue;

	@Override
	public void initialize() {
		autoPlayerFlushContinue = Boolean.TRUE;
		buildGroupBox();
		buildPlayerTab();
		playerAutoFlush();
		buildGameKeyBox();
		buildGameQuicker();
		buildFilterPlayer();
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
		gameStart.setToggleGroup(group);
		gameEnd.setToggleGroup(group);
		gameStart.setUserData(Boolean.TRUE);
		gameEnd.setUserData(Boolean.FALSE);
		gameStart.setSelected(runtimeDomain.getGlobalGameSignal());
		gameEnd.setSelected(runtimeDomain.getGlobalGameSignal() ? Boolean.FALSE
				: Boolean.TRUE);
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
		group.selectedToggleProperty().addListener(
				new ChangeListener<Toggle>() {
					public void changed(ObservableValue<? extends Toggle> ov,
							Toggle toggle, Toggle new_toggle) {
						if (new_toggle == null) {
							runtimeDomain.setGlobalGameSignal(Boolean.FALSE);
						} else {
							runtimeDomain.setGlobalGameSignal((Boolean) group
									.getSelectedToggle().getUserData());
							// save current player when game started
							if ((Boolean) group.getSelectedToggle()
									.getUserData()) {
								gameService.ryncPlayersPoint(playerList);
							}
						}

						// start/end game, the view actions
						if (runtimeDomain.getGlobalGameSignal()) {
							startGameViewAction();
							openMessageBoard(gameService.declareGame());
						} else {
							endGameViewAction();
							openMessageBoard(gameService.declareGame());
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

		Callback<TableColumn<PlayerModel, String>, TableCell<PlayerModel, String>> cellFactory = new Callback<TableColumn<PlayerModel, String>, TableCell<PlayerModel, String>>() {
			public TableCell<PlayerModel, String> call(
					TableColumn<PlayerModel, String> p) {
				return new EditingCell();
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
						if (!StringUtils.matchLong(cell.getNewValue())) {
							return;
						}
						autoPlayerFlushContinue = Boolean.FALSE;
						PlayerModel pModel = cell.getTableView().getItems()
								.get(cell.getTablePosition().getRow());
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
		webWechat.wxInit();
		webWechat.getContact();
		// webWechat.getGroupMembers();
		fillUpGroupBox();
	}

	@FXML
	private void savePlayerPoint(ActionEvent event) {
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				gameService.ryncPlayersPoint(playerList);
				return null;
			};
		};
		service.start();
		bar.progressProperty().bind(service.progressProperty());
	}

	@FXML
	private void openLottery(ActionEvent event) {
		String content = gameService.openLottery();
		openMessageBoard(content);
		bankerBetPoint
				.setText(runtimeDomain.getBankerBetPoint() > 0 ? runtimeDomain
						.getBankerBetPoint().toString() : "0");
	}

	@FXML
	private void publishRanks(ActionEvent event) {
		String content = gameService.publishPointRanks();
		if (content != null) {
			openMessageBoard(content);
		}
	}

	private void openMessageBoard(String content) {
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
					while (autoPlayerFlushContinue) {
						try {
							Thread.sleep(AppUtils.PLAYER_TAB_FLSH_TERVAL);
							PlayerModel pModel = null;
							Player pEntity = null;
							if (playerList == null || playerList.size() < 1) {
								continue;
							}
							for (int i = 0; i < playerList.size(); i++) {
								pModel = playerList.get(i);
								pModel.getPlayerName();
								pEntity = runtimeDomain.getRunningPlayeres()
										.get(pModel.getPlayerName());
								if (pEntity != null) {
									pModel.setPlayerPoint(String
											.valueOf(pEntity.getPoints() == null ? 0
													: pEntity.getPoints()));
								}
							}
						} catch (Exception e) {
							LOGGER.error("player table auto change failed!", e);
						}

					}
					return null;
				}
			};
			if (playerFlushThread == null) {
				playerFlushThread = new Thread(playerFlushTask);
				playerFlushThread.setDaemon(Boolean.TRUE);
				playerFlushThread.start();
			}
			LOGGER.info("Auto player Thread start");
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
