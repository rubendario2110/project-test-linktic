package com.testbackend.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "inventory")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inventory {
    @Id
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;
}
