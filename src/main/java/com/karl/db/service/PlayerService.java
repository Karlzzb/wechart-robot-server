package com.karl.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.karl.db.domain.ApplyPoints;
import com.karl.db.domain.GameInfo;
import com.karl.db.domain.Player;
import com.karl.db.domain.PlayerTrace;
import com.karl.db.repositories.ApplyRepository;
import com.karl.db.repositories.GameRepository;
import com.karl.db.repositories.PlayerRepository;
import com.karl.db.repositories.PlayerTraceRepository;
import com.karl.utils.AppUtils;
import com.karl.utils.StringUtils;

@Component("playerService")
@Transactional
public class PlayerService {

	@Autowired
	public PlayerService(PlayerRepository playerRepository,
			ApplyRepository applyRepository, GameRepository gameRepository,
			PlayerTraceRepository playerTraceRepository) {
		this.playerRepository = playerRepository;
		this.applyRepository = applyRepository;
		this.gameRepository = gameRepository;
		this.playerTraceRepository = playerTraceRepository;
	}

	private final PlayerRepository playerRepository;

	private final ApplyRepository applyRepository;

	private final GameRepository gameRepository;

	private final PlayerTraceRepository playerTraceRepository;

	public Player save(Player player) {
		if (player.getPlayerId() == null || player.getPlayerId().isEmpty()) {
			player.setPlayerId(StringUtils.getMD5(player.getRemarkName()));
		}
		Assert.notNull(player, "player must not be null");
		return playerRepository.save(player);
	}

	public ApplyPoints save(ApplyPoints apply) {
		Assert.notNull(apply, "remarkName must not be null");
		apply.setApprovalStatus(AppUtils.APPROVALNONE);
		return applyRepository.save(apply);
	}

	public List<Player> getPlayeresLikeRemarkName(String remarkName) {
		Assert.notNull(remarkName, "remarkName must not be null");
		return playerRepository.findLikeRemarkName(remarkName);
	}

	public Player getPlayerByRemarkName(String remarkName) {
		Assert.notNull(remarkName, "remarkName must not be null");
		return playerRepository.findByRemarkName(remarkName);
	}

	public Player getPlayerById(String playerId) {
		Assert.notNull(playerId, "player must not be null");
		return playerRepository.findOne(playerId);
	}

	public List<ApplyPoints> findByApprovalStatus(Integer approvalStatus) {
		Assert.notNull(approvalStatus, "approvalStatus must not be null");
		return applyRepository.findByApprovalStatus(approvalStatus);
	}

	public void approveRequest(Long applyId, Integer approvalStatus,
			Long approvalTime) {
		Assert.notNull(applyId, "applyId must not be null");
		Assert.notNull(approvalStatus, "approvalStatus must not be null");
		Assert.notNull(approvalTime, "approvalTime must not be null");
		applyRepository.updateApprovalStatus(applyId, approvalStatus,
				approvalTime);
	}

	public GameInfo getlatestGame() {
		Pageable pageable = new PageRequest(1, 1, Direction.ASC, "bankerPoint");
		Page<GameInfo> misakaPage = gameRepository.search(pageable);
		List<GameInfo> misakaList = misakaPage.getContent();
		return misakaList.get(0);
	}

	public List<PlayerTrace> getPlayerTraceListByGameId(Long gameId) {
		Assert.notNull(gameId, "gameId must not be null");
		return playerTraceRepository.findByGameId(gameId);
	}

	public void updateLuckInfo(Long luckInfo, Long gameSerialNo,
			String playerId, String remarkName, String betInfo) {
		Assert.notNull(luckInfo, "luckInfo must not be null");
		Assert.notNull(gameSerialNo, "gameSerialNo must not be null");
		Assert.notNull(playerId, "playerId must not be null");
		Assert.notNull(remarkName, "remarkName must not be null");
		Assert.notNull(betInfo, "betInfo must not be null");
		playerTraceRepository.updateLuckInfo(luckInfo, gameSerialNo, playerId,
				remarkName, betInfo);
	}

	public void updateLuckInfo(Long luckInfo, Long traceId) {
		Assert.notNull(luckInfo, "luckInfo must not be null");
		Assert.notNull(traceId, "traceId must not be null");
		playerTraceRepository.updateLuckInfo(luckInfo, traceId);
	}

	public void updateResult(Long resultTimes, Long resultPoint,
			Long gameSerialNo, String playerId, String remarkName,
			String betInfo) {
		Assert.notNull(resultTimes, "resultTimes must not be null");
		Assert.notNull(resultPoint, "resultPoint must not be null");
		Assert.notNull(gameSerialNo, "gameSerialNo must not be null");
		Assert.notNull(playerId, "playerId must not be null");
		Assert.notNull(remarkName, "remarkName must not be null");
		Assert.notNull(betInfo, "betInfo must not be null");
		playerTraceRepository.updateResult(resultTimes, resultPoint,
				gameSerialNo, playerId, remarkName, betInfo);

	}

	public void updateResult(Long resultTimes, Long resultPoint, Long traceId) {
		Assert.notNull(resultTimes, "resultTimes must not be null");
		Assert.notNull(resultPoint, "resultPoint must not be null");
		Assert.notNull(traceId, "traceId must not be null");
		playerTraceRepository.updateResult(resultTimes, resultPoint, traceId);
	}

	public PlayerTrace save(PlayerTrace playerTrace) {
		Assert.notNull(playerTrace, "playerTrace must not be null");
		return playerTraceRepository.save(playerTrace);
	}

	public void updateLuckInfo(Double luckInfo, Long gameSerialNo,
			String playerId, String remarkName, Long luckTime, String resultRuleName, Long resultTimes) {
		Assert.notNull(luckInfo, "luckInfo must not be null");
		Assert.notNull(gameSerialNo, "gameSerialNo must not be null");
		Assert.notNull(playerId, "playerId must not be null");
		Assert.notNull(remarkName, "remarkName must not be null");
		Assert.notNull(resultRuleName, "resultRuleName must not be null");
		Assert.notNull(resultTimes, "resultTimes must not be null");
		playerTraceRepository.updateLuckInfo(luckInfo, gameSerialNo, playerId,
				remarkName, luckTime, resultRuleName, resultTimes);
	}

	public void updateLuckInfo(Double luckInfo, Long gameSerialNo,
			Integer betIndex, Long luckTime, String resultRuleName, Long resultTimes) {
		Assert.notNull(luckInfo, "luckInfo must not be null");
		Assert.notNull(gameSerialNo, "gameSerialNo must not be null");
		Assert.notNull(betIndex, "betIndex must not be null");
		Assert.notNull(luckTime, "luckTime must not be null");
		Assert.notNull(resultRuleName, "resultRuleName must not be null");
		Assert.notNull(resultTimes, "resultTimes must not be null");
		playerTraceRepository.updateLuckInfo(luckInfo, gameSerialNo, betIndex,
				luckTime,  resultRuleName, resultTimes);
	}

	public void updateBankerLuckInfo(Long gameId, Double luckInfo, Long luckTime, String resultRuleName, Long resultTimes) {
		Assert.notNull(gameId, "luckInfo must not be null");
		Assert.notNull(luckInfo, "gameSerialNo must not be null");
		gameRepository.updateBankerLuckInfo(gameId, luckInfo, luckTime, resultRuleName, resultTimes);
	}

	public GameInfo save(GameInfo gameInfo) {
		Assert.notNull(gameInfo, "GameInfo must not be null");
		return gameRepository.save(gameInfo);
	}

}
