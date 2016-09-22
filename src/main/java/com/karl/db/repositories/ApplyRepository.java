package com.karl.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.karl.db.domain.ApplyPoints;

public interface ApplyRepository extends CrudRepository<ApplyPoints, String> {
    
    @Query("from ApplyPoints p where p.approvalStatus = ?1 order by p.applyTime DESC")
    List<ApplyPoints> findByApprovalStatus(int approvalStatus);

    @Modifying
    @Query("update ApplyPoints p set p.approvalStatus = ?2, approvalTime = ?3  where p.applyId = ?1")
	void updateApprovalStatus(long applyId, int approvalStatus,long approvalTime);
}
