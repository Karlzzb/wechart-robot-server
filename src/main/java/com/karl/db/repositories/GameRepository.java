package com.karl.db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.karl.db.domain.GameInfo;

public interface GameRepository extends CrudRepository<GameInfo, String> {

    @Query("update GameInfo g set g.luckInfo = ?2, g.luckTime = ?3, g.resultRuleName = ?4, g.resultTimes = ?5 where g.gameSerialNo = ?1")
    @Modifying
	void updateBankerLuckInfo(Long gameId, Double luckInfo, Long luckTime, String resultRuleName, Long resultTimes);

    @Query("from GameInfo")
	Page<GameInfo> search(Pageable pageable);
}
