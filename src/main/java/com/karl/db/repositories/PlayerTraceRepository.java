package com.karl.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.karl.db.domain.PlayerTrace;

public interface PlayerTraceRepository extends CrudRepository<PlayerTrace, Long> {

    @Query("from PlayerTrace p where p.gameSerialNo = ?1 order by resultTimes desc, p.betTime desc, betPoint desc")
    List<PlayerTrace> findByGameId(Long gameId);
    
    @Query("from PlayerTrace p where p.remarkName = ?1")
    List<PlayerTrace> findByRemarkName(String remarkName);

    @Modifying
    @Query("update PlayerTrace p set p.luckInfo = ?1  where p.gameSerialNo = ?2 and p.remarkName = ?3 and p.betInfo = ?4")
	void updateLuckInfo(Long luckInfo, Long gameSerialNo, String remarkName, String betInfo);

    @Modifying
    @Query("update PlayerTrace p set p.luckInfo = ?1  where p.traceId = ?2")
	void updateLuckInfo(Long luckInfo, Long traceId);
    
    @Modifying
    @Query("update PlayerTrace p set p.resultTimes = ?1, p.resultPoint = ?2  where p.traceId = ?3")
	void updateResult(Long resultTimes, Long resultPoint, Long traceId);

    @Modifying
    @Query("update PlayerTrace p set p.luckInfo = ?1, p.luckTime = ?4, p.resultRuleName = ?5, p.resultTimes = ?6  where p.gameSerialNo = ?2 and p.remarkName = ?3")
	void updateLuckInfo(Double luckInfo, Long gameSerialNo,
			String remarkName, Long luckTime, String resultRuleName, Integer resultTimes);

    @Modifying
    @Query("update PlayerTrace p set p.luckInfo = ?1, p.luckTime = ?4, p.resultRuleName = ?5, p.resultTimes = ?6  where p.gameSerialNo = ?2 and p.betIndex = ?3")
	void updateLuckInfo(Double luckInfo, Long currentGameId, Integer betIndex, Long luckTime, String resultRuleName, Integer resultTimes);

    @Query("from PlayerTrace p where p.gameSerialNo = ?1 and p.remarkName = ?2")
	List<PlayerTrace> getPlayerTraceListByGameIdRemarkName(
			Long gameId, String remarkName);

    @Modifying
    @Query("delete from PlayerTrace p where p.gameSerialNo = ?1")
	void deleteTraceByGameId(Long gameId);

    @Modifying
    @Query(value="alter table PLAYER_TRACE ALTER COLUMN TRACE_ID RESTART WITH 1", nativeQuery =true)
	void clearIncrement();    

}
