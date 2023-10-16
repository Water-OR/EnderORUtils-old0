package io.github.enderor.blocks.tileEntities;

import net.minecraft.tileentity.TileEntity;

public interface IHasTileEntity {
  Class<? extends TileEntity> getTileEntity();
}
