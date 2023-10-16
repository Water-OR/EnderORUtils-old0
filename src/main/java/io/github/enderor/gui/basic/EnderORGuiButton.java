package io.github.enderor.gui.basic;

import javafx.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EnderORGuiButton extends GuiButton {
  public final int iconX, iconY, backgroundX, backgroundY;
  public final ResourceLocation icon, background;
  public final boolean hasIcon;
  
  protected boolean selected = false;
  
  protected EnderORGuiButton(int buttonId, int x, int y, int backgroundX, int backgroundY, int iconX, int iconY, int widthIn, int heightIn, ResourceLocation background, ResourceLocation icon, String buttonText) {
    super(buttonId, x, y, widthIn, heightIn, buttonText);
    this.iconX = iconX;
    this.iconY = iconY;
    this.icon = icon;
    this.backgroundX = backgroundX;
    this.backgroundY = backgroundY;
    this.background = background;
    this.hasIcon = (icon == null);
  }
  
  public EnderORGuiButton(int buttonId, int x, int y, int backgroundX, int backgroundY, int widthIn, int heightIn, ResourceLocation background, String buttonText) {
    this(buttonId, x, y, backgroundX, backgroundY, 0, 0, widthIn, heightIn, background, null, buttonText);
  }
  
  public EnderORGuiButton(int buttonId, int x, int y, int backgroundX, int backgroundY, int iconX, int iconY, int widthIn, int heightIn, ResourceLocation background, ResourceLocation icon) {
    this(buttonId, x, y, backgroundX, backgroundY, iconX, iconY, widthIn, heightIn, background, icon, "");
  }
  
  public EnderORGuiButton(int buttonId, int x, int y, int backgroundX, int backgroundY, int iconX, int iconY, int widthIn, int heightIn, ResourceLocation texture) {
    this(buttonId, x, y, backgroundX, backgroundY, iconX, iconY, widthIn, heightIn, texture, texture);
  }
  
  public EnderORGuiButton(int buttonId, int x, int y, int textureX, int textureY, int widthIn, int heightIn, ResourceLocation texture) {
    this(buttonId, x, y, textureX, textureY, textureX, textureY, widthIn, heightIn, texture);
  }
  
  @Override
  public void drawButton(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    if (!this.visible) {
      return;
    }
    mc.getTextureManager().bindTexture(this.background);
    Pair<Integer, Integer> drawPosition = getBackgroundPosition();
    int drawX = drawPosition.getKey(), drawY = drawPosition.getValue();
    
    this.drawTexturedModalRect(this.x, this.y, drawX, drawY, this.width, this.height);
    
    this.hovered = mousePressed(mc, mouseX, mouseY);
    
    if (this.hasIcon) {
      mc.getTextureManager().bindTexture(this.icon);
      drawPosition = getIconPosition();
      drawX = drawPosition.getKey();
      drawY = drawPosition.getValue();
      
      this.drawTexturedModalRect(this.x, this.y, drawX, drawY, this.width, this.height);
    } else {
      this.drawText(mc, mouseX, mouseY, partialTicks);
    }
  }
  
  /**
   * <p>
   * Get the position of button background
   * <br>
   * <br>
   * Some button has different texture on different stat. And Their background textures are at different position.
   * </p>
   *
   * @return The background position of the button
   */
  public Pair<Integer, Integer> getBackgroundPosition() {
    return new Pair<>(this.backgroundX, this.backgroundY);
  }
  
  /**
   * <p>
   * Get the position of button icon.
   * <br>
   * <br>
   * Some button has different texture on different stat. And Their icon textures are at different position.
   * </p>
   *
   * @return The icon position of the button
   */
  public Pair<Integer, Integer> getIconPosition() {
    return new Pair<>(this.iconX, this.iconY);
  }
  
  
  /**
   * <p>
   * Get the color of button text
   * <br>
   * <br>
   * Some button has different color on different stat.
   * </p>
   *
   * @return The color of the text
   */
  public int getTextColor() {
    if (this.packedFGColour != 0) { return this.packedFGColour; }
    if (!this.enabled) { return 10526880; }
    if (this.hovered) { return 16777120; }
    return 14737632;
  }
  
  public boolean isSelected() {
    return selected;
  }
  
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
  
  public void drawText(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    int drawX = this.x + (this.width) / 2;
    int drawY = this.y + (this.height - fontRenderer.FONT_HEIGHT) / 2;
    this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, this.displayString, drawX, drawY, this.getTextColor());
  }
  
  @Override
  protected void mouseDragged(@NotNull Minecraft mc, int mouseX, int mouseY) {
    super.mouseDragged(mc, mouseX, mouseY);
    this.selected = mousePressed(mc, mouseX, mouseY);
  }
  
  @Override
  public void mouseReleased(int mouseX, int mouseY) {
    super.mouseReleased(mouseX, mouseY);
    this.selected = false;
  }
}
