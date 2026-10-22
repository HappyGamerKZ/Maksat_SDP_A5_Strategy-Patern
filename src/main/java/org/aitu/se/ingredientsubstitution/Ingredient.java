package org.aitu.se.ingredientsubstitution;

// entity of ingredient
public class Ingredient {
    private String name;
    private double price;
    private int flavorScore;     // Вкусовой профиль (1-10)
    private int proteinGrams;    // Граммы белка (пищевая ценность)

    public Ingredient(String name, double price, int flavorScore, int proteinGrams) {
        this.name = name;
        this.price = price;
        this.flavorScore = flavorScore;
        this.proteinGrams = proteinGrams;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getFlavorScore() { return flavorScore; }
    public int getProteinGrams() { return proteinGrams; }

    @Override
    public String toString() {
        return name + " (" + price + " ₸)";
    }
}