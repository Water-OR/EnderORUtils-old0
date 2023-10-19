package io.github.enderor.blocks;

import io.github.enderor.blocks.tileEntities.IHasTileEntity;
import io.github.enderor.blocks.tileEntities.TileEntityEnchantMover;
import io.github.enderor.gui.GuiEnchantMover;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockEnchantMover extends BlockContainer implements IHasTileEntity {
  public BlockEnchantMover() {
    super(Material.ROCK, MapColor.GRAY);
    this.setLightOpacity(0);
    EnderORBlockHandler.addModel(this, 0, "inventory");
  }
  
  public static final BlockEnchantMover INSTANCE = new BlockEnchantMover();
  
  @Override
  public @NotNull AxisAlignedBB getBoundingBox(@NotNull IBlockState state, @NotNull IBlockAccess source, @NotNull BlockPos pos) {
    return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
  }
  
  @Override
  public boolean isFullCube(@NotNull IBlockState state) {
    return false;
  }
  
  @Override
  @SideOnly (Side.CLIENT)
  public void randomDisplayTick(@NotNull IBlockState stateIn, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Random rand) {
    super.randomDisplayTick(stateIn, worldIn, pos, rand);
    
    for (int i = -2; i <= 2; ++i) {
      for (int j = -2; j <= 2; ++j) {
        if (i > -2 && i < 2 && j == -1) { j = 2; }
        if (rand.nextInt(4) != 0) { continue; }
        makeParticle(i, j, worldIn, pos, rand);
      }
    }
  }
  
  @SideOnly (Side.CLIENT)
  protected void makeParticle(int i, int j, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Random rand) {
    for (int k = 0; k <= 1; ++k) {
      BlockPos blockpos = pos.add(i, k, j);
      
      if (!(net.minecraftforge.common.ForgeHooks.getEnchantPower(worldIn, blockpos) > 0)) { continue; }
      if (!worldIn.isAirBlock(pos.add(i / 2, 0, j / 2))) { break; }
      
      worldIn.spawnParticle(
        EnumParticleTypes.ENCHANTMENT_TABLE,
        pos.getX() + 0.5D,
        pos.getY() + 2.0D,
        pos.getZ() + 0.5D,
        i + rand.nextFloat() - 0.5D,
        k - rand.nextFloat() - 1.0D,
        j + rand.nextFloat() - 0.5D
      );
    }
  }
  
  @Override
  public boolean isOpaqueCube(@NotNull IBlockState state) {
    return false;
  }
  
  @Override
  public @NotNull EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }
  
  @Override
  public boolean hasTileEntity(@NotNull IBlockState state) { return true; }
  
  @Nullable
  @Override
  public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
    return new TileEntityEnchantMover();
  }
  
  @Override
  public boolean onBlockActivated(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityPlayer playerIn, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
    TileEntity tile0 = worldIn.getTileEntity(pos);
    if (!(tile0 instanceof TileEntityEnchantMover) || !(state.getBlock() instanceof BlockEnchantMover)) {
      return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
    
    TileEntityEnchantMover tile = (TileEntityEnchantMover) tile0;
    
    if (playerIn instanceof EntityPlayerSP) {
      Minecraft.getMinecraft().displayGuiScreen(new GuiEnchantMover(playerIn.inventory, tile));
      return true;
    }
    
    if (!(playerIn instanceof EntityPlayerMP)) { return false; }
    
    EntityPlayerMP playerMP = (EntityPlayerMP) playerIn;
    
    playerMP.getNextWindowId();
    playerMP.connection.sendPacket(new SPacketOpenWindow(playerMP.currentWindowId, tile.getGuiID(), tile.getDisplayName()));
    playerMP.openContainer = tile.createContainer(playerMP.inventory, playerMP);
    playerMP.openContainer.windowId = playerMP.currentWindowId;
    playerMP.openContainer.addListener(playerMP);
    
    MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(playerMP, playerMP.openContainer));
    
    return true;
  }
  
  @Override
  public void onBlockPlacedBy(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityLivingBase placer, @NotNull ItemStack stack) {
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    
    if (!stack.hasDisplayName()) { return; }
    
    TileEntity tileEntity = worldIn.getTileEntity(pos);
    
    if (!(tileEntity instanceof TileEntityEnchantMover)) { return; }
    
    ((TileEntityEnchantMover) tileEntity).setCustomName(stack.getDisplayName());
  }
  
  @Override
  public Class<? extends TileEntity> getTileEntity() {
    return TileEntityEnchantMover.class;
  }
  
  @Nullable
  @Override
  public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
    return new TileEntityEnchantMover();
  }
}
