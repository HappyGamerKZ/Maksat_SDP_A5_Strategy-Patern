# Assignment 5 — Strategy Pattern

Domain: restaurant kitchen. Two algorithm families live in this project.

1. **Kitchen queue scheduling** — the way incoming dishes get ordered for cooking.
2. **Ingredient substitution** — the way a missing ingredient gets replaced.

Every algorithm is a separate class behind one interface. The client picks one manually or lets a selector pick it automatically, and can swap it while the program runs.

Run the demo: `Main.main()`. Run the tests: `mvn test`.

## Class responsibilities

### Family 1: Kitchen queue scheduling

| Class | Responsibility |
|---|---|
| `OrderSchedulingStrategy` | Declares one method that returns dish orders in the sequence the kitchen should cook them |
| `FifoSchedulingStrategy` | Places orders in a first-in-first-out line by their arrival time |
| `TableSyncSchedulingStrategy` | Groups orders per table, serving each table together with its longest dish first |
| `BatchPrepSchedulingStrategy` | Clusters identical dishes into single batches for one efficient cooking run |
| `KitchenQueueContext` | Holds one scheduling strategy, permits replacement at runtime, forwards incoming orders to it |
| `QueueSelector` | Chooses a scheduling strategy from the current kitchen load situation |
| `KitchenLoadSituation` | Enum of the three kitchen load states |
| `DishOrder` | Data holder for a single dish order |
| `SchedulingType` | Enum of the scheduling kinds used by the old monolithic code |
| `MonolithicOrderScheduler` | The before-state class holding all three algorithms behind one switch statement |

### Family 2: Ingredient substitution

| Class | Responsibility |
|---|---|
| `IngredientSubstitutionStrategy` | Declares one method that suggests a replacement for a missing ingredient |
| `BudgetFriendlySubstitutionStrategy` | Returns the cheapest ingredient found in the supplier catalogue |
| `FlavorMatchSubstitutionStrategy` | Returns the ingredient whose taste score is closest to the missing item |
| `NutritionalProfileSubstitutionStrategy` | Returns the ingredient whose protein value is closest to the missing item |
| `IngredientSubstitutionContext` | Holds one substitution strategy, permits replacement, delegates every search request to it |
| `SubstitutionSelector` | Chooses a substitution strategy from the customer order priority |
| `OrderPriority` | Enum of the three customer priorities for a substitution request |
| `Ingredient` | Data holder for one ingredient with name, price, taste score, protein value |
| `IngredientDataProvider` | Port that hides the external ingredient source from the strategies |
| `SubstitutionRequirements` | Data holder for the dietary rules of one substitution request |
| `FakeIngredientDataProvider` | Test double that hands back a fixed list of ingredients |

## Why this is Strategy, not State, not Template Method

**Why not State.** State changes an object's behavior when its *own internal state* changes, and the states usually trigger the transitions themselves. Nothing like that happens here. The scheduling strategy never moves the context from `Fifo` to `BatchPrep` on its own — an outside caller (`QueueSelector` or the client) calls `setStrategy`. The strategies hold no lifecycle, no memory of a previous strategy, no reference to each other, and no reference to the context. They are stateless algorithms over the same input. The same list of orders can be pushed through all three strategies in one run, which is exactly what the demo shows. In State the set of states is part of the behavior (an order can only move to the next allowed state); here any strategy can follow any strategy.

**Why not Template Method.** Template Method is inheritance-based: an abstract base class fixes the skeleton of the algorithm, and subclasses override individual steps. The structure is locked at compile time, so you cannot change it for a live object. Our solution is composition-based: `KitchenQueueContext` holds an `OrderSchedulingStrategy` reference and simply calls `schedule()`. There is no shared skeleton to inherit — `Fifo`, `TableSync` and `BatchPrep` implement completely different logic, not different steps of one loop. Adding a fourth algorithm means one new class implementing the interface, with zero edits to the context, the selector or the existing strategies (Open/Closed Principle). Swapping is also a runtime operation, which Template Method cannot do at all.

**Summary.** The context is the stable shell, the strategies are the interchangeable parts, the selector is the only place allowed to make a choice, and the data source sits behind `IngredientDataProvider` so a real supplier API can replace the fake one without touching any strategy.

## Git history

| Step | Commit |
|---|---|
| Before refactoring (monolithic switch) | [a4e0fbc](https://github.com/HappyGamerKZ/Maksat_SDP_A5_Strategy-Patern/commit/a4e0fbc491eea0553f009f9e0581d0db407fcbc2) |
| After refactoring (Strategy pattern) | _pending — to be filled with the refactoring commit hash_ |
