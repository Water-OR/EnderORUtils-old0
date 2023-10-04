package io.github.enderor.mixins;

import io.github.enderor.EnderORUtils;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class MixinLoader implements IFMLLoadingPlugin {
  public MixinLoader() {
    super();
    try {
      MixinBootstrap.init();
      Mixins.addConfiguration("mixins." + EnderORUtils.MOD_ID + ".json");
      MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
    } catch (Exception e) {
      EnderORUtils.log(Level.ERROR, "Ender OR Utils has a error on loading mixin!");
      throw e;
    }
  }
  
  @Override
  public String[] getASMTransformerClass() {
    return new String[0];
  }
  
  @Override
  public String getModContainerClass() {
    return null;
  }
  
  @Nullable
  @Override
  public String getSetupClass() {
    return null;
  }
  
  @Override
  public void injectData(Map<String, Object> data) {
  
  }
  
  @Override
  public String getAccessTransformerClass() {
    return null;
  }
}
