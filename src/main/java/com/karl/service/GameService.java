package com.karl.service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;

import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.karl.db.domain.Player;
import com.karl.db.service.PlayerService;
import com.karl.domain.LotteryRule;
import com.karl.domain.RuntimeDomain;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.AppUtils;
import com.karl.utils.DigitalUtils;
import com.karl.utils.StringUtils;

@Service
public class GameService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GameService.class);

	@Autowired
	private RuntimeDomain runtimeDomain;

	@Autowired
	private PlayerService playerService;

	@Autowired
	@Lazy
	private WebWechat webWechat;

	/**
	 * open the lottery
	 */
	public void openLottery() {
		Player player = null;
		Player banker = runningBanker();
		Long bankerResult = getResult(banker.getLatestLuck());

		for (String remarkName : runningPlayers().keySet()) {
			player = runningPlayers().get(remarkName);
			player.setLatestResult(getResult(player.getLatestLuck(),
					bankerResult));
			playerService.updateResult(player.getPlayerId(),
					player.getLatestResult());
		}
		resetPlayers(AppUtils.sortByValue(runningPlayers()));
	}

	/**
	 * put bet info into the current player list
	 * 
	 * @param remarkName
	 * @param betInfo
	 */
	public void puttingBetInfo(String webchatId, String remarkName,
			String betInfo) {
		Player player = runningPlayers().get(remarkName);
		if (player == null) {
			player = new Player();
			player.setRemarkName(remarkName);
		}
		player.setWebchatId(webchatId);
		player.setLatestBet(betInfo);
		if (AppUtils.PLAYLONG.equals(runtimeDomain.getCurrentGameKey())) {
			player.setLatestBetValue(Long.valueOf(betInfo));
		} else if (AppUtils.PLAYLONGSPLIT.equals(runtimeDomain
				.getCurrentGameKey())) {
			String[] betInfos = betInfo.split(StringUtils.BETSPLIT);
			player.setLatestBetValue(Long.valueOf(betInfos[1]));
		}
		player.setLatestBetTime(new Date().getTime());
		playerService.updateBetInfo(player.getPlayerId(),
				player.getLatestBet(), player.getLatestBetTime(),
				player.getLatestBetValue());
	}

	/**
	 * put luck info into the current player list
	 * 
	 * @param remarkName
	 * @param luckInfo
	 */
	public void puttingLuckInfo(Integer index, String remarkName,
			Double luckInfo) {
		if (AppUtils.PLAYLONG.equals(runtimeDomain.getCurrentGameKey())) {
			puttingLuckInfo(remarkName, luckInfo);
		} else if (AppUtils.PLAYLONGSPLIT.equals(runtimeDomain
				.getCurrentGameKey())) {
			puttingLuckInfo(index, luckInfo);
		}

	}

	/**
	 * put luck info into the current player list
	 * 
	 * @param remarkName
	 * @param luckInfo
	 */
	private void puttingLuckInfo(Integer index, Double luckInfo) {
		Map<String, Player> runningPlayerMap = runningPlayers();
		String betInfo = "";
		String[] betInfos;
		for (String remarkName : runningPlayerMap.keySet()) {
			betInfo = runningPlayerMap.get(remarkName).getLatestBet();
			betInfos = betInfo.split(StringUtils.BETSPLIT);
			if (betInfos != null && betInfos.length > 1) {
				if (betInfos[0].equals(String.valueOf(index))) {
					playerService.updateLuckInfo(
							runningPlayerMap.get(remarkName).getPlayerId(),
							runningPlayerMap.get(remarkName).getLatestLuck());
					runningPlayerMap.get(remarkName).setLatestLuck(luckInfo);
					break;
				}
			}
		}
	}

	/**
	 * put luck info into the current player list
	 * 
	 * @param remarkName
	 * @param luckInfo
	 */
	private void puttingLuckInfo(String remarkName, Double luckInfo) {
		Player player = runningPlayers().get(remarkName);
		if (player == null) {
			// TODO no user
			LOGGER.warn("User{} luck info failed!", remarkName);
			return;
		}
		playerService.updateLuckInfo(player.getPlayerId(),
				player.getLatestLuck());
		player.setLatestLuck(luckInfo);

	}

	/**
	 * Calculate single player result
	 * 
	 * @param luckInfo
	 * @return
	 */
	private Long getResult(Double luckInfo) {
		Long result = Long.valueOf(0);
		EnumSet<LotteryRule> theRule = currentRule();
		for (Iterator<LotteryRule> iterator = theRule.iterator(); iterator
				.hasNext();) {
			LotteryRule lotteryRule = (LotteryRule) iterator.next();
			if (lotteryRule.getRuleResult(luckInfo)) {
				result = lotteryRule.getTimes();
				break;
			}
		}
		if (result.compareTo(Long.valueOf(0)) == 0) {
			result = DigitalUtils.getSumFromDouble(luckInfo);
		}
		return result;
	}

	public Player findPlayerByRemarkName(String remarkName) {
		if (remarkName == null || remarkName.isEmpty()) {
			return null;
		}
		return playerService.getPlayerByRemarkName(remarkName);
	}

	/**
	 * Calculate player result comparing to banker
	 * 
	 * @param luckInfo
	 * @param bankerResult
	 * @return
	 */
	private Long getResult(Double luckInfo, Long bankerResult) {
		Long selfResult = getResult(luckInfo);
		return selfResult - bankerResult;
	}

	private Map<String, Player> runningPlayers() {
		return runtimeDomain.getRunningPlayeres();
	}

	private void resetPlayers(Map<String, Player> players) {
		runtimeDomain.setRunningPlayeres(players);
	}

	private Player runningBanker() {
		return runningPlayers().get(runtimeDomain.bankerRemarkName);
	}

	private EnumSet<LotteryRule> currentRule() {
		return runtimeDomain.getCurrentRule();
	}

	public void rsyncPlayerModel(PlayerModel playerModle, String remarkName) {
		if (playerModle == null || remarkName == null || remarkName.isEmpty()) {
			return;
		}
		Player playerEntity = playerService.getPlayerByRemarkName(remarkName);
		if (playerEntity != null) {
			playerModle
					.setPlayerPoint(String.valueOf(playerEntity.getPoints()));
			playerModle.setPlayerLatestBet(playerEntity.getLatestBet());
			runningPlayers().put(playerEntity.getRemarkName(), playerEntity);
		}

	}

	public void ryncPlayersPoint(ObservableList<PlayerModel> playerList) {
		PlayerModel playerView = null;
		for (int i = 0; i < playerList.size(); i++) {
			playerView = playerList.get(i);
			ryncPlayerPoint(playerView.getWechatId(), playerView.getPlayerId(),
					playerView.getPlayerNameRaw(),
					Long.valueOf(playerView.getPlayerPoint()));
		}
	}

	private void ryncPlayerPoint(String webchatId, String playerId,
			String remarkName, Long newPointvel) {
		Player playEntity = null;
		playEntity = playerService.getPlayerById(playerId);
		if (playEntity == null) {
			playEntity = new Player();
			playEntity.setPlayerId(playerId);
			playEntity.setRemarkName(remarkName);
		}
		playEntity.setWebchatId(webchatId);
		playEntity.setPoints(newPointvel);
		savePlayEntity(playEntity);
	}

	public String declareGame() {
		Boolean betSignal = runtimeDomain.getGlobalGameSignal();
		if (betSignal == null) {
			LOGGER.error("no message send, bet singal is null!");
			return null;
		}
		if (betSignal) {
			return declareBetStar();
		} else {
			return declareBetEnd();
		}
	}

	private String declareBetStar() {
		String content = MessageFormat.format(
				AppUtils.GAMESTART,
				runtimeDomain.getCurrentGroupName(),
				runtimeDomain.getBankerRemarkName(),
				runtimeDomain.getRunningPlayeres()
						.get(runtimeDomain.getBankerRemarkName()).getPoints(),
				runtimeDomain.getMaximumBet(), runtimeDomain.getMinimumBet(),
				runtimeDomain.getBankerPackageNum(),
				runtimeDomain.getBankerIndex());

		return content;
	}

	private String declareBetEnd() {
		String content = AppUtils.BETRESULTHEAD;
		Map<String, Player> playerMap = runtimeDomain.getRunningPlayeres();
		Player player = null;
		int i = 0;
		long sumPoints = Long.valueOf(0);
		long sumPointsAllIN = Long.valueOf(0);
		if (playerMap != null) {
			for (String remarkName : playerMap.keySet()) {
				player = playerMap.get(remarkName);
				if (player.getLatestBetValue() == null
						|| player.getLatestBetValue()
								.compareTo(Long.valueOf(0)) == 0) {
					continue;
				}
				content += MessageFormat.format(AppUtils.BETRESULTLINE,
						player.getRemarkName(),
						String.valueOf(player.getPoints()),
						player.getLatestBet());
				i++;
				sumPoints += player.getLatestBetValue();
				if (player.getLatestBetValue().compareTo(player.getPoints()) == 0) {
					sumPointsAllIN += player.getLatestBetValue();
				}
			}
		}
		content += MessageFormat.format(AppUtils.BETRESULTTAIL, i, 6, 14,
				sumPoints, sumPoints - sumPointsAllIN, sumPointsAllIN);

		return content;

	}

	/**
	 * TODO no using
	 * 
	 * @param playerId
	 * @param remarkName
	 * @param plusOrMinus
	 * @param newPointvel
	 */
	public void ryncPlayerPoint(String playerId, String remarkName,
			Boolean plusOrMinus, Long newPointvel) {
		Player playEntity = null;
		Long oldPointVel = Long.valueOf(0);
		playEntity = playerService.getPlayerById(playerId);
		if (playEntity == null) {
			LOGGER.warn("User[{}] cann't be found, the point option failed!",
					remarkName);
			return;
		}
		playEntity.setPoints(plusOrMinus ? oldPointVel + newPointvel
				: oldPointVel - newPointvel);
		savePlayEntity(playEntity);
	}

	private void savePlayEntity(Player player) {
		playerService.save(player);
		runningPlayers().put(player.getRemarkName(), player);
	}

}
