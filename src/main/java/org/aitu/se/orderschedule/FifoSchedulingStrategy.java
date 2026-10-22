package org.aitu.se.orderschedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FifoSchedulingStrategy implements OrderSchedulingStrategy {
    @Override
    public List<DishOrder> schedule(List<DishOrder> incomingOrders) {
        if (incomingOrders == null || incomingOrders.isEmpty()) return new ArrayList<>();

        List<DishOrder> result = new ArrayList<>(incomingOrders);
        result.sort(Comparator.comparing(DishOrder::getOrderTime));
        return result;
    }
}