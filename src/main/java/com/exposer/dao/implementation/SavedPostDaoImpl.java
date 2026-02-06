package com.exposer.dao.implementation;

import com.exposer.dao.interfaces.SavedPostDao;
import com.exposer.dao.repository.SavedPostRepository;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.entity.SavedPost;
import com.exposer.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
@RequiredArgsConstructor
class SavedPostDaoImpl implements SavedPostDao {

    private final SavedPostRepository savedPostRepository;


    @Override
    public Optional<SavedPost> findById(String s) {
        return savedPostRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return savedPostRepository.existsById(s);
    }

    @Override
    public void deleteById(String s) {
        savedPostRepository.deleteById(s);
    }

    @Override
    public SavedPost save(SavedPost savedPost) {
        return savedPostRepository.save(savedPost);
    }

    @Override
    public Page<SavedPost> findAll(PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        return savedPostRepository.findAll(pageable);

    }

    @Override
    public Page<SavedPost> findByUser(String userId, PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);
        return savedPostRepository.findByUserId(userId, pageable);
    }

    @Override
    public boolean existsByUserAndPostId(String userId, String postId) {
        return savedPostRepository.existsByUserIdAndPostId(userId, postId);
    }
}
