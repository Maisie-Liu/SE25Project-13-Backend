package com.campus.trading.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 物品数据传输对象
 */
public class ItemDTO {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private String description;
    private List<String> images;
    private Integer condition;
    private Integer status;
    private Integer popularity;
    private Long userId;
    private String username;
    private String userAvatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    public ItemDTO() {
    }
    
    public ItemDTO(Long id, String name, Long categoryId, String categoryName, BigDecimal price, String description, List<String> images, Integer condition, Integer status, Integer popularity, Long userId, String username, String userAvatar, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.description = description;
        this.images = images;
        this.condition = condition;
        this.status = status;
        this.popularity = popularity;
        this.userId = userId;
        this.username = username;
        this.userAvatar = userAvatar;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getImages() {
        return images;
    }
    
    public void setImages(List<String> images) {
        this.images = images;
    }
    
    public Integer getCondition() {
        return condition;
    }
    
    public void setCondition(Integer condition) {
        this.condition = condition;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getPopularity() {
        return popularity;
    }
    
    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private ItemDTO dto = new ItemDTO();
        
        public Builder id(Long id) {
            dto.setId(id);
            return this;
        }
        
        public Builder name(String name) {
            dto.setName(name);
            return this;
        }
        
        public Builder categoryId(Long categoryId) {
            dto.setCategoryId(categoryId);
            return this;
        }
        
        public Builder categoryName(String categoryName) {
            dto.setCategoryName(categoryName);
            return this;
        }
        
        public Builder price(BigDecimal price) {
            dto.setPrice(price);
            return this;
        }
        
        public Builder description(String description) {
            dto.setDescription(description);
            return this;
        }
        
        public Builder imageUrls(List<String> imageUrls) {
            dto.setImages(imageUrls);
            return this;
        }
        
        public Builder condition(Integer condition) {
            dto.setCondition(condition);
            return this;
        }
        
        public Builder status(Integer status) {
            dto.setStatus(status);
            return this;
        }
        
        public Builder popularity(Integer popularity) {
            dto.setPopularity(popularity);
            return this;
        }
        
        public Builder userId(Long userId) {
            dto.setUserId(userId);
            return this;
        }
        
        public Builder username(String username) {
            dto.setUsername(username);
            return this;
        }
        
        public Builder userAvatar(String userAvatar) {
            dto.setUserAvatar(userAvatar);
            return this;
        }
        
        public Builder createTime(LocalDateTime createTime) {
            dto.setCreateTime(createTime);
            return this;
        }
        
        public Builder updateTime(LocalDateTime updateTime) {
            dto.setUpdateTime(updateTime);
            return this;
        }
        
        public ItemDTO build() {
            return dto;
        }
    }
} 