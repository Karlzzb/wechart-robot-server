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
import com.karl.db.domain.GameInfo;
import com.karl.db.domain.Player;
import com.karl.db.domain.PlayerTrace;
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
				approvalTabController.addApply(
						webChatId,
						addPlayApply(webChatId, remarkName,
								AppUtils.APPLYADDPOINT,
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
				approvalTabController.addApply(
						webChatId,
						addPlayApply(webChatId, remarkName,
								AppUtils.APPLYSUBPOINT,
								Long.valueOf(subPointMatcher.group(1))));
			} catch (Exception e) {
				LOGGER.error("User{" + remarkName + "} sub point{"
						+ subPointMatcher.group(1) + "failed!", e);
			}
			return;
		}

		// Match put point
		Matcher putPointMatcher = StringUtils.PUTPOINT.matcher(content);
		while (putPointMatcher.find()) {
			try {
				Long putPoint = Long.valueOf(putPointMatcher.group(1));
				String readyWechatId = runtimeDomain.getReadyWechatId();
				if (readyWechatId == null || readyWechatId.isEmpty()) {
					return;
				}
				String readyRemarkName = runtimeDomain
						.getUserRemarkName(readyWechatId);
				String readyNickName = runtimeDomain
						.getUserNickName(readyWechatId);
				if (readyRemarkName == null || readyRemarkName.isEmpty()) {
					return;
				}
				Player pEntity = putPlayerPoint(readyWechatId, readyRemarkName,
						readyNickName, putPoint, Boolean.TRUE);
				// send feedback
				if (pEntity != null) {
					String replyTemplate = AppUtils.REPLYPOINTAPPLYPUT;
					webWechat.webwxsendmsgM(MessageFormat.format(replyTemplate,
							readyRemarkName, putPoint, pEntity.getPoints()));
				}

			} catch (Exception e) {
				LOGGER.error("User{" + remarkName + "} sub point{"
						+ subPointMatcher.group(1) + "failed!", e);
			}
			return;
		}

		// Match put point
		Matcher drawPointMatcher = StringUtils.DRAWPOINT.matcher(content);
		while (drawPointMatcher.find()) {
			try {
				Long drawPoint = Long.valueOf(drawPointMatcher.group(1));
				String readyWechatId = runtimeDomain.getReadyWechatId();
				if (readyWechatId == null || readyWechatId.isEmpty()) {
					return;
				}
				String readyRemarkName = runtimeDomain
						.getUserRemarkName(readyWechatId);
				String readyNickName = runtimeDomain
						.getUserNickName(readyWechatId);
				if (readyRemarkName == null || readyRemarkName.isEmpty()) {
					return;
				}
				Player pEntity = putPlayerPoint(readyWechatId, readyRemarkName,
						readyNickName, drawPoint, Boolean.FALSE);
				// send feedback
				if (pEntity != null) {
					String replyTemplate = AppUtils.REPLYPOINTAPPLYDRAW;
					webWechat.webwxsendmsgM(MessageFormat.format(replyTemplate,
							readyRemarkName, drawPoint, pEntity.getPoints()));
				}

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
		Long currentGameId = runtimeDomain.getCurrentGameId();
		if (currentGameId == null || currentGameId.compareTo(Long.valueOf(1)) < 0) {
			return;
		}
		GameInfo gameInfo = playerService.getlatestGame();
		if (gameInfo == null) {
			return;
		}
		List<PlayerTrace> traceList = playerService.getPlayerTraceListByGameId(currentGameId);
		if (traceList == null) {
			return;
		}
		Long bankerTimes = getResult(gameInfo.getLuckInfo());
		Long resultTimes = Long.valueOf(0);
		Long resultPoint = Long.valueOf(0);
		Long bankerPoint = gameInfo.getBankerPoint();
		PlayerTrace trace = null;
		for (int i = 0; i < traceList.size(); i++) {
			trace = traceList.get(i);
			resultTimes = getResult(trace.getLuckInfo());
			resultPoint = (bankerTimes - resultTimes)*trace.getBetPoint();
			
			if (bankerPoint.compareTo(Long.valueOf(0))==0||(bankerPoint - resultPoint)<=0) {
				resultPoint = bankerPoint;
				bankerPoint = Long.valueOf(0);
			}else {
				bankerPoint = bankerPoint - resultPoint;
			}
			
			playerService.updateResult(bankerTimes - resultTimes, resultPoint, trace.getTraceId());
			if (Long.valueOf(0).compareTo(resultPoint)==0) {
				continue;
			}
			ryncPlayerPoint(trace.getPlayerId(), resultPoint>0, Math.abs(resultPoint));
		}
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
			return;
		}
		Integer betIndex = Integer.valueOf(0);
		Long betPoint = Long.valueOf(0);
		if (AppUtils.PLAYLONG.equals(runtimeDomain.getCurrentGameKey())) {
			betPoint = Long.valueOf(betInfo);
		} else if (AppUtils.PLAYLONGSPLIT.equals(runtimeDomain
				.getCurrentGameKey())) {
			String[] betInfos = betInfo.split(StringUtils.BETSPLIT);
			betIndex = Integer.valueOf(betInfos[0]);
			betPoint = Long.valueOf(betInfos[1]);
		}

		PlayerTrace playerTrace = new PlayerTrace(
				runtimeDomain.getCurrentGameId(), player.getPlayerId(),
				player.getWebchatId(), player.getWechatName(), remarkName,
				betInfo, betPoint, betIndex, (new Date()).getTime());
		playerService.save(playerTrace);
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
		} else if (AppUtils.PLAYLUCKWAY.equals(runtimeDomain
				.getCurrentGameKey())) {
			//TODO the luck way
		}
	}

	/**
	 * put luck info into the current player list
	 * 
	 * @param remarkName
	 * @param luckInfo
	 */
	private void puttingLuckInfo(Integer index, Double luckInfo) {
		if (index.compareTo(runtimeDomain.getBankerIndex()) == 0) {
			playerService.updateBankerLuckInfo(runtimeDomain.getCurrentGameId(), luckInfo);
			return;
		}
		playerService.updateLuckInfo(luckInfo,
				runtimeDomain.getCurrentGameId(), index);
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
			return;
		}
		if (remarkName.equals(runtimeDomain.getBankerRemarkName())) {
			playerService.updateBankerLuckInfo(runtimeDomain.getCurrentGameId(), luckInfo);
			return;
		}
		playerService.updateLuckInfo(luckInfo,
				runtimeDomain.getCurrentGameId(), player.getPlayerId(),
				remarkName);
		
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

	private EnumSet<LotteryRule> currentRule() {
		return runtimeDomain.getCurrentRule();
	}

	public void initialCurrentPlayer(PlayerModel playerModle) {
		String remarkName = playerModle.getPlayerName();
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
			ryncPlayerPoint(playerView.getWechatId(),
					playerView.getWechatName(), playerView.getPlayerId(),
					playerView.getPlayerNameRaw(),
					Long.valueOf(playerView.getPlayerPoint()));
		}
	}

	private void ryncPlayerPoint(String webchatId, String wechatName,
			String playerId, String remarkName, Long newPointvel) {
		Player playEntity = null;
		playEntity = playerService.getPlayerById(playerId);
		if (playEntity == null) {
			playEntity = new Player();
			playEntity.setPlayerId(playerId);
			playEntity.setRemarkName(remarkName);
			playEntity.setWechatName(wechatName);
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
		PlayerTrace trace = null;
		Player player = null;
		int i = 0;
		long sumPoints = Long.valueOf(0);
		long sumPointsAllIN = Long.valueOf(0);
		
		List<PlayerTrace> playerTraceList = playerService.getPlayerTraceListByGameId(runtimeDomain.getCurrentGameId());
		if (playerTraceList != null) {
			for (int j = 0; j < playerTraceList.size(); j++) {
				player = playerMap.get(trace.getRemarkName());
				if (player == null) {
					continue;
				}
				trace = playerTraceList.get(i);
				content += MessageFormat.format(AppUtils.BETRESULTLINE,
						trace.getRemarkName(),
						String.valueOf(player.getPoints()),
						trace.getBetInfo());
				i++;
				sumPoints += trace.getBetPoint();
				if (trace.getBetPoint().compareTo(player.getPoints()*LotteryRule.MOMO_SAME.getTimes()) >= 0) {
					sumPointsAllIN += trace.getBetPoint();
				}
			}
		}
		content += MessageFormat.format(AppUtils.BETRESULTTAIL, i, 6, 14,
				sumPoints, sumPoints - sumPointsAllIN, sumPointsAllIN);

		return content;

	}

	private Player ryncPlayerPoint(String playerId, Boolean plusOrMinus,
			Long newPointvel) {
		Player playEntity = null;
		playEntity = playerService.getPlayerById(playerId);
		if (playEntity == null) {
			LOGGER.warn("User[{}] cann't be found, the point option failed!",
					playerId);
			return null;
		}
		Long oldPointVel = playEntity.getPoints();
		playEntity.setPoints(plusOrMinus ? oldPointVel + newPointvel
				: oldPointVel - newPointvel);
		savePlayEntity(playEntity);

		return playEntity;
	}

	private Player putPlayerPoint(String webchatId, String remarkName,
			String nickName, Long newPointvel, Boolean plusOrMinus) {
		Player playEntity = null;
		playEntity = playerService.getPlayerByRemarkName(remarkName);
		if (playEntity == null) {
			playEntity = new Player();
			playEntity.setRemarkName(remarkName);
			playEntity.setPoints(Long.valueOf(0));
		}
		Long oldPointVel = playEntity.getPoints();
		playEntity.setWebchatId(webchatId);
		playEntity.setWechatName(nickName);
		playEntity.setPoints(plusOrMinus ? oldPointVel + newPointvel
				: oldPointVel - newPointvel);
		savePlayEntity(playEntity);

		return playEntity;
	}

	private void savePlayEntity(Player player) {
		player.setWechatName(runtimeDomain.getUserNickName(player
				.getWebchatId()));
		playerService.save(player);
		runningPlayers().put(player.getRemarkName(), player);
	}

	public List<ApplyPoints> getUncheckedApplyList() {
		return playerService.findByApprovalStatus(AppUtils.APPROVALNONE);
	}

	public boolean approvalPlayer(Long applyId, String playerId,
			Integer applyType, Integer approvalStatus, Long point,
			String wechatId) {
		try {
			String replyTemplate = "";
			Player pEntity = null;
			if (Integer.compare(AppUtils.APPROVALYES, approvalStatus) == 0) {
				if (Integer.compare(AppUtils.APPLYADDPOINT, applyType) == 0) {
					pEntity = ryncPlayerPoint(playerId, Boolean.TRUE, point);
					replyTemplate = AppUtils.REPLYPOINTAPPLYADD;
				} else if (Integer.compare(AppUtils.APPLYSUBPOINT, applyType) == 0) {
					pEntity = ryncPlayerPoint(playerId, Boolean.FALSE, point);
					replyTemplate = AppUtils.REPLYPOINTAPPLYSUB;
				}
				// send feedback
				webWechat.webwxsendmsg(MessageFormat.format(replyTemplate,
						runtimeDomain.getUserNickName(wechatId), point,
						pEntity.getPoints()));
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

	public ApplyPoints addPlayApply(String webChatId, String remarkName,
			Integer applyType, Long points) {
		Player player = playerService.getPlayerByRemarkName(remarkName);
		if (player != null) {
			ApplyPoints apply = new ApplyPoints();
			apply.setPlayerId(player.getPlayerId());
			apply.setRemarkName(player.getRemarkName());
			apply.setApplyType(applyType);
			apply.setPoints(points);
			apply.setApplyTime((new Date()).getTime());
			apply.setWebChatId(webChatId);
			apply.setWebchatName(runtimeDomain.getUserNickName(webChatId));
			return playerService.save(apply);
		}
		return null;
	}
}
