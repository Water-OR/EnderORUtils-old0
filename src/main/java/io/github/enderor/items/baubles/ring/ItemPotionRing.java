package io.github.enderor.items.baubles.ring;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import io.github.enderor.EnderORUtils;
import io.github.enderor.items.EnderORItemHandler;
import io.github.recipes.EnderORRecipe;
import io.github.recipes.EnderORRecipesHandler;
import io.github.recipes.ShapedRecipe;
import io.github.recipes.ShapelessRecipe;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemPotionRing extends Item implements IBauble {
  public ItemPotionRing() {
    this.setMaxDamage(0);
    this.setMaxStackSize(1);
    EnderORItemHandler.addModel(this, 0, "inventory");
    
    makeItemRecipe();
  }
  
  public void makeItemRecipe() {
    EnderORRecipesHandler.addRecipe((new ShapedRecipe("potion_ring_blank", 3, 3, false))
            .setInput(0, new ItemStack(Items.IRON_NUGGET, 1, 32767))
            .setInput(1, new ItemStack(Items.GOLD_INGOT, 1, 32767))
            .setInput(3, new ItemStack(Items.GOLD_INGOT, 1, 32767))
            .setInput(5, new ItemStack(Items.GOLD_INGOT, 1, 32767))
            .setInput(7, new ItemStack(Items.GOLD_INGOT, 1, 32767)).setOutput(this.getDefaultInstance()));
    
    PotionType.REGISTRY.forEach(potion -> {
      if (!potion.getEffects().isEmpty()) {
        makeRecipe(potion);
      }
    });
  }
  
  public void makeRecipe(PotionType potion) {
    ItemStack outputItem = getDefaultInstance();
    addEffects(outputItem, potion.getEffects().toArray(new PotionEffect[0]));
    List<Ingredient> ingredients = new ArrayList<>();
    ingredients.add(Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1, 32767), potion)));
    ingredients.add(Ingredient.fromStacks(new ItemStack(this, 1, 32767)));
    EnderORRecipesHandler.addRecipe(new ShapelessRecipe(String.format("potion_ring_%s", Objects.requireNonNull(potion.getRegistryName()).getResourcePath()), 2, false) {
      @Override
      public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
        NonNullList<ItemStack> remainingItems = super.getRemainingItems(inv);
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
          ItemStack stack = ForgeHooks.getContainerItem(inv.getStackInSlot(i));
          if (!(stack.getItem() instanceof ItemPotion)) {
            continue;
          }
          ItemStack addItem = new ItemStack(Items.GLASS_BOTTLE, stack.getCount());
          if (stack.getTagCompound() != null) {
            addItem.setTagCompound(stack.getTagCompound());
          }
          remainingItems.set(i, addItem);
        }
        return remainingItems;
      }
      
      @Override
      public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        ItemStack result = ForgeHooks.getContainerItem(output.copy());
        if (result == ItemStack.EMPTY) {
          return ItemStack.EMPTY;
        }
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
          ItemStack stack = ForgeHooks.getContainerItem(inv.getStackInSlot(i));
          if (!(stack.getItem() instanceof ItemPotionRing) || stack.getTagCompound() == null) {
            continue;
          }
          if (result.getTagCompound() != null) {
            result.setTagCompound(new NBTTagCompound());
          }
          result.getTagCompound().merge(stack.getTagCompound());
        }
        return result;
      }
      
      @Override
      public boolean matches(@NotNull InventoryCrafting inv, World worldIn, Comparator<NBTTagCompound> comparator) {
        int matchCount = 0, notEmptyCount = 0;
        for (int i = 0, iMax = inv.getSizeInventory(); i < iMax; ++i) {
          boolean isMatched = false;
          ItemStack stack = ForgeHooks.getContainerItem(inv.getStackInSlot(i));
          if (stack.equals(ItemStack.EMPTY)) {
            continue;
          }
          for (Ingredient ingredient : input) {
            
            if (stack.equals(ItemStack.EMPTY)) {
              ++notEmptyCount;
            }
            
            if (matchesItem(stack.copy(), ingredient, comparator)) {
              isMatched = true;
              ++matchCount;
              break;
            }
          }
          if (!isMatched) {
            return false;
          }
        }
        return matchCount == input.size();
      }
    }.setInput(ingredients).setOutput(outputItem));
  }
  
  public static final ItemPotionRing INSTANCE = new ItemPotionRing();
  public static final String EFFECT_TAG = "effects";
  public static final String EFFECT_ID = "id";
  public static final String EFFECT_LEVEL = "lvl";
  public static final Integer EFFECT_LENGTH = 2000;
  
  @Override
  public @NotNull ItemStack getDefaultInstance() {
    ItemStack stack = super.getDefaultInstance();
    resetEffect(stack);
    return stack;
  }
  
  @Override
  public void onUsingTick(@NotNull ItemStack stack, @NotNull EntityLivingBase player, int count) {
    BaublesContainer container = (BaublesContainer) player.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
    if (container == null) {
      return;
    }
    for (int i = 0; i < container.getSlots(); ++i) {
      if (container.isItemValidForSlot(i, stack, player)) {
        container.insertItem(i, stack, false);
      }
    }
  }
  
  @Override
  public BaubleType getBaubleType(ItemStack itemStack) {
    return BaubleType.RING;
  }
  
  @Override
  public void onEquipped(ItemStack itemstack, @NotNull EntityLivingBase player) {
    getEffects(itemstack).forEach(potion -> player.addPotionEffect(new PotionEffect(potion.getPotion(), 2000, potion.getAmplifier())));
    IBauble.super.onEquipped(itemstack, player);
  }
  
  @Override
  public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
    getEffects(itemstack).forEach(effect -> player.removePotionEffect(effect.getPotion()));
    IBauble.super.onUnequipped(itemstack, player);
  }
  
  @Override
  public void onWornTick(ItemStack itemstack, @NotNull EntityLivingBase player) {
    getEffects(itemstack).forEach(potion -> player.addPotionEffect(new PotionEffect(potion.getPotion(), 2000, potion.getAmplifier())));
    IBauble.super.onWornTick(itemstack, player);
  }
  
  @Override
  public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
    return true;
  }
  
  @Override
  public @NotNull String getItemStackDisplayName(@NotNull ItemStack stack) {
    List<PotionEffect> effects = getEffects(stack);
    switch (effects.size()) {
      case 0:
        return super.getItemStackDisplayName(stack);
      case 1:
        return I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".name.prev").trim() +
                I18n.format(effects.get(0).getEffectName()).trim() +
                I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".name.post").trim();
      default:
        return I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".name.multiple").trim();
    }
  }
  
  @Override
  public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
    super.getSubItems(tab, items);
    if (!this.isInCreativeTab(tab)) {
      return;
    }
    PotionType.REGISTRY.forEach(potion -> {
      if (!potion.getEffects().isEmpty()) {
        ItemStack itemStack = new ItemStack(INSTANCE);
        potion.getEffects().forEach(effect -> addEffect(itemStack, effect));
        items.add(itemStack);
      }
    });
  }
  
  @Override
  public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (!hasEffects(stack)) {
      tooltip.add(I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".empty.description"));
      return;
    }
    tooltip.add(I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".filled.description"));
    List<PotionEffect> effects = getEffects(stack);
    for (PotionEffect effect : effects) {
      String lore = "";
      tooltip.add(""
              .concat((effect.getPotion().isBadEffect() ? TextFormatting.RED : TextFormatting.BLUE).toString())
              .concat(I18n.format(effect.getEffectName()).trim())
              .concat(" ")
              .concat(I18n.format("potion.potency." + effect.getAmplifier()).trim())
      );
    }
  }
  
  public void addInfo(@NotNull ItemStack stack) {
  
  }
  
  @Override
  public boolean hasEffect(@NotNull ItemStack stack) {
    return super.hasEffect(stack) || !hasEffects(stack);
  }
  
  public static boolean hasEffects(@NotNull ItemStack itemStack) {
    if (!(itemStack.getItem() instanceof ItemPotionRing) || !itemStack.hasTagCompound()) {
      return false;
    }
    
    assert itemStack.getTagCompound() != null;
    return itemStack.getTagCompound().hasKey(EFFECT_TAG, 9) && !itemStack.getTagCompound().getTagList(EFFECT_TAG, 10).hasNoTags();
  }
  
  @Contract("_ -> new")
  public static @NotNull List<PotionEffect> getEffects(@NotNull ItemStack itemStack) {
    List<PotionEffect> effects = new ArrayList<>();
    if (!(itemStack.getItem() instanceof ItemPotionRing)) {
      return effects;
    }
    if (!itemStack.hasTagCompound()) {
      resetEffect(itemStack);
    }
    assert itemStack.getTagCompound() != null;
    if (!itemStack.getTagCompound().hasKey(EFFECT_TAG, 9) || itemStack.getTagCompound().getTagList(EFFECT_TAG, 10).tagCount() == 0) {
      resetEffect(itemStack);
      return effects;
    }
    NBTTagList tagList = itemStack.getTagCompound().getTagList(EFFECT_TAG, 10);
    for (int i = 0, iMax = tagList.tagCount(); i < iMax; ++i) {
      NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
      if (!tagCompound.hasKey(EFFECT_ID, 8) || !tagCompound.hasKey(EFFECT_LEVEL, 3)) {
        continue;
      }
      Potion potion = Potion.getPotionFromResourceLocation(tagCompound.getString(EFFECT_ID));
      if (potion == null) {
        continue;
      }
      effects.add(new PotionEffect(potion, EFFECT_LENGTH, tagCompound.getInteger(EFFECT_LEVEL)));
    }
    sortEffects(effects);
    return effects;
  }
  
  public static void addEffects(@NotNull ItemStack itemStack, @NotNull PotionEffect @NotNull [] effects) {
    if (!(itemStack.getItem() instanceof ItemPotionRing)) {
      return;
    }
    List<PotionEffect> rawEffects = getEffects(itemStack);
    List<PotionEffect> addOns = Arrays.asList(effects);
    for (PotionEffect rawEffect : rawEffects)
      for (PotionEffect addOn : addOns) {
        if (addOn.getPotion() != rawEffect.getPotion()) {
          continue;
        }
        if (addOn.getAmplifier() > rawEffect.getAmplifier()) {
          addOns.remove(addOn);
        } else {
          rawEffects.remove(rawEffect);
        }
        break;
      }
    rawEffects.addAll(addOns);
    setEffects(itemStack, rawEffects);
  }
  
  public static void addEffect(@NotNull ItemStack itemStack, @NotNull PotionEffect effect) {
    addEffects(itemStack, new PotionEffect[]{effect});
  }
  
  public static void setEffects(@NotNull ItemStack itemStack, @NotNull List<PotionEffect> effects) {
    resetEffect(itemStack);
    sortEffects(effects);
    assert itemStack.getTagCompound() != null;
    NBTTagList tagList = itemStack.getTagCompound().getTagList(EFFECT_TAG, 9);
    effects.forEach(effect -> {
      NBTTagCompound tagCompound = new NBTTagCompound();
      tagCompound.setString(EFFECT_ID, Objects.requireNonNull(effect.getPotion().getRegistryName()).toString());
      tagCompound.setInteger(EFFECT_LEVEL, effect.getAmplifier());
      tagList.appendTag(tagCompound);
    });
  }
  
  public static void resetEffect(@NotNull ItemStack itemStack) {
    if (!itemStack.hasTagCompound()) {
      itemStack.setTagCompound(new NBTTagCompound());
    }
    assert itemStack.getTagCompound() != null;
    if (itemStack.getTagCompound().hasKey(EFFECT_TAG)) {
      itemStack.getTagCompound().removeTag(EFFECT_TAG);
    }
    itemStack.getTagCompound().setTag(EFFECT_TAG, new NBTTagList());
  }
  
  public static void sortEffects(@NotNull List<PotionEffect> effects) {
    effects.sort((x, y) -> {
      String xName = x.getEffectName(), yName = y.getEffectName();
      for (int i = 0, iMax = Math.min(xName.length(), yName.length()); i < iMax; ++i) {
        if (xName.charAt(i) != yName.charAt(i)) {
          return xName.charAt(i) - yName.charAt(i);
        }
      }
      return xName.length() - yName.length();
    });
  }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent
    public static void onEvent(@NotNull ColorHandlerEvent.Item event) {
      event.getItemColors().registerItemColorHandler(((stack, tintIndex) -> {
        if (tintIndex != 0) {
          return -1;
        }
        List<PotionEffect> potionEffects = getEffects(stack);
        switch (potionEffects.size()) {
          case 0:
            return -1;
          case 1:
            return potionEffects.get(0).getPotion().getLiquidColor();
          default:
            return 3694022;
        }
      }), ItemPotionRing.INSTANCE);
    }
  }
}
