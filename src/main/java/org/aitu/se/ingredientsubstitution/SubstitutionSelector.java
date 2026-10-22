package org.aitu.se.ingredientsubstitution;

public class SubstitutionSelector {
    private IngredientDataProvider dataProvider;

    public SubstitutionSelector(IngredientDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public IngredientSubstitutionStrategy selectStrategy(OrderPriority priority) {
        switch (priority) {
            case CHEAPEST_COST:
                return new BudgetFriendlySubstitutionStrategy(dataProvider);
            case MAINTAIN_NUTRITION:
                return new NutritionalProfileSubstitutionStrategy(dataProvider);
            case MAX_FLAVOR_MATCH:
            default:
                return new FlavorMatchSubstitutionStrategy(dataProvider);
        }
    }
}
