package com.karl.db.service;

import java.util.List;

import com.karl.db.domain.ApplyPoints;
import com.karl.db.domain.GameInfo;
import com.karl.db.domain.Player;
import com.karl.db.domain.PlayerTrace;

public interface PlayerService {

	public List<Player> getPlayeresLikeRemarkName(String remarkName);

	public Player save(Player player);
	
	public PlayerTrace save(PlayerTrace playerTrace);

	public ApplyPoints save(ApplyPoints apply);

	public Player getPlayerByRemarkName(String remarkName);

	public Player getPlayerById(String playerId);

	public List<ApplyPoints> findByApprovalStatus(Integer approvalStatus);

	public void approveRequest(Long applyId, Integer approvalStatus,
			Long approvalTime);

	public Long getlatestGameId();

	public GameInfo getlatestGame();

	public List<PlayerTrace> getPlayerTraceListByGameId(Long gameId);

	public void updateLuckInfo(Long luckInfo, Long gameSerialNo,
			String playerId, String remarkName, String betInfo);

	public void updateLuckInfo(Long luckInfo, Long traceId);

	public void updateResult(Long resultTimes, Long resultPoint,
			Long gameSerialNo, String playerId, String remarkName,
			String betInfo);

	public void updateResult(Long resultTimes, Long resultPoint, Long traceId);

	public void updateLuckInfo(Double luckInfo, Long currentGameId, String playerId,
			String remarkName);
	
	public void updateLuckInfo(Double luckInfo, Long currentGameId, Integer betIndex);

	public void updateBankerLuckInfo(Long gameId, Double luckInfo);

}
