package com.happy.biling.dto.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostWriteRequestDto {
    private String type; //share borrow
    private String title;
    private Integer price;
    private List<MultipartFile> images;
    private String content;
    private String distance;
    private String category;
    private String locationName;
    private Double locationLatitude;
    private Double locationLongitude;
}
