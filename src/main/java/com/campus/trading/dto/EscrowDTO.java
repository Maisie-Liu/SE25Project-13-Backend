package com.campus.trading.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 定金托管DTO
 */
public class EscrowDTO {

    /**
     * 托管ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 买家ID
     */
    private Long buyerId;

    /**
     * 买家名称
     */
    private String buyerName;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 卖家名称
     */
    private String sellerName;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 物品名称
     */
    private String itemName;

    /**
     * 托管金额
     */
    private BigDecimal escrowAmount;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 托管状态
     * 1: 未支付
     * 2: 已支付，交易中
     * 3: 已释放给卖家
     * 4: 已退还给买家
     * 5: 已过期
     */
    private Integer status;

    /**
     * 智能合约地址
     */
    private String contractAddress;

    /**
     * 智能合约交易哈希
     */
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
    private LocalDateTime expireTime;

    /**
     * 状态更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String remark;
    
    public EscrowDTO() {
    }
    
    public EscrowDTO(Long id, Long orderId, String orderNo, Long buyerId, String buyerName,
                   Long sellerId, String sellerName, Long itemId, String itemName,
                   BigDecimal escrowAmount, BigDecimal totalAmount, Integer status,
                   String contractAddress, String transactionHash, Integer paymentMethod,
                   LocalDateTime paymentTime, LocalDateTime expireTime, LocalDateTime updateTime,
                   LocalDateTime createTime, String remark) {
        this.id = id;
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.escrowAmount = escrowAmount;
        this.totalAmount = totalAmount;
        this.status = status;
        this.contractAddress = contractAddress;
        this.transactionHash = transactionHash;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
        this.expireTime = expireTime;
        this.updateTime = updateTime;
        this.createTime = createTime;
        this.remark = remark;
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
    
    public String getBuyerName() {
        return buyerName;
    }
    
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    public String getSellerName() {
        return sellerName;
    }
    
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
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
        private EscrowDTO dto = new EscrowDTO();
        
        public Builder id(Long id) {
            dto.setId(id);
            return this;
        }
        
        public Builder orderId(Long orderId) {
            dto.setOrderId(orderId);
            return this;
        }
        
        public Builder orderNo(String orderNo) {
            dto.setOrderNo(orderNo);
            return this;
        }
        
        public Builder buyerId(Long buyerId) {
            dto.setBuyerId(buyerId);
            return this;
        }
        
        public Builder buyerName(String buyerName) {
            dto.setBuyerName(buyerName);
            return this;
        }
        
        public Builder sellerId(Long sellerId) {
            dto.setSellerId(sellerId);
            return this;
        }
        
        public Builder sellerName(String sellerName) {
            dto.setSellerName(sellerName);
            return this;
        }
        
        public Builder itemId(Long itemId) {
            dto.setItemId(itemId);
            return this;
        }
        
        public Builder itemName(String itemName) {
            dto.setItemName(itemName);
            return this;
        }
        
        public Builder escrowAmount(BigDecimal escrowAmount) {
            dto.setEscrowAmount(escrowAmount);
            return this;
        }
        
        public Builder totalAmount(BigDecimal totalAmount) {
            dto.setTotalAmount(totalAmount);
            return this;
        }
        
        public Builder status(Integer status) {
            dto.setStatus(status);
            return this;
        }
        
        public Builder contractAddress(String contractAddress) {
            dto.setContractAddress(contractAddress);
            return this;
        }
        
        public Builder transactionHash(String transactionHash) {
            dto.setTransactionHash(transactionHash);
            return this;
        }
        
        public Builder paymentMethod(Integer paymentMethod) {
            dto.setPaymentMethod(paymentMethod);
            return this;
        }
        
        public Builder paymentTime(LocalDateTime paymentTime) {
            dto.setPaymentTime(paymentTime);
            return this;
        }
        
        public Builder expireTime(LocalDateTime expireTime) {
            dto.setExpireTime(expireTime);
            return this;
        }
        
        public Builder updateTime(LocalDateTime updateTime) {
            dto.setUpdateTime(updateTime);
            return this;
        }
        
        public Builder createTime(LocalDateTime createTime) {
            dto.setCreateTime(createTime);
            return this;
        }
        
        public Builder remark(String remark) {
            dto.setRemark(remark);
            return this;
        }
        
        public EscrowDTO build() {
            return dto;
        }
    }
} 