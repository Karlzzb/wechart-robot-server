package com.karl.db.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ApplyPoints implements Serializable {

	private static final long serialVersionUID = -2207608948244914216L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long applyId;
	
	@Column(nullable = false)
	private String webChatId;

    @Column(nullable = false)
    private String webchatName;
	
	@Column(nullable = false)
	private String remarkName;

	@Column(nullable = false)
	private Long points;

	@Column(nullable = false)
	private Integer applyType;

	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer approvalStatus;

	@Column(nullable = false)
	private Long applyTime;
	
	@Column(nullable = true)
	private Long approvalTime;

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

	public Integer getApplyType() {
		return applyType;
	}

	public void setApplyType(Integer applyType) {
		this.applyType = applyType;
	}

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public Long getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Long applyTime) {
		this.applyTime = applyTime;
	}

	public Long getApprovalTime() {
		return approvalTime;
	}

	public void setApprovalTime(Long approvalTime) {
		this.approvalTime = approvalTime;
	}

	public Long getApplyId() {
		return applyId;
	}

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	public String getWebChatId() {
		return webChatId;
	}

	public void setWebChatId(String webChatId) {
		this.webChatId = webChatId;
	}

	public String getWebchatName() {
		return webchatName;
	}

	public void setWebchatName(String webchatName) {
		this.webchatName = webchatName;
	}

}
