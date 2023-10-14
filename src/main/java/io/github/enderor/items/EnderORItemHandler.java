package io.github.enderor.items;

import io.github.enderor.EnderORUtils;
import io.github.enderor.items.baubles.ring.ItemPotionRing;
import io.github.enderor.recipes.IHasRecipe;
import javafx.util.Pair;
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

public class EnderORItemHandler {
  
  private static final List<Item> itemList = new ArrayList<>();
  
  private static final HashMap<Pair<Item, Integer>, String> MODEL_MAP = new HashMap<>();
  
  public static void addItem(@NotNull Item item, String registerName) {
    itemList.add(item.setRegistryName(new ResourceLocation(EnderORUtils.MOD_ID, registerName)).setUnlocalizedName(registerName).setCreativeTab(EnderORUtils.MOD_TAB));
    
    if (item instanceof IHasRecipe) {
      ((IHasRecipe) item).makeRecipe();
    }
  }
  
  public static void addModel(Item item, int meta, String itemIn) {
    MODEL_MAP.put(new Pair<>(item, meta), itemIn);
  }
  
  public static void registerModel() {
    MODEL_MAP.forEach((item, itemIn) -> ModelLoader.setCustomModelResourceLocation(item.getKey(), item.getValue(), new ModelResourceLocation(Objects.requireNonNull(item.getKey().getRegistryName()), itemIn)));
  }
  
  static {
    addItem(ItemMod.INSTANCE, "item_mod");
    addItem(ItemPotionRing.INSTANCE, "potion_ring");
    addItem(ItemEnchantedPaper.INSTANCE, "enchanted_paper");
  }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent
    public static void onEvent(RegistryEvent.@NotNull Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();
      itemList.forEach(item -> {
        EnderORUtils.log(Level.WARN, String.format("Register %s item", item.getRegistryName()));
        registry.register(item);
      });
    }
    
    @SubscribeEvent
    public static void onEvent(ModelRegistryEvent event) {
      EnderORUtils.proxy.registerModel();
    }
  }
}
