package org.aitu.se.orderschedule;
import java.time.LocalDateTime;

public class DishOrder {
    private String dishName;
    private int tableNumber;
    private LocalDateTime orderTime;
    private int preparationTimeMinutes;

    public DishOrder(String dishName, int tableNumber, LocalDateTime orderTime, int preparationTimeMinutes) {
        this.dishName = dishName;
        this.tableNumber = tableNumber;
        this.orderTime = orderTime;
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public String getDishName() { return dishName; }
    public int getTableNumber() { return tableNumber; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public int getPreparationTimeMinutes() { return preparationTimeMinutes; }

    @Override
    public String toString() {
        return "Table " + tableNumber + " - " + dishName;
    }
}