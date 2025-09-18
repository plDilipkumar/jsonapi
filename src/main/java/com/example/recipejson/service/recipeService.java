package com.example.recipejson.service;

import com.example.recipejson.model.Recipe;
import com.example.recipejson.repository.recipeRepository;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class recipeService {

    private final recipeRepository recipeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final int BATCH_SIZE = 500;

    @Transactional
    public void importRecipesFromFile(String filePath) throws Exception {
        JsonFactory factory = new JsonFactory();
        try (InputStream inputStream = new FileInputStream(Paths.get(filePath).toFile());
            JsonParser parser = factory.createParser(inputStream)) {
            Map<String, Map<String, Object>> root = objectMapper.readValue(parser,new TypeReference<Map<String, Map<String, Object>>>() {});
            List<Recipe> batch = new ArrayList<>();
            for (Map<String, Object> map : root.values()) {
                Map<String, Object> nutrients = (Map<String, Object>) map.get("nutrients");
                Recipe recipe = Recipe.builder()
                        .continent((String) map.get("Contient")).countryState((String) map.get("Country_State"))
                        .cuisine((String) map.get("cuisine")).title((String) map.get("title"))
                        .url((String) map.get("URL")).rating(map.get("rating") != null ? Double.parseDouble(map.get("rating").toString()) : 0.0)
                        .totalTime(map.get("total_time") != null ? ((Number) map.get("total_time")).intValue() : 0)
                        .prepTime(map.get("prep_time") != null ? ((Number) map.get("prep_time")).intValue() : 0)
                        .cookTime(map.get("cook_time") != null ? ((Number) map.get("cook_time")).intValue() : 0)
                        .description(map.get("description") != null ? map.get("description").toString() : "")
                        .ingredients(map.get("ingredients") != null ? String.join(" | ", (List<String>) map.get("ingredients")) : "")
                        .instructions(map.get("instructions") != null ? String.join(" | ", (List<String>) map.get("instructions")) : "")
                        .calories(nutrients != null ? (String) nutrients.get("calories") : "")
                        .carbohydrateContent(nutrients != null ? (String) nutrients.get("carbohydrateContent") : "")
                        .cholesterolContent(nutrients != null ? (String) nutrients.get("cholesterolContent") : "")
                        .fiberContent(nutrients != null ? (String) nutrients.get("fiberContent") : "")
                        .proteinContent(nutrients != null ? (String) nutrients.get("proteinContent") : "")
                        .saturatedFatContent(nutrients != null ? (String) nutrients.get("saturatedFatContent") : "")
                        .sodiumContent(nutrients != null ? (String) nutrients.get("sodiumContent") : "")
                        .sugarContent(nutrients != null ? (String) nutrients.get("sugarContent") : "")
                        .fatContent(nutrients != null ? (String) nutrients.get("fatContent") : "")
                        .unsaturatedFatContent(nutrients != null ? (String) nutrients.get("unsaturatedFatContent") : "")
                        .serves(map.get("serves") != null ? map.get("serves").toString() : "")
                        .build();
                batch.add(recipe);
                if (batch.size() >= BATCH_SIZE) {
                    recipeRepository.saveAll(batch);
                    recipeRepository.flush();
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                recipeRepository.saveAll(batch);
                recipeRepository.flush();
            }
        }
    }
    public Page<Recipe> getRecipes(int page, int limit) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(limit, 1),Sort.by(Sort.Direction.DESC, "rating"));
        return recipeRepository.findAll(pageable);
    }
    public List<Recipe> searchRecipes(String caloriesExpr,String title,String cuisine,String totalTimeExpr,String ratingExpr) {
        List<Recipe> all = recipeRepository.findAll();
        return all.stream().filter(r -> title == null || title.isBlank() || r.getTitle() != null && r.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(r -> cuisine == null || cuisine.isBlank() ||r.getCuisine() != null && r.getCuisine().equalsIgnoreCase(cuisine))
                .filter(r -> ratingExpr == null || ratingExpr.isBlank() ||compareNumeric(r.getRating(), ratingExpr))
                .filter(r -> totalTimeExpr == null || totalTimeExpr.isBlank() ||compareNumeric(r.getTotalTime(), totalTimeExpr))
                .filter(r -> caloriesExpr == null || caloriesExpr.isBlank() ||compareNumeric(extractFirstNumber(r.getCalories()), caloriesExpr)).toList();
    }
    private static boolean compareNumeric(double actual,String expression) {
        String expr = expression.trim();
        String operator;
        if(expr.startsWith(">="))operator=">=";
        else if(expr.startsWith("<="))operator="<=";
        else if(expr.startsWith(">"))operator=">";
        else if(expr.startsWith("<"))operator="<";
        else if(expr.startsWith("="))operator="=";
        else operator="=";
        String numberPart = expr.substring(operator.length()).trim();
        double target = extractFirstNumber(numberPart);
        return switch(operator){
            case ">=" -> actual >= target;
            case "<=" -> actual <= target;
            case ">" -> actual > target;
            case "<" -> actual < target;
            default -> actual == target;
        };
    }
    private static double extractFirstNumber(String value) {
        if (value==null) return 0.0;
        Pattern p=Pattern.compile("-?\\d+(?:\\.\\d+)?");
        Matcher m=p.matcher(value);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group());
            } catch (NumberFormatException ignored) {
            }
        }
        return 0.0;
    }
}