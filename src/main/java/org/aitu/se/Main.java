package org.aitu.se;

import org.aitu.se.ingredientsubstitution.*;
import org.aitu.se.orderschedule.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {
    static void main() {
        //MonolithicShceduleDemo();

        OrderSchedulerDemo();

        IngredientSubstitutionDemo();
    }

    // cool output tool
    private static void printOrders(List<DishOrder> orders) {
        if (orders.isEmpty()) {
            System.out.println("No orders to schedule.");
            return;
        }
        for (DishOrder order : orders) {
            System.out.printf("Table %d | Dish: %-6s | Prep Time: %d min%n",
                    order.getTableNumber(), order.getDishName(), order.getPreparationTimeMinutes());
        }
    }

    private static void MonolithicShceduleDemo(){
        System.out.println("=== Restaurant Kitchen: Monolithic Scheduler Demo (Commit 1) ===");

        // 1. Создаем общие тестовые данные (входящие заказы)
        LocalDateTime now = LocalDateTime.now();
        List<DishOrder> incomingOrders = Arrays.asList(
                new DishOrder("Steak", 1, now.minusMinutes(10), 20),
                new DishOrder("Salad", 2, now.minusMinutes(5), 5),
                new DishOrder("Steak", 2, now.minusMinutes(4), 20),
                new DishOrder("Soup", 1, now.minusMinutes(8), 10)
        );

        MonolithicOrderScheduler scheduler = new MonolithicOrderScheduler();

        // 2. Демонстрация FIFO (Первым пришел - первым ушел)
        System.out.println("\n--- 1. Type: FIFO (Strict chronologic order) ---");
        List<DishOrder> fifoResult = scheduler.schedule(incomingOrders, SchedulingType.FIFO);
        printOrders(fifoResult);

        // 3. Демонстрация TABLE_SYNC (Группировка по столам)
        System.out.println("\n--- 2. Type: TABLE_SYNC (Group by table, longest prep first) ---");
        List<DishOrder> syncResult = scheduler.schedule(incomingOrders, SchedulingType.TABLE_SYNC);
        printOrders(syncResult);

        // 4. Демонстрация BATCHED (Группировка одинаковых блюд)
        System.out.println("\n--- 3. Type: BATCHED (Group identical dishes for kitchen efficiency) ---");
        List<DishOrder> batchedResult = scheduler.schedule(incomingOrders, SchedulingType.BATCHED);
        printOrders(batchedResult);
    }

    private static void OrderSchedulerDemo(){
        System.out.println("=== Restaurant Kitchen: Strategy Pattern Demo (Commit 2) ===\n");

        LocalDateTime now = LocalDateTime.now();
        List<DishOrder> incomingOrders = Arrays.asList(
                new DishOrder("Steak", 1, now.minusMinutes(10), 20),
                new DishOrder("Salad", 2, now.minusMinutes(5), 5),
                new DishOrder("Steak", 2, now.minusMinutes(4), 20),
                new DishOrder("Soup", 1, now.minusMinutes(8), 10)
        );

        QueueSelector selector = new QueueSelector();

        // 1. Автоматический выбор через Селектор (Normal Day -> FIFO)
        System.out.println("--- 1. Automatic Selection: NORMAL_DAY ---");
        OrderSchedulingStrategy strategy = selector.selectStrategyAutomatically(KitchenLoadSituation.NORMAL_DAY);
        KitchenQueueContext context = new KitchenQueueContext(strategy);
        printOrders(context.execute(incomingOrders));

        // 2. Смена стратегии в рантайме (Runtime switching)
        System.out.println("\n--- 2. Runtime Switch: RUSH_HOUR (Selector chose Batched) ---");
        strategy = selector.selectStrategyAutomatically(KitchenLoadSituation.RUSH_HOUR);
        context.setStrategy(strategy);
        printOrders(context.execute(incomingOrders));

        // 3. Ручной выбор стратегии клиентом (Manual choice)
        System.out.println("\n--- 3. Manual Override: Table Synchronization ---");
        context.setStrategy(new TableSyncSchedulingStrategy());
        printOrders(context.execute(incomingOrders));
    }

    private static void IngredientSubstitutionDemo() {
        System.out.println("=== Restaurant Kitchen: Strategy Pattern Demo ===");
        System.out.println("=== Family 2: Ingredient Substitution ===\n");

        // 1. Инициализация источника данных (Изоляция от изменений - Требование 5)
        IngredientDataProvider dataProvider = () -> Arrays.asList(
                new Ingredient("Cheddar", 900.0, 8, 25),
                new Ingredient("Plant Sauce", 700.0, 3, 2),
                new Ingredient("Tofu", 800.0, 5, 23)
        );

        // 2. Инициализация Селектора и Контекста
        SubstitutionSelector selector = new SubstitutionSelector(dataProvider);
        IngredientSubstitutionContext context = new IngredientSubstitutionContext();

        // 3. Исходная ситуация: на кухне закончилась Моцарелла
        Ingredient missingMozzarella = new Ingredient("Mozzarella", 1000.0, 8, 24);
        System.out.println("Missing Ingredient: " + missingMozzarella.getName() +
                " | Price: " + missingMozzarella.getPrice() +
                " | Flavor: " + missingMozzarella.getFlavorScore() +
                " | Protein: " + missingMozzarella.getProteinGrams());

        // 4. Демонстрация стратегии №1: Экономия бюджета
        System.out.println("\n--- Priority: CHEAPEST_COST ---");
        context.setStrategy(selector.selectStrategy(OrderPriority.CHEAPEST_COST));
        Optional<Ingredient> cheapSub = context.execute(missingMozzarella);
        cheapSub.ifPresent(i -> System.out.println("Selected Substitute: " + i.getName() +
                " (Price: " + i.getPrice() + " ₸) - Выбран самый дешевый вариант"));

        // 5. Демонстрация стратегии №2: Сохранение вкуса
        System.out.println("\n--- Priority: MAX_FLAVOR_MATCH ---");
        context.setStrategy(selector.selectStrategy(OrderPriority.MAX_FLAVOR_MATCH));
        Optional<Ingredient> flavorSub = context.execute(missingMozzarella);
        flavorSub.ifPresent(i -> System.out.println("Selected Substitute: " + i.getName() +
                " (Flavor: " + i.getFlavorScore() + ") - Выбрано минимальное отклонение по вкусу"));

        // 6. Демонстрация стратегии №3: Сохранение пищевой ценности
        System.out.println("\n--- Priority: MAINTAIN_NUTRITION ---");
        context.setStrategy(selector.selectStrategy(OrderPriority.MAINTAIN_NUTRITION));
        Optional<Ingredient> nutritionSub = context.execute(missingMozzarella);
        nutritionSub.ifPresent(i -> System.out.println("Selected Substitute: " + i.getName() +
                " (Protein: " + i.getProteinGrams() + "g) - Выбрано минимальное отклонение по белку"));
    }
}
