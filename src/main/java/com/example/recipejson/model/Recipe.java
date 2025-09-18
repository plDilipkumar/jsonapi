package com.example.recipejson.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String continent;
    private String countryState;
    private String cuisine;
    private String title;
    @Column(length = 1000)
    private String url;
    private double rating;
    private int totalTime;
    private int prepTime;
    private int cookTime;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String ingredients;
    @Column(columnDefinition = "TEXT")
    private String instructions;
    private String calories;
    private String carbohydrateContent;
    private String cholesterolContent;
    private String fiberContent;
    private String proteinContent;
    private String saturatedFatContent;
    private String sodiumContent;
    private String sugarContent;
    private String fatContent;
    private String unsaturatedFatContent;
    private String serves;
}