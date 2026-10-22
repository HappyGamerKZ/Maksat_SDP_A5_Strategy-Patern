package org.aitu.se.ingredientsubstitution;

public class SubstitutionRequirements {
    private final boolean dairyFreeRequired;

    public SubstitutionRequirements(boolean dairyFreeRequired) {
        this.dairyFreeRequired = dairyFreeRequired;
    }

    public boolean isDairyFreeRequired() { return dairyFreeRequired; }
}