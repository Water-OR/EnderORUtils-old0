package io.github.enderor.recipes;

import io.github.enderor.EnderORUtils;
import io.github.enderor.utils.IngredientHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class EnderORRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
  public ItemStack output;
  public List<Ingredient> input;
  public int width, height;
  public boolean matchNBT;
  
  public EnderORRecipe(String name, int width, int height, boolean matchNBT) {
    this.width = width;
    this.height = height;
    this.output = new ItemStack(Items.AIR);
    this.input = Arrays.asList(new Ingredient[this.width * this.height]);
    Collections.fill(this.input, Ingredient.EMPTY);
    this.matchNBT = matchNBT;
    setRegistryName(new ResourceLocation(EnderORUtils.MOD_ID, name));
  }
  
  public EnderORRecipe setInput(int index, @NotNull List<ItemStack> stackList) {
    return setInput(index, stackList.toArray(new ItemStack[0]));
  }
  
  public EnderORRecipe setInput(int index, @NotNull ItemStack... stacks) {
    if (0 <= index && index < this.width * this.height) {
      input.set(index, IngredientHelper.make(stacks));
    }
    return this;
  }
  
  public EnderORRecipe setInput(@NotNull List<Ingredient> input) {
    if (input.size() > this.width * this.height) {
      return this;
    }
    for (int i = 0, iMax = input.size(); i < iMax; ++i) {
      this.input.set(i, input.get(i));
    }
    for (int i = input.size(), iMax = this.input.size(); i < iMax; ++i) {
      this.input.set(i, Ingredient.EMPTY);
    }
    return this;
  }
  
  public EnderORRecipe setOutput(@NotNull ItemStack output) {
    this.output = output.copy();
    return this;
  }
  
  @Override
  public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
    return this.matches(inv, worldIn, NBT_COMPARATOR);
  }
  
  public abstract boolean matches(@NotNull InventoryCrafting inv, World worldIn, Comparator<NBTTagCompound> comparator);
  
  @Override
  public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
    return output.copy();
  }
  
  @Override
  public @NotNull ItemStack getRecipeOutput() {
    return output.copy();
  }
  
  @Override
  public @NotNull NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(Ingredient.EMPTY, input.toArray(new Ingredient[0]));
  }
  
  @Override
  public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
    NonNullList<ItemStack> remainingItems = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      remainingItems.set(i, inv.getStackInSlot(i));
    }
    return remainingItems;
  }
  
  @Override
  public @NotNull String getGroup() {
    return EnderORUtils.MOD_ID;
  }
  
  public static final Comparator<NBTTagCompound> NBT_COMPARATOR = (x, y) -> {
    if (x.getKeySet().size() > y.getKeySet().size()) {
      return 1;
    }
    NBTTagCompound tagCompound = y.copy();
    tagCompound.merge(x);
    if (tagCompound.equals(y)) {
      return 0;
    } else {
      return -1;
    }
  };
  
  public boolean matchesItem(ItemStack stack, @NotNull Ingredient ingredient, Comparator<NBTTagCompound> comparator) {
    ItemStack[] stacks = ingredient.getMatchingStacks();
    if (!ingredient.apply(stack)) {
      return false;
    }
    if (!matchNBT || ingredient.equals(Ingredient.EMPTY)) {
      return true;
    }
    for (ItemStack stack1 : stacks) {
      if (stack.getItem() != stack1.getItem()) {
        continue;
      }
      int meta = stack1.getMetadata();
      if (meta != 32767 && meta != stack.getMetadata()) {
        continue;
      }
      if (comparator.compare(stack.getTagCompound(), stack1.getTagCompound()) != 0) {
        continue;
      }
      return true;
    }
    return false;
  }
}
