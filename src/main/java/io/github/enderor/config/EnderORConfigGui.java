package io.github.enderor.config;

import io.github.enderor.EnderORUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public class EnderORConfigGui implements IModGuiFactory {
  @Override
  public void initialize(Minecraft minecraftInstance) {
  
  }
  
  @Override
  public boolean hasConfigGui() {
    return true;
  }
  
  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {
    return new GuiConfig(
            parentScreen,
            ConfigElement.from(EnderORConfigs.class).getChildElements(),
            EnderORUtils.MOD_ID,
            false,
            false,
            "Config of " + EnderORUtils.MOD_NAME,
            ""
    );
  }
  
  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
    return Collections.emptySet();
  }
  
  @Mod.EventBusSubscriber
  public static class Events {
    @SubscribeEvent
    public static void onEvent(ConfigChangedEvent.@NotNull OnConfigChangedEvent event) {
      if (event.getModID().equals(EnderORUtils.MOD_ID)) {
        ConfigManager.sync(EnderORUtils.MOD_ID, Config.Type.INSTANCE);
      }
    }
  }
}
