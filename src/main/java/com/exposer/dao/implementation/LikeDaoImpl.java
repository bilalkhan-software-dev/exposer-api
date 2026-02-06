package com.exposer.dao.implementation;

import com.exposer.dao.interfaces.LikeDao;
import com.exposer.dao.repository.LikeRepository;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.entity.Like;
import com.exposer.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class LikeDaoImpl implements LikeDao {

    private final LikeRepository likeRepository;


    @Override
    public Optional<Like> findById(String s) {
        return likeRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return likeRepository.existsById(s);
    }

    @Override
    public boolean existsByUserIdAndTargetId(String userId, String targetId) {
        return likeRepository.existsByUserIdAndTargetId(userId, targetId);
    }

    @Override
    public void deleteById(String s) {
        likeRepository.deleteById(s);
    }

    @Override
    public Like save(Like like) {
        return likeRepository.save(like);
    }

    @Override
    public Page<Like> findAll(PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        return likeRepository.findAll(pageable);
    }

    @Override
    public Page<Like> findByTargetId(String targetId, PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        return likeRepository.findByTargetId(targetId, pageable);
    }

    @Override
    public Page<Like> findByUserId(String userId, PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        return likeRepository.findByUserId(userId, pageable);
    }

}
