package io.github.enderor.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class EnchantmentShortBow extends Enchantment {
  public EnchantmentShortBow(String name) {
    super(Rarity.COMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND });
    EnderOREnchantmentHandler.addEnchantment(this, name);
  }
  
  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return 0;
  }
  
  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
    return 0;
  }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onEvent (LivingEntityUseItemEvent.@NotNull Tick event) {
      if (!(event.getEntityLiving() instanceof EntityPlayer)) {
        return;
      }
      if (!(event.getItem().getItem() instanceof ItemBow)) {
        return;
      }
      if (EnchantmentHelper.getEnchantmentLevel(EnderOREnchantmentHandler.ENCHANTMENT_SHORT_BOW, event.getItem()) <= 0) {
        return;
      }
      if (event.getItem().getItem().getMaxItemUseDuration(event.getItem()) - event.getDuration() <= 20) {
        return;
      }
      event.setCanceled(true);
      event.getEntityLiving().stopActiveHand();
    }
  }
}
