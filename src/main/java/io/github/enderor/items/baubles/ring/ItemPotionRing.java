package io.github.enderor.items.baubles.ring;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import io.github.enderor.config.EnderORConfigs;
import io.github.enderor.items.EnderORItemHandler;
import io.github.enderor.recipes.EnderORRecipesHandler;
import io.github.enderor.recipes.IHasRecipe;
import io.github.enderor.recipes.ShapedRecipe;
import io.github.enderor.recipes.ShapelessRecipe;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemPotionRing extends Item implements IBauble, IHasRecipe {
  public ItemPotionRing() {
    this.setMaxDamage(0);
    this.setMaxStackSize(1);
    EnderORItemHandler.addModel(this, 0, "inventory");
  }
  
  @Override
  public void makeRecipe() {
    EnderORRecipesHandler.addRecipe((new ShapedRecipe("potion_ring_blank", 3, 3, false))
                                      .setInput(0, new ItemStack(Items.IRON_NUGGET, 1, 32767))
                                      .setInput(1, new ItemStack(Items.GOLD_INGOT, 1, 32767))
                                      .setInput(3, new ItemStack(Items.GOLD_INGOT, 1, 32767))
                                      .setInput(5, new ItemStack(Items.GOLD_INGOT, 1, 32767))
                                      .setInput(7, new ItemStack(Items.GOLD_INGOT, 1, 32767))
                                      .setOutput(this.getDefaultInstance()));
    
    EnderORRecipesHandler.addRecipe((new ShapelessRecipe("potion_ring_clear", 1, false) {
                                      @Override
                                      public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
                                        ItemStack result = super.getCraftingResult(inv);
                                        EffectHelper.resetEffect(result);
                                        return result;
                                      }
                                    }
                                    )
                                      .setInput(0, this.getDefaultInstance())
                                      .setOutput(this.getDefaultInstance())
    );
    
    EnderORRecipesHandler.addRecipe((new ShapelessRecipe("potion_ring_add", 2, false) {
                                      @Override
                                      public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
                                        ItemStack result = null;
                                        for (int i = 0, iMax = inv.getSizeInventory(); i < iMax; ++i) {
                                          ItemStack stack = inv.getStackInSlot(i);
                                          if (stack.isEmpty() || !(stack.getItem() instanceof ItemPotionRing)) {
                                            continue;
                                          }
                                          result = stack.copy();
                                          break;
                                        }
                                        if (result == null) {
                                          return output.copy();
                                        }
                                        
                                        for (int i = 0, iMax = inv.getSizeInventory(); i < iMax; ++i) {
                                          ItemStack stack = inv.getStackInSlot(i);
                                          if (stack.isEmpty() || !(stack.getItem() instanceof ItemPotion)) {
                                            continue;
                                          }
                                          if (!PotionUtils.getPotionFromItem(stack).equals(PotionTypes.EMPTY)) {
                                            Map<Potion, Integer> effects = new HashMap<>();
                                            PotionUtils.getEffectsFromStack(stack).forEach(effect -> effects.put(effect.getPotion(), effect.getAmplifier()));
                                            EffectHelper.addEffects(result, effects);
                                          }
                                        }
                                        return result;
                                      }
                                      
                                      @Override
                                      public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
                                        NonNullList<ItemStack> remainingItems = super.getRemainingItems(inv);
                                        for (int i = 0, iMax = remainingItems.size(); i < iMax; ++i) {
                                          ItemStack stack = ForgeHooks.getContainerItem(remainingItems.get(i));
                                          if (stack.isEmpty() || !(stack.getItem() instanceof ItemPotion)) {
                                            continue;
                                          }
                                          remainingItems.set(i, new ItemStack(Items.GLASS_BOTTLE, 2, 0));
                                        }
                                        return remainingItems;
                                      }
                                    }
                                    )
                                      .setInput(0, this.getDefaultInstance())
                                      .setInput(1, new ItemStack(Items.POTIONITEM, 1, 32767))
                                      .setOutput(this.getDefaultInstance())
    );
  }
  
  public static final ItemPotionRing INSTANCE = new ItemPotionRing();
  
  @Override
  public @NotNull ItemStack getDefaultInstance() {
    ItemStack result = new ItemStack(this, 1, 0);
    EffectHelper.resetEffect(result);
    return result;
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
    EffectHelper.getEffects(itemstack).forEach((potion, integer) -> player.addPotionEffect(new PotionEffect(potion, EnderORConfigs.EFFECT_LENGTH, integer)));
    IBauble.super.onEquipped(itemstack, player);
  }
  
  @Override
  public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
    EffectHelper.getEffects(itemstack).forEach((potion, integer) -> player.removePotionEffect(potion));
    IBauble.super.onUnequipped(itemstack, player);
  }
  
  @Override
  public void onWornTick(ItemStack itemstack, @NotNull EntityLivingBase player) {
    EffectHelper.getEffects(itemstack).forEach((potion, integer) -> player.addPotionEffect(new PotionEffect(potion, EnderORConfigs.EFFECT_LENGTH, integer)));
    IBauble.super.onWornTick(itemstack, player);
  }
  
  @Override
  public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
    return true;
  }
  
  @Override
  public @NotNull String getItemStackDisplayName(@NotNull ItemStack stack) {
    Map<Potion, Integer> potions = EffectHelper.getEffects(stack);
    switch (potions.size()) {
      case 0:
        return super.getItemStackDisplayName(stack);
      case 1:
        return I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".name.prev").trim() +
               I18n.format(potions.keySet().toArray(new Potion[0])[0].getName()).trim() +
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
        ItemStack itemStack = getDefaultInstance();
        potion.getEffects().forEach(effect -> EffectHelper.addEffect(itemStack, effect));
        items.add(itemStack);
      }
    });
  }
  
  @Override
  public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (!EffectHelper.hasEffects(stack)) {
      tooltip.add(I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".empty.description"));
      return;
    }
    tooltip.add(I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".filled.description"));
    Map<Potion, Integer> effects = EffectHelper.getEffects(stack);
    effects.forEach((potion, integer) -> tooltip.add("".concat((potion.isBadEffect() ? TextFormatting.RED : TextFormatting.BLUE).toString())
                                                       .concat(I18n.format(potion.getName()).trim()).concat(" ")
                                                       .concat(I18n.format("potion.potency." + integer).trim())
    ));
  }
  
  @Override
  public boolean hasEffect(@NotNull ItemStack stack) {
    return super.hasEffect(stack) || EffectHelper.hasEffects(stack);
  }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent
    public static void onEvent(@NotNull ColorHandlerEvent.Item event) {
      event.getItemColors().registerItemColorHandler(((stack, tintIndex) -> {
        if (tintIndex != 0) {
          return -1;
        }
        Map<Potion, Integer> potionEffects = EffectHelper.getEffects(stack);
        switch (potionEffects.size()) {
          case 0:
            return -1;
          case 1:
            return potionEffects.keySet().toArray(new Potion[0])[0].getLiquidColor();
          default:
            return 16253176;
        }
      }
                                                     ), ItemPotionRing.INSTANCE);
    }
  }
  
  public static final class EffectHelper {
    public static final String EFFECT_TAG = "effects";
    public static final String EFFECT_ID = "id";
    public static final String EFFECT_LEVEL = "lvl";
    
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
    
    @Contract ("_ -> new")
    public static @NotNull Map<Potion, Integer> getEffects(@NotNull ItemStack itemStack) {
      Map<Potion, Integer> effects = new HashMap<>();
      if (!(itemStack.getItem() instanceof ItemPotionRing)) {
        return effects;
      }
      if (!itemStack.hasTagCompound()) {
        return effects;
      }
      assert itemStack.getTagCompound() != null;
      if (!itemStack.getTagCompound().hasKey(EFFECT_TAG, 9) || itemStack.getTagCompound().getTagList(EFFECT_TAG, 10).hasNoTags()) {
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
        int level = tagCompound.getInteger(EFFECT_LEVEL);
        if (potion == null) {
          continue;
        }
        effects.put(potion, level);
      }
      return effects;
    }
    
    public static void setEffects(@NotNull ItemStack itemStack, @NotNull Map<Potion, Integer> effects) {
      resetEffect(itemStack);
      assert itemStack.getTagCompound() != null;
      NBTTagList tagList = itemStack.getTagCompound().getTagList(EFFECT_TAG, 10);
      NBTTagCompound compound = new NBTTagCompound();
      effects.forEach((potion, integer) -> {
        compound.setString(EFFECT_ID, Objects.requireNonNull(potion.getRegistryName()).toString());
        compound.setInteger(EFFECT_LEVEL, integer);
        tagList.appendTag(compound.copy());
      });
    }
    
    public static void addEffects(@NotNull ItemStack itemStack, @NotNull Map<Potion, Integer> effects) {
      if (!(itemStack.getItem() instanceof ItemPotionRing)) {
        return;
      }
      Map<Potion, Integer> rawEffects = getEffects(itemStack);
      effects.forEach((potion, integer) -> {
        if (!rawEffects.containsKey(potion)) {
          rawEffects.put(potion, integer);
        } else if (rawEffects.get(potion) < integer) {
          rawEffects.replace(potion, integer);
        }
      });
      setEffects(itemStack, rawEffects);
    }
    
    public static void addEffect(@NotNull ItemStack itemStack, @NotNull Potion potion, int level) {
      if (!(itemStack.getItem() instanceof ItemPotionRing)) {
        return;
      }
      Map<Potion, Integer> rawEffects = getEffects(itemStack);
      if (!rawEffects.containsKey(potion)) {
        rawEffects.put(potion, level);
      } else if (rawEffects.get(potion) < level) {
        rawEffects.replace(potion, level);
      }
      setEffects(itemStack, rawEffects);
    }
    
    public static void addEffect(@NotNull ItemStack itemStack, @NotNull PotionEffect effect) {
      addEffect(itemStack, effect.getPotion(), effect.getAmplifier());
    }
    
    public static boolean hasEffects(@NotNull ItemStack itemStack) {
      if (!(itemStack.getItem() instanceof ItemPotionRing) || !itemStack.hasTagCompound()) {
        return false;
      }
      
      assert itemStack.getTagCompound() != null;
      return itemStack.getTagCompound().hasKey(EFFECT_TAG, 9) && !itemStack.getTagCompound().getTagList(EFFECT_TAG, 10).hasNoTags();
    }
  }
}
