package com.example.Flower.service;

import com.example.Flower.entity.CMPost;
import com.example.Flower.repository.CMPictureRepository;
import com.example.Flower.repository.CMPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CMPostService {

    @Autowired
    private CMPostRepository cmPostRepository;  // CMPostRepository 의존성 주입

    @Autowired
    private CMPictureRepository cmPictureRepository;  // CMPictureRepository 의존성 주입

    public void savePost(CMPost post) {
        cmPostRepository.save(post);  // 게시글 저장
        if (post.getPictures() != null) {
            cmPictureRepository.saveAll(post.getPictures());  // 관련된 사진도 함께 저장
        }
    }

    public List<CMPost> findAllPosts() {
        return cmPostRepository.findByDisableFalse();  // 비활성화 되지 않은 모든 게시글 조회
    }

    public CMPost findPostById(Long id) {
        Optional<CMPost> post = cmPostRepository.findById(id);  // ID로 게시글 조회
        return post.orElse(null);  // 결과 반환
    }

    public void incrementCount(Long id) {
        CMPost post = findPostById(id);
        if (post != null) {
            post.incrementCount();  // 조회수 증가
            savePost(post);  // 업데이트된 게시글 저장
        }
    }

    public void incrementLikeCount(Long id) {
        CMPost post = findPostById(id);
        if (post != null) {
            post.incrementLikeCount();  // 좋아요 수 증가
            savePost(post);  // 업데이트된 게시글 저장
        }
    }

    public List<CMPost> findTop4PostsDesc() {
        return cmPostRepository.findTop4ByOrderByRegdateDesc();  // 최신 게시글 4개 조회
    }

    public List<CMPost> findPostsByUserId(Long userId) {
        return cmPostRepository.findByUserIdOrderByRegdateDesc(userId);  // 사용자의 게시글 조회
    }

}