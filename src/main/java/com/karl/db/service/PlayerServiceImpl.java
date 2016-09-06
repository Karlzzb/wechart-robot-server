package com.karl.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.karl.db.domain.Player;

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

}
