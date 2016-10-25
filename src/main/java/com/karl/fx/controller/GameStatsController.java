package com.karl.fx.controller;

import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.db.domain.GameStats;
import com.karl.fx.model.GameStatsModel;
import com.karl.utils.DateUtils;

@Component
@Lazy
public class GameStatsController extends FxmlController {

	@FXML
	private TableView<GameStatsModel> gameStatsTab;

	@FXML
	private TableColumn<GameStatsModel, String> statsSignCol;

	@FXML
	private TableColumn<GameStatsModel, String> statsTimeCol;

	@FXML
	private TableColumn<GameStatsModel, Long> bankerWinCutCol;

	@FXML
	private TableColumn<GameStatsModel, Long> firstBankerFeeCol;

	@FXML
	private TableColumn<GameStatsModel, Long> manageFeeCol;

	@FXML
	private TableColumn<GameStatsModel, Long> packageFeeCol;

	@FXML
	private TableColumn<GameStatsModel, Integer> gameNumCol;

	@FXML
	private TableColumn<GameStatsModel, Long> statsSumCol;

	@FXML
	private StackedBarChart<String, Number> sbc;

	@FXML
	private PieChart pieChart;

	@FXML
	private Button archiveButton;

	@FXML
	private ImageView imgLoad;

	@Override
	public void initialize() {
		buidlStatsTable();
		buildBarChar();
		buildArchiveAction();
	}

	private void buildArchiveAction() {
		archiveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (runtimeDomain.getGlobalGameSignal()) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("错误操作");
					alert.setContentText("请勿在开局过程中操作, 本操作用于清空当前所有游戏账单信息！");
					alert.showAndWait();
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("确认操作");
				alert.setContentText("本操作用于清空所有游戏账单信息并归档统计！ 是否确定操作？");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					Service<Boolean> service = new Service<Boolean>() {
						@Override
						protected Task<Boolean> createTask() {
							return new Task<Boolean>() {
								@Override
								protected Boolean call() throws Exception {
									return gameService.archievGameInfo();
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
						fillStatsTable();
						buildBarChar();
						clearPieBarChar();
					});
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void buildBarChar() {
		if (sbc.getData() != null) {
			sbc.getData().clear();
		}

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		sbc.setTitle("账期统计");
		xAxis.setLabel("账期");
		xAxis.rotateProperty().add(0.5);
		yAxis.setLabel("收益");
		ObservableList<GameStatsModel> statsModelList = gameStatsTab.getItems();

		if (statsModelList != null && statsModelList.size() > 0) {
			GameStatsModel statsModel = null;
			XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();
			series1.setName("分成");
			XYChart.Series<String, Number> series2 = new XYChart.Series<String, Number>();
			series2.setName("上庄");
			XYChart.Series<String, Number> series3 = new XYChart.Series<String, Number>();
			series3.setName("管理费");
			XYChart.Series<String, Number> series4 = new XYChart.Series<String, Number>();
			series4.setName("包费");
			for (int i = 0; i < statsModelList.size(); i++) {
				statsModel = statsModelList.get(i);
				series1.getData().add(
						new XYChart.Data<String, Number>(statsModelList.get(i)
								.getStatsTime(), statsModel.getBankerWinCut()));
				series2.getData()
						.add(new XYChart.Data<String, Number>(statsModelList
								.get(i).getStatsTime(), statsModel
								.getFirstBankerFee()));
				series3.getData().add(
						new XYChart.Data<String, Number>(statsModelList.get(i)
								.getStatsTime(), statsModel.getManageFee()));
				series4.getData().add(
						new XYChart.Data<String, Number>(statsModelList.get(i)
								.getStatsTime(), statsModel.getPackageFee()));
			}
			sbc.getData().addAll(series1, series2, series3, series4);
		}
	}

	private void buildpieBarChar(GameStatsModel statsModel) {

		ObservableList<PieChart.Data> pieChartData = FXCollections
				.observableArrayList(
						new PieChart.Data("分成", statsModel.getBankerWinCut()),
						new PieChart.Data("上庄", statsModel.getFirstBankerFee()),
						new PieChart.Data("管理费", statsModel.getManageFee()),
						new PieChart.Data("包费", statsModel.getPackageFee()));
		pieChart.setData(pieChartData);
		pieChart.setTitle("收益分布图");
		pieChart.setLabelLineLength(10);
		pieChart.setLegendSide(Side.LEFT);
		final Label caption = new Label("");
		caption.setTextFill(Color.DARKORANGE);
		caption.setStyle("-fx-font: 18 arial;");

		for (final PieChart.Data data : pieChart.getData()) {
			data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
					new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							caption.setTranslateX(e.getSceneX());
							caption.setTranslateY(e.getSceneY());
							caption.setText(String.valueOf(data.getPieValue())
									+ "%");
						}
					});
		}
	}

