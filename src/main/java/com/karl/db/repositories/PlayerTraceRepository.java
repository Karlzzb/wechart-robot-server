package com.karl.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.karl.db.domain.PlayerTrace;

public interface PlayerTraceRepository extends CrudRepository<PlayerTrace, String> {

    @Query("from PlayerTrace p where p.gameSerialNo = ?1 order by betPoint dese, p.betTime desc")
    List<PlayerTrace> findByGameId(Long gameId);
    
    @Query("from PlayerTrace p where p.remarkName = ?1")
    List<PlayerTrace> findByRemarkName(String remarkName);

    @Modifying
    @Query("update PlayerTrace p set p.luckInfo = ?1  where p.gameSerialNo = ?2 and p.playerId = ?3 and p.remarkName = ?4 and p.betInfo = ?5")
	void updateLuckInfo(Long luckInfo, Long gameSerialNo, String playerId, String remarkName, String betInfo);

    @Modifying
    @Query("update PlayerTrace p set p.luckInfo = ?1  where p.traceId = ?2")
	void updateLuckInfo(Long luckInfo, Long traceId);
    
    @Modifying
    @Query("update PlayerTrace p set p.resultTimes = ?1, p.resultPoint = ?2  where p.gameSerialNo = ?3 and p.playerId = ?4 and p.remarkName = ?5 and p.betInfo = ?6")
	void updateResult(Long resultTimes, Long resultPoint, Long gameSerialNo, String playerId, String remarkName, String betInfo);    

    @Modifying
    @Query("update PlayerTrace p set p.resultTimes = ?1, p.resultPoint = ?2  where p.traceId = ?3")
	void updateResult(Long resultTimes, Long resultPoint, Long traceId);

    @Modifying
    @Query("update PlayerTrace p set p.luckInfo = ?1  where p.gameSerialNo = ?2 and p.playerId = ?3 and p.remarkName = ?4")
	void updateLuckInfo(Double luckInfo, Long gameSerialNo, String playerId,
			String remarkName);

    @Modifying
    @Query("update PlayerTrace p set p.luckInfo = ?1  where p.gameSerialNo = ?2 and p.betIndex = ?3")
	void updateLuckInfo(Double luckInfo, Long currentGameId, Integer betIndex);    

}
