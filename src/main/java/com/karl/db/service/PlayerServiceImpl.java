package com.karl.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.karl.db.domain.Player;
import com.karl.db.repositories.PlayerRepository;

@Component("playerService")
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player save(Player player) {
        Assert.notNull(player, "remarkName must not be null");
        return playerRepository.save(player);
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
	public void updateResult(String playerId, Long latestResult) {
        Assert.notNull(playerId, "playerId must not be null");
        Assert.notNull(latestResult, "latestResult must not be null");
        playerRepository.updateResult(playerId, latestResult);
	}

	@Override
	public void updateLuckInfo(String playerId, Double latestLuck) {
        Assert.notNull(playerId, "playerId must not be null");
        Assert.notNull(latestLuck, "latestLuck must not be null");
        playerRepository.updateLuckInfo(playerId, latestLuck);
	}

	@Override
	public void updateBetInfo(String playerId, String latestBet,
			Long latestBetTime, Long latestBetValue) {
        Assert.notNull(playerId, "playerId must not be null");
        Assert.notNull(latestBet, "latestBet must not be null");
        Assert.notNull(latestBetTime, "latestBetTime must not be null");
        Assert.notNull(latestBetValue, "latestBetValue must not be null");
      playerRepository.updateBetInfo(playerId, latestBet, latestBetTime, latestBetValue);
	}

}
