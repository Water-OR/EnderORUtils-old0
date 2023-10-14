package io.github.enderor.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ShapedRecipe extends EnderORRecipe {
  public ShapedRecipe(String registerName, int width, int height, boolean matchNBT) {
    super(registerName, width, height, matchNBT);
  }
  
  @Override
  public boolean canFit(int width, int height) {
    return this.width <= width && this.height <= height;
  }
  
  @Override
  public boolean matches(@NotNull InventoryCrafting inv, World worldIn, Comparator<NBTTagCompound> comparator) {
    for (int i = 0; i <= inv.getWidth() - this.width; ++i)
      for (int j = 0; j <= inv.getHeight() - this.height; ++j)
        if (matchesWithOffset(i, j, inv, worldIn, comparator)) {
          return true;
        }
    return false;
  }
  
  public boolean matchesWithOffset(int offsetWidth, int offsetHeight, @NotNull InventoryCrafting inv, World worldIn, Comparator<NBTTagCompound> comparator) {
    for (int i = 0; i < inv.getWidth(); ++i) {
      int i1 = i - offsetWidth;
      
      for (int j = 0; j < inv.getHeight(); ++j) {
        int j1 = j - offsetHeight;
        Ingredient ingredient = Ingredient.EMPTY;
        
        if (0 <= i1 && i1 < this.width && 0 <= j1 && j1 < this.height) {
          ingredient = input.get(i1 + j1 * this.width);
        }
        
        if (!matchesItem(inv.getStackInRowAndColumn(i, j), ingredient, comparator)) {
          return false;
        }
      }
    }
    return true;
  }
}
