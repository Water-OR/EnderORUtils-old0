package io.github.enderor.utils;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

public class EnchantDescriptionHelper {
  public static @NotNull String getModName(IForgeRegistryEntry.@NotNull Impl<?> registryEntry) {
    ModContainer modContainer = Loader.instance().getIndexedModList().get(getModId(registryEntry));
    return modContainer == null ? "" : modContainer.getName();
  }
  
  public static @NotNull String getModId(IForgeRegistryEntry.@NotNull Impl<?> registryEntry) {
    final ResourceLocation resourceLocation = registryEntry.getRegistryName();
    if (resourceLocation == null) {
      return "";
    }
    return resourceLocation.getResourceDomain();
  }
  
  public static @NotNull String getKey(IForgeRegistryEntry.@NotNull Impl<?> registryEntry) {
    final ResourceLocation resourceLocation = registryEntry.getRegistryName();
    if (resourceLocation == null) {
      return "";
    }
    return resourceLocation.getResourcePath();
  }
  
  public static @NotNull String getEnchantDescription(Enchantment enchantment) {
    String modId = getModId(enchantment);
    String enchantKey = getKey(enchantment);
    String result = I18n.format("enchantment.%s.%s.desc", modId, enchantKey);
    
    if (result.startsWith("enchantment.")) {
      result = I18n.format("tooltip.enchdesc.missing", modId.isEmpty() ? "Null" : getModName(enchantment), enchantKey);
    }
    
    return result.trim();
  }
}
