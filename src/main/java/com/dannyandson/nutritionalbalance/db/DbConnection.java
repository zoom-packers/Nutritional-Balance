package com.dannyandson.nutritionalbalance.db;
//Generate a Java Class that uses Sqlite to connect to a local file database, situated MODS folder
//It should have a table that stores KV values for each food item
// The schema will be:
// CREATE TABLE food_nutrients (
//     food_name TEXT PRIMARY KEY,
//     nutrient_name TEXT,
//     nutrient_value REAL
// );
// The class should have a method that returns a list of nutrients for a given food item
// The class should have a method that writes a list of nutrients for a given food item
// The class will have a init method that creates the connection to the database

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbConnection {
    private static Connection connection;

    public static void init() {
        // First create a file if it doesn't exist
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:nutritionalbalance.db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS food_nutrients (food_name TEXT PRIMARY KEY, nutrient_name TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Nutrient> getNutrients(String foodName) {
        List<Nutrient> nutrients = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT nutrient_name FROM food_nutrients WHERE food_name = ?");
            statement.setString(1, foodName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                nutrients.add(new Nutrient(resultSet.getString("nutrient_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nutrients;
    }

    public static HashMap<String, List<Nutrient>> getAllNutrients() {
        if (connection == null) {
            init();
        }
        HashMap<String, List<Nutrient>> nutrients = new HashMap<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT food_name, nutrient_name FROM food_nutrients");
            while (resultSet.next()) {
                String foodName = resultSet.getString("food_name");
                Nutrient nutrient = new Nutrient(resultSet.getString("nutrient_name"));
                if (!nutrients.containsKey(foodName)) {
                    nutrients.put(foodName, new ArrayList<>());
                }
                nutrients.get(foodName).add(nutrient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nutrients;
    }

    public static void writeNutrients(String foodName, List<Nutrient> nutrients) {
        if (connection == null) {
            init();
        }
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO food_nutrients (food_name, nutrient_name) VALUES (?, ?)");
            for (Nutrient nutrient : nutrients) {
                statement.setString(1, foodName);
                statement.setString(2, nutrient.name);
                statement.executeUpdate();
            }
            if (nutrients.size() == 0) {
                statement.setString(1, foodName);
                statement.setString(2, "NONE");
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
