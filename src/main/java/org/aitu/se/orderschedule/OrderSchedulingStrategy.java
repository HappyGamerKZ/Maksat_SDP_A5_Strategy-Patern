package org.aitu.se.orderschedule;

import java.util.List;

public interface OrderSchedulingStrategy {
    List<DishOrder> schedule(List<DishOrder> incomingOrders);
}
