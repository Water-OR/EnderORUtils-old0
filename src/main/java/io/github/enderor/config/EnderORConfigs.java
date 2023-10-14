package io.github.enderor.config;

import io.github.enderor.EnderORUtils;
import net.minecraftforge.common.config.Config;

@Config(modid = EnderORUtils.MOD_ID, name = EnderORUtils.MOD_NAME, category = "default")
@Config.LangKey("config." + EnderORUtils.MOD_ID + ".default")
public class EnderORConfigs {
  @Config.Comment("Effect Length while wearing effect ring, too short may cause problem")
  @Config.Name("Effect Ring effect Length")
  @Config.LangKey("config." + EnderORUtils.MOD_ID + ".default.effect_length")
  @Config.RangeInt(min = 1)
  public static int EFFECT_LENGTH = 2000;
}
