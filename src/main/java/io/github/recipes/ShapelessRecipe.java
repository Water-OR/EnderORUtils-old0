package io.github.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShapelessRecipe extends EnderORRecipe {
  public ShapelessRecipe(String name, int size, boolean matchNBT) {
    super(name, size, 1, matchNBT);
  }
  @Override
  public boolean canFit(int width, int height) {
    return this.width * this.height <= width * height;
  }
  
  @Override
  public boolean matches(@NotNull InventoryCrafting inv, World worldIn, Comparator<NBTTagCompound> comparator) {
    List<ItemStack> matchItems = new ArrayList<>();
    Boolean[] isUsed = new Boolean[inv.getSizeInventory()];
    for (Ingredient ingredient : input) {
      boolean isMatched = false;
      for (int i = 0, iMax = inv.getSizeInventory(); i < iMax; ++i) {
        if (isUsed[i]) {
          continue;
        }
        ItemStack stack = ForgeHooks.getContainerItem(inv.getStackInSlot(i));
        if (matchesItem(stack, ingredient, comparator)) {
          matchItems.add(stack);
          isMatched = true;
          isUsed[i] = true;
        }
      }
      if (!isMatched) {
        return false;
      }
    }
    return matchItems.size() == input.size();
  }
}
