package com.karl.db.service;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.karl.db.domain.Player;

public interface PlayerRepository extends Repository<Player, Long> {

    @Query("select id, remarkName, points from Player p where p.remarkName like ?1")
    List<Player> findLikeRemarkName(String remarkName);

    @Query("select id, remarkName, points from Player p where p.remarkName = ?1")
    Player findByRemarkName(String remarkName);

    Player save(Player player);

}
