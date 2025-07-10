package com.campus.trading.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("ORDER")
public class OrderMessage extends Message {
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "status_text")
    private String statusText;
} 