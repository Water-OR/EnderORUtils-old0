package io.github.recipes;

import io.github.enderor.EnderORUtils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShapelessRecipe extends EnderORRecipe {
  public ShapelessRecipe(String name, int size, boolean matchNBT) {
    super(name, size, 1, matchNBT);
  }
  @Override
  public boolean canFit(int width, int height) {
    return this.width * this.height <= width * height;
  }
  
  @Override
  public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
    return this.matches(inv, worldIn, DEFAULT_COMPARATOR);
  }
  
  @Override
  public boolean matches(@NotNull InventoryCrafting inv, World worldIn, Comparator<NBTTagCompound> comparator) {
    List<ItemStack> rawItems = new ArrayList<>();
    for (int i = 0, iMax = inv.getSizeInventory(); i < iMax; ++i) {
      ItemStack stack = inv.getStackInSlot(i);
      if (!stack.isEmpty()) {
        rawItems.add(stack);
      }
    }
    
    if (rawItems.isEmpty()) {
      return false;
    }
    
    EnderORUtils.log(Level.WARN, "Try to match.");
    
    for (Ingredient ingredient : input) {
      if (ingredient.equals(Ingredient.EMPTY)) {
        continue;
      }
      boolean isMatched = false;
      ItemStack matchStack = null;
      for (ItemStack stack : rawItems) {
        if (matchesItem(stack, ingredient, comparator)) {
          matchStack = stack;
          break;
        }
      }
      if (matchStack == null) {
        return false;
      } else {
        rawItems.remove(matchStack);
      }
    }
    return rawItems.isEmpty();
  }
}
