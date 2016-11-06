package com.karl.service;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
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
import com.karl.db.domain.GameStats;
import com.karl.db.domain.Player;
import com.karl.db.domain.PlayerTrace;
import com.karl.db.service.PlayerService;
import com.karl.domain.LotteryRule;
import com.karl.domain.RuntimeDomain;
import com.karl.fx.controller.ApprovalTabController;
import com.karl.fx.controller.GameRunningTabController;
import com.karl.fx.controller.MainDeskController;
import com.karl.fx.controller.PlayerTableController;
import com.karl.fx.model.LuckInfoModel;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.AppUtils;
import com.karl.utils.DateUtils;
import com.karl.utils.StringUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

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

	@Autowired
	@Lazy
	private PlayerTableController playerTableController;

	@Autowired
	@Lazy
	private MainDeskController mainDeskController;

	public void mainSelfMessageHandle(String content) {
		if (!runtimeDomain.getGlobalGameSignal()) {
			return;
		}

		if (!StringUtils.matchSelfPackageHead(content)) {
			return;
		}

		LOGGER.debug("Self package info【" + content + "】 Got!");
		try {
			String[] packageArray = content.split(StringUtils.SELFPACKSPLIT);
			if (packageArray == null || packageArray.length < 2) {
				return;
			}
			String line = null;
			runtimeDomain.removeCurrentFirstPacageTime();
			runtimeDomain.removeCurrentLastPackegeTime();
			runtimeDomain.clearIllegalPlayer();
			Integer index;
			String remarkName;
			Double luckInfo;
			Date luckTime = null;
			BigDecimal sumPackage = new BigDecimal(0);
			ObservableList<LuckInfoModel> luckInfoModeList = runtimeDomain
					.getLuckInfoModeList();
			luckInfoModeList.clear();
			for (int i = 1; i < packageArray.length; i++) {
				line = packageArray[i];
				if (line == null || line.isEmpty()) {
					continue;
				}
				try {
					Matcher m = StringUtils.SELFPACKLINE.matcher(line);
					if (m.find()) {
						index = Integer.valueOf(m.group(1).trim());
						remarkName = m.group(2).trim();
						luckInfo = Double.valueOf(m.group(3).trim());
						luckTime = DateUtils.parsePageDateTime(m.group(4)
								.trim());
						if (index == null || index == 0 || remarkName == null
								|| remarkName.isEmpty() || luckInfo == null
								|| luckTime == null) {
							continue;
						}
						puttingLuckInfo(index, remarkName, luckInfo, luckTime);
						runtimeDomain.setcurrentFirstPacageTime(luckTime);
						runtimeDomain.setcurrentLastPacageTime(luckTime);
						sumPackage = sumPackage.add(new BigDecimal(luckInfo));

						// for show luck table
						String playerRole = LuckInfoModel.PLAYERROLENOMAL;
						if (runningPlayers().get(remarkName) == null) {
							playerRole = LuckInfoModel.PLAYERROLENONE;
							runtimeDomain.addIllegalPlayer(remarkName);
						} else if (runningPlayers().get(remarkName).getPoints() == null
								|| runningPlayers()
										.get(remarkName)
										.getPoints()
										.compareTo(
												runtimeDomain.getDefiendBet()) < 0) {
							playerRole = LuckInfoModel.PLAYERROLENOPOINT;
							runtimeDomain.addIllegalPlayer(remarkName);
						}else if (remarkName.equals(runtimeDomain.getBankerRemarkName())) {
							playerRole = LuckInfoModel.PLAYERROLEBANKER;
						}
						luckInfoModeList.add(new LuckInfoModel(index,
								remarkName, luckInfo.toString(), m.group(4)
										.trim(), playerRole));
						LOGGER.debug("Self package single line【" + line
								+ "】 analyze success!");
					}
				} catch (Exception e) {
					LOGGER.error("Self package single line【" + line
							+ "】 analyze failed!", e);
				}
			}
			runtimeDomain.setCurrentRealPackageFee(sumPackage.longValue());
			recievedluckUIhandle();
			LOGGER.debug("Self package info【" + content + "】 analyze success!");
		} catch (Exception e) {
			LOGGER.error("Self package info【" + content + "】 analyze failed!",
					e);
		}
	}

	private void recievedluckUIhandle() {
		gameRunningTabController.openLuckInfo();
	}

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
				LOGGER.error("User{" + remarkName + "} apply add point{"
						+ content + "failed!", e);
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
				LOGGER.error("User{" + remarkName + "} apply sub point{"
						+ content + "failed!", e);
			}
			return;
		}

		// Match put point
		if (messageFrom.equals(runtimeDomain.getCurrentMGroupId())) {
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
					LOGGER.error("User{" + remarkName + "} put point{"
							+ content + "failed!", e);
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
							+ content + "failed!", e);
				}
				return;
			}
		}
	}

	@Transactional
	public GameInfo undoTheGame(GameInfo gameInfo) {
		List<PlayerTrace> traceList = playerService
				.getPlayerTraceListByGameId(gameInfo.getGameSerialNo());
		if (gameInfo == null || traceList == null) {
			return gameInfo;
		}
		// write the data to db
		for (int i = 0; i < traceList.size(); i++) {
			if (Long.valueOf(0).compareTo(traceList.get(i).getResultPoint()) == 0) {
				continue;
			}
			ryncPlayerPoint(traceList.get(i).getRemarkName(), traceList.get(i)
					.getResultPoint() < 0, Math.abs(traceList.get(i)
					.getResultPoint()));
		}
		// banker point consistent
		ryncPlayerPoint(gameInfo.getBankerRemarkName(), gameInfo
				.getResultPoint().compareTo(Long.valueOf(0)) < 0,
				Math.abs(gameInfo.getResultPoint()));
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
		Long bankerLuckTime = gameInfo.getLuckTime();

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
		Long firstBankerFee = 0L;
		if (runtimeDomain.getBeforeGameInfo() == null
				|| (runtimeDomain.getBeforeGameInfo().getGameSerialNo() > 0 && !runtimeDomain
						.getBeforeGameInfo().getBankerRemarkName()
						.equals(gameInfo.getBankerRemarkName()))) {
			firstBankerFee = runtimeDomain.getFirstBankerFee();
		}
		bankerState -= firstBankerFee;

		// caculate win or lose
		PlayerTrace trace = null;
		Player pEntity = null;
		PlayerTrace playerTraceBanker = null;
		for (int i = 0; i < traceList.size(); i++) {
			trace = traceList.get(i);
			pEntity = runningPlayers().get(trace.getRemarkName());
			if (pEntity == null) {
				LOGGER.error("Player{} is not in running Map",
						trace.getRemarkName());
				continue;
			}

			if (pEntity.getRemarkName().equals(
					runtimeDomain.getBankerRemarkName())) {
				playerTraceBanker = trace;
				continue;
			}

			// check banker has not lunckinfo
			if (bankerLuckTime == null
					|| bankerLuckTime.compareTo(Long.valueOf(0)) <= 0) {
				trace.setResultPoint(Long.valueOf(0));
			}

			// check banker time out
			if (bankerLuckTime - firstPackgeTime > runtimeDomain
					.getCurrentTimeOut() * 1000) {
				if (runtimeDomain.getCurrentTimeOutRuleBanker().equals(
						AppUtils.TIMEOUTPAIDALL)) {
					trace.setResultPoint(trace.getResultTimes()
							* trace.getBetPoint());
					winnerList.add(trace);
					bankerState -= trace.getResultPoint();
				} else if (runtimeDomain.getCurrentTimeOutRuleBanker().equals(
						AppUtils.TIMEOUTPAIDONETIME)) {
					trace.setResultPoint(trace.getBetPoint());
					winnerList.add(trace);
					bankerState -= trace.getResultPoint();
				} else if (runtimeDomain.getCurrentTimeOutRuleBanker().equals(
						AppUtils.TIMEOUTPAIDNONE)) {
					trace.setResultPoint(Long.valueOf(0));
				}
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
		if (Long.valueOf(
				bankerState + runtimeDomain.getManageFee() + packageFee
						+ firstBankerFee).compareTo(Long.valueOf(0)) > 0) {
			bankerWinCut = (bankerState + runtimeDomain.getManageFee() + packageFee)
					* runtimeDomain.getBankerWinCutRate() / 100L;
			bankerState -= bankerWinCut;
		}

		// write the data to db
		playerTraceBanker.setResultPoint(bankerState);
		for (int i = 0; i < traceList.size(); i++) {
			if (Long.valueOf(0).compareTo(traceList.get(i).getResultPoint()) != 0
					&& !traceList.get(i).getRemarkName()
							.equals(runtimeDomain.getBankerRemarkName())) {
				ryncPlayerPoint(traceList.get(i).getRemarkName(), traceList
						.get(i).getResultPoint() > 0, Math.abs(traceList.get(i)
						.getResultPoint()));
			}
			playerService.save(traceList.get(i));
		}

		// banker point consistent
		ryncPlayerPoint(gameInfo.getBankerRemarkName(),
				bankerState.compareTo(Long.valueOf(0)) > 0,
				Math.abs(bankerState));
		// game info rync to db
		gameInfo.setResultPoint(bankerState);
		gameInfo.setManageFee(runtimeDomain.getManageFee());
		gameInfo.setPackageFee(packageFee);
		gameInfo.setFirstBankerFee(firstBankerFee);
		gameInfo.setBankerWinCut(bankerWinCut);
		playerService.save(gameInfo);

		// sync banker betpoint on the view
		runtimeDomain.setBankerBetPoint(bankerState
				+ runtimeDomain.getBankerBetPoint());

		// config the message
		String content = buildBillMsg2(gameInfo, traceList,
				firstPackgeTime, bankerLuckTime, winnerList, loserList,
				paceList, allInList, packageFee, firstBankerFee, bankerWinCut);
		runtimeDomain.setBeforeGameInfo(gameInfo);
		return content;
	}

	private String buildBillMsg2(GameInfo gameInfo,
			List<PlayerTrace> traceList, Long firstPackgeTime,
			Long bankerLuckTime, List<PlayerTrace> winnerList,
			List<PlayerTrace> loserList, List<PlayerTrace> paceList,
			List<PlayerTrace> allInList, Long packageFee, Long firstBankerFee,
			Long bankerWinCut) {

		Map<Object, Object> root = new HashMap<Object, Object>();
		Template temp = runtimeDomain.getBillTemplate();
		if (temp == null) {
			return "";
		}
		StringWriter write = new StringWriter();
		try {
			Boolean bankerTimeOut = false;
			root.put("winnerList", winnerList);
			root.put("loserList", loserList);
			root.put("allInList", allInList);
			List<PlayerTrace> expireList = new ArrayList<PlayerTrace>();
			for (int i = 0; i < traceList.size(); i++) {
				if (traceList.get(i).getLuckTime() - firstPackgeTime <= runtimeDomain
						.getCurrentTimeOut() * 1000) {
					continue;
				}

				if (traceList.get(i).getRemarkName()
						.equals(gameInfo.getBankerRemarkName())) {
					bankerTimeOut = true;
					continue;
				}
				expireList.add(traceList.get(i));
			}

			root.put("bankerTimeOut", bankerTimeOut);
			root.put("expireList", expireList);
			root.put("paceList", paceList);

			root.put("showManageFee", runtimeDomain.getShowManageFee());
			root.put("lastPackageTime", DateUtils
					.timeStampTimeFormat(runtimeDomain
							.getCurrentLastPackegeTime().getTime()));
			root.put("bankerPackageTime",
					DateUtils.timeStampTimeFormat(gameInfo.getLuckTime()));
			root.put("firstPackageTime", DateUtils
					.timeStampTimeFormat(runtimeDomain
							.getCurrentFirstPackegeTime().getTime()));
			root.put("packageInterval", (runtimeDomain
					.getCurrentLastPackegeTime().getTime() - runtimeDomain
					.getCurrentFirstPackegeTime().getTime()) / 1000);
			root.put("currentGameId", runtimeDomain.getCurrentGameId());
			root.put("currentGroupName", runtimeDomain.getCurrentGroupName());
			root.put("gameInfo", gameInfo);
			root.put("bankerBetPoint", runtimeDomain.getBankerBetPoint());
			root.put("R", StringUtils.labelR());
			root.put("Face", StringUtils.labelFace());
			root.put("FaceEM", StringUtils.labelFaceEM());
			root.put("RUN", StringUtils.labelRUN());
			root.put("illegalPlayers", runtimeDomain.getIllegalPlayer());
			root.put("EXPIRE", StringUtils.labelEXPIRE());
			temp.process(root, write);
		} catch (TemplateException | IOException e) {
			LOGGER.error("bill construct failed!", e);
		} finally {
			root = null;
		}

		return write.toString();
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
				runtimeDomain.getCurrentGameId(), player.getWebchatId(),
				player.getWechatName(), remarkName, betInfo, betPoint,
				isLowRisk, betIndex, (new Date()).getTime());
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
		LOGGER.debug("index[{}] remarkName[{}] luckInfo[{}] time[{}]", index,
				remarkName, luckInfo, time);

		if (AppUtils.PLAYLONG.equals(runtimeDomain.getCurrentGameKey())) {
			puttingLuckInfo(remarkName, luckInfo, time);
		} else if (AppUtils.PLAYLONGSPLIT.equals(runtimeDomain
				.getCurrentGameKey())) {
			puttingLuckInfo(index, luckInfo, time);
		} else if (AppUtils.PLAYLUCKWAY.equals(runtimeDomain
				.getCurrentGameKey())) {
			puttingLuckInfoWithBetInfo(index, remarkName, luckInfo, time);
		}
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
		Boolean isBanker = Boolean.FALSE;
		LotteryRule playerLottery = getResult(luckInfo);
		// benker info
		if (remarkName.equals(runtimeDomain.getBankerRemarkName())) {
			playerService.updateBankerLuckInfoWithBetInfo(
					runtimeDomain.getCurrentGameId(), luckInfo, time.getTime(),
					playerLottery.getRuleName(), playerLottery.getTimes(),
					betIndex);
			isBanker = Boolean.TRUE;
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
				runtimeDomain.getCurrentGameId(), player.getWebchatId(),
				player.getWechatName(), remarkName, betIndex
						+ "/"
						+ (isBanker ? runtimeDomain.getBankerBetPoint()
								: Long.valueOf(betPoint)),
				isBanker ? runtimeDomain.getBankerBetPoint() : betPoint,
				Boolean.FALSE, betIndex, time.getTime());
		// luckinfo
		playerTrace.setLuckInfo(luckInfo);
		playerTrace.setLuckTime(time.getTime());
		playerTrace.setResultRuleName(playerLottery.getRuleName());
		playerTrace.setResultTimes(playerLottery.getTimes());
		playerTrace.setIsBanker(isBanker);
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

			Player player = runningPlayers().get(
					runtimeDomain.getBankerBetPoint());
			PlayerTrace playerTrace = playerService.getTraceByGameIdRemarkName(
					runtimeDomain.getBankerRemarkName(),
					runtimeDomain.getCurrentGameId());
			if (playerTrace == null) {
				playerTrace = new PlayerTrace(runtimeDomain.getCurrentGameId(),
						player.getWebchatId(), player.getWechatName(),
						player.getRemarkName(), index + "/"
								+ runtimeDomain.getBankerBetPoint(),
						runtimeDomain.getBankerBetPoint(), Boolean.FALSE,
						index, luckTime.getTime());
			}
			// luckinfo
			playerTrace.setLuckInfo(luckInfo);
			playerTrace.setLuckTime(luckTime.getTime());
			playerTrace.setResultRuleName(playerLottery.getRuleName());
			playerTrace.setResultTimes(playerLottery.getTimes());
			playerTrace.setIsBanker(Boolean.TRUE);
			playerService.save(playerTrace);
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
			PlayerTrace playerTrace = playerService.getTraceByGameIdRemarkName(
					runtimeDomain.getBankerRemarkName(),
					runtimeDomain.getCurrentGameId());
			if (playerTrace == null) {
				playerTrace = new PlayerTrace(runtimeDomain.getCurrentGameId(),
						player.getWebchatId(), player.getWechatName(),
						remarkName, runtimeDomain.getBankerIndex() + "/"
								+ runtimeDomain.getBankerBetPoint(),
						runtimeDomain.getBankerBetPoint(), Boolean.FALSE,
						runtimeDomain.getBankerIndex(), luckTime.getTime());
			}
			// luckinfo
			playerTrace.setLuckInfo(luckInfo);
			playerTrace.setLuckTime(luckTime.getTime());
			playerTrace.setResultRuleName(playerLottery.getRuleName());
			playerTrace.setResultTimes(playerLottery.getTimes());
			playerTrace.setIsBanker(Boolean.TRUE);
			playerService.save(playerTrace);
			return;
		}
		playerService.updateLuckInfo(luckInfo,
				runtimeDomain.getCurrentGameId(), remarkName,
				luckTime.getTime(), playerLottery.getRuleName(),
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

	/**
	 * 
	 * @param remarkName
	 * @param wechatId
	 * @param wechatName
	 */
	public void rsynPlayerEntityWechatInfo(String remarkName, String wechatId,
			String wechatName) {
		if (remarkName == null || remarkName.isEmpty()) {
			return;
		}
		try {
			Player playerEntity = playerService
					.getPlayerByRemarkName(remarkName);
			if (playerEntity != null) {
				playerEntity.setWebchatId(wechatId);
				playerEntity.setWechatName(wechatName);
				playerEntity = playerService.save(playerEntity);
				runningPlayers()
						.put(playerEntity.getRemarkName(), playerEntity);
			}
		} catch (Exception e) {
			LOGGER.error("remarkName=" + remarkName
					+ "flush wechat info failed!", e);
		}
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
			playerModle.setPlayerPoint(playerEntity.getPoints() == null ? 0L
					: playerEntity.getPoints());
		}
	}

	public void ryncPlayersPoint(ObservableList<PlayerModel> playerList) {
		PlayerModel playerView = null;
		for (int i = 0; i < playerList.size(); i++) {
			playerView = playerList.get(i);
			ryncPlayerPoint(playerView.getWechatId(),
					playerView.getWechatName(), playerView.getPlayerName(),
					Long.valueOf(playerView.getPlayerPoint()),
					playerView.getWechatName());
		}
	}

	public void ryncPlayersPoint(PlayerModel playerView) {
		ryncPlayerPoint(playerView.getWechatId(), playerView.getWechatName(),
				playerView.getPlayerName(),
				Long.valueOf(playerView.getPlayerPoint()),
				playerView.getWechatName());
	}

	private void ryncPlayerPoint(String webchatId, String wechatName,
			String remarkName, Long newPointvel, String nickName) {
		synchronized (this) {
			Player playEntity = null;

			playEntity = playerService.getPlayerById(remarkName);
			if (playEntity == null) {
				playEntity = newPlayerHandle(webchatId, remarkName, nickName);
			}
			if (playEntity == null) {
				LOGGER.error("User{} remarkName{} initi Failed!", nickName,
						remarkName);
				return;
			}
			playEntity.setWebchatId(webchatId);
			playEntity.setPoints(newPointvel);
			savePlayEntity(playEntity);
		}
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
		playerService.save(gameInfo);
		runtimeDomain.setCurrentGameId(gameInfo.getGameSerialNo());
		runtimeDomain.removeCurrentFirstPacageTime();
		runtimeDomain.removeCurrentLastPackegeTime();

		String content = MessageFormat.format(AppUtils.GAMESTART,
				runtimeDomain.getDefinedStartInfo(),// 0
				gameInfo.getGameSerialNo(),// 1
				runtimeDomain.getBankerRemarkName(),// 2
				runtimeDomain.getBankerBetPoint(),// 3
				runtimeDomain.getPackageNumber(),// 4
				runtimeDomain.getDefiendBet(),// 5
				runtimeDomain.getCurrentGameKey(),// 6
				runtimeDomain.getCurrentTimeOut());// 7

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
				content += MessageFormat.format(AppUtils.BETRESULTLINE, trace
						.getRemarkName().length() > 5 ? trace.getRemarkName()
						.substring(0, 5) : trace.getRemarkName(), String
						.valueOf(player.getPoints()), trace.getBetInfo());
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
	private Player ryncPlayerPoint(String remarkName, Boolean plusOrMinus,
			Long newPointvel) {
		Player playEntity = null;
		playEntity = playerService.getPlayerById(remarkName);
		if (playEntity == null) {
			LOGGER.warn("User[{}] cann't be found, the point option failed!",
					remarkName);
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
			playEntity = newPlayerHandle(webchatId, remarkName, nickName);
		}
		if (playEntity == null) {
			LOGGER.error("User{} remarkName Failed!", remarkName);
			return null;
		}

		Long oldPointVel = playEntity.getPoints();
		playEntity.setWebchatId(webchatId);
		playEntity.setWechatName(nickName);
		playEntity.setPoints(plusOrMinus ? oldPointVel + newPointvel
				: oldPointVel - newPointvel);
		savePlayEntity(playEntity);

		return playEntity;
	}

	private Player newPlayerHandle(String webchatId, String remarkName,
			String nickName) {
		Player playEntity = null;

		Boolean remarkNameOk = true;
		if (remarkName.equals(nickName)) {
			remarkNameOk = false;
			remarkName = StringUtils.replaceHtml(remarkName.trim());
			remarkName = remarkName.length() > 8 ? remarkName.substring(0, 8)
					: remarkName;

			Player existEntity = playerService
					.getPlayerByRemarkName(remarkName);
			String shotRemarkName = remarkName;
			for (int i = 0; i < 10; i++) {
				if (existEntity == null) {
					remarkNameOk = true;
					break;
				}
				remarkName = shotRemarkName + i;
				existEntity = playerService.getPlayerByRemarkName(remarkName);
			}
			remarkNameOk = webWechat.changeRemarkName(nickName, webchatId,
					remarkName);
		}

		if (remarkNameOk) {
			playEntity = new Player();
			playEntity.setRemarkName(remarkName);
			playEntity.setPoints(Long.valueOf(0));
		}
		return playEntity;
	}

	public void savePlayEntity(Player player) {
		player.setWechatName(runtimeDomain.getUserNickName(player
				.getWebchatId()));
		playerService.save(player);

		// update view
		if (runningPlayers().get(player.getRemarkName()) == null) {
			playerTableController.addNewPlayer(player);
		}
		runningPlayers().put(player.getRemarkName(), player);
	}

	public List<ApplyPoints> getUncheckedApplyList() {
		return playerService.findByApprovalStatus(AppUtils.APPROVALNONE);
	}

	public boolean approvalPlayer(Long applyId, String remarkName,
			Integer applyType, Integer approvalStatus, Long point,
			String wechatId) {
		try {
			String replyTemplate = "";
			Player pEntity = null;
			if (Integer.compare(AppUtils.APPROVALYES, approvalStatus) == 0) {
				if (Integer.compare(AppUtils.APPLYADDPOINT, applyType) == 0) {
					pEntity = ryncPlayerPoint(remarkName, Boolean.TRUE, point);
					replyTemplate = AppUtils.REPLYPOINTAPPLYADD;
				} else if (Integer.compare(AppUtils.APPLYSUBPOINT, applyType) == 0) {
					pEntity = ryncPlayerPoint(remarkName, Boolean.FALSE, point);
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

	public List<PlayerTrace> getCurrentPlayTrace() {
		Long currentGameId = runtimeDomain.getCurrentGameId();
		if (currentGameId == null
				|| currentGameId.compareTo(Long.valueOf(1)) < 0) {
			return null;
		}
		return playerService.getPlayerTraceListByGameId(currentGameId);
	}

	public String publishPointRanks2() {
		String bankerReMarkerName = runtimeDomain.getBankerRemarkName();
		Player banker = null;

		List<Player> playerList = playerService.getPlayerListDescPoint();
		if (playerList == null) {
			return null;
		}
		String body = "";
		int order = 1;
		Long sumPoint = 0L;
		String shotRemarkName = null;
		for (int i = 0; i < playerList.size(); i++) {
			// if (runtimeDomain.getRunningPlayeres().get(
			// playerList.get(i).getRemarkName()) == null) {
			// continue;
			// }
			if (playerList.get(i).getPoints().compareTo(Long.valueOf(0)) == 0) {
				continue;
			}

			if (bankerReMarkerName != null
					&& bankerReMarkerName.equals(playerList.get(i)
							.getRemarkName())) {
				banker = playerList.get(i);
				continue;
			}

			shotRemarkName = playerList.get(i).getRemarkName();
			body += MessageFormat.format(
					AppUtils.PUBLICPOINTRANKLINE,
					order++,
					shotRemarkName.length() > 5 ? shotRemarkName
							.substring(0, 5) : shotRemarkName, playerList
							.get(i).getPoints());
			sumPoint += playerList.get(i).getPoints();
		}
		if (banker != null) {
			sumPoint += banker.getPoints();
			shotRemarkName = banker.getRemarkName();
			body = MessageFormat.format(
					AppUtils.PUBLICPOINTRANKLINEBANKER,
					shotRemarkName.length() > 8 ? shotRemarkName
							.substring(0, 8) : shotRemarkName, banker
							.getPoints())
					+ body;
			order += 1;
		}

		String head = MessageFormat.format(AppUtils.PUBLICPOINTRANKHEAD,
				order - 1, sumPoint);
		String tail = MessageFormat.format(AppUtils.PUBLICPOINTRANKTAIL,
				sumPoint);
		return head + body + tail;
	}

	public String publishPointRanks() {
		Map<Object, Object> root = new HashMap<Object, Object>();
		Template temp = runtimeDomain.getRankTemplate();
		if (temp == null) {
			return "";
		}

		List<Player> playerList = playerService.getPlayerListDescPoint();
		if (playerList == null) {
			return "";
		}
		String bankerReMarkerName = runtimeDomain.getBankerRemarkName();
		Player banker = null;
		StringWriter write = new StringWriter();

		try {
			Long sumPoint = 0L;
			List<Player> resultList = new ArrayList<Player>();
			for (int i = 0; i < playerList.size(); i++) {
				if (playerList.get(i).getPoints().compareTo(Long.valueOf(0)) == 0) {
					continue;
				}

				if (bankerReMarkerName != null
						&& bankerReMarkerName.equals(playerList.get(i)
								.getRemarkName())) {
					banker = playerList.get(i);
					sumPoint += playerList.get(i).getPoints();
					continue;
				}

				sumPoint += playerList.get(i).getPoints();
				resultList.add(playerList.get(i));
			}

			root.put("sumPoint", sumPoint);
			root.put("rankSize", banker == null ? resultList.size()
					: resultList.size() + 1);
			root.put("rankList", resultList);
			root.put("R", StringUtils.labelR());
			root.put("TOP", StringUtils.labelTOP());
			root.put("banker", banker);

			temp.process(root, write);
		} catch (TemplateException | IOException e) {
			LOGGER.error("Template proces failed!", e);
		}

		return write.toString();
	}

	public GameInfo getGameById(Long gameId) {
		if (gameId == null) {
			return null;
		}
		return playerService.getGameById(gameId);
	}

	@Transactional
	public PlayerTrace updatePlayerTraceBetInfo(Long traceId, String betInfo) {
		try {
			PlayerTrace trace = null;
			trace = playerService.getPlayerTraceById(traceId);
			trace.setBetInfo(betInfo);
			String[] betInfoStr = betInfo.split(StringUtils.BETSPLIT);
			if (betInfoStr.length < 2) {
				trace.setBetIndex(Integer.valueOf(betInfoStr[0]));
				trace.setBetPoint(Long.valueOf(betInfoStr[1]));
			}
			// for banker update
			if (trace.getIsBanker()
					&& !AppUtils.PLAYLONG.equals(runtimeDomain
							.getCurrentGameKey())) {
				trace.setBetPoint(runtimeDomain.getBankerBetPoint());
				runtimeDomain.setBankerIndex(trace.getBetIndex());
				trace.setBetInfo(trace.getBetIndex() + StringUtils.BETSPLIT
						+ trace.getBetPoint());
				GameInfo gameInfo = getGameById(runtimeDomain
						.getCurrentGameId());
				if (gameInfo == null) {
					return null;
				}
				gameInfo.setBetIndex(trace.getBetIndex());
				playerService.save(gameInfo);
			}
			return playerService.save(trace);
		} catch (Exception e) {
			LOGGER.error("update PlayerTrace[" + traceId + "] betInfo["
					+ betInfo + "] failed!", e);
			return null;
		}
	}

	public PlayerTrace updatePlayerTraceLuckInfo(Long traceId, Double luckInfo) {
		PlayerTrace trace = null;
		try {
			trace = playerService.getPlayerTraceById(traceId);
			LotteryRule playerLottery = getResult(luckInfo);
			trace.setLuckInfo(luckInfo);
			trace.setResultTimes(playerLottery.getTimes());
			trace.setResultRuleName(playerLottery.getRuleName());

			// for banker update
			if (trace.getIsBanker()) {
				GameInfo gameInfo = getGameById(runtimeDomain
						.getCurrentGameId());
				if (gameInfo == null) {
					return null;
				}
				gameInfo.setLuckInfo(trace.getLuckInfo());
				gameInfo.setResultTimes(trace.getResultTimes());
				gameInfo.setResultRuleName(trace.getResultRuleName());
				playerService.save(gameInfo);
			}

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

	@Transactional
	public GameInfo cleanTraceInfo(Long gameId) {
		GameInfo gameInfo = null;

		if (gameId == null || gameId < 1) {
			return gameInfo;
		}

		gameInfo = playerService.getGameById(gameId);
		if (gameInfo == null) {
			return gameInfo;
		}
		gameInfo.setLuckInfo(null);
		gameInfo.setLuckTime(null);
		gameInfo.setResultRuleName(null);
		gameInfo.setResultTimes(null);
		gameInfo = playerService.save(gameInfo);
		playerService.deleteTraceByGameId(gameId);

		return gameInfo;

	}

	@Transactional
	public Boolean archievGameInfo() {
		Boolean result = Boolean.FALSE;
		try {
			List<GameInfo> currentGameInfo = playerService
					.getVaiidGameInfoList();
			if (currentGameInfo != null && currentGameInfo.size() > 0) {
				Long statsTime = new Date().getTime();
				Long manageFee = 0L;
				Long packageFee = 0L;
				Long firstBankerFee = 0L;
				Long bankerWinCut = 0L;
				Integer gameNum = currentGameInfo.size();
				for (int i = 0; i < currentGameInfo.size(); i++) {
					manageFee += currentGameInfo.get(i).getManageFee() == null ? 0L
							: currentGameInfo.get(i).getManageFee();
					packageFee += currentGameInfo.get(i).getPackageFee() == null ? 0L
							: currentGameInfo.get(i).getPackageFee();
					firstBankerFee += currentGameInfo.get(i)
							.getFirstBankerFee() == null ? 0L : currentGameInfo
							.get(i).getFirstBankerFee();
					bankerWinCut += currentGameInfo.get(i).getBankerWinCut() == null ? 0L
							: currentGameInfo.get(i).getBankerWinCut();
				}
				GameStats currentStats = new GameStats();
				currentStats.setStatsTime(statsTime);
				currentStats.setManageFee(manageFee);
				currentStats.setPackageFee(packageFee);
				currentStats.setFirstBankerFee(firstBankerFee);
				currentStats.setBankerWinCut(bankerWinCut);
				currentStats.setGameNum(gameNum);
				currentStats = playerService.save(currentStats);
			}
			playerService.removeAllGameInfo();
			result = Boolean.TRUE;
		} catch (Exception e) {
			LOGGER.error("Achieve Game Data failed!", e);
		}

		return result;
	}

	public GameStats getCurrentGameStats() {
		GameStats currentStats = null;
		List<GameInfo> currentGameInfo = playerService.getVaiidGameInfoList();
		if (currentGameInfo != null && currentGameInfo.size() > 0) {
			Long statsTime = new Date().getTime();
			Long manageFee = 0L;
			Long packageFee = 0L;
			Long firstBankerFee = 0L;
			Long bankerWinCut = 0L;
			Integer gameNum = currentGameInfo.size();
			for (int i = 0; i < currentGameInfo.size(); i++) {
				manageFee += currentGameInfo.get(i).getManageFee() == null ? 0L
						: currentGameInfo.get(i).getManageFee();
				packageFee += currentGameInfo.get(i).getPackageFee() == null ? 0L
						: currentGameInfo.get(i).getPackageFee();
				firstBankerFee += currentGameInfo.get(i).getFirstBankerFee() == null ? 0L
						: currentGameInfo.get(i).getFirstBankerFee();
				bankerWinCut += currentGameInfo.get(i).getBankerWinCut() == null ? 0L
						: currentGameInfo.get(i).getBankerWinCut();
			}
			currentStats = new GameStats();
			currentStats.setStatsTime(statsTime);
			currentStats.setManageFee(manageFee);
			currentStats.setPackageFee(packageFee);
			currentStats.setFirstBankerFee(firstBankerFee);
			currentStats.setBankerWinCut(bankerWinCut);
			currentStats.setGameNum(gameNum);
		}
		return currentStats;
	}

	public List<GameStats> getGameStatsList() {
		return playerService.getGameStatsList();
	}

	public List<Player> getAllPlayers() {
		return playerService.getPlayerListDescPoint();
	}
}
