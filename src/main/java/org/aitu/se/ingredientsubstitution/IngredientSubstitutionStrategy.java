package org.aitu.se.ingredientsubstitution;

import java.util.Optional;

public interface IngredientSubstitutionStrategy {
    Optional<Ingredient> findBestSubstitute(Ingredient missingIngredient);
}
