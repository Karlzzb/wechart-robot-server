package com.karl.db.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.karl.db.domain.GameInfo;

public interface GameRepository extends PagingAndSortingRepository<GameInfo, Long> {

    @Query("update GameInfo g set g.luckInfo = ?2, g.luckTime = ?3, g.resultRuleName = ?4, g.resultTimes = ?5 where g.gameSerialNo = ?1")
    @Modifying
	void updateBankerLuckInfo(Long gameId, Double luckInfo, Long luckTime, String resultRuleName, Integer resultTimes);

    @Query("from GameInfo order by gameSerialNo desc")
	List<GameInfo> search(Pageable pageable);
    
    @Query("from GameInfo g where g.isUndo = null and g.luckInfo != null order by g.gameSerialNo desc")
	List<GameInfo> getValidList();

    @Query("update GameInfo g set g.luckInfo = ?2, g.luckTime = ?3, g.resultRuleName = ?4, g.resultTimes = ?5, g.betIndex = ?6 where g.gameSerialNo = ?1")
    @Modifying
	void updateBankerLuckInfo(Long gameId, Double luckInfo, Long luckTime,
			String resultRuleName, Integer resultTimes, Integer betIndex);

    @Modifying
    @Query(value="alter table GAME_INFO ALTER COLUMN GAME_SERIAL_NO RESTART WITH 1", nativeQuery =true)
	void clearIncrement();
}
