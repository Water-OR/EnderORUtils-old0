package io.github.enderor.gui;

import io.github.enderor.EnderORUtils;
import io.github.enderor.blocks.tileEntities.TileEntityEnchantMover;
import io.github.enderor.gui.basic.EnderORGuiButton;
import io.github.enderor.gui.basic.EnderORGuiString;
import io.github.enderor.items.ItemEnchantedPaper;
import io.github.enderor.utils.EnchantDescriptionHelper;
import javafx.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly (Side.CLIENT)
public class GuiEnchantMover extends GuiContainer {
  protected static final ResourceLocation ENCHANT_MOVER_GUI = new ResourceLocation(EnderORUtils.MOD_ID, "textures/gui/container/enchant_mover.png");
  protected final TileEntityEnchantMover tileEnchantMover;
  public EnderORGuiString displayName1, displayName2;
  public final ButtonGetAll buttonGetAll;
  public final ButtonTakeALL buttonTakeAll;
  public final Map<Enchantment, ButtonGet> buttonGets = new HashMap<>();
  public final Map<Enchantment, ButtonTake> buttonTakes = new HashMap<>();
  
  protected int buttonGetsMaxHeight;
  protected int buttonTakesMaxHeight;
  protected int buttonGetsScroll = 0;
  protected int buttonTakesScroll = 0;
  
  public final int buttonGetsX = 8;
  public final int buttonGetsY = 8;
  public final int buttonGetsWidth = 114;
  public final int buttonGetsHeight = 120;
  
  public final int buttonTakesX = 134;
  public final int buttonTakesY = 8;
  public final int buttonTakesWidth = 114;
  public final int buttonTakesHeight = 120;
  
  public GuiEnchantMover(InventoryPlayer inventoryPlayer, TileEntityEnchantMover tileEnchantMover) {
    super(new ContainerEnchantMover(inventoryPlayer, tileEnchantMover));
    this.tileEnchantMover = tileEnchantMover;
    this.xSize = 256;
    this.ySize = 235;
    this.buttonGetAll = new ButtonGetAll(0, 231, 155);
    this.buttonTakeAll = new ButtonTakeALL(1, 7, 155);
    
    ItemStack stack0 = this.tileEnchantMover.getStackInSlot(0);
    ItemStack stack1 = this.tileEnchantMover.getStackInSlot(1);
    
    Map<Enchantment, Integer> enchants0 = stack0.getItem() instanceof ItemEnchantedPaper ? ItemEnchantedPaper.EnchantHelper.getEnchants(stack0) : EnchantmentHelper.getEnchantments(stack0);
    Map<Enchantment, Integer> enchants1 = stack1.getItem() instanceof ItemEnchantedPaper ? ItemEnchantedPaper.EnchantHelper.getEnchants(stack1) : EnchantmentHelper.getEnchantments(stack1);
    
    int[] yOffset = new int[2];
    
    enchants0.forEach((enchantment, level) -> {
      buttonGets.put(enchantment, new ButtonGet(0, this.buttonGetsX + this.buttonGetsWidth - 18, this.buttonGetsY + yOffset[0], enchantment, level));
      yOffset[0] += 18;
    });
    
    enchants1.forEach((enchantment, level) -> {
      buttonTakes.put(enchantment, new ButtonTake(1, this.buttonTakesX + this.buttonTakesWidth - 18, this.buttonTakesY + yOffset[1], enchantment, level));
      yOffset[1] += 18;
    });
    
    this.buttonGetsMaxHeight = Math.min(this.buttonGetsHeight, yOffset[0]);
    this.buttonTakesMaxHeight = Math.min(this.buttonTakesHeight, yOffset[1]);
  }
  
  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    this.buttonGetAll.drawButton(this.mc, mouseX, mouseY, partialTicks);
    this.buttonTakeAll.drawButton(this.mc, mouseX, mouseY, partialTicks);
    
    List<String> hoveringTest = new ArrayList<>();
    
    GL11.glPushMatrix();
    GL11.glEnable(GL11.GL_SCISSOR_TEST);
    drawButtonGets(mouseX, mouseY, partialTicks, hoveringTest);
    drawButtonTakes(mouseX, mouseY, partialTicks, hoveringTest);
    GL11.glDisable(GL11.GL_SCISSOR_TEST);
    GL11.glPopMatrix();
    
