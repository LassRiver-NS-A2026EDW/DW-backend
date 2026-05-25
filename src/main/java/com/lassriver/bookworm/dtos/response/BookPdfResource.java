package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@Builder
public class BookPdfResource {
    private Resource resource;
    private String filename;
}
