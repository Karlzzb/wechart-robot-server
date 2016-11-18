package com.karl.fx.controller;

import java.util.regex.Matcher;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;

import org.springframework.stereotype.Component;

import com.karl.utils.AppUtils;
import com.karl.utils.StringUtils;

@Component
public class GameSettingController extends FxmlController {
	@FXML
	private ChoiceBox<String> gamekeyBox;
	@FXML
	private ChoiceBox<String> lotteryRuleChoice;
	@FXML
	private ChoiceBox<String> bankerOutTime;
	@FXML
	private ChoiceBox<String> playerOutTime;
	
	private final ToggleGroup paceLotteryRuleGroup = new ToggleGroup();
	@FXML
	private RadioButton pacePWinView;
	@FXML
	private RadioButton paceBWinView;
	@FXML
	private RadioButton paceNOWinView;
	@FXML
	private RadioButton paceLargeWinView;
	@FXML
	private TextField timeOutView;
	
	@Override
	public void initialize() {
		buildGameKeyBox();
		buildLotteryRuleChoise();
		buildOutTimeBoxBanker();
		buildOutTimeBoxPlayer();
		buildPaceBox();
		buildTimeOutText();
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
			if (lotteryRuleChoice.getItems().get(i)
					.equals(runtimeDomain.getCurrentLotteryRule())) {
				lotteryRuleChoice.getSelectionModel().select(i);
				break;
			}
		}
	}
	
	private void buildOutTimeBoxBanker() {
		bankerOutTime.setItems(FXCollections.observableArrayList(
				AppUtils.TIMEOUTPAIDALL, AppUtils.TIMEOUTPAIDONETIME,
				AppUtils.TIMEOUTPAIDNONE));
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
			if (bankerOutTime.getItems().get(i)
					.equals(runtimeDomain.getCurrentTimeOutRuleBanker())) {
				bankerOutTime.getSelectionModel().select(i);
				break;
			}
		}
	}
	
	private void buildOutTimeBoxPlayer() {
		playerOutTime.setItems(FXCollections.observableArrayList(
				AppUtils.TIMEOUTPAIDALL, AppUtils.TIMEOUTPAIDONETIME,
				AppUtils.TIMEOUTPAIDNONE));
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
			if (playerOutTime.getItems().get(i)
					.equals(runtimeDomain.getCurrentTimeOutRule())) {
				playerOutTime.getSelectionModel().select(i);
				break;
			}
		}
	}
	
	private void buildPaceBox() {
		pacePWinView.setToggleGroup(paceLotteryRuleGroup);
		pacePWinView.selectedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean before, Boolean now) {
						if (now) {
							runtimeDomain
									.setPaceLotteryRule(AppUtils.PACEPWIN);
						}
					}
				});
		paceBWinView.setToggleGroup(paceLotteryRuleGroup);
		paceBWinView.selectedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean before, Boolean now) {
						if (now) {
							runtimeDomain
									.setPaceLotteryRule(AppUtils.PACEBWIN);
						}
					}
				});
		paceNOWinView.setToggleGroup(paceLotteryRuleGroup);
		paceNOWinView.selectedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean before, Boolean now) {
						if (now) {
							runtimeDomain
									.setPaceLotteryRule(AppUtils.PACENOWIN);
						}
					}
				});
		paceLargeWinView.setToggleGroup(paceLotteryRuleGroup);
		paceLargeWinView.selectedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean before, Boolean now) {
						if (now) {
							runtimeDomain
									.setPaceLotteryRule(AppUtils.PACELARGEWIN);
						}
					}
				});
		
		// SET selected model
		switch (runtimeDomain.getPaceLotteryRule()) {
		case AppUtils.PACEPWIN:
			pacePWinView.setSelected(Boolean.TRUE);
			break;
		case AppUtils.PACEBWIN:
			paceBWinView.setSelected(Boolean.TRUE);
			break;
		case AppUtils.PACENOWIN:
			paceNOWinView.setSelected(Boolean.TRUE);
			break;
		case AppUtils.PACELARGEWIN:
			paceLargeWinView.setSelected(Boolean.TRUE);
			break;
		default:
			break;
		}
	}
	
	private void buildTimeOutText() {
		timeOutView.setText(String.valueOf(runtimeDomain.getCurrentTimeOut()));
		timeOutView.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov,
					String oldValue, String newValue) {
				try {
					Matcher matcher = StringUtils.LONG.matcher(newValue);
					if (matcher.find()) {
						timeOutView.setText(newValue);
						runtimeDomain.setCurrentTimeOut(Integer.valueOf(matcher
								.group()));
					} else {
						timeOutView.setText(oldValue);
					}
				} catch (Exception e) {
					timeOutView.setText(oldValue);
				}
			}
		});
	}

}
