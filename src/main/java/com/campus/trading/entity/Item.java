package com.campus.trading.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@Entity
@Table(name = "t_item")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Integer condition;

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
} 