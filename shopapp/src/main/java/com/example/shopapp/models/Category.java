package com.example.shopapp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "categories") // This tells Hibernate to name the table as `categories`
@Data // Lombok's annotation to generate getters and setters, toString, and other boilerplate code
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate ID, auto increment
    @Column(name = "id") // Column name in the database
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}
