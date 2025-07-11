package com.campus.trading.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 */
@Entity
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 密码
     */
    @Column(nullable = false)
    private String password;

    /**
     * 昵称
     */
    @Column(length = 50)
    private String nickname;

    /**
     * 邮箱
     */
    @Column(unique = true, length = 100)
    private String email;

    /**
     * 手机号
     */
    @Column(length = 20)
    private String phone;

    /**
     * 头像图片ID
     */
    @Column(length = 255)
    private String avatarImageId;

    /**
     * 用户角色
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "t_user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    /**
     * 账号状态：0-禁用，1-启用
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 个人简介
     */
    @Column(length = 500)
    private String bio;
    
    /**
     * 所在地
     */
    @Column(length = 100)
    private String location;

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

    /**
     * 是否允许个性化推荐和数据采集
     */
    @Column(nullable = false)
    private boolean allowPersonalizedRecommend = true;
    
    public User() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAvatarImageId() {
        return avatarImageId;
    }
    
    public void setAvatarImageId(String avatarImageId) {
        this.avatarImageId = avatarImageId;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
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
    
    public boolean isAllowPersonalizedRecommend() {
        return allowPersonalizedRecommend;
    }
    
    public void setAllowPersonalizedRecommend(boolean allowPersonalizedRecommend) {
        this.allowPersonalizedRecommend = allowPersonalizedRecommend;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private User user = new User();
        
        public Builder id(Long id) {
            user.setId(id);
            return this;
        }
        
        public Builder username(String username) {
            user.setUsername(username);
            return this;
        }
        
        public Builder password(String password) {
            user.setPassword(password);
            return this;
        }
        
        public Builder nickname(String nickname) {
            user.setNickname(nickname);
            return this;
        }
        
        public Builder email(String email) {
            user.setEmail(email);
            return this;
        }
        
        public Builder phone(String phone) {
            user.setPhone(phone);
            return this;
        }
        
        public Builder avatarImageId(String avatarImageId) {
            user.setAvatarImageId(avatarImageId);
            return this;
        }
        
        public Builder roles(Set<String> roles) {
            user.setRoles(roles);
            return this;
        }
        
        public Builder status(Integer status) {
            user.setStatus(status);
            return this;
        }
        
        public Builder lastLoginTime(LocalDateTime lastLoginTime) {
            user.setLastLoginTime(lastLoginTime);
            return this;
        }
        
        public Builder bio(String bio) {
            user.setBio(bio);
            return this;
        }
        
        public Builder location(String location) {
            user.setLocation(location);
            return this;
        }
        
        public Builder createTime(LocalDateTime createTime) {
            user.setCreateTime(createTime);
            return this;
        }
        
        public Builder updateTime(LocalDateTime updateTime) {
            user.setUpdateTime(updateTime);
            return this;
        }
        
        public Builder allowPersonalizedRecommend(boolean allowPersonalizedRecommend) {
            user.setAllowPersonalizedRecommend(allowPersonalizedRecommend);
            return this;
        }
        
        public User build() {
            return user;
        }
    }
} 