package com.campus.trading.service.impl;

import com.campus.trading.config.SecurityUtil;
import com.campus.trading.dto.BuyRequestCommentCreateDTO;
import com.campus.trading.dto.BuyRequestCommentDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.BuyRequest;
import com.campus.trading.entity.BuyRequestComment;
import com.campus.trading.entity.User;
import com.campus.trading.repository.BuyRequestCommentRepository;
import com.campus.trading.repository.BuyRequestRepository;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.service.BuyRequestCommentService;
import com.campus.trading.service.ImageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyRequestCommentServiceImpl implements BuyRequestCommentService {
    @Autowired
    private BuyRequestCommentRepository commentRepository;
    @Autowired
    private BuyRequestRepository buyRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageService imageService;

    @Override
    public PageResponseDTO<BuyRequestCommentDTO> pageList(Long buyRequestId, Pageable pageable) {
        Page<BuyRequestComment> page = commentRepository.findByBuyRequestId(buyRequestId, pageable);
        List<BuyRequestCommentDTO> dtoList = page.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return new PageResponseDTO<>(
            dtoList,
            page.getTotalElements(),
            page.getNumber(),
            page.getSize(),
            page.getTotalPages()
        );
    }

    @Override
    @Transactional
    public BuyRequestCommentDTO create(BuyRequestCommentCreateDTO dto) {
        User user = userRepository.findById(SecurityUtil.getCurrentUser().getId()).orElseThrow(() -> new RuntimeException("用户不存在"));
        BuyRequest buyRequest = buyRequestRepository.findById(dto.getBuyRequestId()).orElseThrow(() -> new RuntimeException("求购帖不存在"));
        BuyRequestComment comment = BuyRequestComment.builder()
                .content(dto.getContent())
                .user(user)
                .buyRequest(buyRequest)
                .parentId(dto.getParentId())
                .replyUserId(dto.getReplyUserId())
                .status(1)
                .build();
        comment = commentRepository.save(comment);
        return toDTO(comment);
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        BuyRequestComment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("评论不存在"));
        Long currentUserId = SecurityUtil.getCurrentUser().getId();
        // 允许评论作者或求购帖作者都能删除
        if (!comment.getUser().getId().equals(currentUserId)
            && !comment.getBuyRequest().getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("无权删除该评论");
        }
        commentRepository.delete(comment);
    }

    private BuyRequestCommentDTO toDTO(BuyRequestComment entity) {
        BuyRequestCommentDTO dto = new BuyRequestCommentDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setUserId(entity.getUser().getId());
        dto.setUsername(entity.getUser().getUsername());
        dto.setUserAvatar(entity.getUser().getAvatarImageId() != null ? imageService.generateImageAccessToken(entity.getUser().getAvatarImageId()) : null);
        return dto;
    }
} 