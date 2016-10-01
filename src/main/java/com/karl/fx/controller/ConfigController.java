package com.karl.fx.controller;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.regex.Matcher;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.domain.LotteryRule;
import com.karl.fx.model.CheckBoxButtonCellPlayRule;
import com.karl.fx.model.PlayRule;
import com.karl.utils.AppUtils;
import com.karl.utils.StringUtils;

@Component
@Lazy
public class ConfigController extends FxmlController {
	
    
    @FXML
    private TableView<PlayRule> ruleTab;
    
    @FXML private TableColumn<PlayRule,Boolean> ruleCheck;
    
    @FXML private TableColumn<PlayRule,String> ruleName;
    
    @FXML private TableColumn<PlayRule,String> ruleDetail;
    
    @FXML private ChoiceBox<String> bankerOutTime;
    
    @FXML private ChoiceBox<String> playerOutTime;
    
    @FXML private TextField timeOut;
    
    private ObservableList<PlayRule> ruleList;


    @Override
    public void initialize() {
    	buidRuleTab();
    	buildOutTimeBox();
    	buildOutTimeBoxBanker();
    	timeOut.setText(String.valueOf(runtimeDomain.getCurrentTimeOut()));
    	timeOut.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov,
					String oldValue, String newValue) {
				try {
				Matcher matcher = StringUtils.LONG.matcher(newValue);
				if (matcher.find()) {
					timeOut.setText(newValue);
					runtimeDomain.setCurrentTimeOut(Integer.valueOf(matcher.group()));
				}else {
					timeOut.setText(oldValue);
				}
				}catch(Exception e) {
					timeOut.setText(oldValue);
				}
			}
    	});
    }
    
    private void buildOutTimeBox() {
    	playerOutTime.setItems(FXCollections.observableArrayList(
    			AppUtils.TIMEOUTPAIDALL, AppUtils.TIMEOUTPAIDONETIME, AppUtils.TIMEOUTPAIDNONE));
    	playerOutTime.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> paramObservableValue,
					String paramT1, String newValue) {
				if (newValue != null && !newValue.isEmpty()) {
					runtimeDomain.setCurrentTimeOutRule(newValue);
				}
			}
		});
		
		for (int i = 0; i < playerOutTime.getItems().size(); i++) {
			if (playerOutTime.getItems().get(i).equals(runtimeDomain.getCurrentTimeOutRule())) {
				playerOutTime.getSelectionModel().select(i);
				break;
			}
		}
		
	}
    
    private void buildOutTimeBoxBanker() {
    	bankerOutTime.setItems(FXCollections.observableArrayList(
    			AppUtils.TIMEOUTPAIDALL, AppUtils.TIMEOUTPAIDONETIME, AppUtils.TIMEOUTPAIDNONE));
    	bankerOutTime.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> paramObservableValue,
					String paramT1, String newValue) {
				if (newValue != null && !newValue.isEmpty()) {
					runtimeDomain.setCurrentTimeOutRuleBanker(newValue);
				}
			}
		});
		
		for (int i = 0; i < bankerOutTime.getItems().size(); i++) {
			if (bankerOutTime.getItems().get(i).equals(runtimeDomain.getCurrentTimeOutRuleBanker())) {
				bankerOutTime.getSelectionModel().select(i);
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
