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

/**
 * 订单实体类
 */
@Entity
@Table(name = "t_order")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单编号
     */
    @Column(nullable = false, unique = true, length = 32)
    private String orderNo;

    /**
     * 买家
     */
    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    /**
     * 卖家
     */
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    /**
     * 物品
     */
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * 交易金额
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 订单状态：0-待确认，1-已确认，2-已拒绝，3-已完成，4-已取消
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 交易方式：0-线下交易，1-在线支付
     */
    @Column(nullable = false)
    private Integer tradeType;

    /**
     * 交易地点
     */
    @Column(length = 200)
    private String tradeLocation;

    /**
     * 交易时间
     */
    private LocalDateTime tradeTime;

    /**
     * 买家留言
     */
    @Column(length = 500)
    private String buyerMessage;

    /**
     * 卖家备注
     */
    @Column(length = 500)
    private String sellerRemark;

    /**
     * 买家评价（卖家对买家的评价）
     */
    @Column(length = 1000)
    private String buyerComment;

    /**
     * 卖家评价（买家对卖家的评价）
     */
    @Column(length = 1000)
    private String sellerComment;

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

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    private Integer buyerRating;
    private Integer sellerRating;
    
    private String trackingNumber;
    
    // 便利方法
    public Long getBuyerId() {
        return buyer != null ? buyer.getId() : null;
    }
    
    public Long getSellerId() {
        return seller != null ? seller.getId() : null;
    }
    
    public Long getItemId() {
        return item != null ? item.getId() : null;
    }
    
    public String getItemName() {
        return item != null ? item.getName() : null;
    }
    
    public BigDecimal getItemPrice() {
        return item != null ? item.getPrice() : null;
    }
} 