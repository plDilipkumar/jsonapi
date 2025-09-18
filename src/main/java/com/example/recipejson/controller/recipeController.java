package com.example.recipejson.controller;

import com.example.recipejson.model.Recipe;
import com.example.recipejson.service.recipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class recipeController {

    private final recipeService recipeService;
    @PostMapping("/import")
    public ResponseEntity<String> importRecipesFromFile() {
        try {
            recipeService.importRecipesFromFile("C:\\Users\\pldil\\Downloads\\recipejson\\recipejson\\src\\main\\resources\\US_recipes_null.json");
            return ResponseEntity.ok("Recipes Imported successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<Page<Recipe>> getAllRecipes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recipeService.getRecipes(page, limit));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(
            @RequestParam(required = false) String calories,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String cuisine,
            @RequestParam(name = "total_time", required = false) String totalTime,
            @RequestParam(required = false) String rating) {
        return ResponseEntity.ok(recipeService.searchRecipes(calories, title, cuisine, totalTime, rating));
    }

}