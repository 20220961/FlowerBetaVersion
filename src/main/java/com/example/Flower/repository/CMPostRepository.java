package com.example.Flower.repository;

import com.example.Flower.entity.CMPost;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CMPostRepository extends JpaRepository<CMPost, Long> {
    List<CMPost> findTop4ByOrderByRegdateDesc(); // 최신 게시글 4개 조회
    List<CMPost> findAll(Sort regdate);

    // 특정 사용자가 작성한 게시글을 내림차순으로 조회하는 쿼리 메서드
    List<CMPost> findByUserIdOrderByRegdateDesc(Long userId);

    // 게시글을 비활성화 여부에 따라 조회
    List<CMPost> findByDisableFalse();
}
