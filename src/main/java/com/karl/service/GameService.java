package com.karl.service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.karl.db.domain.ApplyPoints;
import com.karl.db.domain.Player;
import com.karl.db.service.PlayerService;
import com.karl.domain.LotteryRule;
import com.karl.domain.RuntimeDomain;
import com.karl.fx.controller.ApprovalTabController;
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
	
	@Autowired
	@Lazy
	private ApprovalTabController approvalTabController;

	public void mainMessageHandle(String webChatId, String remarkName,
			String content) {

		// Message is the pattern of betting
		if (runtimeDomain.getGlobalGameSignal()) {
			Matcher matcher = StringUtils.LONGSPLIT.matcher(content);
			if (AppUtils.PLAYLONG.equals(runtimeDomain.getCurrentGameKey())) {
				matcher = StringUtils.LONG.matcher(content);
			} else if (AppUtils.PLAYLONGSPLIT.equals(runtimeDomain
					.getCurrentGameKey())) {
				matcher = StringUtils.LONGSPLIT.matcher(content);
			}

			if (matcher.find()) {
				puttingBetInfo(webChatId, remarkName, matcher.group());
				return;
			}
		}

		// Message is the pattern of apply
		Matcher addPointMatcher = StringUtils.ADDPOINT.matcher(content);
		if (addPointMatcher.find()) {
			try {
				approvalTabController.addApply(addPlayApply(remarkName, AppUtils.APPLYADDPOINT,
						Long.valueOf(addPointMatcher.group(1))));
			} catch (Exception e) {
				LOGGER.error("User{" + remarkName + "} add point{"
						+ addPointMatcher.group(1) + "failed!", e);
			}
			return;
		}

		Matcher subPointMatcher = StringUtils.SUBPOINT.matcher(content);
		if (subPointMatcher.find()) {
			try {
				approvalTabController.addApply(addPlayApply(remarkName, AppUtils.APPLYADDPOINT,
						Long.valueOf(subPointMatcher.group(1))));
			} catch (Exception e) {
				LOGGER.error("User{" + remarkName + "} sub point{"
						+ subPointMatcher.group(1) + "failed!", e);
			}
			return;
		}

	}

	/**
	 * TODO no use yet open the lottery
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

	public void initialCurrentPlayer(PlayerModel playerModle, String remarkName) {
		if (playerModle == null || remarkName == null || remarkName.isEmpty()) {
			return;
		}
		Player playerEntity = playerService.getPlayerByRemarkName(remarkName);
		if (playerEntity != null) {
			rsynPlayerEntityToModel(playerModle, playerEntity);
			runningPlayers().put(playerEntity.getRemarkName(), playerEntity);
		}
	}

	public void rsynPlayerEntityToModel(PlayerModel playerModle,
			Player playerEntity) {
		if (playerEntity != null && playerModle != null) {
			playerModle.setPlayerName(playerEntity.getRemarkName());
			playerModle.setWechatId(playerEntity.getWebchatId());
			playerModle.setPlayerId(playerEntity.getPlayerId());
			playerModle
					.setPlayerPoint(String
							.valueOf(playerEntity.getPoints() == null ? 0
									: playerEntity.getPoints()));
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
				if (player.getLatestBet() == null
						|| AppUtils.NONEBET.equals(player.getLatestBet())
						|| player.getLatestBetValue() == null
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


	private void ryncPlayerPoint(String playerId,
			Boolean plusOrMinus, Long newPointvel) {
		Player playEntity = null;
		Long oldPointVel = Long.valueOf(0);
		playEntity = playerService.getPlayerById(playerId);
		if (playEntity == null) {
			LOGGER.warn("User[{}] cann't be found, the point option failed!",
					playerId);
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

	public List<ApplyPoints> getUncheckedApplyList() {
		return playerService.findByApprovalStatus(AppUtils.APPROVALNONE);
	}

	public boolean approvalPlayer(Long applyId, String playerId, Integer approvalStatus, Long point) {
		try {
			
			if (Integer.compare(AppUtils.APPLYADDPOINT, approvalStatus) == 0) {
				ryncPlayerPoint(playerId, Boolean.TRUE, point);
			}else if (Integer.compare(AppUtils.APPLYSUBPOINT, approvalStatus) == 0) {
				ryncPlayerPoint(playerId, Boolean.FALSE, point);
			}
			playerService.approveRequest(applyId, approvalStatus,
					(new Date()).getTime());
			return Boolean.TRUE;
		} catch (Exception e) {
			LOGGER.error(
					"Apply{" + applyId + "} change approvalStatus failed!", e);
		}
		return Boolean.FALSE;
	}

	public ApplyPoints addPlayApply(String remarkName, Integer applyType, Long points) {
		Player player = playerService.getPlayerByRemarkName(remarkName);
		if (player != null) {
			ApplyPoints apply = new ApplyPoints();
			apply.setPlayerId(player.getPlayerId());
			apply.setRemarkName(player.getRemarkName());
			apply.setApplyType(applyType);
			apply.setPoints(points);
			apply.setApplyTime((new Date()).getTime());
			return playerService.save(apply);
		}
		return null;
	}

}
