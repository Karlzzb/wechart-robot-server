package com.karl.fx.controller;

import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.db.domain.GameInfo;
import com.karl.fx.model.ChatGroupModel;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.AppUtils;

@Component
@Lazy
public class MainDeskController extends FxmlController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MainDeskController.class);

	@FXML
	private ImageView imgLoad;

	@FXML
	private Button clearBankerBut;

	@FXML
	private ChoiceBox<ChatGroupModel> groupBox;

	@FXML
	private ChoiceBox<ChatGroupModel> groupBoxM;
	
	@FXML
	private ChoiceBox<ChatGroupModel> groupBoxSentor;

	@FXML
	private ChoiceBox<ChatGroupModel> groupBoxSentorM;

	@FXML
	private TextField bankerBetPoint;

	@FXML
	private TextField definedBet;

	@FXML
	private Button gameSingal;

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

	ToggleGroup playerGroup = new ToggleGroup();

	@Autowired
	@Lazy
	private MessageController messageController;

	@Autowired
	@Lazy
	private PlayerTableController playerTableController;

	@Autowired
	@Lazy
	private GameRunningTabController gameRunningTabController;

	@FXML
	private Button undoGameButton;

	@FXML
	private Button singlePlayerInfoSend;

	private Boolean isInitializing;

	@Override
	public void initialize() {
		isInitializing = Boolean.TRUE;
		buildGroupBox();
		buildGroupBoxSentor();
		buildGameQuicker();
		isInitializing = Boolean.FALSE;
	}

	@FXML
	private void clearBanker(ActionEvent event) {
		if (runtimeDomain.getGlobalGameSignal()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("错误操作");
			alert.setContentText("请勿在开局过程中操作, 本操作用于清空当前庄家信息！");
			alert.showAndWait();
			return;
		}
		runtimeDomain.setBankerBetPoint(Long.valueOf(0));
		this.bankerBetPoint.setText("0");
		runtimeDomain.setBankerRemarkName(null);
		bankerLabel.setText("当前庄家： 【 】");
		playerGroup.selectToggle(null);
		gameRunningTabController.cleanCurrentTrace();
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

		if (runtimeDomain.getBeforeGameInfo() == null) {
			Alert alert2 = new Alert(AlertType.WARNING);
			alert2.setTitle("错误操作");
			alert2.setContentText("未找到上局信息，请确认已完成计算操作！");
			alert2.showAndWait();
			return;
		}

		if (runtimeDomain.getBeforeGameInfo() != null
				&& runtimeDomain.getBeforeGameInfo().getIsUndo()) {
			Alert alert2 = new Alert(AlertType.WARNING);
			alert2.setTitle("错误操作");
			alert2.setContentText("第【"
					+ runtimeDomain.getBeforeGameInfo().getGameSerialNo()
					+ "】期 已作废，请勿重复操作！");
			alert2.showAndWait();
			return;
		}

		if (runtimeDomain.getBeforeGameInfo() != null
				&& runtimeDomain.getBeforeGameInfo().getGameSerialNo() > 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("作废 第【"
					+ runtimeDomain.getBeforeGameInfo().getGameSerialNo()
					+ "】期");
			alert.setContentText("本操作用于恢复最近一次完成计算的游戏局！ 是否确定作废 第【"
					+ runtimeDomain.getBeforeGameInfo().getGameSerialNo()
					+ "】期？");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				GameInfo gameInfo = gameService.undoTheGame(runtimeDomain
						.getBeforeGameInfo());
				runtimeDomain.setBankerBetPoint(runtimeDomain
						.getBankerBetPoint() - gameInfo.getResultPoint());
				bankerBetPoint.setText(runtimeDomain.getBankerBetPoint()
						.toString());
				gameRunningTabController.cleanCurrentTrace();
				runtimeDomain.setBeforeGameInfo(gameInfo);
			}
		}
	}

	private void buildGameQuicker() {
		if (runtimeDomain.getGlobalGameSignal()) {
			gameSingal.getStyleClass().clear();
			gameSingal.getStyleClass().add("end-button");
			gameSingal.setText("结束");
		} else {
			gameSingal.getStyleClass().clear();
			gameSingal.getStyleClass().add("start-button");
			gameSingal.setText("开局");
		}

		gameSingal.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				gameSingalHandle();
			}
		});

		bankerBetPoint.setText(runtimeDomain.getBankerBetPoint().toString());
		bankerBetPoint.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (bankerBetPoint.getText() == null
						|| bankerBetPoint.getText().isEmpty()
						|| !newValue.matches("\\d*")
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

	private void gameSingalHandle() {
		Boolean currentGameSignal = !runtimeDomain.getGlobalGameSignal();
		// game start condition check
		if (currentGameSignal) {
			if (runtimeDomain.getBankerRemarkName() == null
					|| runtimeDomain.getBankerRemarkName().isEmpty()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("错误操作");
				alert.setContentText("请先选择庄家，再开局！");
				alert.showAndWait();
				return;
			}
			if (runtimeDomain.getBankerBetPoint().compareTo(Long.valueOf(0)) <= 0) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("错误操作");
				alert.setContentText("请确认庄家的上庄积分大于0，再开局！");
				alert.showAndWait();
				return;
			}
		}

		if (runtimeDomain.getCurrentGroupId() == null
				|| runtimeDomain.getCurrentGroupId().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("错误操作");
			alert.setContentText("请先选择玩家群，再开局！");
			alert.showAndWait();
			return;
		}

		// change the game status
		runtimeDomain.setGlobalGameSignal(currentGameSignal);
		// start/end game, the view actions
		if (runtimeDomain.getGlobalGameSignal()) {
			gameService.ryncPlayersPoint(playerTableController.getPlayerList());
			startGameViewAction();
			gameSingal.getStyleClass().clear();
			gameSingal.getStyleClass().add("end-button");
			gameSingal.setText("结束");
			openMessageBoard(gameService.declareGame());
			gameRunningTabController.gameStartFlush();
		} else {
			gameSingal.setText("开局");
			gameSingal.getStyleClass().clear();
			gameSingal.getStyleClass().add("start-button");
			endGameViewAction();
			if (!AppUtils.PLAYLUCKWAY.equals(runtimeDomain.getCurrentGameKey())) {
				openMessageBoard(gameService.declareGame());
			}
		}
	}

	public void setCurrentBankSign(String bankerName) {
		bankerLabel.setText("当前庄家： 【 " + bankerName + "】");
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
							// fillPlayerTab();
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

	public void fillUpGroupBox() {
		ObservableList<ChatGroupModel> groupList = groupBox.getItems();
		ObservableList<ChatGroupModel> groupListFiniance = groupBoxM.getItems();

		if (groupList != null)
			groupList.clear();
		if (groupListFiniance != null) {
			groupListFiniance.clear();
		}
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
		groupBox.getSelectionModel().select(selected);
		groupBoxM.getSelectionModel().select(selectedM);
	}
	
	private void buildGroupBoxSentor() {
		groupBoxSentor.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<ChatGroupModel>() {
					@Override
					public void changed(
							ObservableValue<? extends ChatGroupModel> observable,
							ChatGroupModel oldValue, ChatGroupModel newValue) {
						if (newValue != null
								&& !newValue.getGroupId().equals(
										String.valueOf(0))) {
							sentorDomain.setCurrentGroupId(newValue
									.getGroupId());
							sentorDomain.setCurrentGroupName(newValue
									.getGroupName());
						}
					}
				});
		groupBoxSentorM.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<ChatGroupModel>() {
					@Override
					public void changed(
							ObservableValue<? extends ChatGroupModel> observable,
							ChatGroupModel oldValue, ChatGroupModel newValue) {
						if (newValue != null
								&& !newValue.getGroupId().equals(
										String.valueOf(0))) {
							sentorDomain.setCurrentMGroupId(newValue
									.getGroupId());
						}
					}
				});

		fillUpGroupBoxSentor();
	}
	
	public void fillUpGroupBoxSentor() {
		ObservableList<ChatGroupModel> groupList = groupBoxSentor.getItems();
		ObservableList<ChatGroupModel> groupListFiniance = groupBoxSentorM.getItems();

		if (groupList != null)
			groupList.clear();
		if (groupListFiniance != null) {
			groupListFiniance.clear();
		}
		ChatGroupModel groupModel = null;
		int i = 1;
		int selected = 0;
		int selectedM = 0;
		groupList.add(new ChatGroupModel(String.valueOf(0), "请选择玩家群", 0));
		groupListFiniance
				.add(new ChatGroupModel(String.valueOf(0), "请选择财务群", 0));
		for (String groupId : sentorDomain.getGroupMap().keySet()) {
			groupModel = new ChatGroupModel(groupId, sentorDomain
					.getGroupMap().get(groupId).getString("NickName")
					.replaceAll("</?[^>]+>", ""), sentorDomain.getGroupMap()
					.get(groupId).getJSONArray("MemberList").size());
			if (groupId.equals(sentorDomain.getCurrentGroupId())) {
				selected = i;
			}
			if (groupId.equals(sentorDomain.getCurrentMGroupId())) {
				selectedM = i;
			}
			groupList.add(groupModel);
			groupListFiniance.add(groupModel);
			i++;
		}
		groupBoxSentor.getSelectionModel().select(selected);
		groupBoxSentorM.getSelectionModel().select(selectedM);
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

		if (runtimeDomain.getBeforeGameInfo() != null
				&& runtimeDomain.getBeforeGameInfo().getGameSerialNo()
						.compareTo(runtimeDomain.getCurrentGameId()) == 0) {
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
			alert.setContentText("庄家无包信息，继续操作仅仅扣除管理费。是否按照超时处理，默认点数牛一？");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.CANCEL) {
				return;
			}
			gameInfo.setLuckInfo(0.01);
			gameInfo.setLuckTime(runtimeDomain.getCurrentLastPackegeTime()
					.getTime() + runtimeDomain.getCurrentTimeOut());
		}

		if (AppUtils.PLAYLUCKWAY.equals(runtimeDomain.getCurrentGameKey())
				&& runtimeDomain.getGlobalGameSignal()) {
			gameSingalHandle();
		}

		// DO it
		String content = gameService.openLottery();
		openMessageBoard(new String[] { content,
				gameService.publishPointRanks() });
		bankerBetPoint
				.setText(runtimeDomain.getBankerBetPoint() > 0 ? runtimeDomain
						.getBankerBetPoint().toString() : "0");
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
		gameRunningTabController.clearLuckInfo();

		// clean time
		runtimeDomain.removeCurrentFirstPacageTime();
		runtimeDomain.removeCurrentLastPackegeTime();
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

	private void openMessageBoard(String[] content) {
		if (isInitializing) {
			return;
		}
		runtimeDomain.setSentOutMessage(content);
		if (runtimeDomain.getMessageBoardCount() < 1) {
			stageManager.popMessageWindow(runtimeDomain);
			runtimeDomain.setMessageBoardCount(1);
		} else {
			messageController.changeMessage();
		}
	}

	private void startGameViewAction() {
		PlayerModel pModel = null;
		ObservableList<PlayerModel> playerList = playerTableController
				.getPlayerList();
		for (int i = 0; i < playerList.size(); i++) {
			pModel = playerList.get(i);
			pModel.getPlayerName();
			pModel.setPlayerLatestBet(AppUtils.NONEBET);
		}
		groupBox.setDisable(runtimeDomain.getGlobalGameSignal());
		bankerBetPoint.setEditable(Boolean.FALSE);
		definedBet.setEditable(Boolean.FALSE);
	}

	private void endGameViewAction() {
		groupBox.setDisable(runtimeDomain.getGlobalGameSignal());
		bankerBetPoint.setEditable(Boolean.TRUE);
		definedBet.setEditable(Boolean.TRUE);
	}

	public void openImageLoad() {
		imgLoad.setVisible(true);
	}

	public void closeImageLoad() {
		imgLoad.setVisible(false);
		fillUpGroupBox();
	}

	public ToggleGroup getPlayerGroup() {
		return playerGroup;
	}

	public void setPlayerGroup(ToggleGroup playerGroup) {
		this.playerGroup = playerGroup;
	}

	public void setBankerBetPoint(Long betPoint) {
		bankerBetPoint.setText(betPoint.toString());
	}
}
