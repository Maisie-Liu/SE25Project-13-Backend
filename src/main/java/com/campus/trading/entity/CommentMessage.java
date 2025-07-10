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
@DiscriminatorValue("COMMENT")
public class CommentMessage extends Message {
    
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
} 