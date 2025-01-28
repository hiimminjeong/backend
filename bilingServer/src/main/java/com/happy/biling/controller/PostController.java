package com.happy.biling.controller;

import com.happy.biling.domain.service.PostService;
import com.happy.biling.dto.post.FilteredPostPreviewResponseDto;
import com.happy.biling.dto.post.PostDetailResponseDto;
import com.happy.biling.dto.post.PostPreviewResponseDto;
import com.happy.biling.dto.post.PostWriteRequestDto;
import com.happy.biling.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;
    
    // 게시글 상세보기
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDetailResponseDto> getPostDetail(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.substring(7); // "Bearer " 이후의 토큰 추출
            Long userId = Long.valueOf(jwtUtil.getUserIdFromToken(token));
            PostDetailResponseDto responseDto = postService.getPostDetail(id, userId);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); //실패
        }
    }
    
    // 게시글 생성
    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPost(
            @RequestHeader("Authorization") String authHeader,
            @ModelAttribute PostWriteRequestDto requestDto) {

        log.info("Entering createPost method. Authorization Header: {}, Request DTO: {}", authHeader, requestDto);

        try {
            String token = authHeader.substring(7); // "Bearer " 이후의 토큰 추출
            Long userId = Long.valueOf(jwtUtil.getUserIdFromToken(token));
            log.info("Extracted User ID: {}", userId);

            postService.createPost(requestDto, userId);
            log.info("Post creation completed.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 성공
        } catch (Exception e) {
            log.error("Error during post creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 실패
        }
    }

    // 게시글 목록 조회
    @GetMapping("/posts")
    public ResponseEntity<List<FilteredPostPreviewResponseDto>> getFilteredPosts(
            @RequestParam("type") String type,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "radius", required = false) String radius,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.substring(7); // "Bearer " 제거
            Long userId = Long.valueOf(jwtUtil.getUserIdFromToken(token));

            List<FilteredPostPreviewResponseDto> posts = postService.getFilteredPosts(
                    type, category, radius, keyword, userId
            );

            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            log.error("Error fetching filtered posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
