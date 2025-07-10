package com.campus.trading.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.elasticsearch.annotations.DateFormat;

@Document(indexName = "items")
public class ItemDocument {
    @Id
    private Long id;
    @Field(type = FieldType.Text)
    private String name;
    @Field(type = FieldType.Long)
    private Long categoryId;
    @Field(type = FieldType.Text)
    private String categoryName;
    @Field(type = FieldType.Double)
    private BigDecimal price;
    @Field(type = FieldType.Text)
    private String description;
    @Field(type = FieldType.Keyword)
    private List<String> imageIds;
    @Field(type = FieldType.Integer)
    private Integer itemCondition;
    @Field(type = FieldType.Integer)
    private Integer status;
    @Field(type = FieldType.Integer)
    private Integer popularity;
    @Field(type = FieldType.Long)
    private Long userId;
    @Field(type = FieldType.Text)
    private String username;
    @Field(type = FieldType.Text)
    private String userAvatar;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updateTime;
    @Field(type = FieldType.Integer)
    private Integer stock;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getImageIds() { return imageIds; }
    public void setImageIds(List<String> imageIds) { this.imageIds = imageIds; }
    public Integer getItemCondition() { return itemCondition; }
    public void setItemCondition(Integer itemCondition) { this.itemCondition = itemCondition; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPopularity() { return popularity; }
    public void setPopularity(Integer popularity) { this.popularity = popularity; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
} 