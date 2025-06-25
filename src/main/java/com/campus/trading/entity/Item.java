package com.campus.trading.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 物品实体类
 */
@Entity
@Table(name = "t_item")
@EntityListeners(AuditingEntityListener.class)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 物品名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 物品分类
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * 物品价格
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 物品描述
     */
    @Column(length = 2000)
    private String description;

    /**
     * 物品图片列表
     */
    @ElementCollection
    @CollectionTable(name = "t_item_images", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "image_url", length = 255)
    private List<String> images = new ArrayList<>();

    /**
     * 物品新旧程度：1-10，1表示全新，10表示旧
     */
    @Column(nullable = false)
    private Integer itemCondition;

    /**
     * 物品状态：0-下架，1-上架，2-已售出
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 物品热度
     */
    @Column(nullable = false)
    private Integer popularity;

    /**
     * 物品所有者
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 创建时间
     */
    @CreatedDate
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    private LocalDateTime updateTime;
    
    public Item() {
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
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
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
    
    public Integer getItemCondition() {
        return itemCondition;
    }
    
    public void setItemCondition(Integer itemCondition) {
        this.itemCondition = itemCondition;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
        private Item item = new Item();
        
        public Builder id(Long id) {
            item.setId(id);
            return this;
        }
        
        public Builder name(String name) {
            item.setName(name);
            return this;
        }
        
        public Builder category(Category category) {
            item.setCategory(category);
            return this;
        }
        
        public Builder price(BigDecimal price) {
            item.setPrice(price);
            return this;
        }
        
        public Builder description(String description) {
            item.setDescription(description);
            return this;
        }
        
        public Builder images(List<String> images) {
            item.setImages(images);
            return this;
        }
        
        public Builder itemCondition(Integer itemCondition) {
            item.setItemCondition(itemCondition);
            return this;
        }
        
        public Builder status(Integer status) {
            item.setStatus(status);
            return this;
        }
        
        public Builder popularity(Integer popularity) {
            item.setPopularity(popularity);
            return this;
        }
        
        public Builder user(User user) {
            item.setUser(user);
            return this;
        }
        
        public Builder createTime(LocalDateTime createTime) {
            item.setCreateTime(createTime);
            return this;
        }
        
        public Builder updateTime(LocalDateTime updateTime) {
            item.setUpdateTime(updateTime);
            return this;
        }
        
        public Item build() {
            return item;
        }
    }
} 