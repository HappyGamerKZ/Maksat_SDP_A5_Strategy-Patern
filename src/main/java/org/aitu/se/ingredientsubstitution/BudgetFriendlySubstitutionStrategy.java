package org.aitu.se.ingredientsubstitution;

import java.util.Comparator;
import java.util.Optional;

public class BudgetFriendlySubstitutionStrategy implements IngredientSubstitutionStrategy {
    private IngredientDataProvider dataProvider;

    public BudgetFriendlySubstitutionStrategy(IngredientDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public Optional<Ingredient> findBestSubstitute(Ingredient missingIngredient) {
        return dataProvider.getAvailableIngredients().stream().min(Comparator.comparingDouble(Ingredient::getPrice));
    }
}