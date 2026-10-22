import org.aitu.se.MonolithicOrderScheduler;
import org.aitu.se.orderschedule.DishOrder;
import org.aitu.se.orderschedule.SchedulingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonolithicOrderSchedulerTest {

    private MonolithicOrderScheduler scheduler;
    private List<DishOrder> orders;

    @BeforeEach
    void setUp() {
        scheduler = new MonolithicOrderScheduler();
        LocalDateTime now = LocalDateTime.now();

        // create entry orders
        orders = Arrays.asList(
                new DishOrder("Steak", 1, now.minusMinutes(10), 20),
                new DishOrder("Salad", 2, now.minusMinutes(5), 5),
                new DishOrder("Steak", 2, now.minusMinutes(4), 20),
                new DishOrder("Soup", 1, now.minusMinutes(8), 10)
        );
    }

    @Test
    void testFifoScheduling() {
        List<DishOrder> result = scheduler.schedule(orders, SchedulingType.FIFO);

        // awaiting FIFO (Strict chronologic order)
        assertEquals("Steak", result.get(0).getDishName()); // minusMinutes(10)
        assertEquals("Soup", result.get(1).getDishName());  // minusMinutes(8)
        assertEquals("Salad", result.get(2).getDishName()); // minusMinutes(5)
        assertEquals("Steak", result.get(3).getDishName()); // minusMinutes(4)
    }

    @Test
    void testTableSyncScheduling() {
        List<DishOrder> result = scheduler.schedule(orders, SchedulingType.TABLE_SYNC);

        // awaiting TABLE_SYNC (Group by table, longest prep first)
        long table1Count = result.stream().limit(2).filter(o -> o.getTableNumber() == 1).count();
        assertEquals(2, table1Count, "First two orders should belong to the same table");

        // stake should be first inside table 1, as it takes longer to prepare
        assertEquals("Steak", result.get(0).getDishName());
        assertEquals("Soup", result.get(1).getDishName());
    }

    @Test
    void testBatchedScheduling() {
        List<DishOrder> result = scheduler.schedule(orders, SchedulingType.BATCHED);

        // awaiting BATCHED (Group identical dishes for kitchen efficiency)
        long steakCount = 0;
        for (int i = 0; i < result.size() - 1; i++) {
            if (result.get(i).getDishName().equals(result.get(i + 1).getDishName())) {
                steakCount++;
            }
        }
        // awaiting two stakes in row
        assertEquals(1, steakCount, "Identical dishes should be grouped together");
    }
}