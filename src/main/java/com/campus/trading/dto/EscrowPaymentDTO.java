package com.campus.trading.dto;

import java.math.BigDecimal;

/**
 * 定金支付DTO
 */
public class EscrowPaymentDTO {

    /**
     * 托管ID
     */
    private Long escrowId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付方式
     * 1: 支付宝
     * 2: 微信支付
     */
    private Integer paymentMethod;

    /**
     * 支付订单号
     */
    private String paymentOrderNo;

    /**
     * 支付链接
     */
    private String paymentUrl;
    
    /**
     * 支付二维码内容
     */
    private String qrCodeContent;
    
    /**
     * 支付状态
     * 1: 待支付
     * 2: 支付成功
     * 3: 支付失败
     */
    private Integer status;
    
    /**
     * 过期时间（秒）
     */
    private Integer expireSeconds;
    
    public EscrowPaymentDTO() {
    }
    
    public EscrowPaymentDTO(Long escrowId, String orderNo, BigDecimal amount, Integer paymentMethod, 
                           String paymentOrderNo, String paymentUrl, String qrCodeContent, 
                           Integer status, Integer expireSeconds) {
        this.escrowId = escrowId;
        this.orderNo = orderNo;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentOrderNo = paymentOrderNo;
        this.paymentUrl = paymentUrl;
        this.qrCodeContent = qrCodeContent;
        this.status = status;
        this.expireSeconds = expireSeconds;
    }
    
    public Long getEscrowId() {
        return escrowId;
    }
    
    public void setEscrowId(Long escrowId) {
        this.escrowId = escrowId;
    }
    
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Integer getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentOrderNo() {
        return paymentOrderNo;
    }
    
    public void setPaymentOrderNo(String paymentOrderNo) {
        this.paymentOrderNo = paymentOrderNo;
    }
    
    public String getPaymentUrl() {
        return paymentUrl;
    }
    
    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
    
    public String getQrCodeContent() {
        return qrCodeContent;
    }
    
    public void setQrCodeContent(String qrCodeContent) {
        this.qrCodeContent = qrCodeContent;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getExpireSeconds() {
        return expireSeconds;
    }
    
    public void setExpireSeconds(Integer expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private EscrowPaymentDTO dto = new EscrowPaymentDTO();
        
        public Builder escrowId(Long escrowId) {
            dto.setEscrowId(escrowId);
            return this;
        }
        
        public Builder orderNo(String orderNo) {
            dto.setOrderNo(orderNo);
            return this;
        }
        
        public Builder amount(BigDecimal amount) {
            dto.setAmount(amount);
            return this;
        }
        
        public Builder paymentMethod(Integer paymentMethod) {
            dto.setPaymentMethod(paymentMethod);
            return this;
        }
        
        public Builder paymentOrderNo(String paymentOrderNo) {
            dto.setPaymentOrderNo(paymentOrderNo);
            return this;
        }
        
        public Builder paymentUrl(String paymentUrl) {
            dto.setPaymentUrl(paymentUrl);
            return this;
        }
        
        public Builder qrCodeContent(String qrCodeContent) {
            dto.setQrCodeContent(qrCodeContent);
            return this;
        }
        
        public Builder status(Integer status) {
            dto.setStatus(status);
            return this;
        }
        
        public Builder expireSeconds(Integer expireSeconds) {
            dto.setExpireSeconds(expireSeconds);
            return this;
        }
        
        public EscrowPaymentDTO build() {
            return dto;
        }
    }
} 