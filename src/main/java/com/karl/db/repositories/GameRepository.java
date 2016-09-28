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
}
