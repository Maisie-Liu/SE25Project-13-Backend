package com.campus.trading.entity;

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
    
    public Order() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public User getBuyer() {
        return buyer;
    }
    
    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }
    
    public User getSeller() {
        return seller;
    }
    
    public void setSeller(User seller) {
        this.seller = seller;
    }
    
    public Item getItem() {
        return item;
    }
    
    public void setItem(Item item) {
        this.item = item;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getTradeType() {
        return tradeType;
    }
    
    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }
    
    public String getTradeLocation() {
        return tradeLocation;
    }
    
    public void setTradeLocation(String tradeLocation) {
        this.tradeLocation = tradeLocation;
    }
    
    public LocalDateTime getTradeTime() {
        return tradeTime;
    }
    
    public void setTradeTime(LocalDateTime tradeTime) {
        this.tradeTime = tradeTime;
    }
    
    public String getBuyerMessage() {
        return buyerMessage;
    }
    
    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }
    
    public String getSellerRemark() {
        return sellerRemark;
    }
    
    public void setSellerRemark(String sellerRemark) {
        this.sellerRemark = sellerRemark;
    }
    
    public String getBuyerComment() {
        return buyerComment;
    }
    
    public void setBuyerComment(String buyerComment) {
        this.buyerComment = buyerComment;
    }
    
    public String getSellerComment() {
        return sellerComment;
    }
    
    public void setSellerComment(String sellerComment) {
        this.sellerComment = sellerComment;
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
    
    public LocalDateTime getFinishTime() {
        return finishTime;
    }
    
    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }
    
    public Integer getBuyerRating() {
        return buyerRating;
    }
    
    public void setBuyerRating(Integer buyerRating) {
        this.buyerRating = buyerRating;
    }
    
    public Integer getSellerRating() {
        return sellerRating;
    }
    
    public void setSellerRating(Integer sellerRating) {
        this.sellerRating = sellerRating;
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    
    // Helper methods for EscrowServiceImpl
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
        return amount;
    }
    
    public static class Builder {
        private Order order = new Order();
        
        public Builder id(Long id) {
            order.setId(id);
            return this;
        }
        
        public Builder orderNo(String orderNo) {
            order.setOrderNo(orderNo);
            return this;
        }
        
        public Builder buyer(User buyer) {
            order.setBuyer(buyer);
            return this;
        }
        
        public Builder seller(User seller) {
            order.setSeller(seller);
            return this;
        }
        
        public Builder item(Item item) {
            order.setItem(item);
            return this;
        }
        
        public Builder amount(BigDecimal amount) {
            order.setAmount(amount);
            return this;
        }
        
        public Builder status(Integer status) {
            order.setStatus(status);
            return this;
        }
        
        public Builder tradeType(Integer tradeType) {
            order.setTradeType(tradeType);
            return this;
        }
        
        public Builder tradeLocation(String tradeLocation) {
            order.setTradeLocation(tradeLocation);
            return this;
        }
        
        public Builder tradeTime(LocalDateTime tradeTime) {
            order.setTradeTime(tradeTime);
            return this;
        }
        
        public Builder buyerMessage(String buyerMessage) {
            order.setBuyerMessage(buyerMessage);
            return this;
        }
        
        public Builder sellerRemark(String sellerRemark) {
            order.setSellerRemark(sellerRemark);
            return this;
        }
        
        public Builder buyerComment(String buyerComment) {
            order.setBuyerComment(buyerComment);
            return this;
        }
        
        public Builder sellerComment(String sellerComment) {
            order.setSellerComment(sellerComment);
            return this;
        }
        
        public Builder createTime(LocalDateTime createTime) {
            order.setCreateTime(createTime);
            return this;
        }
        
        public Builder updateTime(LocalDateTime updateTime) {
            order.setUpdateTime(updateTime);
            return this;
        }
        
        public Builder finishTime(LocalDateTime finishTime) {
            order.setFinishTime(finishTime);
            return this;
        }
        
        public Builder buyerRating(Integer buyerRating) {
            order.setBuyerRating(buyerRating);
            return this;
        }
        
        public Builder sellerRating(Integer sellerRating) {
            order.setSellerRating(sellerRating);
            return this;
        }
        
        public Builder trackingNumber(String trackingNumber) {
            order.setTrackingNumber(trackingNumber);
            return this;
        }
        
        public Order build() {
            return order;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
} 