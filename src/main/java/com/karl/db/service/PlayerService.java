package com.karl.db.service;

import java.util.List;

import com.karl.db.domain.Player;

public interface PlayerService {

    public List<Player> getPlayeresLikeRemarkName(String remarkName);

    public Player save(Player player);

    public Player getPlayerByRemarkName(String remarkName);
    
    public Player getPlayerById(String playerId);

	public void updateResult(String playerId, Long latestResult);

	public void updateBetInfo(String playerId, String latestBet,
			Long latestBetTime, Long latestBetValue);

	public void updateLuckInfo(String playerId, Double latestLuck);

}
