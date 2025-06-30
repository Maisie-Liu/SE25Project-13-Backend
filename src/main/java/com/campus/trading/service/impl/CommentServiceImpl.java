package com.campus.trading.service.impl;

import com.campus.trading.dto.CommentDTO;
import com.campus.trading.entity.Comment;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import com.campus.trading.repository.CommentRepository;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CommentDTO addComment(CommentDTO commentDTO, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("用户不存在"));
        Item item = itemRepository.findById(commentDTO.getItemId()).orElseThrow(() -> new RuntimeException("物品不存在"));
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setUser(user);
        comment.setItem(item);
        comment.setParentId(commentDTO.getParentId());
        comment.setReplyUserId(commentDTO.getReplyUserId());
        comment.setStatus(1); // 默认通过
        Comment saved = commentRepository.save(comment);
        return toDTO(saved, user.getUsername(), user.getAvatarImageId(), null);
    }

    @Override
    public List<CommentDTO> getCommentsByItemId(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("物品不存在"));
        List<Comment> comments = commentRepository.findByItemAndParentIdIsNullOrderByCreateTimeDesc(item);
        return comments.stream().map(c -> toDTOWithReplies(c)).collect(Collectors.toList());
    }

    private CommentDTO toDTOWithReplies(Comment comment) {
        User user = comment.getUser();
        CommentDTO dto = toDTO(comment, user.getUsername(), user.getAvatarImageId(), null);
        List<Comment> replies = commentRepository.findByParentIdOrderByCreateTimeAsc(comment.getId());
        List<CommentDTO> replyDTOs = new ArrayList<>();
        for (Comment reply : replies) {
            User replyUser = reply.getUser();
            String replyUsername = null;
            if (reply.getReplyUserId() != null) {
                User replyToUser = userRepository.findById(reply.getReplyUserId()).orElse(null);
                replyUsername = replyToUser != null ? replyToUser.getUsername() : null;
            }
            replyDTOs.add(toDTO(reply, 
                replyUser != null ? replyUser.getUsername() : null, 
                replyUser != null ? replyUser.getAvatarImageId() : null, 
                replyUsername));
        }
        dto.setReplies(replyDTOs);
        return dto;
    }

    private CommentDTO toDTO(Comment comment, String username, String avatar, String replyUsername) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(username);
        dto.setUserAvatar(avatar);
        dto.setItemId(comment.getItem().getId());
        dto.setParentId(comment.getParentId());
        dto.setReplyUserId(comment.getReplyUserId());
        dto.setReplyUsername(replyUsername);
        dto.setStatus(comment.getStatus());
        dto.setCreateTime(comment.getCreateTime());
        return dto;
    }
} 