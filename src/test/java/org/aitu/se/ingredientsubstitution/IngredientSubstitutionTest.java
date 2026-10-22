package org.aitu.se.ingredientsubstitution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class IngredientSubstitutionTest {

    // Фейковая реализация для тестов (Изоляция от изменений - Требование 5)
    static class FakeIngredientDataProvider implements IngredientDataProvider {
        @Override
        public List<Ingredient> getAvailableIngredients() {
            return Arrays.asList(
                    new Ingredient("Cheddar", 900.0, 8, 25),
                    new Ingredient("Plant Sauce", 700.0, 3, 2),
                    new Ingredient("Tofu", 800.0, 5, 23)
            );
        }
    }

    private IngredientSubstitutionContext context;
    private SubstitutionSelector selector;
    private Ingredient missingMozzarella;

    @BeforeEach
    void setUp() {
        IngredientDataProvider fakeProvider = new FakeIngredientDataProvider();
        selector = new SubstitutionSelector(fakeProvider);
        context = new IngredientSubstitutionContext();

        // Оригинальный продукт: Цена 1000, Вкус 8, Белок 24
        missingMozzarella = new Ingredient("Mozzarella", 1000.0, 8, 24);
    }

    @Test
    void testBudgetFriendlyStrategy() {
        context.setStrategy(selector.selectStrategy(OrderPriority.CHEAPEST_COST));
        Optional<Ingredient> result = context.execute(missingMozzarella);

        // Самый дешевый - это Plant Sauce (700.0)
        assertTrue(result.isPresent());
        assertEquals("Plant Sauce", result.get().getName());
    }

    @Test
    void testNutritionalStrategy() {
        context.setStrategy(selector.selectStrategy(OrderPriority.MAINTAIN_NUTRITION));
        Optional<Ingredient> result = context.execute(missingMozzarella);

        // Моцарелла (Белок 24). Ближайший по белку - Cheddar (25) или Tofu (23).
        // Оба отклоняются на 1, вернется первый встретившийся (Cheddar).
        assertTrue(result.isPresent());
        assertEquals("Cheddar", result.get().getName());
    }

    @Test
    void testFlavorMatchStrategy() {
        context.setStrategy(selector.selectStrategy(OrderPriority.MAX_FLAVOR_MATCH));
        Optional<Ingredient> result = context.execute(missingMozzarella);

        // Моцарелла (Вкус 8). Самый близкий - Cheddar (Вкус 8).
        assertTrue(result.isPresent());
        assertEquals("Cheddar", result.get().getName());
    }

    // --- ТРЕБОВАНИЕ №6: Параметризованный тест для семейства ---

    static Stream<OrderPriority> providePriorities() {
        return Stream.of(OrderPriority.CHEAPEST_COST, OrderPriority.MAX_FLAVOR_MATCH, OrderPriority.MAINTAIN_NUTRITION);
    }

    @ParameterizedTest
    @MethodSource("providePriorities")
    void testAllStrategiesReturnASubstitute(OrderPriority priority) {
        context.setStrategy(selector.selectStrategy(priority));
        Optional<Ingredient> result = context.execute(missingMozzarella);

        // Независимо от выбранной стратегии, система должна всегда находить хотя бы 1 замену,
        // если база данных поставщика не пустая.
        assertTrue(result.isPresent(), "Strategy should not return empty result for valid missing ingredient");
    }
}