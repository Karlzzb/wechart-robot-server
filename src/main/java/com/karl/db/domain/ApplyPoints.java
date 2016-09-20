package com.karl.db.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ApplyPoints implements Serializable {
	
	private static final long serialVersionUID = -2207608948244914216L;

	@Id
	private String playerId;

    @Column(nullable = false)
    private String remarkName;

}
