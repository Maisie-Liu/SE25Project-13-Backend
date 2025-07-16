package com.campus.trading.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "t_user_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    /**
     * 类别兴趣分数，key为类别ID，value为兴趣权重
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_profile_category_interest", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "category_id")
    @Column(name = "interest_score")
    private Map<Long, Double> categoryInterest;

    /**
     * 信誉分，初始100分
     */
    @Column(nullable = false)
    private Integer reputationScore = 100;

    public Integer getReputationScore() {
        return reputationScore;
    }
    public void setReputationScore(Integer reputationScore) {
        this.reputationScore = reputationScore;
    }

    /**
     * 画像最后更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updateTime;
} 