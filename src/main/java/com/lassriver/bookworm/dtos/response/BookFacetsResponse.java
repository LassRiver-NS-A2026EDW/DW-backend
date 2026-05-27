package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookFacetsResponse {
    private List<String> categories;
    private List<String> languages;
}
