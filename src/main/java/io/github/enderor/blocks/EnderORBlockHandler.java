package io.github.enderor.blocks;

import io.github.enderor.EnderORUtils;
import io.github.enderor.recipes.IHasRecipe;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EnderORBlockHandler {
  
  private static final List<Block> blockList = new ArrayList<>();
  
  private static final HashMap<Pair<Block, Integer>, String> MODEL_MAP = new HashMap<>();
  
  public static void addBlock(@NotNull Block block, String registerName) {
    blockList.add(block.setRegistryName(new ResourceLocation(EnderORUtils.MOD_ID, registerName)).setUnlocalizedName(registerName).setCreativeTab(EnderORUtils.MOD_TAB));
    
    if (block instanceof IHasRecipe) {
      ((IHasRecipe) block).makeRecipe();
    }
  }
  
  public static void addModel(Block block, int meta, String blockIn) {
    MODEL_MAP.put(new Pair<>(block, meta), blockIn);
  }
  
  public static void registerModel() {
    MODEL_MAP.forEach((block, blockIn) -> ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block.getKey()), block.getValue(), new ModelResourceLocation(Objects.requireNonNull(block.getKey().getRegistryName()), blockIn)));
  }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent
    public static void onEvent(RegistryEvent.@NotNull Register<Block> event) {
      IForgeRegistry<Block> registry = event.getRegistry();
      blockList.forEach(block -> {
        EnderORUtils.log(Level.WARN, String.format("Register %s item", block.getRegistryName()));
        registry.register(block);
      });
    }
    
    @SubscribeEvent
    public static void onEvent(ModelRegistryEvent event) {
      EnderORUtils.proxy.registerModel();
    }
  }
}
