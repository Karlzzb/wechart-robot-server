package com.karl.db.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.karl.db.domain.GameInfo;

public interface GameRepository extends CrudRepository<GameInfo, String> {
	
    @Query("from GameInfo g order by g.bankerPoint desc limit 1")
    GameInfo findLaestGame();
    
    @Query("select max(p.gameSerialNo) from GameInfo g")
    Long getMaxGameId();

    @Query("update GameInfo g set luckInfo = ?2 where g.gameSerialNo = ?1")
    @Modifying
	void updateBankerLuckInfo(Long gameId, Double luckInfo);
}
