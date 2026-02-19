package com.example.springboot.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点分类实体类
 */
public class ScenicCategory {
    /** 分类ID */
    private Integer id;

    /** 分类名称 */
    private String name;

    /** 分类描述 */
    private String description;

    /** 图标 */
    private String icon;

    /** 父分类ID */
    private Integer parentId;

    /** 排序序号 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // 非数据库字段
    /** 子分类列表 */
    private List<ScenicCategory> children;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    public List<ScenicCategory> getChildren() {
        return children;
    }

    public void setChildren(List<ScenicCategory> children) {
        this.children = children;
    }
}