package com.karl.fx.controller;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.domain.LotteryRule;
import com.karl.fx.model.ChatGroupModel;
import com.karl.fx.model.CheckBoxButtonCell;
import com.karl.fx.model.EditingCell;
import com.karl.fx.model.PlayRule;
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
    private TableColumn<PlayerModel, Integer> colAutoID;

    @FXML
    private TableColumn<PlayerModel, String> colPlayerName;

    @FXML
    TableColumn<PlayerModel, String> colPlayerPoint;

    @FXML
    private TableView<PlayerModel> playerTab;

    private ObservableList<ChatGroupModel> groupList;
    
    private ObservableList<PlayerModel> playerList;
    
    @FXML
    private TableView<PlayRule> ruleTab;
    
    @FXML private TableColumn<PlayRule,Boolean> ruleCheck;
    
    @FXML private TableColumn<PlayRule,String> ruleName;
    
    @FXML private TableColumn<PlayRule,String> ruleDetail;
    
    private ObservableList<PlayRule> ruleList;


    @Override
    public void initialize() {
    	buildGroupBox();
    	buidRuleBox();
    	buildPlayerTab();
    }
    
    private void buidRuleBox() {
    	ruleTab.setEditable(true);
    	ruleCheck.setCellFactory(new Callback<TableColumn<PlayRule,Boolean>, TableCell<PlayRule,Boolean>>() {
			@Override
			public TableCell<PlayRule, Boolean> call(
					TableColumn<PlayRule, Boolean> arg0) {
				return new CheckBoxButtonCell();
			}
    		
		});
    	
    	ruleName.setCellValueFactory(new PropertyValueFactory<PlayRule, String>(
    			PlayRule.RULENAMEKEY));
    	ruleDetail.setCellValueFactory(new PropertyValueFactory<PlayRule, String>(
    			PlayRule.RULEDETAILKEY));
    	fillRuleTab();
    }
    
    private void buildGroupBox() {
    	groupBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ChatGroupModel>() {

			@Override
			public void changed(
					ObservableValue<? extends ChatGroupModel> observable,
					ChatGroupModel oldValue, ChatGroupModel newValue) {
				if (newValue != null && !newValue.getGroupId().equals(String.valueOf(0))) {
					runtimeDomain.setCurrentGroupId(newValue.getGroupId());
					groupSizeLable.setText("群人数 :"+String.valueOf(newValue.getGroupSize()));
					fillPlayerTab();
				}
			}
		});
    	fillUpGroupBox();
    }
    

    private void buildPlayerTab() {
        playerTab.setEditable(true);
        Callback<TableColumn<PlayerModel, String>, TableCell<PlayerModel, String>> cellFactory =
                new Callback<TableColumn<PlayerModel, String>, TableCell<PlayerModel, String>>() {
                    public TableCell<PlayerModel, String> call(TableColumn<PlayerModel, String> p) {
                       return new EditingCell();
                    }
                };
        
        
        colAutoID.setCellValueFactory(new PropertyValueFactory<PlayerModel, Integer>(
        		PlayerModel.AUDOIDCOLKEY));
        colPlayerName.setCellValueFactory(new PropertyValueFactory<PlayerModel, String>(
        		PlayerModel.PLAYERNAMECOLKEY));
        colPlayerName.setEditable(Boolean.FALSE);
        
        colPlayerPoint.setCellValueFactory(new PropertyValueFactory<PlayerModel, String>(
        		PlayerModel.PLAYERPOINTCOLKEY));
        colPlayerPoint.setCellFactory(cellFactory);
        colPlayerPoint.setOnEditCommit(new EventHandler<CellEditEvent<PlayerModel,String>>() {
			@Override
			public void handle(CellEditEvent<PlayerModel, String> cell) {
				if (!StringUtils.matchLong(cell.getNewValue())) {
					return;
				}
				cell.getTableView().getItems().get(cell.getTablePosition().getRow()).setPlayerPoint(cell.getNewValue());
				// TODO DATABASE options
			}
		});
        
        
        fillPlayerTab();
    }
    
    
    
    private void fillPlayerTab() {
    	if (playerList != null) {
    		playerList.clear();
    	}
        playerList = runtimeDomain.getPlayerList();
        PlayerModel playerModle = null;
        List<String> currentPlayersName = runtimeDomain.getCurrentPlayersName();
        for (int i = 0; i < currentPlayersName.size(); i++) {
        	currentPlayersName.get(i);
        	playerModle = new PlayerModel(i+1, currentPlayersName.get(i), 0);
        	//TODO database part
        	gameService.rsyncPlayerModel(playerModle, currentPlayersName.get(i));
        	playerList.add(playerModle);
		}
        playerTab.setItems(playerList);
    }

    private void fillUpGroupBox() {
        if (groupList != null)
            groupList.clear();
        groupList = runtimeDomain.getGroupList();
        ChatGroupModel groupModel = null;
        int  i = 1;
        int selected = 0;
        groupList.add(new ChatGroupModel(String.valueOf(0),"请选择群",0));
        for (String groupId : runtimeDomain.getGroupMap().keySet()) {
            groupModel = new ChatGroupModel(groupId, runtimeDomain.getGroupMap().get(groupId)
                    .getString("NickName").replaceAll("</?[^>]+>", ""), runtimeDomain.getGroupMap()
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
    
    private void fillRuleTab() {
    	if (ruleList != null) 
    		ruleList.clear();
    	ruleList = runtimeDomain.getRuleList();
    	
		EnumSet<LotteryRule> theRule = runtimeDomain.getCurrentRule();
		if (theRule == null) {
			return;
		}
		for (Iterator<LotteryRule> iterator = theRule.iterator(); iterator
				.hasNext();) {
			LotteryRule lotteryRule = (LotteryRule) iterator.next();
			ruleList.add(new PlayRule(Boolean.TRUE,lotteryRule.getRuleName(),lotteryRule.getRuleDetail()));
		}
		ruleTab.setItems(ruleList);
    }


    @FXML
    private void flushGroup(ActionEvent event) {
        webWechat.wxInit();
        // webWechat.getContact();
        fillUpGroupBox();
    }
    
    @FXML
    private void savePlayerPoint(ActionEvent event) {
    	gameService.ryncPlayersPoint(playerList);
    }



}
