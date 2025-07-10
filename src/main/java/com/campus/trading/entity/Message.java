package com.campus.trading.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "message_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_read")
    private boolean read;
    
    @Column(name = "message_type", insertable = false, updatable = false)
    private String messageType;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 