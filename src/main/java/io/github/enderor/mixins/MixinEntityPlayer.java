package io.github.enderor.mixins;

import io.github.enderor.items.ItemMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityPlayer.class})
public abstract class MixinEntityPlayer {
  @Shadow private ItemStack itemStackMainHand;
  
  @Shadow public abstract ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn);
  
  @Inject(method = "setDead", at = @At("HEAD"), cancellable = true)
  public void setDead1(CallbackInfo ci) {
    if ((this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof ItemMod) ||
            (this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem() instanceof ItemMod)) {
      ci.cancel();
    }
  }
}
