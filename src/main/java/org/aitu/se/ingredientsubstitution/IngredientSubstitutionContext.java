package org.aitu.se.ingredientsubstitution;

import java.util.Optional;

public class IngredientSubstitutionContext {
    private IngredientSubstitutionStrategy strategy;

    public void setStrategy(IngredientSubstitutionStrategy strategy) {
        this.strategy = strategy;
    }

    public Optional<Ingredient> execute(Ingredient missingIngredient) {
        if (strategy == null) {
            throw new IllegalStateException("Substitution strategy is not set");
        }
        return strategy.findBestSubstitute(missingIngredient);
    }
}