package com.campus.trading.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "t_buy_request")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BuyRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 求购标题
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 物品分类
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * 物品新旧程度：1-10，1表示全新，10表示旧
     */
    @Column(nullable = false)
    private Integer requestCondition;

    /**
     * 期望价格
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal expectedPrice;

    /**
     * 是否可议价
     */
    @Column(nullable = false)
    private Boolean negotiable;

    /**
     * 求购详情
     */
    @Column(length = 2000)
    private String description;

    /**
     * 联系方式
     */
    @Column(length = 100)
    private String contact;

    /**
     * 发布用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 评论列表
     */
    @OneToMany(mappedBy = "buyRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuyRequestComment> comments;

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