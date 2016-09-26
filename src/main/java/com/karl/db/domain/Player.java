package com.karl.db.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Player implements Serializable {
    private static final long serialVersionUID = -7909930972652408103L;

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String playerId;

    @Column(nullable = true)
    private String webchatId;
    
    @Column(nullable = false)
    private String wechatName;

    @Column(nullable = false)
    private String remarkName;

    @Column(nullable = true)
    private Long points;


    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Player() {
        super();
    }

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getWebchatId() {
		return webchatId;
	}

	public void setWebchatId(String webchatId) {
		this.webchatId = webchatId;
	}

	public String getWechatName() {
		return wechatName;
	}

	public void setWechatName(String wechatName) {
		this.wechatName = wechatName;
	}



}
