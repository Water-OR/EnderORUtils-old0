package io.github.enderor;

import io.github.enderor.items.EnderORCreativeTab;
import io.github.enderor.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(
        modid = EnderORUtils.MOD_ID,
        name = EnderORUtils.MOD_NAME,
        version = EnderORUtils.MOD_VERSION,
        dependencies = EnderORUtils.MOD_DEPENDENCIES,
        acceptedMinecraftVersions = EnderORUtils.MOD_ACCEPT_VERSION
)
public class EnderORUtils {
  public final static String MOD_ID = "enderor";
  public final static String MOD_NAME = "Ender OR Utils";
  public final static String MOD_VERSION = "1.0";
  public final static String MOD_DEPENDENCIES = "required-after:baubles;";
  public final static String MOD_ACCEPT_VERSION = "[1.12.2]";
  public static final CreativeTabs MOD_TAB = new EnderORCreativeTab();
  
  @SidedProxy(serverSide = "io.github.enderor.proxy.CommonProxy", clientSide = "io.github.enderor.proxy.ClientProxy")
  public static CommonProxy proxy;
  
  public static final EnderORUtils instance = new EnderORUtils();
  
  public EnderORUtils() {}
  
  private static Logger logger;
  
  @Mod.EventHandler
  public static void onPreInit(@NotNull FMLPreInitializationEvent event) {
    logger = event.getModLog();
    log(Level.WARN, "=========Welcome to EnderOR=========");
    log(Level.WARN, "|                                  |");
    log(Level.WARN, "|  [][][][]     ()()     {}{}{}    |");
    log(Level.WARN, "|  []         ()    ()   {}    {}  |");
    log(Level.WARN, "|  [][][][]   ()    ()   {}{}{}    |");
    log(Level.WARN, "|  []         ()    ()   {}    {}  |");
    log(Level.WARN, "|  [][][][]     ()()     {}    {}  |");
    log(Level.WARN, "|                                  |");
    log(Level.WARN, "====================================");
  }
  
  @Mod.EventHandler
  public static void onInit(FMLInitializationEvent event) {
  }
  
  @Mod.EventHandler
  public static void onPostInit(FMLPostInitializationEvent event) {
  }
  
  public static void log(Level level, Object message) {
    logger.log(level, message);
  }
  
  public static void log(Level level, String format, Object... args) {
    logger.log(level, String.format(format, args));
  }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent
    public static void onEvent(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityPlayer) {
        event.getEntity().sendMessage(new TextComponentString(String.format("[%s] Hi!", MOD_NAME)));
      }
    }
  }
}
