package com.karl.fx.controller;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.regex.Matcher;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
    
    @FXML private ChoiceBox<String> lotteryRuleChoice;
    
    @FXML private TextField timeOut;
    
    @FXML private TextField manageFee;
    
    @FXML private CheckBox showManageFee;
    
    @FXML private CheckBox invainBanker;
    
    @FXML private CheckBox invainPlayer;
    
    @FXML private RadioButton fixedPackageFeeModel;
    @FXML private TextField fixedPackageFee;
    @FXML private RadioButton mathPackageFeeModel;
    @FXML private TextField mathPackageFeeB;
    @FXML private TextField mathPackageFeeC;
    @FXML private RadioButton realPackageFeeModel;
    @FXML private TextField bankerWinCut;
    @FXML private TextField firstBankerFee;
    
    
    
    private ObservableList<PlayRule> ruleList;

	
	private final ToggleGroup packageFeeGroup = new ToggleGroup();

    @Override
    public void initialize() {
    	buidRuleTab();
    	buildOutTimeBox();
    	buildOutTimeBoxBanker();
    	buildTimeOutText();
    	buildLotteryRuleChoise();
    	buildInvainBox();
    	buildAllFee();
    }
    
    private void buildAllFee() {
    	/*  mamager fee  */ 
   	    manageFee.setText(String.valueOf(runtimeDomain.getManageFee()));
    	manageFee.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov,
					String oldValue, String newValue) {
				try {
				Matcher matcher = StringUtils.LONG.matcher(newValue);
				if (matcher.find()) {
					manageFee.setText(newValue);
					runtimeDomain.setManageFee(Long.valueOf(matcher.group()));
				}else {
					manageFee.setText(oldValue);
				}
				}catch(Exception e) {
					manageFee.setText(oldValue);
				}
			}
    	});
    	showManageFee.setSelected(runtimeDomain.getShowManageFee());
    	showManageFee.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean before,
                    Boolean now) {
            	runtimeDomain.setShowManageFee(now);
            }
        });
    	
    	/* package fee */
    	//fixed package fee
    	fixedPackageFee.setText(String.valueOf(runtimeDomain.getFixedPackageFee()));
    	fixedPackageFee.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov,
					String oldValue, String newValue) {
				try {
				Matcher matcher = StringUtils.LONG.matcher(newValue);
				if (matcher.find()) {
					fixedPackageFee.setText(newValue);
					runtimeDomain.setFixedPackageFee(Long.valueOf(matcher.group()));
				}else {
					fixedPackageFee.setText(oldValue);
				}
				}catch(Exception e) {
					fixedPackageFee.setText(oldValue);
				}
			}
    	});
    	fixedPackageFeeModel.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean before, Boolean now) {
				if (now) {
					fixedPackageFee.setDisable(Boolean.FALSE);
					runtimeDomain.setPackageFeeModel(AppUtils.FIXEDPACKAGEFEEMODEL);
					runtimeDomain.setFixedPackageFee(Long.valueOf(fixedPackageFee.getText()));
					mathPackageFeeB.setDisable(Boolean.TRUE);
					mathPackageFeeC.setDisable(Boolean.TRUE);
				}
			}
		});
    	fixedPackageFeeModel.setToggleGroup(packageFeeGroup);
    	
    	//math package fee
    	mathPackageFeeB.setText(String.valueOf(runtimeDomain.getMathPackageFeeB()));
    	mathPackageFeeB.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov,
					String oldValue, String newValue) {
				try {
				Matcher matcher = StringUtils.LONG.matcher(newValue);
				if (matcher.find()) {
					mathPackageFeeB.setText(newValue);
					runtimeDomain.setMathPackageFeeB(Long.valueOf(matcher.group()));
				}else {
					mathPackageFeeB.setText(oldValue);
				}
				}catch(Exception e) {
					mathPackageFeeB.setText(oldValue);
				}
			}
    	});
    	mathPackageFeeC.setText(String.valueOf(runtimeDomain.getMathPackageFeeC()));
    	mathPackageFeeC.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov,
					String oldValue, String newValue) {
				try {
				Matcher matcher = StringUtils.LONG.matcher(newValue);
				if (matcher.find()) {
					mathPackageFeeC.setText(newValue);
					runtimeDomain.setMathPackageFeeC(Long.valueOf(matcher.group()));
				}else {
					mathPackageFeeC.setText(oldValue);
				}
				}catch(Exception e) {
					mathPackageFeeC.setText(oldValue);
				}
			}
    	});
    	mathPackageFeeModel.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean before, Boolean now) {
				if (now) {
					mathPackageFeeB.setDisable(Boolean.FALSE);
					mathPackageFeeC.setDisable(Boolean.FALSE);
					runtimeDomain.setPackageFeeModel(AppUtils.MATHPACKAGEFEEMODEL);
					runtimeDomain.setMathPackageFeeB(Long.valueOf(mathPackageFeeB.getText()));
					runtimeDomain.setMathPackageFeeC(Long.valueOf(mathPackageFeeC.getText()));
					fixedPackageFee.setDisable(Boolean.TRUE);
				}
			}
		});
    	mathPackageFeeModel.setToggleGroup(packageFeeGroup);
    	//real package fee
    	realPackageFeeModel.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean before, Boolean now) {
				if (now) {
					runtimeDomain.setPackageFeeModel(AppUtils.REALPACKAGEFEEMODEL);
					fixedPackageFee.setDisable(Boolean.TRUE);
					mathPackageFeeB.setDisable(Boolean.TRUE);
					mathPackageFeeC.setDisable(Boolean.TRUE);
				}
			}
		});
    	realPackageFeeModel.setToggleGroup(packageFeeGroup);
    	
    	//SET selected model
    	if(runtimeDomain.getPackageFeeModel().equals(AppUtils.FIXEDPACKAGEFEEMODEL)) {
    		fixedPackageFeeModel.setSelected(Boolean.TRUE);
    	}else if(runtimeDomain.getPackageFeeModel().equals(AppUtils.MATHPACKAGEFEEMODEL)){
    		mathPackageFeeModel.setSelected(Boolean.TRUE);
    	}else if(runtimeDomain.getPackageFeeModel().equals(AppUtils.REALPACKAGEFEEMODEL)) {
    		realPackageFeeModel.setSelected(Boolean.TRUE);
    	}
    	
    	/* banker win cut */
    	bankerWinCut.setText(String.valueOf(runtimeDomain.getBankerWinCutRate()));
    	bankerWinCut.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov,
					String oldValue, String newValue) {
				try {
				Matcher matcher = StringUtils.LONG.matcher(newValue);
				if (matcher.find()) {
					bankerWinCut.setText(newValue);
					runtimeDomain.setBankerWinCutRate(Long.valueOf(matcher.group()));
				}else {
					bankerWinCut.setText(oldValue);
				}
				}catch(Exception e) {
					bankerWinCut.setText(oldValue);
				}
			}
    	});
    	
    	/*  first banker fee  */ 
   	    firstBankerFee.setText(String.valueOf(runtimeDomain.getFirstBankerFee()));
   	    firstBankerFee.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov,
					String oldValue, String newValue) {
				try {
				Matcher matcher = StringUtils.LONG.matcher(newValue);
				if (matcher.find()) {
					firstBankerFee.setText(newValue);
					runtimeDomain.setFirstBankerFee(Long.valueOf(matcher.group()));
				}else {
					firstBankerFee.setText(oldValue);
				}
				}catch(Exception e) {
					firstBankerFee.setText(oldValue);
				}
			}
    	});

    }
    
    private void buildInvainBox() {
    	invainBanker.setSelected(runtimeDomain.getAllowInvainBanker());
    	
    	invainBanker.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean before,
                    Boolean now) {
            	runtimeDomain.setAllowInvainBanker(now);
            }
        });
    	
    	invainPlayer.setSelected(runtimeDomain.getAllowInvainPlayer());
    	invainPlayer.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean before,
                    Boolean now) {
            	runtimeDomain.setAllowInvainPlayer(now);
            }
        });
    }
    
    private void buildLotteryRuleChoise() {
    	lotteryRuleChoice.setItems(FXCollections.observableArrayList(
    			AppUtils.LOTTERYRULE3, AppUtils.LOTTERYRULE2));
    	lotteryRuleChoice.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> paramObservableValue,
					String paramT1, String newValue) {
				if (newValue != null && !newValue.isEmpty()) {
					runtimeDomain.setCurrentLotteryRule(newValue);
				}
			}
		});
		
		for (int i = 0; i < lotteryRuleChoice.getItems().size(); i++) {
			if (lotteryRuleChoice.getItems().get(i).equals(runtimeDomain.getCurrentLotteryRule())) {
				lotteryRuleChoice.getSelectionModel().select(i);
				break;
			}
		}
    }
    
    private void buildTimeOutText() {
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
