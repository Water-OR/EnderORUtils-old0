package io.github.enderor.enchantments;

import io.github.enderor.EnderORUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnderOREnchantmentHandler {
  
  public static final List<Enchantment> enchantmentList = new ArrayList<>();
  
  public static void addEnchantment(@NotNull Enchantment enchantment, String name) {
    enchantmentList.add(enchantment.setRegistryName(name).setName(name));
  }
  
  public static final EnchantmentShortBow ENCHANTMENT_SHORT_BOW = new EnchantmentShortBow("short_bow");
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent
    public static void onEvent(RegistryEvent.@NotNull Register<Enchantment> event) {
      IForgeRegistry<Enchantment> registry = event.getRegistry();
      enchantmentList.forEach(enchantment -> {
        EnderORUtils.log(Level.WARN, String.format("Register %s enchantment.", enchantment.getRegistryName()));
        registry.register(enchantment);
      });
    }
  }
}
