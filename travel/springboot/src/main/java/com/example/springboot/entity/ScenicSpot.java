package com.example.springboot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点实体类
 */
public class ScenicSpot {
    /** 景点ID */
    private Integer id;

    /** 景点名称 */
    private String name;

    /** 景点描述 */
    private String description;

    /** 地理位置 */
    private String location;

    /** 分类ID */
    private Integer categoryId;

    /** 票价 */
    private BigDecimal price;

    /** 开放时间 */
    private String openingHours;

    /** 图片URL（主图） */
    private String imageUrl;

    /** 图片列表，JSON格式存储 */
    private String imageList;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // 非数据库字段
    /** 分类信息（关联查询用） */
    private ScenicCategory categoryInfo;

    /** 评分 */
    private Double rating;

    /** 评论数 */
    private Long commentCount;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageList() {
        return imageList;
    }

    public void setImageList(String imageList) {
        this.imageList = imageList;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public ScenicCategory getCategoryInfo() {
        return categoryInfo;
    }

    public void setCategoryInfo(ScenicCategory categoryInfo) {
        this.categoryInfo = categoryInfo;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
}