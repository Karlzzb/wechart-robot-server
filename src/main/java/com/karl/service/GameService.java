package com.karl.service;

import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;

import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.karl.db.domain.Player;
import com.karl.db.service.PlayerService;
import com.karl.domain.LotteryRule;
import com.karl.domain.RuntimeDomain;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.AppUtils;
import com.karl.utils.DigitalUtils;

@Service
public class GameService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GameService.class);

	@Autowired
	private RuntimeDomain runtimeDomain;

	@Autowired
	private PlayerService playerService;

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
		}
		resetPlayers(AppUtils.sortByValue(runningPlayers()));
	}

	/**
	 * put bet info into the current player list
	 * 
	 * @param remarkName
	 * @param betInfo
	 */
	public void puttingBetInfo(String remarkName, Double betInfo) {
		Player player = runningPlayers().get(remarkName);
		if (player == null) {
			player = new Player();
			player.setRemarkName(remarkName);
		}
		player.setLatestBet(betInfo.longValue());
		player.setLatestBetTime(new Date().getTime());
		runningPlayers().put(remarkName, player);
	}

	/**
	 * put luck info into the current player list
	 * 
	 * @param remarkName
	 * @param luckInfo
	 */
	public void puttingLuckInfo(String remarkName, Double luckInfo) {
		Player player = runningPlayers().get(remarkName);
		if (player == null) {
			player = new Player();
			player.setRemarkName(remarkName);
		}
		player.setLatestLuck(luckInfo);
		runningPlayers().put(remarkName, player);
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
			// TODO copy db entity to view model
		}

	}

	public void ryncPlayersPoint(ObservableList<PlayerModel> playerList) {
		PlayerModel playerView = null;
		for (int i = 0; i < playerList.size(); i++) {
			playerView = playerList.get(i);
			ryncPlayerPoint(playerView.getPlayerId(), playerView.getPlayerNameRaw(), Long.valueOf(playerView.getPlayerPoint()));
		}
	}

	public void ryncPlayerPoint(String playerId, String remarkName, Long newPointvel) {
		Player playEntity = null;
		playEntity = playerService.getPlayerById(playerId);
		if (playEntity == null) {
			playEntity = new Player();
			playEntity.setId(playerId);
			playEntity.setRemarkName(remarkName);
		}
		playEntity.setPoints(newPointvel);
		playerService.save(playEntity);
	}
	
	public void ryncPlayerPoint(String playerId, String remarkName, Boolean plusOrMinus, Long newPointvel) {
		Player playEntity = null;
		Long oldPointVel = Long.valueOf(0);
		playEntity = playerService.getPlayerById(playerId);
		if (playEntity == null) {
			LOGGER.warn("User[{}] cann't be found, the point option failed!",remarkName);
			return ;
		}
		playEntity.setPoints(plusOrMinus?oldPointVel + newPointvel:oldPointVel - newPointvel);
		playerService.save(playEntity);
	}

}
