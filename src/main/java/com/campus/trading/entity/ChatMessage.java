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
@DiscriminatorValue("CHAT")
public class ChatMessage extends Message {
    
    @Column(name = "content")
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    
    @Column(name = "chat_id")
    private Long chatId;
} 