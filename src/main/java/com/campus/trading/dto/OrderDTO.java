package com.campus.trading.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据传输对象
 */
public class OrderDTO {

    /**
     * ID
     */
    private Long id;

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
     * 交易金额
     */
    private BigDecimal itemPrice;

    /**
     * 订单状态：0-待确认，1-已确认，2-已拒绝，3-已完成，4-已取消
     */
    private Integer status;
    
    /**
     * 订单状态描述
     */
    private String statusText;

    /**
     * 交易方式：0-线下交易，1-在线支付
     */
    private Integer tradeType;
    
    /**
     * 交易方式描述
     */
    private String tradeTypeText;

    /**
     * 交易地点
     */
    private String tradeLocation;

    /**
     * 交易时间
     */
    private LocalDateTime tradeTime;

    /**
     * 买家留言
     */
    private String buyerMessage;

    /**
     * 卖家备注
     */
    private String sellerRemark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    private ItemDTO item;
    
    private String buyerComment;
    private String sellerComment;
    
    private Integer buyerRating;
    private Integer sellerRating;
    
    private String trackingNumber;
    
    public OrderDTO() {
    }
    
    public OrderDTO(Long id, String orderNo, Long buyerId, String buyerName, Long sellerId, String sellerName, Long itemId, String itemName, BigDecimal itemPrice, Integer status, String statusText, Integer tradeType, String tradeTypeText, String tradeLocation, LocalDateTime tradeTime, String buyerMessage, String sellerRemark, LocalDateTime createTime, LocalDateTime updateTime, LocalDateTime finishTime) {
        this.id = id;
        this.orderNo = orderNo;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.status = status;
        this.statusText = statusText;
        this.tradeType = tradeType;
        this.tradeTypeText = tradeTypeText;
        this.tradeLocation = tradeLocation;
        this.tradeTime = tradeTime;
        this.buyerMessage = buyerMessage;
        this.sellerRemark = sellerRemark;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.finishTime = finishTime;
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
    
    public BigDecimal getItemPrice() {
        return itemPrice;
    }
    
    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getStatusText() {
        return statusText;
    }
    
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    
    public Integer getTradeType() {
        return tradeType;
    }
    
    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }
    
    public String getTradeTypeText() {
        return tradeTypeText;
    }
    
    public void setTradeTypeText(String tradeTypeText) {
        this.tradeTypeText = tradeTypeText;
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
    
    public ItemDTO getItem() {
        return item;
    }
    
    public void setItem(ItemDTO item) {
        this.item = item;
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
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private OrderDTO dto = new OrderDTO();
        
        public Builder id(Long id) {
            dto.setId(id);
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
        
        public Builder itemPrice(BigDecimal itemPrice) {
            dto.setItemPrice(itemPrice);
            return this;
        }
        
        public Builder status(Integer status) {
            dto.setStatus(status);
            return this;
        }
        
        public Builder statusText(String statusText) {
            dto.setStatusText(statusText);
            return this;
        }
        
        public Builder tradeType(Integer tradeType) {
            dto.setTradeType(tradeType);
            return this;
        }
        
        public Builder tradeTypeText(String tradeTypeText) {
            dto.setTradeTypeText(tradeTypeText);
            return this;
        }
        
        public Builder tradeLocation(String tradeLocation) {
            dto.setTradeLocation(tradeLocation);
            return this;
        }
        
        public Builder tradeTime(LocalDateTime tradeTime) {
            dto.setTradeTime(tradeTime);
            return this;
        }
        
        public Builder buyerMessage(String buyerMessage) {
            dto.setBuyerMessage(buyerMessage);
            return this;
        }
        
        public Builder sellerRemark(String sellerRemark) {
            dto.setSellerRemark(sellerRemark);
            return this;
        }
        
        public Builder createTime(LocalDateTime createTime) {
            dto.setCreateTime(createTime);
            return this;
        }
        
        public Builder updateTime(LocalDateTime updateTime) {
            dto.setUpdateTime(updateTime);
            return this;
        }
        
        public Builder finishTime(LocalDateTime finishTime) {
            dto.setFinishTime(finishTime);
            return this;
        }
        
        public Builder item(ItemDTO item) {
            dto.setItem(item);
            return this;
        }
        
        public Builder buyerComment(String buyerComment) {
            dto.setBuyerComment(buyerComment);
            return this;
        }
        
        public Builder sellerComment(String sellerComment) {
            dto.setSellerComment(sellerComment);
            return this;
        }
        
        public Builder buyerRating(Integer buyerRating) {
            dto.setBuyerRating(buyerRating);
            return this;
        }
        
        public Builder sellerRating(Integer sellerRating) {
            dto.setSellerRating(sellerRating);
            return this;
        }
        
        public Builder trackingNumber(String trackingNumber) {
            dto.setTrackingNumber(trackingNumber);
            return this;
        }
        
        public OrderDTO build() {
            return dto;
        }
    }
} 