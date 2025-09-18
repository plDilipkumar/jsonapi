package com.example.recipejson.repository;
import com.example.recipejson.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;


public interface recipeRepository extends JpaRepository<Recipe, Long> {
   
                                   
}