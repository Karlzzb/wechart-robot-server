package com.karl.fx.controller;

import java.util.EnumSet;
import java.util.Iterator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.domain.LotteryRule;
import com.karl.fx.model.CheckBoxButtonCellPlayRule;
import com.karl.fx.model.PlayRule;
import com.karl.utils.AppUtils;

@Component
@Lazy
public class ConfigController extends FxmlController {
	
    
    @FXML
    private TableView<PlayRule> ruleTab;
    
    @FXML private TableColumn<PlayRule,Boolean> ruleCheck;
    
    @FXML private TableColumn<PlayRule,String> ruleName;
    
    @FXML private TableColumn<PlayRule,String> ruleDetail;
    
    private ObservableList<PlayRule> ruleList;
    
	@FXML
	private ChoiceBox<String> gamekeyBox;



    @Override
    public void initialize() {
    	buidRuleTab();
    	gamekeyBox.setItems(FXCollections.observableArrayList(
    			AppUtils.PLAYLONG, AppUtils.PLAYLONGSPLIT, AppUtils.PLAYLUCKWAY));
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
			if (gamekeyBox.getItems().get(i).equals(runtimeDomain.getCurrentGameKey())) {
				gamekeyBox.getSelectionModel().select(i);
				break;
			}
		}
    }
    
    private void buidRuleTab() {
    	ruleTab.setEditable(true);
    	ruleCheck.setCellFactory(new Callback<TableColumn<PlayRule,Boolean>, TableCell<PlayRule,Boolean>>() {
			@Override
			public TableCell<PlayRule, Boolean> call(
					TableColumn<PlayRule, Boolean> arg0) {
				return new CheckBoxButtonCellPlayRule();
			}
    		
		});
    	
    	ruleName.setCellValueFactory(new PropertyValueFactory<PlayRule, String>(
    			PlayRule.RULENAMEKEY));
    	ruleDetail.setCellValueFactory(new PropertyValueFactory<PlayRule, String>(
    			PlayRule.RULEDETAILKEY));
    	fillRuleTab();
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



}
