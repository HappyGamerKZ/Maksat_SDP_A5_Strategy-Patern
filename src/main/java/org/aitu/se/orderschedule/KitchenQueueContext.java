package org.aitu.se.orderschedule;

import java.util.List;

public class KitchenQueueContext {
    private OrderSchedulingStrategy strategy;

    public KitchenQueueContext(OrderSchedulingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(OrderSchedulingStrategy strategy) {
        this.strategy = strategy;
    }

    public List<DishOrder> execute(List<DishOrder> incomingOrders){
        if (strategy == null) {
            throw new IllegalStateException("Strategy is not set");
        }
        return strategy.schedule(incomingOrders);
    }
}
