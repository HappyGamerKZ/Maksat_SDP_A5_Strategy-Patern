package org.aitu.se.ingredientsubstitution;

import java.util.List;

public interface IngredientDataProvider {
    List<Ingredient> getAvailableIngredients();
}
