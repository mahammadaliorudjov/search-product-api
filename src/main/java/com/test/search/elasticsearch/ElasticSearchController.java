package com.test.search.elasticsearch;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ElasticSearchController {
    private final ElasticSearchService elasticSearchService;

    @PostMapping("/copyData")
    public ResponseEntity<String> copyData() {
        try {
            elasticSearchService.copyDataToElasticSearch();
            return ResponseEntity.ok("Data copied successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> searchProducts(
            @RequestParam("query") String query,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            List<Map<String, Object>> results = elasticSearchService.searchProducts(query, active, startDate);
            return ResponseEntity.ok(results);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

