package com.karl.fx.controller;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.fx.model.ChatGroupModel;
import com.karl.fx.model.EditingCell;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.StringUtils;

@Component
@Lazy
public class MainDeskController extends FxmlController {

	@FXML
	private Button groupFlush;

	@FXML
	private Button syncPlayer;

	@FXML
	private ChoiceBox<ChatGroupModel> groupBox;

	@FXML
	private Label groupSizeLable;
	
	@FXML
	private TableColumn<PlayerModel, Boolean> colBankerSgin;

	@FXML
	private TableColumn<PlayerModel, Integer> colAutoID;

	@FXML
	private TableColumn<PlayerModel, String> colPlayerName;

	@FXML
	private TableColumn<PlayerModel, String> colPlayerPoint;
	
	@FXML
	private TableColumn<PlayerModel, String> colPlayerLatestBet;

	@FXML
	private TableView<PlayerModel> playerTab;

	private ObservableList<ChatGroupModel> groupList;

	private ObservableList<PlayerModel> playerList;

	@FXML
	private TextArea messageBoard;

	@FXML
	private Button msgSendBut;

	@FXML
	private ToggleButton gameStart;

	@FXML
	private ToggleButton gameEnd;

	@FXML
	private Label bankerLabel;

	final ToggleGroup group = new ToggleGroup();
	
	final ToggleGroup playerGroup = new ToggleGroup();

	@Override
	public void initialize() {
		gameStart.setToggleGroup(group);
		gameEnd.setToggleGroup(group);
		gameStart.setUserData(Boolean.TRUE);
		gameEnd.setUserData(Boolean.FALSE);
		gameStart.setSelected(runtimeDomain.getGlobalGameSignal());
		gameEnd
				.setSelected(runtimeDomain.getGlobalGameSignal() ? Boolean.FALSE
						: Boolean.TRUE);
		group.selectedToggleProperty().addListener(
				new ChangeListener<Toggle>() {
					public void changed(ObservableValue<? extends Toggle> ov,
							Toggle toggle, Toggle new_toggle) {
						if (new_toggle == null) {
							runtimeDomain.setGlobalGameSignal(Boolean.FALSE);
						} else {
							runtimeDomain.setGlobalGameSignal((Boolean) group
									.getSelectedToggle().getUserData());
							 messageBoard.setText(gameService.declareGame());
						}
					}
				});

		buildGroupBox();
		buildPlayerTab();
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
							runtimeDomain.setCurrentGroupName(newValue.getGroupName());
							groupSizeLable.setText("群人数 :"
									+ String.valueOf(newValue.getGroupSize()));
							fillPlayerTab();
						}
					}
				});
		fillUpGroupBox();
	}

	private void buildPlayerTab() {
		playerTab.setEditable(true);

		Callback<TableColumn<PlayerModel, String>, TableCell<PlayerModel, String>> cellFactory = new Callback<TableColumn<PlayerModel, String>, TableCell<PlayerModel, String>>() {
			public TableCell<PlayerModel, String> call(
					TableColumn<PlayerModel, String> p) {
				return new EditingCell();
			}
		};
		
	   Callback<TableColumn<PlayerModel, Boolean>, TableCell<PlayerModel, Boolean>> radioFactory = new Callback<TableColumn<PlayerModel, Boolean>, TableCell<PlayerModel, Boolean>>() {
            @Override
            public TableCell<PlayerModel, Boolean> call(TableColumn<PlayerModel, Boolean> p) {
                return new RadioButtonCell();
            }
        };
        colBankerSgin.setCellFactory(radioFactory);

		colAutoID
				.setCellValueFactory(new PropertyValueFactory<PlayerModel, Integer>(
						PlayerModel.AUDOIDCOLKEY));
		colPlayerName
				.setCellValueFactory(new PropertyValueFactory<PlayerModel, String>(
						PlayerModel.PLAYERNAMECOLKEY));
		colPlayerName.setEditable(Boolean.FALSE);

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
						cell.getTableView().getItems()
								.get(cell.getTablePosition().getRow())
								.setPlayerPoint(cell.getNewValue());
						// TODO DATABASE options
					}
				});
		colPlayerLatestBet.setCellValueFactory(new PropertyValueFactory<PlayerModel, String>(
				PlayerModel.PLAYERBETCOLKEY));

		fillPlayerTab();
	}

	private void fillPlayerTab() {
		if (playerList != null) {
			playerList.clear();
		}
		playerList = runtimeDomain.getPlayerList();
		PlayerModel playerModle = null;
		Map<String, String> currentPlayersName = runtimeDomain
				.getCurrentPlayersName();
		int i = 1;
		for (String remarkName : currentPlayersName.keySet()) {
			playerModle = new PlayerModel(i++,
					currentPlayersName.get(remarkName), 0, remarkName);
			gameService.rsyncPlayerModel(playerModle,
					currentPlayersName.get(remarkName));
			playerList.add(playerModle);
		}
		playerTab.setItems(playerList);
	}

	private void fillUpGroupBox() {
		if (groupList != null)
			groupList.clear();
		groupList = runtimeDomain.getGroupList();
		ChatGroupModel groupModel = null;
		int i = 1;
		int selected = 0;
		groupList.add(new ChatGroupModel(String.valueOf(0), "请选择群", 0));
		for (String groupId : runtimeDomain.getGroupMap().keySet()) {
			groupModel = new ChatGroupModel(groupId, runtimeDomain
					.getGroupMap().get(groupId).getString("NickName")
					.replaceAll("</?[^>]+>", ""), runtimeDomain.getGroupMap()
					.get(groupId).getJSONArray("MemberList").size());
			if (groupId.equals(runtimeDomain.getCurrentGroupId())) {
				selected = i;
			}
			groupList.add(groupModel);
			i++;
		}
		groupBox.setItems(groupList);
		groupBox.getSelectionModel().select(selected);
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
		gameService.ryncPlayersPoint(playerList);
	}
	
	@FXML
	private void sendMessage(ActionEvent event) {
		webWechat.webwxsendmsg(messageBoard.getText());
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
	            public void changed(ObservableValue<? extends Boolean> arg0, Boolean before,
	                    Boolean now) {
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
	            	runtimeDomain.setBankerRemarkName(playerModel.getPlayerName());
	            	bankerLabel.setText(playerModel.getPlayerName());
	            } else {
	            	playerModel.setIsBanker(Boolean.FALSE);
	            }
	        }
	    }

	    @Override
	    public void updateItem(Boolean item, boolean empty) {
	        super.updateItem(item, empty);
	        final ObservableList<PlayerModel> items = getTableView().getItems();
	        if (items != null) {
	            if (getIndex() < items.size()) {
	                radio.setSelected(items.get(getIndex()).getIsBanker());
	                setGraphic(radio);
	            }
	        }

	    }
	}	
	

}
