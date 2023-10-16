package io.github.enderor.gui;

import io.github.enderor.blocks.tileEntities.TileEntityEnchantMover;
import io.github.enderor.items.ItemEnchantedPaper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerEnchantMover extends Container {
  public IInventory tableInventory;
  private final TileEntityEnchantMover tileEnchantMover;
  
  public ContainerEnchantMover(InventoryPlayer playerInv, TileEntityEnchantMover tileEnchantMover) {
    this.tileEnchantMover = tileEnchantMover;
    this.tableInventory = new InventoryBasic("Enchant Mover", true, 2) {
      public void markDirty() {
        super.markDirty();
        ContainerEnchantMover.this.onCraftMatrixChanged(this);
      }
    };
    this.addSlotToContainer(new Slot(this.tableInventory, 0, 105, 133) {
      @Override
      public int getSlotStackLimit() { return 1; }
      
      @Override
      public boolean isItemValid(@NotNull ItemStack stack) {
        return stack.getItem() instanceof ItemEnchantedPaper || stack.getItem().isEnchantable(stack);
      }
    });
    this.addSlotToContainer(new Slot(this.tableInventory, 1, 133, 133) {
      @Override
      public int getSlotStackLimit() { return 1; }
      
      @Override
      public boolean isItemValid(@NotNull ItemStack stack) {
        return stack.getItem() instanceof ItemEnchantedPaper || stack.getItem().isEnchantable(stack);
      }
    });
    
    for (int index = 0; index < 27; ++index) {
      this.addSlotToContainer(new Slot(playerInv, index + 9, 48 + (index % 9) * 18, 174 + (index / 9) * 18));
    }
    
    for (int index = 0; index < 9; ++index) {
      this.addSlotToContainer(new Slot(playerInv, index, 48 + index * 18, 232));
    }
  }
  
  @Override
  public boolean canInteractWith(@NotNull EntityPlayer playerIn) {
    return this.tableInventory.isUsableByPlayer(playerIn);
  }
  
  @Override
  public void onContainerClosed(@NotNull EntityPlayer playerIn) {
    super.onContainerClosed(playerIn);
    tableInventory.closeInventory(playerIn);
  }
}