    int newScroll = Mouse.getDWheel();
    newScroll = Integer.compare(newScroll, 0);
    if (isShiftKeyDown()) { newScroll *= 7; }
    if (this.isHoveringButtonTakes(mouseX, mouseY)) {
      this.buttonGetsScroll += newScroll;
      this.buttonGetsScroll = Math.max(0, Math.min(this.buttonGetsScroll, this.buttonGetsMaxHeight));
    } else if (this.isHoveringButtonGets(mouseX, mouseY)) {
      this.buttonTakesScroll += newScroll;
      this.buttonTakesScroll = Math.max(0, Math.min(this.buttonTakesScroll, this.buttonTakesMaxHeight));
    } else {
      hoveringTest.clear();
    }
    this.drawHoveringText(hoveringTest, mouseX, mouseY);
  }
  
  protected void drawButtonGets(int mouseX, int mouseY, float partialTicks, List<String> hoveringTest) {
    ButtonInfo buttonInfo = new ButtonInfo(0, 0, 0, "");
    
    GL11.glScissor(this.buttonGetsX, this.buttonGetsY, this.buttonGetsWidth, this.buttonGetsHeight);
    GL11.glTranslated(0, -this.buttonGetsScroll, 0);
    
    buttonInfo.x = this.buttonGetsX;
    buttonInfo.y = this.buttonGetsY;
    
    this.mc.getTextureManager().bindTexture(ENCHANT_MOVER_GUI);
    int finalMouseY1 = mouseY + this.buttonGetsScroll;
    
    this.buttonGets.forEach((enchantment, button) -> {
      buttonInfo.displayString = I18n.format(enchantment.getName());
      buttonInfo.drawButton(this.mc, mouseX, finalMouseY1, partialTicks);
      buttonInfo.y += button.height;
      
      if (buttonInfo.isMouseOver()) {
        hoveringTest.clear();
        hoveringTest.add("".concat((button.level < enchantment.getMaxLevel() ? TextFormatting.GRAY :
                                    button.level > enchantment.getMaxLevel() ? TextFormatting.GOLD : TextFormatting.BLUE
                                   ).toString())
                           .concat(I18n.format(enchantment.getName()))
                           .concat(" ")
                           .concat(I18n.format("enchantment.level." + button.level))
                           .concat("\n")
                           .concat(EnchantDescriptionHelper.getEnchantDescription(enchantment))
        );
      }
    });
    
    this.buttonGets.forEach((enchantment, button) -> {
      button.drawButton(this.mc, mouseX, finalMouseY1, partialTicks);
      
      if (buttonInfo.isMouseOver()) {
        hoveringTest.clear();
        hoveringTest.add("".concat((button.level < enchantment.getMaxLevel() ? TextFormatting.GRAY :
                                    button.level > enchantment.getMaxLevel() ? TextFormatting.GOLD : TextFormatting.BLUE
                                   ).toString())
                           .concat(I18n.format(enchantment.getName()))
                           .concat(" ")
                           .concat(I18n.format("enchantment.level." + button.level))
                           .concat("\n")
                           .concat(EnchantDescriptionHelper.getEnchantDescription(enchantment))
        );
      }
    });
    
    GL11.glLoadIdentity();
  }
  
  protected void drawButtonTakes(int mouseX, int mouseY, float partialTicks, List<String> hoveringTest) {
    ButtonInfo buttonInfo = new ButtonInfo(0, 0, 0, "");
    
    GL11.glScissor(this.buttonTakesX, this.buttonTakesY, this.buttonTakesWidth, this.buttonTakesHeight);
    GL11.glTranslated(0, -this.buttonTakesScroll, 0);
    
    buttonInfo.x = this.buttonTakesX;
    buttonInfo.y = this.buttonTakesY;
    
    this.mc.getTextureManager().bindTexture(ENCHANT_MOVER_GUI);
    int finalMouseY2 = mouseY + this.buttonTakesScroll;
    
    this.buttonTakes.forEach((enchantment, button) -> {
      buttonInfo.displayString = I18n.format(enchantment.getName());
      buttonInfo.drawButton(this.mc, mouseX, finalMouseY2, partialTicks);
      buttonInfo.y += button.height;
      
      if (buttonInfo.isMouseOver()) {
        hoveringTest.clear();
        hoveringTest.add("".concat((button.level < enchantment.getMaxLevel() ? TextFormatting.GRAY :
                                    button.level > enchantment.getMaxLevel() ? TextFormatting.GOLD : TextFormatting.BLUE
                                   ).toString())
                           .concat(I18n.format(enchantment.getName()))
                           .concat(" ")
                           .concat(I18n.format("enchantment.level." + button.level))
                           .concat("\n")
                           .concat(EnchantDescriptionHelper.getEnchantDescription(enchantment))
        );
      }
    });
    
    this.buttonTakes.forEach((enchantment, button) -> {
      button.drawButton(this.mc, mouseX, finalMouseY2, partialTicks);
      
      ResourceLocation registerName = enchantment.getRegistryName();
      
      if (buttonInfo.isMouseOver()) {
        hoveringTest.clear();
        hoveringTest.add("".concat((button.level < enchantment.getMaxLevel() ? TextFormatting.GRAY :
                                    button.level > enchantment.getMaxLevel() ? TextFormatting.GOLD : TextFormatting.BLUE
                                   ).toString())
                           .concat(I18n.format(enchantment.getName()))
                           .concat(" ")
                           .concat(I18n.format("enchantment.level." + button.level))
                           .concat("\n")
                           .concat(EnchantDescriptionHelper.getEnchantDescription(enchantment))
        );
      }
    });
    
    GL11.glLoadIdentity();
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.mc.getTextureManager().bindTexture(ENCHANT_MOVER_GUI);
    int offsetX = (this.width - this.xSize) / 2;
    int offsetY = (this.height - this.ySize) / 2;
    this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
  }
  
  public static class ButtonBasic extends EnderORGuiButton {
    public ButtonBasic(int buttonId, int x, int y, int iconX, int iconY) {
      super(buttonId, x, y, 96, 238, iconX, iconY, 18, 18, ENCHANT_MOVER_GUI, ENCHANT_MOVER_GUI);
    }
    
    @Override
    public Pair<Integer, Integer> getBackgroundPosition() {
      Pair<Integer, Integer> result = super.getBackgroundPosition();
      if (this.selected) { result = new Pair<>(result.getKey(), result.getValue() + this.width); }
      return result;
    }
  }
  
  public boolean isHoveringButtonGets(int mouseX, int mouseY) {
    return
      this.buttonGetsX < mouseX && mouseX <= this.buttonGetsX + this.buttonGetsWidth &&
      this.buttonGetsY < mouseY && mouseY <= this.buttonGetsY + this.buttonGetsHeight;
  }
  
  public boolean isHoveringButtonTakes(int mouseX, int mouseY) {
    return
      this.buttonTakesX < mouseX && mouseX <= this.buttonTakesX + this.buttonTakesWidth &&
      this.buttonTakesY < mouseY && mouseY <= this.buttonTakesY + this.buttonTakesHeight;
  }
  
  public static class ButtonInfo extends EnderORGuiButton {
    public ButtonInfo(int buttonId, int x, int y, String buttonText) {
      super(buttonId, x, y, 0, 238, 96, 18, ENCHANT_MOVER_GUI, buttonText);
    }
    
    @Override
    public void drawText(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
      int drawX = this.x + 5;
      int drawY = this.y + (this.height - fontRenderer.FONT_HEIGHT) / 2;
      this.drawString(Minecraft.getMinecraft().fontRenderer, this.displayString, drawX, drawY, this.getTextColor());
    }
  }
  
  public class ButtonTake extends ButtonBasic {
    public Enchantment enchant;
    public int level;
    
    public ButtonTake(int buttonId, int x, int y, Enchantment enchant, int level) {
      super(buttonId, x, y, 132, 238);
      this.enchant = enchant;
      this.level = level;
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
      if (this.selected) {
        this.takeEnchant();
      }
      super.mouseReleased(mouseX, mouseY);
    }
    
    protected void takeEnchant() {
      ItemStack stack0 = GuiEnchantMover.this.tileEnchantMover.getStackInSlot(0);
      if (stack0.getItem() instanceof ItemEnchantedPaper) {
        ItemEnchantedPaper.EnchantHelper.deleteEnchant(stack0, enchant);
        return;
      }
      ItemStack stack1 = GuiEnchantMover.this.tileEnchantMover.getStackInSlot(1);
      Map<Enchantment, Integer> rawEnchants = stack1.getItem() instanceof ItemEnchantedPaper ? ItemEnchantedPaper.EnchantHelper.getEnchants(stack1) : EnchantmentHelper.getEnchantments(stack1);
      EnchantmentHelper.getEnchantments(stack1);
      rawEnchants.remove(this.enchant);
      EnchantmentHelper.setEnchantments(rawEnchants, stack1);
    }
  }
  
  public class ButtonGet extends ButtonBasic {
    public Enchantment enchant;
    public int level;
    
    public ButtonGet(int buttonId, int x, int y, Enchantment enchant, int level) {
      super(buttonId, x, y, 150, 238);
      this.enchant = enchant;
      this.level = level;
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
      if (this.selected) {
        this.getEnchant();
      }
      super.mouseReleased(mouseX, mouseY);
    }
    
    protected void getEnchant() {
      ItemStack stack0 = GuiEnchantMover.this.tileEnchantMover.getStackInSlot(0);
      if (stack0.getItem() instanceof ItemEnchantedPaper) {
        ItemEnchantedPaper.EnchantHelper.addEnchant(stack0, this.enchant, this.level);
      }
      ItemStack stack1 = GuiEnchantMover.this.tileEnchantMover.getStackInSlot(1);
      Map<Enchantment, Integer> rawEnchants = stack1.getItem() instanceof ItemEnchantedPaper ? ItemEnchantedPaper.EnchantHelper.getEnchants(stack1) : EnchantmentHelper.getEnchantments(stack1);
      if (!rawEnchants.containsKey(this.enchant)) {
        rawEnchants.put(this.enchant, this.level);
      } else if (rawEnchants.get(this.enchant) < this.level) {
        rawEnchants.replace(this.enchant, this.level);
      }
      EnchantmentHelper.setEnchantments(rawEnchants, stack1);
    }
  }
  
  public class ButtonGetAll extends ButtonBasic {
    public ButtonGetAll(int buttonId, int x, int y) {
      super(buttonId, x, y, 168, 238);
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
      if (this.selected) {
        this.getEnchants();
      }
      super.mouseReleased(mouseX, mouseY);
    }
    
    protected void getEnchants() {
      ItemStack stack0 = GuiEnchantMover.this.tileEnchantMover.getStackInSlot(0);
      ItemStack stack1 = GuiEnchantMover.this.tileEnchantMover.getStackInSlot(1);
      Map<Enchantment, Integer> enchants = stack1.getItem() instanceof ItemEnchantedPaper ? ItemEnchantedPaper.EnchantHelper.getEnchants(stack1) : EnchantmentHelper.getEnchantments(stack1);
      if (stack0.getItem() instanceof ItemEnchantedPaper) {
        ItemEnchantedPaper.EnchantHelper.addEnchants(stack0, enchants);
        return;
      }
      Map<Enchantment, Integer> rawEnchants = EnchantmentHelper.getEnchantments(stack0);
      rawEnchants.forEach((enchantment, integer) -> {
        if (!enchants.containsKey(enchantment)) {
          enchants.put(enchantment, integer);
        } else if (enchants.get(enchantment) < integer) {
          enchants.replace(enchantment, integer);
        }
      });
    }
  }
  
  public class ButtonTakeALL extends ButtonBasic {
    public ButtonTakeALL(int buttonId, int x, int y) {
      super(buttonId, x, y, 186, 238);
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
      if (this.selected) {
        this.takeEnchants();
      }
      super.mouseReleased(mouseX, mouseY);
    }
    
    protected void takeEnchants() {
      ItemStack stack = GuiEnchantMover.this.tileEnchantMover.getStackInSlot(0);
      if (stack.getItem() instanceof ItemEnchantedPaper) {
        ItemEnchantedPaper.EnchantHelper.resetEnchant(stack);
      } else {
        EnchantmentHelper.setEnchantments(new HashMap<>(), stack);
      }
    }
  }
}
