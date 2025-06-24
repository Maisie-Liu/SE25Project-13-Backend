package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 物品创建请求数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
} 