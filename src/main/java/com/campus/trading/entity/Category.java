package com.campus.trading.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 物品分类实体类
 */
@Data
@Entity
@Table(name = "t_category")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 分类名称
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 分类描述
     */
    @Column(length = 200)
    private String description;

    /**
     * 分类图标
     */
    @Column(length = 255)
    private String icon;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 父分类ID
     */
    private Long parentId;

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