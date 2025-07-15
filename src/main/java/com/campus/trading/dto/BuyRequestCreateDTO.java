package com.campus.trading.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class BuyRequestCreateDTO {
    /**
     * 求购标题
     */
    @NotBlank(message = "求购标题不能为空")
    private String title;

    /**
     * 物品分类ID
     */
    @NotNull(message = "物品分类不能为空")
    private Long categoryId;

    /**
     * 物品新旧程度：1-10，1表示全新，10表示旧
     */
    @NotNull(message = "物品新旧程度不能为空")
    @Min(value = 1, message = "物品新旧程度必须在1-10之间")
    @Max(value = 10, message = "物品新旧程度必须在1-10之间")
    private Integer condition;

    /**
     * 物品价格
     */
    @NotNull(message = "物品价格不能为空")
    @DecimalMin(value = "0.01", message = "物品价格必须大于0")
    @DecimalMax(value = "999999.99", message = "物品价格不能超过999999.99")
    private BigDecimal expectedPrice;

    private Boolean negotiable;
    private String description;
    private String contact;
} 