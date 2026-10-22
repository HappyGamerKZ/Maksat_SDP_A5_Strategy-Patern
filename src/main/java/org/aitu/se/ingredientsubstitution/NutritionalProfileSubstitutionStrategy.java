package org.aitu.se.ingredientsubstitution;

import java.util.Comparator;
import java.util.Optional;

public class NutritionalProfileSubstitutionStrategy implements IngredientSubstitutionStrategy {
    private IngredientDataProvider dataProvider;

    public NutritionalProfileSubstitutionStrategy(IngredientDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public Optional<Ingredient> findBestSubstitute(Ingredient missingIngredient) {
        return dataProvider.getAvailableIngredients().stream()
                .min(Comparator.comparingInt(i ->
                        Math.abs(i.getProteinGrams() - missingIngredient.getProteinGrams())));
    }
}