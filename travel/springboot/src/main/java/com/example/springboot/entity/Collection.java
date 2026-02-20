package com.example.springboot.entity;

import java.util.Date;

public class Collection {
    private Integer id;
    private Integer userId;
    private Integer guideId;
    private Date createTime;

    private String username;
    private String userAvatar;
    private String guideTitle;
    private Integer guideViews;
    private String guideCoverImage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGuideId() {
        return guideId;
    }

    public void setGuideId(Integer guideId) {
        this.guideId = guideId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getGuideTitle() {
        return guideTitle;
    }

    public void setGuideTitle(String guideTitle) {
        this.guideTitle = guideTitle;
    }

    public Integer getGuideViews() {
        return guideViews;
    }

    public void setGuideViews(Integer guideViews) {
        this.guideViews = guideViews;
    }

    public String getGuideCoverImage() {
        return guideCoverImage;
    }

    public void setGuideCoverImage(String guideCoverImage) {
        this.guideCoverImage = guideCoverImage;
    }
}