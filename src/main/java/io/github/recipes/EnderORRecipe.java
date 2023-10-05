package io.github.recipes;

import io.github.enderor.EnderORUtils;
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

import java.util.*;

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
  
  public EnderORRecipe setInput(int index, List<ItemStack> stackList) {
    if (0 <= index && index < this.width * this.height) {
      input.set(index, getIngredientFromStacks(stackList));
    }
    return this;
  }
  
  public EnderORRecipe setInput(int index, ItemStack... stackList) {
    if (0 <= index && index < this.width * this.height) {
      input.set(index, getIngredientFromStacks(stackList));
    }
    return this;
  }
  
  public static @NotNull Ingredient getIngredientFromStacks(@NotNull List<ItemStack> stacks) {
    return Ingredient.fromStacks(stacks.toArray(new ItemStack[0]));
  }
  
  public static @NotNull Ingredient getIngredientFromStacks(ItemStack... stacks) {
    return Ingredient.fromStacks(stacks);
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
  
  public EnderORRecipe setOutput(ItemStack output) {
    this.output = output;
    return this;
  }
  
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
  
  @Override
  public boolean canFit(int width, int height) {
    return this.width <= width && this.height <= height;
  }
  
  public static final Comparator<NBTTagCompound> DEFAULT_COMPARATOR = (x, y) -> {
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
  
  @Override
  public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
    return matches(inv, worldIn, DEFAULT_COMPARATOR);
  }
  
  
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
  
  public boolean matchesItem(ItemStack stack, @NotNull Ingredient ingredient) {
    return matchesItem(stack, ingredient, DEFAULT_COMPARATOR);
  }
  
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
  
  public @NotNull List<ItemStack> getMatchItems(ItemStack stack, @NotNull Ingredient ingredient) {
    return getMatchItems(stack, ingredient, DEFAULT_COMPARATOR);
  }
  
  public @NotNull List<ItemStack> getMatchItems(ItemStack stack, @NotNull Ingredient ingredient, Comparator<NBTTagCompound> comparator) {
    List<ItemStack> matchItems = new ArrayList<>();
    if (!ingredient.apply(stack)) {
      return matchItems;
    }
    if (!matchNBT && ingredient.equals(Ingredient.EMPTY)) {
      matchItems.addAll(Arrays.asList(Ingredient.EMPTY.getMatchingStacks()));
      return matchItems;
    }
    ItemStack[] stacks = ingredient.getMatchingStacks();
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
      matchItems.add(stack1);
    }
    return matchItems;
  }
  
  public Ingredient getIngredientInRowAndColumn(int row, int column) {
    return 0 > row || row >= this.width || 0 > column || column >= height ? input.get(row + column * width) : Ingredient.EMPTY;
  }
}
