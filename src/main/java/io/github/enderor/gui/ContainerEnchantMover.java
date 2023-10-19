package io.github.enderor.gui;

import io.github.enderor.blocks.tileEntities.TileEntityEnchantMover;
import io.github.enderor.items.ItemEnchantedPaper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerEnchantMover extends Container {
  public final TileEntityEnchantMover tileEnchantMover;
  
  public ContainerEnchantMover(InventoryPlayer playerInv, TileEntityEnchantMover tileEnchantMover, EntityPlayer user) {
    this.tileEnchantMover = tileEnchantMover;
    this.tileEnchantMover.openInventory(user);
    this.addSlotToContainer(new Slot(this.tileEnchantMover, 0, 106, 134) {
      @Override
      public int getSlotStackLimit() { return 1; }
      
      @Override
      public boolean isItemValid(@NotNull ItemStack stack) {
        return stack.getItem() instanceof ItemEnchantedPaper || stack.getItem().isEnchantable(stack);
      }
    });
    this.addSlotToContainer(new Slot(this.tileEnchantMover, 1, 134, 134) {
      @Override
      public int getSlotStackLimit() { return 1; }
      
      @Override
      public boolean isItemValid(@NotNull ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemEnchantedPaper) { return true; }
        if (item instanceof ItemBook) { return true; }
        if (item instanceof ItemEnchantedBook) { return true; }
        return item.isEnchantable(stack);
      }
    });
    
    for (int index = 0; index < 27; ++index) {
      this.addSlotToContainer(new Slot(playerInv, index + 9, 48 + (index % 9) * 18, 156 + (index / 9) * 18));
    }
    
    for (int index = 0; index < 9; ++index) {
      this.addSlotToContainer(new Slot(playerInv, index, 48 + index * 18, 214));
    }
  }
  
  @Override
  public boolean canInteractWith(@NotNull EntityPlayer playerIn) {
    return this.tileEnchantMover.isUsableByPlayer(playerIn);
  }
  
  @Override
  public @NotNull ItemStack transferStackInSlot(@NotNull EntityPlayer playerIn, int index) {
    ItemStack result = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    
    if (slot == null || !slot.getHasStack()) { return result; }
    ItemStack itemStack = slot.getStack();
    result = itemStack.copy();
    
    if (index < 2) {
      if (!this.mergeItemStack(itemStack, 2, this.inventorySlots.size(), true)) { return ItemStack.EMPTY; }
    } else {
      if (!this.mergeItemStack(itemStack, 0, 2, false)) { return ItemStack.EMPTY; }
    }
    
    if (itemStack.isEmpty()) {
      slot.putStack(ItemStack.EMPTY);
    } else {
      slot.onSlotChanged();
    }
    
    return result;
  }
  
  @Override
  public void onContainerClosed(@NotNull EntityPlayer playerIn) {
    super.onContainerClosed(playerIn);
    this.tileEnchantMover.closeInventory(playerIn);
  }
}
