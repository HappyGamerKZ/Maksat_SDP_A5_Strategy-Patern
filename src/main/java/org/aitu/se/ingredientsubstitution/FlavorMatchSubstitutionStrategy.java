package org.aitu.se.ingredientsubstitution;

import java.util.Comparator;
import java.util.Optional;

public class FlavorMatchSubstitutionStrategy implements IngredientSubstitutionStrategy {
    private IngredientDataProvider dataProvider;

    public FlavorMatchSubstitutionStrategy(IngredientDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public Optional<Ingredient> findBestSubstitute(Ingredient missingIngredient) {
        return dataProvider.getAvailableIngredients().stream()
                .min(Comparator.comparingInt(i ->
                        Math.abs(i.getFlavorScore() - missingIngredient.getFlavorScore())));
    }
}