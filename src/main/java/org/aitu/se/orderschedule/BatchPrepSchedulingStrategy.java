package org.aitu.se.orderschedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BatchPrepSchedulingStrategy implements OrderSchedulingStrategy {
    @Override
    public List<DishOrder> schedule(List<DishOrder> incomingOrders) {
        if (incomingOrders == null || incomingOrders.isEmpty()) return new ArrayList<>();

        // Используем LinkedHashMap для предсказуемого порядка в тестах
        Map<String, List<DishOrder>> byDishName = incomingOrders.stream()
                .collect(Collectors.groupingBy(
                        DishOrder::getDishName,
                        java.util.LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<DishOrder> result = new ArrayList<>();
        for (List<DishOrder> dishBatch : byDishName.values()) {
            result.addAll(dishBatch);
        }
        return result;
    }
}