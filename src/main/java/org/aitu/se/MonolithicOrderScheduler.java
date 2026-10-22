package org.aitu.se;
import org.aitu.se.orderschedule.DishOrder;
import org.aitu.se.orderschedule.SchedulingType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonolithicOrderScheduler {

    public List<DishOrder> schedule(List<DishOrder> incomingOrders, SchedulingType type) {
        if (incomingOrders == null || incomingOrders.isEmpty()) {
            return new ArrayList<>();
        }

        switch (type) {
            case FIFO:
                // logic: first in first out
                List<DishOrder> fifoResult = new ArrayList<>(incomingOrders);
                fifoResult.sort(Comparator.comparing(DishOrder::getOrderTime));
                return fifoResult;

            case TABLE_SYNC:
                // logic: group by table
                // first serve dishes for table 1, then for table 2, etc.
                // within a table, sort by descending preparation time (longest dishes start earlier)
                Map<Integer, List<DishOrder>> byTable = incomingOrders.stream()
                        .collect(Collectors.groupingBy(DishOrder::getTableNumber));

                List<DishOrder> syncResult = new ArrayList<>();
                for (List<DishOrder> tableOrders : byTable.values()) {
                    tableOrders.sort((o1, o2) -> Integer.compare(
                            o2.getPreparationTimeMinutes(),
                            o1.getPreparationTimeMinutes()
                    ));
                    syncResult.addAll(tableOrders);
                }
                return syncResult;

            case BATCHED:
                // logic: group by dish name
                Map<String, List<DishOrder>> byDishName = incomingOrders.stream()
                        .collect(Collectors.groupingBy(DishOrder::getDishName));

                List<DishOrder> batchedResult = new ArrayList<>();
                for (List<DishOrder> dishBatch : byDishName.values()) {
                    batchedResult.addAll(dishBatch);
                }
                return batchedResult;

            default:
                throw new IllegalArgumentException("Unknown scheduling type");
        }
    }
}