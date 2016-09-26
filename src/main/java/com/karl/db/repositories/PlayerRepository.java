package com.karl.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.karl.db.domain.Player;

public interface PlayerRepository extends CrudRepository<Player, String> {

    @Query("select id, remarkName, points from Player p where p.remarkName like ?1")
    List<Player> findLikeRemarkName(String remarkName);

    Player findByRemarkName(String remarkName);
}
