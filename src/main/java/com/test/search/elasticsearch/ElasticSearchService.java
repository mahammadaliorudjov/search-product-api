package com.test.search.elasticsearch;

import com.test.search.product.Product;
import com.test.search.product.ProductService;
import com.test.search.sku.Sku;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {
    private final ProductService productService;
    private final RestHighLevelClient restHighLevelClient;

    public void copyDataToElasticSearch() throws IOException {
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            IndexRequest indexRequest = new IndexRequest("products")
                    .id(product.getId().toString())
                    .source(convertProductToMap(product));
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    private Map<String, Object> convertProductToMap(Product product) {
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("name", product.getName());
        productMap.put("description", product.getDescription());
        productMap.put("active", product.getActive());
        productMap.put("startDate", product.getStartDate());

        List<Map<String, Object>> skuList = new ArrayList<>();
        for (Sku sku : product.getSku()) {
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("skuCode", sku.getSkuCode());
            skuMap.put("price", sku.getPrice());
            skuList.add(skuMap);
        }
        productMap.put("sku", skuList);
        return productMap;
    }

    public List<Map<String, Object>> searchProducts(String query, Boolean active, LocalDate startDate) throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.multiMatchQuery(query, "name", "description", "sku.skuCode"));
        if (active != null) {
            boolQuery.filter(QueryBuilders.termQuery("active", active));
        }
        if (startDate != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("startDate").gte(startDate));
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQuery)
                .size(10);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            results.add(hit.getSourceAsMap());
        }
        return results;
    }
}

