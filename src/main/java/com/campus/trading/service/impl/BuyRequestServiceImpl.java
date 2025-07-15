package com.campus.trading.service.impl;

import com.campus.trading.config.SecurityUtil;
import com.campus.trading.dto.BuyRequestCreateDTO;
import com.campus.trading.dto.BuyRequestDTO;
import com.campus.trading.dto.BuyRequestUpdateDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.BuyRequest;
import com.campus.trading.entity.User;
import com.campus.trading.repository.BuyRequestRepository;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.service.BuyRequestService;
import com.campus.trading.service.CategoryService;
import com.campus.trading.service.ImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Log4j2
@Service
public class BuyRequestServiceImpl implements BuyRequestService {
    @Autowired
    private BuyRequestRepository buyRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ImageService imageService;

    @Override
    public PageResponseDTO<BuyRequestDTO> pageList(String keyword, Long categoryId, Pageable pageable) {
        // 简单实现：按标题/描述模糊、类别精确，分页
        Page<BuyRequest> page = buyRequestRepository.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();
            if (keyword != null && !keyword.isEmpty()) {
                predicates = cb.and(predicates, cb.or(
                        cb.like(root.get("title"), "%" + keyword + "%"),
                        cb.like(root.get("description"), "%" + keyword + "%")
                ));
            }
            if (categoryId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("category").get("id"), categoryId));
            }
            return predicates;
        }, pageable);
        List<BuyRequestDTO> dtoList = page.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return new PageResponseDTO<>(
                dtoList,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages()
        );
    }

    @Override
    public BuyRequestDTO getById(Long id) {
        BuyRequest buyRequest = buyRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("求购帖不存在"));
        return toDTO(buyRequest);
    }

    @Override
    @Transactional
    public BuyRequestDTO create(BuyRequestCreateDTO dto) {
        User user = userRepository.findById(SecurityUtil.getCurrentUser().getId()).orElseThrow(() -> new RuntimeException("用户不存在"));
        BuyRequest buyRequest = BuyRequest.builder()
                .title(dto.getTitle())
                .category(categoryService.findById(dto.getCategoryId()))
                .requestCondition(dto.getCondition())
                .expectedPrice(dto.getExpectedPrice())
                .negotiable(dto.getNegotiable())
                .description(dto.getDescription())
                .contact(dto.getContact())
                .user(user)
                .build();
        log.info("in create buyrequest: {}", buyRequest);
        buyRequest = buyRequestRepository.save(buyRequest);
        return toDTO(buyRequest);
    }

    @Override
    @Transactional
    public BuyRequestDTO update(Long id, BuyRequestUpdateDTO dto) {
        BuyRequest buyRequest = buyRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("求购帖不存在"));
        if (!buyRequest.getUser().getId().equals(SecurityUtil.getCurrentUser().getId())) {
            throw new RuntimeException("无权编辑该求购帖");
        }
        BeanUtils.copyProperties(dto, buyRequest, "id", "user", "createTime", "updateTime");
        buyRequest = buyRequestRepository.save(buyRequest);
        return toDTO(buyRequest);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BuyRequest buyRequest = buyRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("求购帖不存在"));
        if (!buyRequest.getUser().getId().equals(SecurityUtil.getCurrentUser().getId())) {
            throw new RuntimeException("无权删除该求购帖");
        }
        buyRequestRepository.delete(buyRequest);
    }

    private BuyRequestDTO toDTO(BuyRequest entity) {
        BuyRequestDTO dto = new BuyRequestDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setCategoryId(entity.getCategory() != null ? entity.getCategory().getId() : null);
        dto.setCategoryName(entity.getCategory().getName());
        dto.setCondition(entity.getRequestCondition());
        dto.setUserId(entity.getUser().getId());
        dto.setUsername(entity.getUser().getUsername());
        dto.setUserAvatar(entity.getUser().getAvatarImageId() != null ? imageService.generateImageAccessToken(entity.getUser().getAvatarImageId()) : null);
        dto.setCommentCount(entity.getComments() != null ? entity.getComments().size() : 0);
        return dto;
    }
} 