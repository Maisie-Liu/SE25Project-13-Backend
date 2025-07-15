package com.campus.trading.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_buy_request_comment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BuyRequestComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 评论内容
     */
    @Column(nullable = false, length = 500)
    private String content;

    /**
     * 评论用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 所属求购帖
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_request_id", nullable = false)
    private BuyRequest buyRequest;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 被回复用户ID
     */
    private Long replyUserId;

    /**
     * 评论状态：0-待审核，1-通过，2-拒绝
     */
    @Column(nullable = false)
    private Integer status;

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
} 