package com.karl.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.karl.db.domain.Player;

public interface PlayerRepository extends CrudRepository<Player, String> {

    @Query("select id, remarkName, points from Player p where p.remarkName like ?1")
    List<Player> findLikeRemarkName(String remarkName);

    Player findByRemarkName(String remarkName);

    @Modifying
    @Query("update Player p set p.latestResult = ?2  where p.playerId = ?1")
	void updateResult(String playerId, Long latestResult);

    @Modifying
    @Query("update Player p set p.latestBet = ?2, latestBetTime = ?3, p.latestBetValue = ?4 where p.playerId = ?1")
	void updateBetInfo(String playerId, String latestBet, Long latestBetTime, Long latestBetValue);

    @Modifying
    @Query("update Player p set p.latestLuck = ?2  where p.playerId = ?1")
	void updateLuckInfo(String playerId, Double latestLuck);
}
