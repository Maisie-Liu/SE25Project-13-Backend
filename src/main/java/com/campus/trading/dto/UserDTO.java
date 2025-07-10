package com.campus.trading.dto;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户数据传输对象
 */
public class UserDTO {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatarUrl;
    private Set<String> roles;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private String bio;
    private String location;
    private LocalDateTime createTime;
    private Boolean allowPersonalizedRecommend;
    
    public UserDTO() {
    }
    
    public UserDTO(Long id, String username, String nickname, String email, String phone, String avatarUrl, Set<String> roles, Integer status, LocalDateTime lastLoginTime, LocalDateTime createTime, String bio) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.roles = roles;
        this.status = status;
        this.lastLoginTime = lastLoginTime;
        this.bio = bio;
        this.location = location;
        this.createTime = createTime;
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
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    /**
     * 设置用户头像图片ID的别名方法
     * @param avatarImageId 头像图片ID
     */
    public void setAvatarImageId(String avatarImageId) {
        this.avatarUrl = avatarImageId;
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
    
    public Boolean getAllowPersonalizedRecommend() {
        return allowPersonalizedRecommend;
    }
    
    public void setAllowPersonalizedRecommend(Boolean allowPersonalizedRecommend) {
        this.allowPersonalizedRecommend = allowPersonalizedRecommend;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UserDTO dto = new UserDTO();
        
        public Builder id(Long id) {
            dto.setId(id);
            return this;
        }
        
        public Builder username(String username) {
            dto.setUsername(username);
            return this;
        }
        
        public Builder nickname(String nickname) {
            dto.setNickname(nickname);
            return this;
        }
        
        public Builder email(String email) {
            dto.setEmail(email);
            return this;
        }
        
        public Builder phone(String phone) {
            dto.setPhone(phone);
            return this;
        }
        
        public Builder avatarUrl(String avatarUrl) {
            dto.setAvatarUrl(avatarUrl);
            return this;
        }
        
        public Builder roles(Set<String> roles) {
            dto.setRoles(roles);
            return this;
        }
        
        public Builder status(Integer status) {
            dto.setStatus(status);
            return this;
        }
        
        public Builder lastLoginTime(LocalDateTime lastLoginTime) {
            dto.setLastLoginTime(lastLoginTime);
            return this;
        }
        
        public Builder bio(String bio) {
            dto.setBio(bio);
            return this;
        }
        
        public Builder location(String location) {
            dto.setLocation(location);
            return this;
        }
        
        public Builder createTime(LocalDateTime createTime) {
            dto.setCreateTime(createTime);
            return this;
        }
        
        public Builder allowPersonalizedRecommend(Boolean allowPersonalizedRecommend) {
            dto.setAllowPersonalizedRecommend(allowPersonalizedRecommend);
            return this;
        }
        
        public UserDTO build() {
            return dto;
        }
    }
} 