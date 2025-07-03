package com.campus.trading.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 物品创建请求数据传输对象
 */
public class ItemCreateRequestDTO {

    /**
     * 物品名称
     */
    @NotBlank(message = "物品名称不能为空")
    @Size(max = 100, message = "物品名称长度不能超过100个字符")
    private String name;

    /**
     * 物品分类ID
     */
    @NotNull(message = "物品分类不能为空")
    private Long categoryId;

    /**
     * 物品价格
     */
    @NotNull(message = "物品价格不能为空")
    @DecimalMin(value = "0.01", message = "物品价格必须大于0")
    @DecimalMax(value = "999999.99", message = "物品价格不能超过999999.99")
    private BigDecimal price;

    /**
     * 物品描述
     */
    @Size(max = 2000, message = "物品描述长度不能超过2000个字符")
    private String description;

    /**
     * 物品图片列表
     */
    @Size(min = 1, message = "至少上传一张物品图片")
    private List<String> images;

    /**
     * 物品新旧程度：1-10，1表示全新，10表示旧
     */
    @NotNull(message = "物品新旧程度不能为空")
    @Min(value = 1, message = "物品新旧程度必须在1-10之间")
    @Max(value = 10, message = "物品新旧程度必须在1-10之间")
    private Integer condition;

    /**
     * 库存
     */
    @NotNull(message = "库存不能为空")
    @Min(value = 1, message = "库存必须大于0")
    private Integer stock;
    
    public ItemCreateRequestDTO() {
    }
    
    public ItemCreateRequestDTO(String name, Long categoryId, BigDecimal price, String description, List<String> images, Integer condition, Integer stock) {
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.description = description;
        this.images = images;
        this.condition = condition;
        this.stock = stock;
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
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
} 