package com.campus.trading.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 定金托管实体类
 */
@Entity
@Table(name = "t_escrow")
public class Escrow {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单ID
     */
    @Column(nullable = false)
    private Long orderId;

    /**
     * 订单编号
     */
    @Column(length = 64, nullable = false)
    private String orderNo;

    /**
     * 买家ID
     */
    @Column(nullable = false)
    private Long buyerId;

    /**
     * 卖家ID
     */
    @Column(nullable = false)
    private Long sellerId;

    /**
     * 物品ID
     */
    @Column(nullable = false)
    private Long itemId;

    /**
     * 物品名称
     */
    @Column(length = 128, nullable = false)
    private String itemName;

    /**
     * 托管金额
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal escrowAmount;

    /**
     * 总金额
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    /**
     * 托管状态
     * 1: 未支付
     * 2: 已支付，交易中
     * 3: 已释放给卖家
     * 4: 已退还给买家
     * 5: 已过期
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 智能合约地址
     */
    @Column(length = 64)
    private String contractAddress;

    /**
     * 智能合约交易哈希
     */
    @Column(length = 128)
    private String transactionHash;

    /**
     * 支付方式
     * 1: 支付宝
     * 2: 微信支付
     */
    private Integer paymentMethod;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 过期时间
     */
    @Column(nullable = false)
    private LocalDateTime expireTime;

    /**
     * 状态更新时间
     */
    @UpdateTimestamp
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 备注
     */
    @Column(length = 512)
    private String remark;
    
    public Escrow() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public Long getBuyerId() {
        return buyerId;
    }
    
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public BigDecimal getEscrowAmount() {
        return escrowAmount;
    }
    
    public void setEscrowAmount(BigDecimal escrowAmount) {
        this.escrowAmount = escrowAmount;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getContractAddress() {
        return contractAddress;
    }
    
    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
    
    public String getTransactionHash() {
        return transactionHash;
    }
    
    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
    
    public Integer getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
    
    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Escrow escrow = new Escrow();
        
        public Builder id(Long id) {
            escrow.setId(id);
            return this;
        }
        
        public Builder orderId(Long orderId) {
            escrow.setOrderId(orderId);
            return this;
        }
        
        public Builder orderNo(String orderNo) {
            escrow.setOrderNo(orderNo);
            return this;
        }
        
        public Builder buyerId(Long buyerId) {
            escrow.setBuyerId(buyerId);
            return this;
        }
        
        public Builder sellerId(Long sellerId) {
            escrow.setSellerId(sellerId);
            return this;
        }
        
        public Builder itemId(Long itemId) {
            escrow.setItemId(itemId);
            return this;
        }
        
        public Builder itemName(String itemName) {
            escrow.setItemName(itemName);
            return this;
        }
        
        public Builder escrowAmount(BigDecimal escrowAmount) {
            escrow.setEscrowAmount(escrowAmount);
            return this;
        }
        
        public Builder totalAmount(BigDecimal totalAmount) {
            escrow.setTotalAmount(totalAmount);
            return this;
        }
        
        public Builder status(Integer status) {
            escrow.setStatus(status);
            return this;
        }
        
        public Builder contractAddress(String contractAddress) {
            escrow.setContractAddress(contractAddress);
            return this;
        }
        
        public Builder transactionHash(String transactionHash) {
            escrow.setTransactionHash(transactionHash);
            return this;
        }
        
        public Builder paymentMethod(Integer paymentMethod) {
            escrow.setPaymentMethod(paymentMethod);
            return this;
        }
        
        public Builder paymentTime(LocalDateTime paymentTime) {
            escrow.setPaymentTime(paymentTime);
            return this;
        }
        
        public Builder expireTime(LocalDateTime expireTime) {
            escrow.setExpireTime(expireTime);
            return this;
        }
        
        public Builder updateTime(LocalDateTime updateTime) {
            escrow.setUpdateTime(updateTime);
            return this;
        }
        
        public Builder createTime(LocalDateTime createTime) {
            escrow.setCreateTime(createTime);
            return this;
        }
        
        public Builder remark(String remark) {
            escrow.setRemark(remark);
            return this;
        }
        
        public Escrow build() {
            return escrow;
        }
    }
} 