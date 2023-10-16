package io.github.enderor.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemEnchantedPaper extends Item {
  public ItemEnchantedPaper() {
    this.setMaxDamage(0);
    this.setMaxStackSize(1);
    EnderORItemHandler.addModel(this, 0, "inventory");
  }
  
  public static final ItemEnchantedPaper INSTANCE = new ItemEnchantedPaper();
  
  @Override
  public @NotNull ItemStack getDefaultInstance() {
    ItemStack result = new ItemStack(this, 1, 0);
    EnchantHelper.resetEnchant(result);
    return result;
  }
  
  @Override
  public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
    super.getSubItems(tab, items);
    ItemStack stack = this.getDefaultInstance();
    if (Objects.equals(this.getCreativeTab(), tab)) {
      Enchantment.REGISTRY.forEach(enchantment -> {
        for (int i = enchantment.getMinLevel(), iMax = enchantment.getMaxLevel(); i < iMax; ++i) {
          EnchantHelper.resetEnchant(stack);
          EnchantHelper.addEnchant(stack, enchantment, i);
          items.add(stack.copy());
        }
      });
    }
    if (tab.equals(CreativeTabs.SEARCH)) {
      Enchantment.REGISTRY.forEach(enchantment -> {
        EnchantHelper.resetEnchant(stack);
        EnchantHelper.addEnchant(stack, enchantment, enchantment.getMaxLevel());
        items.add(stack.copy());
      });
    }
  }
  
  @Override
  public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (!EnchantHelper.hasEnchants(stack)) {
      tooltip.add(I18n.format(this.getUnlocalizedName() + ".empty.description").trim());
      return;
    }
    Map<Enchantment, Integer> enchants = EnchantHelper.getEnchants(stack);
    enchants.forEach((enchantment, integer) -> tooltip.add("".concat((integer < enchantment.getMaxLevel() ? TextFormatting.GRAY :
                                                                      integer > enchantment.getMaxLevel() ? TextFormatting.GOLD : TextFormatting.BLUE
                                                                     ).toString())
                                                           .concat(I18n.format(enchantment.getName()).trim()).concat(" ")
                                                           .concat(I18n.format("enchantment.level." + integer).trim())
    ));
  }
  
  @Override
  public boolean hasEffect(@NotNull ItemStack stack) {
    return super.hasEffect(stack) || EnchantHelper.hasEnchants(stack);
  }
  
  public static class EnchantHelper {
    public static final String ENCHANT_TAG = "enchants";
    public static final String ENCHANT_TAG_ID = "id";
    public static final String ENCHANT_TAG_LEVEL = "lvl";
    
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
    
    @Contract ("_ -> new")
    public static @NotNull Map<Enchantment, Integer> getEnchants(@NotNull ItemStack itemStack) {
      Map<Enchantment, Integer> enchants = new HashMap<>();
      if (!(itemStack.getItem() instanceof ItemEnchantedPaper)) {
        return enchants;
      }
      if (!itemStack.hasTagCompound()) {
        return enchants;
      }
      assert itemStack.getTagCompound() != null;
      if (!itemStack.getTagCompound().hasKey(ENCHANT_TAG, 9) || itemStack.getTagCompound().getTagList(ENCHANT_TAG, 10).hasNoTags()) {
        resetEnchant(itemStack);
        return enchants;
      }
      NBTTagList tagList = itemStack.getTagCompound().getTagList(ENCHANT_TAG, 10);
      for (int i = 0, iMax = tagList.tagCount(); i < iMax; ++i) {
        NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
        if (!tagCompound.hasKey(ENCHANT_TAG_ID, 8) || !tagCompound.hasKey(ENCHANT_TAG_LEVEL, 3)) {
          continue;
        }
        Enchantment enchantment = Enchantment.getEnchantmentByLocation(tagCompound.getString(ENCHANT_TAG_ID));
        int level = tagCompound.getInteger(ENCHANT_TAG_LEVEL);
        if (enchantment == null || level == 0) {
          continue;
        }
        enchants.put(enchantment, level);
      }
      return enchants;
    }
    
    public static void setEnchants(@NotNull ItemStack itemStack, @NotNull Map<Enchantment, Integer> enchants) {
      resetEnchant(itemStack);
      assert itemStack.getTagCompound() != null;
      NBTTagList tagList = itemStack.getTagCompound().getTagList(ENCHANT_TAG, 10);
      NBTTagCompound tagCompound = new NBTTagCompound();
      enchants.forEach((enchantment, integer) -> {
        tagCompound.setString(ENCHANT_TAG_ID, Objects.requireNonNull(enchantment.getRegistryName()).toString());
        tagCompound.setInteger(ENCHANT_TAG_LEVEL, integer);
        tagList.appendTag(tagCompound.copy());
      });
    }
    
    public static void addEnchants(@NotNull ItemStack itemStack, @NotNull Map<Enchantment, Integer> enchants) {
      if (!(itemStack.getItem()instanceof ItemEnchantedPaper)) {
        return;
      }
      Map<Enchantment, Integer> rawEnchants = getEnchants(itemStack);
      enchants.forEach((enchantment, integer) -> {
        if (!rawEnchants.containsKey(enchantment)) {
          rawEnchants.put(enchantment, integer);
        } else if (rawEnchants.get(enchantment) < integer) {
          rawEnchants.replace(enchantment, integer);
        }
      });
      setEnchants(itemStack, rawEnchants);
    }
    
    public static void addEnchant(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment, int level) {
      if (!(itemStack.getItem() instanceof ItemEnchantedPaper)) {
        return;
      }
      Map<Enchantment, Integer> rawEnchants = getEnchants(itemStack);
      if (!rawEnchants.containsKey(enchantment)) {
        rawEnchants.put(enchantment, level);
      } else if (rawEnchants.get(enchantment) < level) {
        rawEnchants.replace(enchantment, level);
      }
      setEnchants(itemStack, rawEnchants);
    }
    
    public static void deleteEnchants(@NotNull ItemStack itemStack, @NotNull Collection<Enchantment> enchantments) {
      Map<Enchantment, Integer> rawEnchants = getEnchants(itemStack);
      enchantments.forEach(rawEnchants::remove);
      setEnchants(itemStack, rawEnchants);
    }
    
    public static void deleteEnchant(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment) {
      Map<Enchantment, Integer> rawEnchants = getEnchants(itemStack);
      rawEnchants.remove(enchantment);
      setEnchants(itemStack, rawEnchants);
    }
    
    public static boolean hasEnchants(@NotNull ItemStack itemStack) {
      if (!(itemStack.getItem() instanceof ItemEnchantedPaper) || !itemStack.hasTagCompound()) {
        return false;
      }
      
      assert itemStack.getTagCompound() != null;
      return itemStack.getTagCompound().hasKey(ENCHANT_TAG_LEVEL, 9) && !itemStack.getTagCompound().getTagList(ENCHANT_TAG_LEVEL, 10).hasNoTags();
    }
  }
}
