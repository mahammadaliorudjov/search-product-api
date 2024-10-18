package com.test.search.sku;

import com.test.search.product.Product;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String skuCode;
    private Double price;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}

