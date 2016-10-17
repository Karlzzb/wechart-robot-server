package com.karl.service;

import java.text.MessageFormat;
import java.util.ArrayList;
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
import org.springframework.transaction.annotation.Transactional;

import com.karl.db.domain.ApplyPoints;
import com.karl.db.domain.GameInfo;
import com.karl.db.domain.Player;
import com.karl.db.domain.PlayerTrace;
import com.karl.db.service.PlayerService;
import com.karl.domain.LotteryRule;
import com.karl.domain.RuntimeDomain;
import com.karl.fx.controller.ApprovalTabController;
import com.karl.fx.controller.GameRunningTabController;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.AppUtils;
import com.karl.utils.DateUtils;
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
	
	
	@Autowired
	@Lazy
	private GameRunningTabController gameRunningTabController;

	public void mainMessageHandle(String messageFrom, String webChatId,
			String remarkName, String content) {

		// Message is the pattern of betting
		if (runtimeDomain.getGlobalGameSignal()
				&& messageFrom.equals(runtimeDomain.getCurrentGroupId())
				&& !runtimeDomain.getCurrentGameKey().equals(
						AppUtils.PLAYLUCKWAY)) {
			Matcher matcher = StringUtils.LONGSPLIT.matcher(content);
			if (AppUtils.PLAYLONG.equals(runtimeDomain.getCurrentGameKey())) {
				matcher = StringUtils.LONG.matcher(content);
			} else if (AppUtils.PLAYLONGSPLIT.equals(runtimeDomain
					.getCurrentGameKey())) {
				matcher = StringUtils.LONGSPLIT.matcher(content);
			}

			if (matcher.find()) {
				puttingBetInfo(webChatId, remarkName, matcher.group(),
						Boolean.FALSE);
				return;
			}
		}

		// Message is the pattern of betting suoha
		if (runtimeDomain.getGlobalGameSignal()
				&& messageFrom.equals(runtimeDomain.getCurrentGroupId())
				&& !runtimeDomain.getCurrentGameKey().equals(
						AppUtils.PLAYLUCKWAY) && runtimeDomain.getAllowAllIn()) {
			Matcher matcher = StringUtils.SUOHAPERF.matcher(content);
			if (AppUtils.PLAYLONG.equals(runtimeDomain.getCurrentGameKey())) {
				matcher = StringUtils.LONG.matcher(content);
			} else if (AppUtils.PLAYLONGSPLIT.equals(runtimeDomain
					.getCurrentGameKey())) {
				matcher = StringUtils.LONGSPLIT.matcher(content);
			}

			if (matcher.find()) {
				puttingBetInfo(webChatId, remarkName, matcher.group(1),
						Boolean.TRUE);
				return;
			}
		}

		// Message is the pattern of apply add point
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

		// Message is the pattern of apply sub point
		Matcher subPointMatcher = StringUtils.SUBPOINT.matcher(content);
		if (subPointMatcher.find()) {
			try {
				Long subPoint = Long.valueOf(subPointMatcher.group(1));

				// in case of point eq zero
				Player pEntity = runtimeDomain.getRunningPlayeres().get(
						remarkName);
				if (pEntity == null) {
					webWechat.webwxsendmsgM(MessageFormat.format(
							AppUtils.REPLYPOINTAPPLYERROR2, remarkName, 0,
							subPoint, remarkName));
					return;
				}
				if (pEntity.getPoints().compareTo(subPoint) < 0) {
					webWechat.webwxsendmsgM(MessageFormat.format(
							AppUtils.REPLYPOINTAPPLYERROR2, remarkName,
							pEntity.getPoints(), subPoint, remarkName));
					return;
				}

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
		if (messageFrom.equals(runtimeDomain.getCurrentGroupId())) {
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
					Player pEntity = putPlayerPoint(readyWechatId,
							readyRemarkName, readyNickName, putPoint,
							Boolean.TRUE);
					// send feedback
					if (pEntity != null) {
						String replyTemplate = AppUtils.REPLYPOINTAPPLYPUT;
						webWechat.webwxsendmsgM(MessageFormat.format(
								replyTemplate, readyRemarkName, putPoint,
								pEntity.getPoints()));
					}

				} catch (Exception e) {
					LOGGER.error("User{" + remarkName + "} sub point{"
							+ subPointMatcher.group(1) + "failed!", e);
				}
				return;
			}

			// Match sub point
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

					// in case of point eq zero
					Player pEntity = runtimeDomain.getRunningPlayeres().get(
							readyRemarkName);
					if (pEntity == null) {
						webWechat.webwxsendmsgM(MessageFormat.format(
								AppUtils.REPLYPOINTAPPLYERROR, readyRemarkName,
								0, drawPoint));
						return;
					}
					if (pEntity.getPoints().compareTo(drawPoint) < 0) {
						webWechat.webwxsendmsgM(MessageFormat.format(
								AppUtils.REPLYPOINTAPPLYERROR, readyRemarkName,
								pEntity.getPoints(), drawPoint));
						return;
					}

					// sub points
					pEntity = putPlayerPoint(readyWechatId, readyRemarkName,
							readyNickName, drawPoint, Boolean.FALSE);
					if (pEntity != null) {
						String replyTemplate = AppUtils.REPLYPOINTAPPLYDRAW;

						webWechat.webwxsendmsgM(MessageFormat.format(
								replyTemplate, readyRemarkName, drawPoint,
								pEntity.getPoints()));
					}

				} catch (Exception e) {
					LOGGER.error("User{" + remarkName + "} sub point{"
							+ subPointMatcher.group(1) + "failed!", e);
				}
				return;
			}
		}
	}

	@Transactional
	public GameInfo undoTheGame(Long gameId) {
		if (gameId == null || gameId.compareTo(0L) < 0) {
			return null;
		}
		List<PlayerTrace> traceList = playerService
				.getPlayerTraceListByGameId(gameId);
		GameInfo gameInfo = playerService.getGameById(gameId);
		if (gameInfo == null || traceList == null) {
			return gameInfo;
		}

		// write the data to db
		for (int i = 0; i < traceList.size(); i++) {
			if (Long.valueOf(0).compareTo(traceList.get(i).getResultPoint()) == 0) {
				continue;
			}
			ryncPlayerPoint(traceList.get(i).getPlayerId(), traceList.get(i)
					.getResultPoint() < 0, Math.abs(traceList.get(i)
					.getResultPoint()));
		}
		// banker point consistent
		ryncPlayerPoint(gameInfo.getPlayerId(), gameInfo.getResultPoint()
				.compareTo(Long.valueOf(0)) < 0, Math.abs(gameInfo
				.getResultPoint()));
		gameInfo.setIsUndo(Boolean.TRUE);
		playerService.save(gameInfo);
		return gameInfo;
	}

	@Transactional
	public String openLottery() {

		Long currentGameId = runtimeDomain.getCurrentGameId();
		if (currentGameId == null
				|| currentGameId.compareTo(Long.valueOf(1)) < 0) {
			return null;
		}
		GameInfo gameInfo = playerService.getGameById(currentGameId);
		if (gameInfo == null) {
			return null;
		}
		List<PlayerTrace> traceList = playerService
				.getPlayerTraceListByGameId(currentGameId);
		if (traceList == null || traceList.size() < 1) {
			return null;
		}

		// Get first package time
		Long firstPackgeTime = runtimeDomain.getCurrentFirstPackegeTime()
				.getTime();

		// banker
		Integer bankerTimes = gameInfo.getResultTimes();
		Long bankerState = 0L;
		Double bankerLuck = gameInfo.getLuckInfo();
		Long bankerPackageTime = gameInfo.getLuckTime();

		// pure math divide player to different groups
		List<PlayerTrace> winnerList = new ArrayList<PlayerTrace>();
		List<PlayerTrace> loserList = new ArrayList<PlayerTrace>();
		List<PlayerTrace> paceList = new ArrayList<PlayerTrace>();
		List<PlayerTrace> allInList = new ArrayList<PlayerTrace>();

		// cut the all fee
		bankerState -= runtimeDomain.getManageFee();
		Long packageFee = runtimeDomain.getCurrentPackageFee(traceList,
				gameInfo);
		bankerState -= packageFee;

		// caculate win or lose
		PlayerTrace trace = null;
		Player pEntity = null;
		for (int i = 0; i < traceList.size(); i++) {
			trace = traceList.get(i);
			pEntity = runningPlayers().get(trace.getRemarkName());
			if (pEntity == null) {
				LOGGER.error("Player{} is not in running Map",
						trace.getRemarkName());
				continue;
			}

			// check banker time out
			if (bankerPackageTime - firstPackgeTime > runtimeDomain
					.getCurrentTimeOut() * 1000) {
				if (runtimeDomain.getCurrentTimeOutRuleBanker().equals(
						AppUtils.TIMEOUTPAIDALL)) {
					trace.setResultPoint(trace.getResultTimes()
							* trace.getBetPoint());
				} else if (runtimeDomain.getCurrentTimeOutRuleBanker().equals(
						AppUtils.TIMEOUTPAIDONETIME)) {
					trace.setResultPoint(trace.getBetPoint());
				} else if (runtimeDomain.getCurrentTimeOutRuleBanker().equals(
						AppUtils.TIMEOUTPAIDNONE)) {
					trace.setResultPoint(Long.valueOf(0));
					continue;
				}

				winnerList.add(trace);
				bankerState -= trace.getResultPoint();
				continue;
			}

			// check player time out
			if (trace.getLuckTime() - firstPackgeTime > runtimeDomain
					.getCurrentTimeOut() * 1000) {
				if (runtimeDomain.getCurrentTimeOutRule().equals(
						AppUtils.TIMEOUTPAIDALL)) {
					singleLostHandle(bankerTimes, loserList, allInList, trace,
							pEntity);
					continue;
				} else if (runtimeDomain.getCurrentTimeOutRule().equals(
						AppUtils.TIMEOUTPAIDONETIME)) {
					trace.setResultPoint(-trace.getBetPoint());
					loserList.add(trace);
					bankerState -= trace.getResultPoint();
					continue;
				} else if (runtimeDomain.getCurrentTimeOutRule().equals(
						AppUtils.TIMEOUTPAIDNONE)) {
					trace.setResultPoint(Long.valueOf(0));
				}
				continue;
			}

			// compare point between banker and player
			if (bankerTimes > trace.getResultTimes()
					|| (bankerTimes == trace.getResultTimes() && bankerLuck
							.compareTo(trace.getLuckInfo()) > 0)) {
				singleLostHandle(bankerTimes, loserList, allInList, trace,
						pEntity);
			} else if (bankerTimes < trace.getResultTimes()
					|| (bankerTimes == trace.getResultTimes() && bankerLuck
							.compareTo(trace.getLuckInfo()) < 0)) {
				singleWinHandle(winnerList, allInList, trace, pEntity);
			} else {
				// pace
				if (runtimeDomain.getAllowPace()) {
					trace.setResultPoint(Long.valueOf(0));
					paceList.add(trace);
				} else {
					trace.setResultPoint(-trace.getBetPoint());
					loserList.add(trace);
				}
			}
			bankerState -= trace.getResultPoint();
		}

		// if allow banker betpoint < 0
		if (!runtimeDomain.getAllowInvainBanker()
				&& Long.valueOf(0).compareTo(
						bankerState + runtimeDomain.getBankerBetPoint()) > 0) {
			for (int i = winnerList.size() - 1; i > -1; i--) {
				bankerState += winnerList.get(i).getResultPoint();
				if (Long.valueOf(0).compareTo(
						bankerState + runtimeDomain.getBankerBetPoint()) < 0) {
					winnerList.get(i).setResultPoint(
							bankerState + runtimeDomain.getBankerBetPoint());
					bankerState -= winnerList.get(i).getResultPoint();
				} else {
					winnerList.get(i).setResultPoint(Long.valueOf(0));
				}
				if (Long.valueOf(0).compareTo(
						bankerState + runtimeDomain.getBankerBetPoint()) == 0) {
					break;
				}
			}
		}

		// banker win cut
		Long bankerWinCut = 0L;
		if (bankerState.compareTo(Long.valueOf(0)) > 0) {
			bankerWinCut = bankerState * runtimeDomain.getBankerWinCutRate()
					/ 100L;
			bankerState -= bankerWinCut;
		}

		// write the data to db
		for (int i = 0; i < traceList.size(); i++) {
			if (Long.valueOf(0).compareTo(traceList.get(i).getResultPoint()) == 0) {
				continue;
			}
			ryncPlayerPoint(traceList.get(i).getPlayerId(), traceList.get(i)
					.getResultPoint() > 0, Math.abs(traceList.get(i)
					.getResultPoint()));
			playerService.save(traceList.get(i));
		}

		// banker point consistent
		ryncPlayerPoint(gameInfo.getPlayerId(),
				bankerState.compareTo(Long.valueOf(0)) > 0,
				Math.abs(bankerState));
		gameInfo.setResultPoint(bankerState);
		playerService.save(gameInfo);

		// sync banker betpoint on the view
		runtimeDomain.setBankerBetPoint(bankerState > 0 ? runtimeDomain
				.getBankerBetPoint() : bankerState
				+ runtimeDomain.getBankerBetPoint());

		// config the message
		String winListStr = "";
		for (int i = 0; i < winnerList.size(); i++) {
			if (winnerList.get(i).getResultPoint().compareTo(Long.valueOf(0)) != 0) {
				winListStr += MessageFormat.format(
						AppUtils.GAMERESULTWIN,
						winnerList.get(i).getRemarkName(),
						winnerList.get(i).getBetPoint(),
						winnerList.get(i).getResultRuleName() + "("
								+ winnerList.get(i).getLuckInfo() + ")",
						winnerList.get(i).getResultPoint()).toString();
			} else {
				winListStr += MessageFormat.format(
						AppUtils.GAMERESULTINVAIN,
						winnerList.get(i).getRemarkName(),
						winnerList.get(i).getBetPoint(),
						winnerList.get(i).getResultRuleName() + "("
								+ winnerList.get(i).getLuckInfo() + ")")
						.toString();
			}
			winListStr += "\n";
		}
		String loseListStr = "";
		for (int i = 0; i < loserList.size(); i++) {
			loseListStr += MessageFormat.format(
					AppUtils.GAMERESULTLOSE,
					loserList.get(i).getRemarkName(),
					loserList.get(i).getBetPoint(),
					loserList.get(i).getResultRuleName() + "("
							+ loserList.get(i).getLuckInfo() + ")",
					Math.abs(loserList.get(i).getResultPoint())).toString();
			loseListStr += "\n";
		}
		String allInListStr = "";
		for (int i = 0; i < allInList.size(); i++) {
			if (allInList.get(i).getResultPoint().compareTo(Long.valueOf(0)) > 0) {
				allInListStr += MessageFormat.format(
						AppUtils.GAMERESULTWIN,
						allInList.get(i).getRemarkName(),
						allInList.get(i).getBetPoint(),
						allInList.get(i).getResultRuleName() + "("
								+ allInList.get(i).getLuckInfo() + ")",
						allInList.get(i).getResultPoint()).toString();
			} else {
				allInListStr += MessageFormat.format(
						AppUtils.GAMERESULTLOSE,
						allInList.get(i).getRemarkName(),
						allInList.get(i).getBetPoint(),
						allInList.get(i).getResultRuleName() + "("
								+ allInList.get(i).getLuckInfo() + ")",
						Math.abs(allInList.get(i).getResultPoint())).toString();
			}
			allInListStr += "\n";
		}
		String timeoutStr = "---------[超时]---------\n";
		for (int i = 0; i < traceList.size(); i++) {
			if (traceList.get(i).getLuckTime() - firstPackgeTime <= runtimeDomain
					.getCurrentTimeOut() * 1000) {
				continue;
			}
			timeoutStr += MessageFormat.format(
					AppUtils.GAMERESULTTIMEOUT,
					traceList.get(i).getRemarkName(),
					traceList.get(i).getResultRuleName() + "("
							+ traceList.get(i).getLuckInfo() + ")",
					runtimeDomain.getCurrentTimeOutRule().equals(
							AppUtils.TIMEOUTPAIDNONE) ? "0" : Math
							.abs(traceList.get(i).getResultPoint())).toString();
		}

		String content = MessageFormat.format(
				AppUtils.GAMERESULT,
				runtimeDomain.getCurrentGameId(),
				runtimeDomain.getCurrentGameKey(),
				runtimeDomain.getCurrentGroupName(),
				winListStr,
				loseListStr,
				allInListStr,
				DateUtils.timeStamp(runtimeDomain.getCurrentLastPackegeTime()
						.getTime()),
				DateUtils.timeStamp(runtimeDomain.getCurrentFirstPackegeTime()
						.getTime() + runtimeDomain.getCurrentTimeOut() * 1000),
				DateUtils.timeStamp(runtimeDomain.getCurrentFirstPackegeTime()
						.getTime()),
				gameInfo.getBankerRemarkName(),
				gameInfo.getLuckInfo(),
				gameInfo.getResultRuleName(),
				gameInfo.getResultTimes(),
				winnerList.size(),// 13
				loserList.size(),// 14
				paceList.size(),// 15
				runtimeDomain.getBankerBetPoint(),
				traceList.size(),
				packageFee,
				bankerWinCut,
				runtimeDomain.getRunningPlayeres()
						.get(runtimeDomain.getBankerRemarkName()).getPoints(),
				timeoutStr,
				runtimeDomain.getShowManageFee() ? "管理费： "
						+ runtimeDomain.getManageFee() + "\n" : "",
				gameInfo.getResultPoint() + runtimeDomain.getManageFee()
						+ packageFee + bankerWinCut,
				(bankerPackageTime - firstPackgeTime > runtimeDomain
						.getCurrentTimeOut() * 1000) ? "庄家超时: "
						+ DateUtils.timeStamp(bankerPackageTime) + "\n" : "");

		return content;
	}

	private void singleWinHandle(List<PlayerTrace> winnerList,
			List<PlayerTrace> allInList, PlayerTrace trace, Player pEntity) {
		// win
		if (trace.getIslowRisk()) {
			// self want low risk
			trace.setResultPoint(trace.getBetPoint());
			allInList.add(trace);
		} else if (pEntity.getPoints().compareTo(
				trace.getResultTimes() * trace.getBetPoint()) <= 0) {
			// have to low risk
			trace.setResultPoint(pEntity.getPoints());
			allInList.add(trace);
		} else if (!trace.getIslowRisk()
				&& pEntity.getPoints().compareTo(
						trace.getResultTimes() * trace.getBetPoint()) > 0) {
			// normal
			trace.setResultPoint(trace.getResultTimes() * trace.getBetPoint());
		}
		winnerList.add(trace);
	}

	private void singleLostHandle(Integer bankerTimes,
			List<PlayerTrace> loserList, List<PlayerTrace> allInList,
			PlayerTrace trace, Player pEntity) {
		// lose
		if (trace.getIslowRisk()) {
			// self want low risk
			trace.setResultPoint(-trace.getBetPoint());
			allInList.add(trace);
		} else if (pEntity.getPoints().compareTo(
				Math.abs(bankerTimes * trace.getBetPoint())) <= 0
				&& !runtimeDomain.getAllowInvainPlayer()) {
			// have to low risk
			trace.setResultPoint(-pEntity.getPoints());
			allInList.add(trace);
		} else if (!trace.getIslowRisk()
				&& pEntity.getPoints().compareTo(
						Math.abs(bankerTimes * trace.getBetPoint())) > 0) {
			// normal
			trace.setResultPoint(-bankerTimes * trace.getBetPoint());
		} else if (runtimeDomain.getAllowInvainPlayer()) {
			// normal
			trace.setResultPoint(-bankerTimes * trace.getBetPoint());
		}
		loserList.add(trace);
	}

	/**
	 * put bet info into the current player list
	 * 
	 * @param remarkName
	 * @param betInfo
	 */
	@Transactional
	public void puttingBetInfo(String webchatId, String remarkName,
			String betInfo, Boolean isLowRisk) {
		Player player = runningPlayers().get(remarkName);
		if (player == null || runtimeDomain.getCurrentGameId() == null) {
			return;
		}
		Integer betIndex = Integer.valueOf(0);
		Long betPoint = Long.valueOf(0);
		if (betPoint.compareTo(player.getPoints()) > 0) {
			webWechat.webwxsendmsg("@" + player.getWechatName() + " 下注有误。余额("
					+ player.getPoints() + ")不足支付！");
			return;
		}

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
				betInfo, betPoint, isLowRisk, betIndex, (new Date()).getTime());
		playerService.save(playerTrace);
		gameRunningTabController.flushBetInfo();
	}

	/**
	 * put luck info into the current player list
	 * 
	 * @param remarkName
	 * @param luckInfo
	 */
	public void puttingLuckInfo(Integer index, String remarkName,
			Double luckInfo, Date time) {
		if (AppUtils.PLAYLONG.equals(runtimeDomain.getCurrentGameKey())) {
			puttingLuckInfo(remarkName, luckInfo, time);
		} else if (AppUtils.PLAYLONGSPLIT.equals(runtimeDomain
				.getCurrentGameKey())) {
			puttingLuckInfo(index, luckInfo, time);
		} else if (AppUtils.PLAYLUCKWAY.equals(runtimeDomain
				.getCurrentGameKey())) {
			puttingLuckInfoWithBetInfo(index, remarkName, luckInfo, time);
		}
		gameRunningTabController.flushLuckInfo();
	}

	/**
	 * put betinfo & luckinfo simultaneously
	 * 
	 * @param betIndex
	 * @param remarkName
	 * @param luckInfo
	 * @param time
	 */
	@Transactional
	private void puttingLuckInfoWithBetInfo(Integer betIndex,
			String remarkName, Double luckInfo, Date time) {
		if (!runtimeDomain.getGlobalGameSignal()) {
			// Game is not start. Give up the package!
			return;
		}

		Player player = runningPlayers().get(remarkName);
		if (player == null
				|| runtimeDomain.getCurrentGameId() == null
				|| player.getPoints().compareTo(runtimeDomain.getDefiendBet()) < 0) {
			return;
		}
		LotteryRule playerLottery = getResult(luckInfo);
		// benker info
		if (remarkName.equals(runtimeDomain.getBankerRemarkName())) {
			playerService.updateBankerLuckInfoWithBetInfo(
					runtimeDomain.getCurrentGameId(), luckInfo, time.getTime(),
					playerLottery.getRuleName(), playerLottery.getTimes(),
					betIndex);
			return;
		}

		// cannot duplicate bet
		Boolean isExisted = playerService.isExistByGameIdRemarkName(remarkName,
				runtimeDomain.getCurrentGameId());
		if (isExisted) {
			return;
		}

		Long betPoint = runtimeDomain.getDefiendBet();
		// betinfo
		PlayerTrace playerTrace = new PlayerTrace(
				runtimeDomain.getCurrentGameId(), player.getPlayerId(),
				player.getWebchatId(), player.getWechatName(), remarkName,
				betIndex + "/" + Long.valueOf(betPoint), betPoint,
				Boolean.FALSE, betIndex, time.getTime());
		// luckinfo
		playerTrace.setLuckInfo(luckInfo);
		playerTrace.setLuckTime(time.getTime());
		playerTrace.setResultRuleName(playerLottery.getRuleName());
		playerTrace.setResultTimes(playerLottery.getTimes());
		playerService.save(playerTrace);
	}

	/**
	 * put luck info into the current player list
	 * 
	 * @param remarkName
	 * @param luckInfo
	 */
	@Transactional
	private void puttingLuckInfo(Integer index, Double luckInfo, Date luckTime) {
		LotteryRule playerLottery = getResult(luckInfo);
		if (index.compareTo(runtimeDomain.getBankerIndex()) == 0) {
			playerService.updateBankerLuckInfo(
					runtimeDomain.getCurrentGameId(), luckInfo,
					luckTime.getTime(), playerLottery.getRuleName(),
					playerLottery.getTimes());
			return;
		}
		playerService.updateLuckInfo(luckInfo,
				runtimeDomain.getCurrentGameId(), index, luckTime.getTime(),
				playerLottery.getRuleName(), playerLottery.getTimes());
	}

	/**
	 * put luck info into the current player list
	 * 
	 * @param remarkName
	 * @param luckInfo
	 */
	@Transactional
	private void puttingLuckInfo(String remarkName, Double luckInfo,
			Date luckTime) {
		Player player = runningPlayers().get(remarkName);
		if (player == null) {
			return;
		}
		LotteryRule playerLottery = getResult(luckInfo);
		if (remarkName.equals(runtimeDomain.getBankerRemarkName())) {
			playerService.updateBankerLuckInfo(
					runtimeDomain.getCurrentGameId(), luckInfo,
					luckTime.getTime(), playerLottery.getRuleName(),
					playerLottery.getTimes());
			return;
		}
		playerService.updateLuckInfo(luckInfo,
				runtimeDomain.getCurrentGameId(), player.getPlayerId(),
				remarkName, luckTime.getTime(), playerLottery.getRuleName(),
				playerLottery.getTimes());

	}

	/**
	 * Calculate single player result
	 * 
	 * @param luckInfo
	 * @return
	 */
	public LotteryRule getResult(Double luckInfo) {
		LotteryRule lotteryRule = null;
		EnumSet<LotteryRule> theRule = currentRule();
		for (Iterator<LotteryRule> iterator = theRule.iterator(); iterator
				.hasNext();) {
			lotteryRule = (LotteryRule) iterator.next();
			if (runtimeDomain.getCurrentLotteryRule().equals(
					AppUtils.LOTTERYRULE3)) {
				if (lotteryRule.getRuleResult3(luckInfo)) {
					break;
				}
			} else if (runtimeDomain.getCurrentLotteryRule().equals(
					AppUtils.LOTTERYRULE2)) {
				if (lotteryRule.getRuleResult2(luckInfo)) {
					break;
				}
			}
		}
		return lotteryRule;
	}

	public Player findPlayerByRemarkName(String remarkName) {
		if (remarkName == null || remarkName.isEmpty()) {
			return null;
		}
		return playerService.getPlayerByRemarkName(remarkName);
	}

	private Map<String, Player> runningPlayers() {
		return runtimeDomain.getRunningPlayeres();
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
					Long.valueOf(playerView.getPlayerPoint()),
					playerView.getWechatName());
		}
	}

	public void ryncPlayersPoint(PlayerModel playerView) {
		ryncPlayerPoint(playerView.getWechatId(), playerView.getWechatName(),
				playerView.getPlayerId(), playerView.getPlayerNameRaw(),
				Long.valueOf(playerView.getPlayerPoint()),
				playerView.getWechatName());
	}

	private void ryncPlayerPoint(String webchatId, String wechatName,
			String playerId, String remarkName, Long newPointvel,
			String nickName) {
		Player playEntity = null;
		playEntity = playerService.getPlayerById(playerId);
		if (playEntity == null) {
			if (remarkName.equals(nickName)) {
				if (!webWechat
						.changeRemarkName(nickName, webchatId, remarkName)) {
					LOGGER.error("User{} remarkName Failed!", remarkName);
					return;
				}
			}
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

	public String declareBetStar() {

		Player banker = playerService.getPlayerByRemarkName(runtimeDomain
				.getBankerRemarkName());
		if (banker == null) {
			LOGGER.error("Banker{} is not existed!",
					runtimeDomain.getBankerRemarkName());
			return null;
		}
		GameInfo gameInfo = new GameInfo();
		gameInfo.setBankerPoint(runtimeDomain.getBankerBetPoint());
		gameInfo.setBankerRemarkName(banker.getRemarkName());
		gameInfo.setBetIndex(runtimeDomain.getBankerIndex());
		gameInfo.setPlayerId(banker.getPlayerId());
		playerService.save(gameInfo);
		runtimeDomain.setCurrentGameId(gameInfo.getGameSerialNo());
		runtimeDomain.removeCurrentFirstPacageTime();
		runtimeDomain.removeCurrentLastPackegeTime();

		String content = MessageFormat.format(
				AppUtils.GAMESTART,
				runtimeDomain.getCurrentGroupName(),
				runtimeDomain.getBankerRemarkName(),
				runtimeDomain.getBankerBetPoint(),
				runtimeDomain.getMaximumBet(),
				runtimeDomain.getMinimumBet(),
				runtimeDomain.getPackageNumber(),
				runtimeDomain.getBankerIndex(),
				runtimeDomain.getCurrentGameKey()
						+ (runtimeDomain.getAllowAllIn() ? "+梭哈" : ""),
				gameInfo.getGameSerialNo());

		if (runtimeDomain.getCurrentGameKey().equals(AppUtils.PLAYLUCKWAY)) {
			content += "默认下注：" + runtimeDomain.getDefiendBet();
		}

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
		if (runtimeDomain.getCurrentGameId() == null
				|| runtimeDomain.getCurrentGameId() < 1) {
			return "";
		}

		List<PlayerTrace> playerTraceList = playerService
				.getPlayerTraceListByGameId(runtimeDomain.getCurrentGameId());
		if (playerTraceList != null) {
			for (int j = 0; j < playerTraceList.size(); j++) {
				trace = playerTraceList.get(i);
				player = playerMap.get(trace.getRemarkName());
				if (player == null) {
					continue;
				}
				content += MessageFormat.format(AppUtils.BETRESULTLINE,
						trace.getRemarkName(),
						String.valueOf(player.getPoints()), trace.getBetInfo());
				i++;
				sumPoints += trace.getBetPoint();
				if (trace.getBetPoint().compareTo(
						player.getPoints() * LotteryRule.MOMO_SAME.getTimes()) >= 0) {
					sumPointsAllIN += trace.getBetPoint();
				}
			}
		}
		content += MessageFormat.format(AppUtils.BETRESULTTAIL, i, 6, 14,
				sumPoints, sumPoints - sumPointsAllIN, sumPointsAllIN);

		return content;

	}

	@Transactional
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
			if (remarkName.equals(nickName)) {
				if (!webWechat
						.changeRemarkName(nickName, webchatId, remarkName)) {
					LOGGER.error("User{} remarkName Failed!", remarkName);
					return null;
				}
			}
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

	public void savePlayEntity(Player player) {
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

	public String publishRanking() {
		String body = "";
		Player pEntity = null;
		Long sumPoint = Long.valueOf(0);
		int i = 1;
		for (String remarkName : runningPlayers().keySet()) {
			pEntity = runningPlayers().get(remarkName);
			body += MessageFormat.format(AppUtils.RANKINGLINE, i++, remarkName,
					pEntity.getPoints());
		}

		return MessageFormat.format(
				AppUtils.RANKINGLAYOUT,
				runtimeDomain.getBankerRemarkName(),
				runtimeDomain.getRunningPlayeres()
						.get(runtimeDomain.getBankerRemarkName()).getPoints(),
				runtimeDomain.getBankerBetPoint(), runningPlayers().size(),
				sumPoint, body);
	}

	public List<PlayerTrace> getCurrentPlayTrace() {
		Long currentGameId = runtimeDomain.getCurrentGameId();
		if (currentGameId == null
				|| currentGameId.compareTo(Long.valueOf(1)) < 0) {
			return null;
		}
		return playerService.getPlayerTraceListByGameId(currentGameId);
	}

	public String publishPointRanks() {
		List<Player> playerList = playerService.getPlayerListDescPoint();
		if (playerList == null) {
			return null;
		}
		String body = "";
		int order = 1;
		Long sumPoint = 0L;
		for (int i = 0; i < playerList.size(); i++) {
			if (runtimeDomain.getRunningPlayeres().get(
					playerList.get(i).getRemarkName()) == null) {
				continue;
			}
			body += MessageFormat.format(AppUtils.PUBLICPOINTRANKLINE, order++,
					playerList.get(i).getRemarkName(), playerList.get(i)
							.getPoints());
			sumPoint += playerList.get(i).getPoints();
		}

		String head = MessageFormat.format(AppUtils.PUBLICPOINTRANKHEAD,
				order - 1, sumPoint);
		String tail = MessageFormat.format(AppUtils.PUBLICPOINTRANKTAIL,
				sumPoint);
		return head + body + tail;
	}

	public GameInfo getGameById(Long gameId) {
		if (gameId == null) {
			return null;
		}
		return playerService.getGameById(gameId);
	}

	public PlayerTrace updatePlayerTraceBetInfo(Long traceId, String betInfo) {
		PlayerTrace trace = null;
		try {
			trace = playerService.getPlayerTraceById(traceId);
			trace.setBetInfo(betInfo);
			String[] betInfoStr = betInfo.split(StringUtils.BETSPLIT);
			if (betInfoStr.length < 2) {
				trace.setBetIndex(Integer.valueOf(betInfoStr[0]));
				trace.setBetPoint(Long.valueOf(betInfoStr[1]));
			}
			return playerService.save(trace);
		} catch (Exception e) {
			LOGGER.error("update PlayerTrace[" + traceId + "] betInfo["
					+ betInfo + "] failed!", e);
		}
		return trace;
	}

	public PlayerTrace updatePlayerTraceLuckInfo(Long traceId, Double luckInfo) {
		PlayerTrace trace = null;
		try {
			trace = playerService.getPlayerTraceById(traceId);
			LotteryRule playerLottery = getResult(luckInfo);
			trace.setLuckInfo(luckInfo);
			trace.setResultTimes(playerLottery.getTimes());
			trace.setResultRuleName(playerLottery.getRuleName());
			return playerService.save(trace);
		} catch (Exception e) {
			LOGGER.error("update PlayerTrace[" + traceId + "] luckInfo["
					+ luckInfo + "] failed!", e);
		}
		return trace;
	}

	public boolean deleteTraceById(Long traceId) {
		if (traceId == null || traceId < 0) {
			return Boolean.FALSE;
		}
		try {
			playerService.deleteTraceById(traceId);
			return Boolean.TRUE;
		} catch (Exception e) {

		}
		return Boolean.FALSE;
	}
}
