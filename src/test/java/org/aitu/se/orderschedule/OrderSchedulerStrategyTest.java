package org.aitu.se.orderschedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderSchedulerStrategyTest {

    private KitchenQueueContext context;
    private QueueSelector selector;
    private List<DishOrder> orders;

    @BeforeEach
    void setUp() {
        selector = new QueueSelector();
        context = new KitchenQueueContext(new FifoSchedulingStrategy()); // дефолтная стратегия

        LocalDateTime now = LocalDateTime.now();
        orders = Arrays.asList(
                new DishOrder("Steak", 1, now.minusMinutes(10), 20),
                new DishOrder("Salad", 2, now.minusMinutes(5), 5),
                new DishOrder("Steak", 2, now.minusMinutes(4), 20),
                new DishOrder("Soup", 1, now.minusMinutes(8), 10)
        );
    }

    @Test
    void testFifoScheduling() {
        context.setStrategy(selector.selectStrategyAutomatically(KitchenLoadSituation.NORMAL_DAY));
        List<DishOrder> result = context.execute(orders);

        // Проверки не изменились
        assertEquals("Steak", result.get(0).getDishName());
        assertEquals("Soup", result.get(1).getDishName());
        assertEquals("Salad", result.get(2).getDishName());
        assertEquals("Steak", result.get(3).getDishName());
    }

    @Test
    void testTableSyncScheduling() {
        context.setStrategy(selector.selectStrategyAutomatically(KitchenLoadSituation.BANQUET_SERVICE));
        List<DishOrder> result = context.execute(orders);

        // Проверки не изменились
        long table1Count = result.stream().limit(2).filter(o -> o.getTableNumber() == 1).count();
        assertEquals(2, table1Count, "First two orders should belong to the same table");
        assertEquals("Steak", result.get(0).getDishName());
        assertEquals("Soup", result.get(1).getDishName());
    }

    @Test
    void testBatchedScheduling() {
        context.setStrategy(selector.selectStrategyAutomatically(KitchenLoadSituation.RUSH_HOUR));
        List<DishOrder> result = context.execute(orders);

        // Проверки не изменились
        long steakCount = 0;
        for (int i = 0; i < result.size() - 1; i++) {
            if (result.get(i).getDishName().equals(result.get(i + 1).getDishName())) {
                steakCount++;
            }
        }
        assertEquals(1, steakCount, "Identical dishes should be grouped together");
    }

    // --- ТРЕБОВАНИЕ №6: Параметризованный тест для всех стратегий семейства ---

    static Stream<OrderSchedulingStrategy> provideStrategies() {
        return Stream.of(
                new FifoSchedulingStrategy(),
                new TableSyncSchedulingStrategy(),
                new BatchPrepSchedulingStrategy()
        );
    }

    @ParameterizedTest
    @MethodSource("provideStrategies")
    void testAllStrategiesReturnSameAmountOfOrders(OrderSchedulingStrategy strategy) {
        context.setStrategy(strategy);
        List<DishOrder> result = context.execute(orders);

        // Независимо от того, какая стратегия используется,
        // ни один заказ не должен быть потерян.
        assertNotNull(result);
        assertEquals(4, result.size(), "Strategy should not drop any orders");
    }
}
