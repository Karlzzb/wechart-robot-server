package com.karl.fx.controller;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.db.domain.Player;
import com.karl.db.domain.PlayerTrace;
import com.karl.fx.model.PlayerTraceModel;
import com.karl.utils.AppUtils;

@Component
@Lazy
public class GameRunningTabController extends FxmlController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GameRunningTabController.class);

	@FXML
	private TableView<PlayerTraceModel> traceTab;

	@FXML
	private TableColumn<PlayerTraceModel, String> playerName;

	@FXML
	private TableColumn<PlayerTraceModel, Long> playerPoint;

	@FXML
	private TableColumn<PlayerTraceModel, String> betInfo;

	@FXML
	private TableColumn<PlayerTraceModel, String> resultRuleName;

	@FXML
	private TableColumn<PlayerTraceModel, String> resultInfo;

	private ObservableList<PlayerTraceModel> traceModeList;

	@FXML
	private Button bulkApproveButton;
	@FXML
	private Button bulkRejectButton;

	@Override
	public void initialize() {
		buidApprovalTab();
		fillTraceTab();
		traceAutoFlush();
	}

	private void buidApprovalTab() {
		traceTab.setEditable(false);
		traceModeList = FXCollections.observableArrayList();
		playerName
				.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, String>(
						PlayerTraceModel.PLAYERNAMECOLKEY));
		playerPoint
				.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, Long>(
						PlayerTraceModel.PLAYERPOINTCOLKEY));
		betInfo.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, String>(
				PlayerTraceModel.BETINFOKEY));
		resultRuleName
				.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, String>(
						PlayerTraceModel.RESULTRULENAMEKEY));
		resultInfo
				.setCellValueFactory(new PropertyValueFactory<PlayerTraceModel, String>(
						PlayerTraceModel.RESULTINFOKEY));
	}

	private void fillTraceTab() {
		if (traceModeList != null) {
			traceModeList.clear();
		}
		List<PlayerTrace> traceList = gameService.getCurrentPlayTrace();
		if (traceList != null) {
			PlayerTrace trace = null;
			Player pEntity = null;
			String resultInfo;
			for (int i = 0; i < traceList.size(); i++) {
				trace = traceList.get(i);
				pEntity = runtimeDomain.getRunningPlayeres().get(
						trace.getRemarkName());
				if (pEntity == null) {
					continue;
				}
				resultInfo = "";
				if (trace.getResultPoint() != null) {
					resultInfo = trace.getResultPoint() > 0 ? "赢"
							+ trace.getResultPoint() : "输"
							+ Math.abs(trace.getResultPoint());
				}

				traceModeList.add(new PlayerTraceModel(trace.getPlayerId(),
						trace.getRemarkName(), pEntity.getPoints(), trace
								.getBetInfo(), trace.getResultRuleName(),
						resultInfo));
			}
		}
		traceTab.setItems(traceModeList);
	}

	/**
	 * auto rync player table
	 */
	private void traceAutoFlush() {
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				while (true) {
					try {
						Thread.sleep(AppUtils.TRACE_TAB_FLSH_TERVAL);
						List<PlayerTrace> traceList = gameService
								.getCurrentPlayTrace();
						if (traceList != null) {
							if (traceModeList == null) {
								return null;
							}
							if (traceModeList.size() > 0) {
								traceModeList.clear();
							}
							PlayerTrace trace = null;
							for (int i = 0; i < traceList.size(); i++) {
								trace = traceList.get(i);
								traceModeList
										.add(new PlayerTraceModel(
												trace.getPlayerId(),
												trace.getRemarkName(),
												runtimeDomain
														.getRunningPlayeres()
														.get(trace
																.getRemarkName())
														.getPoints(),
												trace.getBetInfo(),
												trace.getResultRuleName() == null ? ""
														: trace.getResultRuleName(),
												trace.getResultPoint() == null ? ""
														: trace.getResultPoint() > 0 ? "赢"
																+ trace.getResultPoint()
																: "输"
																		+ Math.abs(trace
																				.getResultPoint())));
							}
						}
					} catch (Exception e) {
						LOGGER.error("player table auto change failed!", e);
					}

				}
			}
		};
		Thread t1 = new Thread(task);
		t1.setDaemon(Boolean.TRUE);
		t1.start();
	}
}
