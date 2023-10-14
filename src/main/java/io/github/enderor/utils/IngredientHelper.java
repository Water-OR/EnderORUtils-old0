package io.github.enderor.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IngredientHelper {
  public static @NotNull Ingredient make(@NotNull List<ItemStack> stacks) {
    return Ingredient.fromStacks(stacks.toArray(new ItemStack[0]));
  }
  
  public static @NotNull Ingredient make(@NotNull ItemStack... stacks) {
    return Ingredient.fromStacks(stacks);
  }
}
