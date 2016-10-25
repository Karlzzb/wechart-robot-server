package com.karl.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.karl.db.domain.GameStats;

public interface GameStatsRepository extends PagingAndSortingRepository<GameStats, Long> {
    @Query("from GameStats s order by s.statsTime desc")
	List<GameStats> search();
}
