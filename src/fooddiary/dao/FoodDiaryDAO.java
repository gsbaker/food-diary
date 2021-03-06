package fooddiary.dao;

import fooddiary.Food;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodDiaryDAO {

    private Connection connection;

    public FoodDiaryDAO() throws SQLException, ClassNotFoundException {
        final String driverClassName = "com.mysql.cj.jdbc.Driver";
        final String databaseUrl = "jdbc:mysql://localhost:3306/FoodDiary?useSSL=false";
        final String user = "myuser";
        final String password = "password";
        Class.forName(driverClassName);
        connection = DriverManager.getConnection(databaseUrl, user, password);
    }

    public void shutdown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public ArrayList<Food> getAllFoods() throws SQLException {
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM food");
        ){
            return getFoods(resultSet);
        }
    }


    public List<String> getFoodNames() throws SQLException {
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM food");
        ){
            List<String> foodNames = new ArrayList<>();

            while (resultSet.next()) {
                foodNames.add(resultSet.getString("name"));
            }
            return foodNames;
        }
    }

    public int getCalories(String name) throws SQLException {
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM food");
        ){

            while (resultSet.next()) {
                if (resultSet.getString("name").equals(name)) {
                    return resultSet.getInt("calories");
                }
            }
        }
        return 0;
    }

    public void insertFood(Food food) throws SQLException {
        int id = food.getId();
        String name = food.getName();
        int calories = food.getCalories();
        Statement statement = connection.createStatement();
        String values = "(" + id + "," + "'" + name + "'" + "," + calories + ")";
        statement.executeUpdate("INSERT INTO food VALUES " + values);
    }

    public int generateId() throws SQLException {
        ArrayList<Food> currentFoods = getAllFoods();
        if (currentFoods.size() > 0) {
            return currentFoods.get(currentFoods.size() - 1).getId() + 1;
        }
        return 0;
    }

    public ArrayList<Food> search(String searchTerm) throws SQLException {

        // TODO: Go through each char in search term and check how many foods are matched. If the food matchedCount ++

        String query = "SELECT * FROM food WHERE name LIKE '%" + searchTerm + "%'";

        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
        ){
            return getFoods(resultSet);
        }
    }

    private ArrayList<Food> getFoods(ResultSet resultSet) throws SQLException {
        ArrayList<Food> found = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int calories = resultSet.getInt("calories");
            Food food = new Food(id, name, calories);
            found.add(food);
        }
        return found;
    }

}
