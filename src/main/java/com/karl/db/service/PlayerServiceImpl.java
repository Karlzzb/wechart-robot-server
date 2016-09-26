package com.karl.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository,
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

    @Override
    public Player save(Player player) {
    	if(player.getPlayerId() == null || player.getPlayerId().isEmpty()) {
    		player.setPlayerId(StringUtils.getMD5(player.getRemarkName()));
    	}
        Assert.notNull(player, "player must not be null");
        return playerRepository.save(player);
    }
    
	@Override
	public ApplyPoints save(ApplyPoints apply) {
        Assert.notNull(apply, "remarkName must not be null");
        apply.setApprovalStatus(AppUtils.APPROVALNONE);
        return applyRepository.save(apply);
	}


    @Override
    public List<Player> getPlayeresLikeRemarkName(String remarkName) {
        Assert.notNull(remarkName, "remarkName must not be null");
        return playerRepository.findLikeRemarkName(remarkName);
    }

    @Override
    public Player getPlayerByRemarkName(String remarkName) {
        Assert.notNull(remarkName, "remarkName must not be null");
        return playerRepository.findByRemarkName(remarkName);
    }
    
    @Override
    public Player getPlayerById(String playerId) {
        Assert.notNull(playerId, "player must not be null");
        return playerRepository.findOne(playerId);
    }


	@Override
	public List<ApplyPoints> findByApprovalStatus(Integer approvalStatus) {
        Assert.notNull(approvalStatus, "approvalStatus must not be null");
        return applyRepository.findByApprovalStatus(approvalStatus);
	}

	@Override
	public void approveRequest(Long applyId, Integer approvalStatus,
			Long approvalTime) {
        Assert.notNull(applyId, "applyId must not be null");
        Assert.notNull(approvalStatus, "approvalStatus must not be null");
        Assert.notNull(approvalTime, "approvalTime must not be null");
		applyRepository.updateApprovalStatus(applyId, approvalStatus, approvalTime);
	}

	@Override
	public Long getlatestGameId() {
		Long maxGameId = gameRepository.getMaxGameId();
		if (maxGameId == null) {
			maxGameId = Long.valueOf(1);
		}
		return maxGameId;
	}

	@Override
	public GameInfo getlatestGame() {
		return gameRepository.findLaestGame();
	}

	@Override
	public List<PlayerTrace> getPlayerTraceListByGameId(Long gameId) {
        Assert.notNull(gameId, "gameId must not be null");
        return playerTraceRepository.findByGameId(gameId);
	}

	@Override
	public void updateLuckInfo(Long luckInfo, Long gameSerialNo,
			String playerId, String remarkName, String betInfo) {
        Assert.notNull(luckInfo, "luckInfo must not be null");
        Assert.notNull(gameSerialNo, "gameSerialNo must not be null");
        Assert.notNull(playerId, "playerId must not be null");
        Assert.notNull(remarkName, "remarkName must not be null");
        Assert.notNull(betInfo, "betInfo must not be null");
        playerTraceRepository.updateLuckInfo(luckInfo, gameSerialNo, playerId, remarkName, betInfo);
	}

	@Override
	public void updateLuckInfo(Long luckInfo, Long traceId) {
        Assert.notNull(luckInfo, "luckInfo must not be null");
        Assert.notNull(traceId, "traceId must not be null");
        playerTraceRepository.updateLuckInfo(luckInfo, traceId);
	}

	@Override
	public void updateResult(Long resultTimes, Long resultPoint,
			Long gameSerialNo, String playerId, String remarkName,
			String betInfo) {
        Assert.notNull(resultTimes, "resultTimes must not be null");
        Assert.notNull(resultPoint, "resultPoint must not be null");
        Assert.notNull(gameSerialNo, "gameSerialNo must not be null");
        Assert.notNull(playerId, "playerId must not be null");
        Assert.notNull(remarkName, "remarkName must not be null");
        Assert.notNull(betInfo, "betInfo must not be null");
		playerTraceRepository.updateResult(resultTimes, resultPoint, gameSerialNo, playerId, remarkName, betInfo);
		
	}

	@Override
	public void updateResult(Long resultTimes, Long resultPoint, Long traceId) {
        Assert.notNull(resultTimes, "resultTimes must not be null");
        Assert.notNull(resultPoint, "resultPoint must not be null");
        Assert.notNull(traceId, "traceId must not be null");
		playerTraceRepository.updateResult(resultTimes, resultPoint, traceId);
	}

	@Override
	public PlayerTrace save(PlayerTrace playerTrace) {
        Assert.notNull(playerTrace, "playerTrace must not be null");
		return playerTraceRepository.save(playerTrace);
	}

	@Override
	public void updateLuckInfo(Double luckInfo, Long gameSerialNo, String playerId,
			String remarkName) {
        Assert.notNull(luckInfo, "luckInfo must not be null");
        Assert.notNull(gameSerialNo, "gameSerialNo must not be null");
        Assert.notNull(playerId, "playerId must not be null");
        Assert.notNull(remarkName, "remarkName must not be null");
        playerTraceRepository.updateLuckInfo(luckInfo, gameSerialNo, playerId, remarkName);
	}

	@Override
	public void updateLuckInfo(Double luckInfo, Long currentGameId,
			Integer betIndex) {
		playerTraceRepository.updateLuckInfo(luckInfo, currentGameId, betIndex);
		
	}

	@Override
	public void updateBankerLuckInfo(Long gameId, Double luckInfo) {
        Assert.notNull(gameId, "luckInfo must not be null");
        Assert.notNull(luckInfo, "gameSerialNo must not be null");
		gameRepository.updateBankerLuckInfo(gameId, luckInfo);
	}

}
