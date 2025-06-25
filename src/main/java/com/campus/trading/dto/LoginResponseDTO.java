package com.campus.trading.dto;

import java.util.Set;

/**
 * 登录响应数据传输对象
 */
public class LoginResponseDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色列表
     */
    private Set<String> roles;

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 令牌过期时间（毫秒）
     */
    private Long expiresIn;
    
    public LoginResponseDTO() {
    }
    
    public LoginResponseDTO(Long userId, String username, String nickname, String avatar, Set<String> roles, String token, Long expiresIn) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.roles = roles;
        this.token = token;
        this.expiresIn = expiresIn;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private LoginResponseDTO dto = new LoginResponseDTO();
        
        public Builder userId(Long userId) {
            dto.setUserId(userId);
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
        
        public Builder avatar(String avatar) {
            dto.setAvatar(avatar);
            return this;
        }
        
        public Builder roles(Set<String> roles) {
            dto.setRoles(roles);
            return this;
        }
        
        public Builder token(String token) {
            dto.setToken(token);
            return this;
        }
        
        public Builder expiresIn(Long expiresIn) {
            dto.setExpiresIn(expiresIn);
            return this;
        }
        
        public LoginResponseDTO build() {
            return dto;
        }
    }
} 