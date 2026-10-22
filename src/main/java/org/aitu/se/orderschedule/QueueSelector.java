package org.aitu.se.orderschedule;

public class QueueSelector {
    public OrderSchedulingStrategy selectStrategyAutomatically(KitchenLoadSituation situation) {
        switch (situation) {
            case RUSH_HOUR:
                // В час пик группируем блюда, чтобы повара готовили массово
                return new BatchPrepSchedulingStrategy();
            case BANQUET_SERVICE:
                // На банкете критично отдавать блюда за один стол одновременно
                return new TableSyncSchedulingStrategy();
            case NORMAL_DAY:
            default:
                // В обычное время готовим в порядке живой очереди
                return new FifoSchedulingStrategy();
        }
    }
}
