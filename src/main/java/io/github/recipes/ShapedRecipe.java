package io.github.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ShapedRecipe extends EnderORRecipe {
  public ShapedRecipe(String registerName, int width, int height, boolean matchNBT) {
    super(registerName, width, height, matchNBT);
  }
}
