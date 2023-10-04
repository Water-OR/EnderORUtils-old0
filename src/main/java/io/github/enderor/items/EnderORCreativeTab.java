package io.github.enderor.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnderORCreativeTab extends CreativeTabs {
  public EnderORCreativeTab() {
    super("enderor_items");
  }
  
  @Override
  public @NotNull ItemStack getTabIconItem() {
    return new ItemStack(ItemMod.INSTANCE);
  }
}
