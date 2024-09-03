package com.example.Flower.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = {"user", "cmPost", "recomments", "parentComment"}) // 순환 참조 방지
@Entity
public class CMComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 유저 정보 가져오기

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmPost_id")
    @JsonBackReference // 순환 참조를 방지하기 위해 사용
    private CMPost cmPost; // 게시글 정보 가져오기

    @Column(name = "content")
    private String content; // 댓글 내용

    @Column
    private int likeCount; // 댓글 좋아요

    @Column
    private LocalDateTime regdate; // 댓글 작성 시간

    @Column
    private boolean disable; // 비활성화 여부

    @PrePersist
    protected void onCreate() {
        this.regdate = LocalDateTime.now(); // 현재 시간을 regdate에 설정
    }

    // 좋아요 수 증가 메서드
    public void incrementLikeCount() {
        this.likeCount++;
    }

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // 순환 참조를 방지하기 위해 사용
    private List<CMComment> recomments; // 대댓글 목록 가져오기

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @JsonBackReference // 순환 참조를 방지하기 위해 사용
    private CMComment parentComment; // 부모 댓글 참조

    // 유저 ID 가져오기
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    // 부모 댓글 ID 가져오기
    public Long getParentCommentId() {
        return parentComment != null ? parentComment.getId() : null;
    }

    @Setter
    @Getter
    @Transient
    private boolean canEdit;  // 수정 가능 여부

}