	private void clearPieBarChar() {
		pieChart.getData().clear();
	}

	private void buidlStatsTable() {
		statsSignCol
				.setCellValueFactory(new PropertyValueFactory<GameStatsModel, String>(
						GameStatsModel.STATSSIGNCOL));

		statsTimeCol
				.setCellValueFactory(new PropertyValueFactory<GameStatsModel, String>(
						GameStatsModel.STATSTIMECOL));

		manageFeeCol
				.setCellValueFactory(new PropertyValueFactory<GameStatsModel, Long>(
						GameStatsModel.MANAGEFEECOL));
		packageFeeCol
				.setCellValueFactory(new PropertyValueFactory<GameStatsModel, Long>(
						GameStatsModel.PACKAGEFEECOL));
		firstBankerFeeCol
				.setCellValueFactory(new PropertyValueFactory<GameStatsModel, Long>(
						GameStatsModel.FIRSTBANKERFEECOL));
		bankerWinCutCol
				.setCellValueFactory(new PropertyValueFactory<GameStatsModel, Long>(
						GameStatsModel.BANKERWINCUTCOL));
		gameNumCol
				.setCellValueFactory(new PropertyValueFactory<GameStatsModel, Integer>(
						GameStatsModel.GAMENUMCOL));

		statsSumCol
				.setCellValueFactory(new PropertyValueFactory<GameStatsModel, Long>(
						GameStatsModel.STATSSUMCOL));

		gameStatsTab.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, selectedPModel) -> {
					if (selectedPModel != null) {
						buildpieBarChar(selectedPModel);
					} else {
						clearPieBarChar();
					}
				});

		fillStatsTable();
	}

	private void fillStatsTable() {
		ObservableList<GameStatsModel> statsModelList = gameStatsTab.getItems();
		if (statsModelList.size() > 0) {
			statsModelList.clear();
		}

		GameStats currentGameStats = gameService.getCurrentGameStats();
		if (currentGameStats != null) {
			statsModelList.add(new GameStatsModel("当前数据", DateUtils
					.timeStampHH(currentGameStats.getStatsTime()),
					currentGameStats.getManageFee(), currentGameStats
							.getPackageFee(), currentGameStats
							.getFirstBankerFee(), currentGameStats
							.getBankerWinCut(), currentGameStats.getGameNum()));
		}

		List<GameStats> gameStatsList = gameService.getGameStatsList();
		if (gameStatsList == null || gameStatsList.size() < 1) {
			return;
		}
		for (int i = 0; i < gameStatsList.size(); i++) {
			statsModelList.add(new GameStatsModel("历史数据", DateUtils
					.timeStampHH(gameStatsList.get(i).getStatsTime()),
					gameStatsList.get(i).getManageFee(), gameStatsList.get(i)
							.getPackageFee(), gameStatsList.get(i)
							.getFirstBankerFee(), gameStatsList.get(i)
							.getBankerWinCut(), gameStatsList.get(i)
							.getGameNum()));
		}

	}

}
