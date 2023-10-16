package io.github.enderor.blocks.tileEntities;

import io.github.enderor.EnderORUtils;
import io.github.enderor.gui.ContainerEnchantMover;
import io.github.enderor.items.ItemEnchantedPaper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

public class TileEntityEnchantMover extends TileEntityLockable implements ITickable, IInteractionObject {
  public int tickCount;
  public double pageFlip;
  public double pageFlipPrev;
  public double flipT;
  public double flipA;
  public double bookSpread;
  public double bookSpreadPrev;
  public double bookRotation;
  public double bookRotationPrev;
  public double tRot;
  
  ItemStack[] containedItems = new ItemStack[2];
  
  private static final Random rand = new Random();
  
  protected String customName;
  
  public TileEntityEnchantMover() {
    Arrays.fill(containedItems, ItemStack.EMPTY);
  }
  
  public static final String NBT_KEY = "slots";
  public static final String NBT_KEY_SLOT = "slot";
  public static final String NBT_KEY_STACK = "stack";
  
  @Override
  public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
    super.writeToNBT(compound);
    
    if (this.hasCustomName()) { compound.setString("CustomName", this.customName); }
    
/*
    NBTTagList tagList = new NBTTagList();

    for (int i = 0; i < this.containedItems.length; i++) {
      NBTTagCompound compound1 = new NBTTagCompound();
      compound1.setInteger(NBT_KEY_SLOT, i);
      compound1.setTag(NBT_KEY_STACK, this.containedItems[i].serializeNBT());
      tagList.appendTag(compound1);
    }

    compound.setTag(NBT_KEY, tagList);
*/
    
    return compound;
  }
  
  @Override
  public void readFromNBT(@NotNull NBTTagCompound compound) {
    super.readFromNBT(compound);
    
    if (compound.hasKey("CustomName", 8)) { this.customName = compound.getString("CustomName"); }

/*
    if (!compound.hasKey(NBT_KEY, 9)) {
      return;
    }
    NBTTagList tagList = compound.getTagList(NBT_KEY, 10);
    tagList.forEach(nbtBase -> {
      NBTTagCompound compound2 = (NBTTagCompound) nbtBase;
      if (compound2.hasKey(NBT_KEY_SLOT, 3) && compound2.hasKey(NBT_KEY_STACK, 10)) {
        int slot = compound2.getInteger(NBT_KEY_SLOT);
        if (this.containedItems.length <= slot || slot < 0) { return; }
        NBTTagCompound stack = compound2.getCompoundTag(NBT_KEY_STACK);
        ItemStack stack1 = new ItemStack(stack);
        if (stack1.isEmpty()) { return; }
        this.containedItems[slot] = stack1;
      }
    });
*/
  }
  
  @Override
  public void update() {
    this.bookSpreadPrev = this.bookSpread;
    this.bookRotationPrev = this.bookRotation;
    EntityPlayer entityplayer = this.world.getClosestPlayer(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 3.0D, false);
    
    if (entityplayer != null) {
      double dx = entityplayer.posX - (this.pos.getX() + 0.5D);
      double dy = entityplayer.posZ - (this.pos.getZ() + 0.5D);
      this.tRot = MathHelper.atan2(dy, dx);
      this.bookSpread += 0.1D;
      
      if (this.bookSpread < 0.5D || rand.nextInt(40) == 0) {
        double flip = this.flipT;
        do { this.flipT += (rand.nextInt(4) - rand.nextInt(4)); } while (flip == this.flipT);
      }
    } else {
      this.tRot += 0.02D;
      this.bookSpread -= 0.1D;
    }
    
    while (this.bookRotation >= Math.PI) { this.bookRotation -= Math.PI * 2D; }
    while (this.bookRotation < -Math.PI) { this.bookRotation += Math.PI * 2D; }
    while (this.tRot >= Math.PI) { this.tRot -= Math.PI * 2D; }
    while (this.tRot < -Math.PI) { this.tRot += Math.PI * 2D; }
    double rotate = this.tRot - this.bookRotation;
    while (rotate >= Math.PI) { rotate -= Math.PI * 2D; }
    while (rotate < -Math.PI) { rotate += Math.PI * 2D; }
    
    this.bookRotation += rotate * 0.4F;
    this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0D, 1.0D);
    ++this.tickCount;
    
    this.pageFlipPrev = this.pageFlip;
    double flip = (this.flipT - this.pageFlip) * 0.4D;
    flip = MathHelper.clamp(flip, -0.2D, 0.2D);
    this.flipA += (flip - this.flipA) * 0.9D;
    this.pageFlip += this.flipA;
  }
  
  
  @Override
  public @NotNull Container createContainer(@NotNull InventoryPlayer playerInventory, @NotNull EntityPlayer playerIn) {
    return new ContainerEnchantMover(playerInventory, this);
  }
  
  @Override
  public @NotNull String getGuiID() {
    return EnderORUtils.MOD_ID + ":enchant_mover";
  }
  
  @Override
  public @NotNull String getName() {
    return this.hasCustomName() ? this.customName : "container.enchant_mover";
  }
  
  @Override
  public boolean hasCustomName() {
    return this.customName != null && !this.customName.isEmpty();
  }
  
  public void setCustomName(String customName) {
    this.customName = customName;
  }
  
  @Override
  public @NotNull ITextComponent getDisplayName() {
    return this.hasCustomName()
           ? new TextComponentString(this.getName())
           : new TextComponentTranslation(this.getName());
  }
  
  @Override
  public int getSizeInventory() {
    return 2;
  }
  
  @Override
  public boolean isEmpty() {
    for (int i = 0; i < this.containedItems.length; i++) {
      if (!this.containedItems[i].isEmpty()) { return true; }
    }
    return false;
  }
  
  @Override
  public @NotNull ItemStack getStackInSlot(int index) {
    return 0 < index && index < this.containedItems.length ? this.containedItems[index] : ItemStack.EMPTY;
  }
  
  @Override
  public @NotNull ItemStack decrStackSize(int index, int count) {
    if (this.containedItems.length <= index || index <= 0 || this.containedItems[index].isEmpty()) {
      return ItemStack.EMPTY;
    }
    if (this.containedItems[index].getCount() < count) { return this.removeStackFromSlot(index); }
    return this.containedItems[index].splitStack(count);
  }
  
  @Override
  public @NotNull ItemStack removeStackFromSlot(int index) {
    ItemStack result = this.containedItems[index];
    this.containedItems[index] = ItemStack.EMPTY;
    return result;
  }
  
  @Override
  public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
    if (0 <= index && index < this.containedItems.length) { this.containedItems[index] = stack; }
  }
  
  @Override
  public int getInventoryStackLimit() { return 1; }
  
  @Override
  public boolean isUsableByPlayer(@NotNull EntityPlayer player) {
    return this.world.getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
  }
  
  @Override
  public void openInventory(@NotNull EntityPlayer player) { }
  
  @Override
  public void closeInventory(@NotNull EntityPlayer player) { }
  
  @Override
  public boolean isItemValidForSlot(int index, @NotNull ItemStack stack) {
    return 0 <= index && index < this.containedItems.length && (index != 1 || stack.getItem() instanceof ItemEnchantedPaper);
  }
  
  @Override
  public int getField(int id) {
    return 0;
  }
  
  @Override
  public void setField(int id, int value) {
  
  }
  
  @Override
  public int getFieldCount() {
    return 0;
  }
  
  @Override
  public void clear() {
    Arrays.fill(this.containedItems, ItemStack.EMPTY);
  }
}
