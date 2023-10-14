package io.github.enderor.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ItemEnchantedPaper extends Item {
  public ItemEnchantedPaper() {
    this.setMaxStackSize(1);
    this.setMaxDamage(0);
    EnderORItemHandler.addModel(this, 0, "inventory");
  }
  
  public static final ItemEnchantedPaper INSTANCE = new ItemEnchantedPaper();
  
  private final HashMap<Enchantment, Integer> enchantmentList = new HashMap<>();
  
  @Override
  public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
    super.getSubItems(tab, items);
    ItemStack stack = this.getDefaultInstance();
    if (Objects.equals(this.getCreativeTab(), tab)) {
      Enchantment.REGISTRY.forEach(enchantment -> {
        for (int i = enchantment.getMinLevel(), iMax = enchantment.getMaxLevel(); i < iMax; ++i) {
          EnchantHelper.addEnchant(stack, enchantment, i);
          items.add(stack);
          EnchantHelper.resetEnchant(stack);
        }
      });
    }
    if (tab.equals(CreativeTabs.SEARCH)) {
      Enchantment.REGISTRY.forEach(enchantment -> {
        EnchantHelper.addEnchant(stack, enchantment, enchantment.getMaxLevel());
        items.add(stack);
        EnchantHelper.resetEnchant(stack);
      });
    }
  }
  
  @Override
  public @NotNull ItemStack getDefaultInstance() {
    ItemStack result = new ItemStack(INSTANCE, 1, 0);
    EnchantHelper.resetEnchant(result);
    return result;
  }
  
  public ItemEnchantedPaper getFromStack(@NotNull ItemStack stack) {
    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
    enchantmentList.clear();
    this.enchantmentList.putAll(enchantments);
    return this;
  }
  
  public ItemEnchantedPaper addToStack(@NotNull ItemStack stack) {
    Map<Enchantment, Integer> rawEnchantments = EnchantmentHelper.getEnchantments(stack);
    this.enchantmentList.forEach((enchantment, integer) -> {
      if (!rawEnchantments.containsKey(enchantment)) {
        rawEnchantments.put(enchantment, integer);
      } else if (rawEnchantments.get(enchantment) < integer) {
        rawEnchantments.replace(enchantment, integer);
      }
    });
    return this;
  }
  
  public Set<Enchantment> getEnchantments() { return enchantmentList.keySet(); }
  
  public boolean hasEnchantment(Enchantment enchantment) { return enchantmentList.containsKey(enchantment); }
  
  public int getEnchantmentLevel(Enchantment enchantment) { return hasEnchantment(enchantment) ? 0 : enchantmentList.get(enchantment); }
  
  public ItemEnchantedPaper setEnchantmentLevel(Enchantment enchantment, int level) {
    if (!hasEnchantment(enchantment)) {
      enchantmentList.put(enchantment, level);
    } else {
      enchantmentList.replace(enchantment, level);
    }
    return this;
  }
  
  public ItemEnchantedPaper removeEnchantmentLevel(Enchantment enchantment) {
    if (hasEnchantment(enchantment)) {
      enchantmentList.remove(enchantment);
    }
    return this;
  }
  
  public static class EnchantHelper {
    public static final String ENCHANT_TAG = "enchants";
    public static final String ENCHANT_ID = "id";
    public static final String ENCHANT_LEVEL = "lvl";
    
    public static void resetEnchant(@NotNull ItemStack itemStack) {
      if (!itemStack.hasTagCompound()) {
        itemStack.setTagCompound(new NBTTagCompound());
      }
      assert itemStack.getTagCompound() != null;
      if (itemStack.getTagCompound().hasKey(ENCHANT_TAG)) {
        itemStack.getTagCompound().removeTag(ENCHANT_TAG);
      }
      itemStack.getTagCompound().setTag(ENCHANT_TAG, new NBTTagList());
    }
    
    public static @NotNull Map<Enchantment, Integer> getEnchants(@NotNull ItemStack itemStack) {
      Map<Enchantment, Integer> enchants = new HashMap<>();
      if (!(itemStack.getItem() instanceof ItemEnchantedPaper)) {
        return enchants;
      }
      if (!itemStack.hasTagCompound()) {
        return enchants;
      }
      assert itemStack.getTagCompound() != null;
      if (!itemStack.getTagCompound().hasKey(ENCHANT_TAG, 9) || itemStack.getTagCompound().getTagList(ENCHANT_TAG, 10).tagCount() == 0) {
        resetEnchant(itemStack);
        return enchants;
      }
      NBTTagList tagList = itemStack.getTagCompound().getTagList(ENCHANT_TAG, 10);
      for (int i = 0, iMax = tagList.tagCount(); i < iMax; ++i) {
        NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
        if (!tagCompound.hasKey(ENCHANT_ID, 8) || !tagCompound.hasKey(ENCHANT_LEVEL, 3)) {
          continue;
        }
        Enchantment enchantment = Enchantment.getEnchantmentByLocation(tagCompound.getString(ENCHANT_ID));
        if (enchantment == null || tagCompound.getInteger(ENCHANT_LEVEL) == 0) {
          continue;
        }
        enchants.put(enchantment, tagCompound.getInteger(ENCHANT_LEVEL));
      }
      return enchants;
    }
    
    public static void setEnchants(@NotNull ItemStack itemStack, @NotNull Map<Enchantment, Integer> enchants) {
      resetEnchant(itemStack);
      assert itemStack.getTagCompound() != null;
      NBTTagList tagList = itemStack.getTagCompound().getTagList(ENCHANT_TAG, 9);
      enchants.forEach((enchantment, integer) -> {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString(ENCHANT_ID, Objects.requireNonNull(enchantment.getRegistryName()).toString());
        tagCompound.setInteger(ENCHANT_LEVEL, integer);
        tagList.appendTag(tagCompound);
      });
    }
    
    public static void addEnchants(@NotNull ItemStack itemStack, @NotNull Map<Enchantment, Integer> enchants) {
      Map<Enchantment, Integer> rawEnchants = getEnchants(itemStack);
      Map<Enchantment, Integer> add = new HashMap<>();
      enchants.forEach((enchantment, integer) -> {
        if (!rawEnchants.containsKey(enchantment)) {
          add.put(enchantment, integer);
        } else if (rawEnchants.get(enchantment) < integer) {
          rawEnchants.replace(enchantment, integer);
        }
      });
      rawEnchants.putAll(add);
      setEnchants(itemStack, rawEnchants);
    }
    
    public static void addEnchant(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment, int level) {
      Map<Enchantment, Integer> rawEnchants = getEnchants(itemStack);
      if (!rawEnchants.containsKey(enchantment)) {
        rawEnchants.put(enchantment, level);
      } else if (rawEnchants.get(enchantment) < level) {
        rawEnchants.replace(enchantment, level);
      }
      setEnchants(itemStack, rawEnchants);
    }
    
    public static boolean hasEnchants(@NotNull ItemStack itemStack) {
      if (!(itemStack.getItem() instanceof ItemEnchantedPaper) || !itemStack.hasTagCompound()) {
        return false;
      }
      
      assert itemStack.getTagCompound() != null;
      return itemStack.getTagCompound().hasKey(ENCHANT_LEVEL, 9) && !itemStack.getTagCompound().getTagList(ENCHANT_LEVEL, 10).hasNoTags();
    }
  }
}
