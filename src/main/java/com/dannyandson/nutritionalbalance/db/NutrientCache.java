package com.dannyandson.nutritionalbalance.db;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;

import java.util.HashMap;
import java.util.List;

public class NutrientCache {
    public static HashMap<String, List<Nutrient>> nutrientCache = new HashMap<>();

    public static void loadNutrients() {
        DbConnection.init();
        nutrientCache = DbConnection.getAllNutrients();
    }

    public static void writeNutrients(String foodName, List<Nutrient> nutrients) {
        DbConnection.writeNutrients(foodName, nutrients);
        nutrientCache.put(foodName, nutrients);
    }

    public static List<Nutrient> getNutrients(String foodName) {
        return nutrientCache.get(foodName);
    }
}