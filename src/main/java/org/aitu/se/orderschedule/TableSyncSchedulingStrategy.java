package org.aitu.se.orderschedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableSyncSchedulingStrategy implements OrderSchedulingStrategy {
    @Override
    public List<DishOrder> schedule(List<DishOrder> incomingOrders) {
        if (incomingOrders == null || incomingOrders.isEmpty()) return new ArrayList<>();

        Map<Integer, List<DishOrder>> byTable = incomingOrders.stream()
                .collect(Collectors.groupingBy(DishOrder::getTableNumber));

        List<DishOrder> result = new ArrayList<>();
        for (List<DishOrder> tableOrders : byTable.values()) {
            // Сортируем внутри стола: сначала то, что готовится дольше всего
            tableOrders.sort((o1, o2) -> Integer.compare(
                    o2.getPreparationTimeMinutes(),
                    o1.getPreparationTimeMinutes()
            ));
            result.addAll(tableOrders);
        }
        return result;
    }
}