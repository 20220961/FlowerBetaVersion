package com.example.Flower.api;

import com.example.Flower.controller.SessionCheckController;
import com.example.Flower.dto.DiaryForm;
import com.example.Flower.entity.Diary;
import com.example.Flower.entity.User;
import com.example.Flower.service.DiaryService;
import com.example.Flower.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diaries")
public class DiaryApiController extends SessionCheckController {

    private static final Logger logger = LoggerFactory.getLogger(DiaryApiController.class); // 로그 설정

    @Autowired
    private DiaryService diaryService; // DiaryService 의존성 주입

    @Autowired
    private UserService userService; // UserService 의존성 주입

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/create")
    public ResponseEntity<String> createDiary(@ModelAttribute DiaryForm form, HttpSession session) {
        logger.info("Request to create new diary: {}", form);  // 새 다이어리 생성 요청 로그

        Long userId = (Long) session.getAttribute("userId");
        User loginUser = userService.getLoginUserById(userId);
        if (loginUser == null) {
            logger.error("Logged in user not found.");
            return ResponseEntity.badRequest().body("User not found");
        }

        // MultipartFile을 byte[]로 변환
        List<byte[]> pictureBytesList = form.getPictures().stream().map(picture -> {
            try {
                return picture.getBytes();
            } catch (IOException e) {
                logger.error("Error reading picture bytes: {}", e.getMessage());
                return null;
            }
        }).collect(Collectors.toList());

        // 다이어리 엔티티 생성
        Diary diary = form.toEntity(pictureBytesList);  // 폼 데이터를 엔티티로 변환
        diary.setUser(loginUser);  // 로그인한 사용자 정보를 다이어리에 설정
        diary.setIsPublic(form.getIsPublic());  // 사용자가 선택한 공개 여부 설정

        diaryService.saveDiary(diary);  // 다이어리 저장
        logger.info("Diary saved successfully: {}", diary);  // 다이어리 저장 완료 로그

        return ResponseEntity.ok("Diary created successfully");
    }


    @GetMapping
    public ResponseEntity<List<DiaryForm>> listDiaries(HttpSession session) {
        logger.info("Requesting diary list");  // 다이어리 목록 요청 로그

        Long userId = (Long) session.getAttribute("userId");
        User loginUser = userService.getLoginUserById(userId);
        if (loginUser == null) {
            logger.error("Logged in user not found.");
            return ResponseEntity.badRequest().body(null);
        }

        List<Diary> diaries = diaryService.findDiariesForUser(userId); // 사용자에게 보여줄 다이어리 필터링
        List<DiaryForm> diaryForms = diaries.stream().map(diary -> {
            List<String> pictureBase64List = diary.getPictures() != null ? diary.getPictures().stream()
                    .map(picture -> Base64.getEncoder().encodeToString(picture))  // byte[] 데이터를 Base64 문자열로 변환
                    .collect(Collectors.toList()) : null;

            return new DiaryForm(
                    diary.getId(),
                    diary.getUser(),
                    diary.getTitle(),
                    diary.getContent(),
                    pictureBase64List,
                    diary.getRegdate(),
                    diary.getIsPublic() == 1L
            );
        }).collect(Collectors.toList());

        logger.info("Diary list retrieved successfully");  // 다이어리 목록 조회 완료 로그 출력
        return ResponseEntity.ok(diaryForms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDiary(@PathVariable Long id) {
        logger.info("Requesting diary detail: Diary ID {}", id);  // 로그: 다이어리 상세 조회 요청
        Diary diary = diaryService.findDiaryById(id);  // ID로 다이어리 조회
        if (diary == null) {
            return ResponseEntity.notFound().build();  // 다이어리를 찾지 못한 경우 404 응답 반환
        }

        if (diary.isDisable()) {
            logger.warn("Attempted access to disabled diary: Diary ID {}", id);  // 비활성화된 다이어리 접근 시 로그 출력
            // 비활성화된 다이어리에 대한 메시지를 반환
            return ResponseEntity.ok("This diary has been disabled.");
        }

        List<String> pictureBase64List = diary.getPictures() != null ? diary.getPictures().stream()
                .map(picture -> Base64.getEncoder().encodeToString(picture))  // byte[] 데이터를 Base64 문자열로 변환
                .collect(Collectors.toList()) : null;

        DiaryForm diaryForm = new DiaryForm(
                diary.getId(),
                diary.getUser(),
                diary.getTitle(),
                diary.getContent(),
                pictureBase64List,
                diary.getRegdate(),
                diary.getIsPublic() == 1L
        );

        logger.info("Diary detail retrieved successfully: Diary ID {}", id);  // 로그: 다이어리 상세 조회 성공
        return ResponseEntity.ok(diaryForm);  // DiaryForm 객체를 응답으로 반환
    }


    @PutMapping("/{id}/update")
    public ResponseEntity<String> updateDiary(
            @PathVariable Long id,
            @ModelAttribute DiaryForm form,
            HttpSession session) {

        logger.info("Request to update diary ID {}: {}", id, form);  // 다이어리 수정 요청 로그

        Long userId = (Long) session.getAttribute("userId");
        User loginUser = userService.getLoginUserById(userId);
        if (loginUser == null) {
            logger.error("Logged in user not found.");
            return ResponseEntity.badRequest().body("User not found");
        }

        Diary existingDiary = diaryService.findDiaryById(id);
        if (existingDiary == null || !existingDiary.getUser().getId().equals(loginUser.getId())) {
            logger.error("Diary not found or unauthorized update attempt.");
            return ResponseEntity.status(403).body("Diary not found or unauthorized");
        }

        existingDiary.setTitle(form.getTitle());
        existingDiary.setContent(form.getContent());

        if (form.getPictures() != null && !form.getPictures().isEmpty()) {
            List<byte[]> pictureBytesList = form.getPictures().stream()
                    .map(picture -> {
                        try {
                            return picture.getBytes();
                        } catch (IOException e) {
                            logger.error("Error reading picture bytes: {}", e.getMessage());
                            return null;
                        }
                    })
                    .collect(Collectors.toList());

            existingDiary.getPictures().clear();  // 기존 사진 제거
            existingDiary.getPictures().addAll(pictureBytesList);  // 새 사진 추가
        }

        diaryService.saveDiary(existingDiary);  // 수정된 다이어리 저장
        logger.info("Diary updated successfully: {}", existingDiary);  // 다이어리 수정 완료 로그
        return ResponseEntity.ok("Diary updated successfully");
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<String> deleteDiary(@PathVariable Long id, HttpSession session) {
        logger.info("Request to delete diary ID {}", id);  // 다이어리 삭제 요청 로그

        Long userId = (Long) session.getAttribute("userId");
        User loginUser = userService.getLoginUserById(userId);
        if (loginUser == null) {
            logger.error("Logged in user not found.");
            return ResponseEntity.badRequest().body("User not found");
        }

        Diary existingDiary = diaryService.findDiaryById(id);
        if (existingDiary == null || !existingDiary.getUser().getId().equals(loginUser.getId())) {
            logger.error("Diary not found or unauthorized delete attempt.");
            return ResponseEntity.status(403).body("Diary not found or unauthorized");
        }

        existingDiary.setDisable(true);  // 다이어리를 삭제로 설정
        diaryService.saveDiary(existingDiary);  // 수정된 다이어리 저장

        logger.info("Diary deleted successfully: {}", existingDiary);  // 다이어리 삭제 완료 로그
        return ResponseEntity.ok("Diary deleted successfully");
    }
}
