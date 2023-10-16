package io.github.enderor.gui.basic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class EnderORGuiString extends EnderORGuiBasic {
  public int scroll, maxWidth;
  public GuiTextField textField;
  
  public boolean focused;
  
  public EnderORGuiString(int id, int x, int y, int width, int height) {
    super(id, x, y, width, height);
    this.textField = new GuiTextField(id, Minecraft.getMinecraft().fontRenderer, x, y, width, height);
    this.textField.setEnableBackgroundDrawing(false);
    this.textField.setMaxStringLength(32767);
    this.textField.setTextColor(-1);
  }
  
  @Override
  public void draw(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    this.maxWidth = Math.min(mc.fontRenderer.getStringWidth(this.textField.getText()) + 10, this.width);
    
    GL11.glPushMatrix();
    GL11.glEnable(GL11.GL_SCISSOR_TEST);
    GL11.glScissor(this.x, this.y, this.width, this.height);
    GL11.glTranslated(0, -this.scroll, 0);
    this.textField.drawTextBox();
    GL11.glLoadIdentity();
    GL11.glDisable(GL11.GL_SCISSOR_TEST);
    GL11.glPopMatrix();
  }
  
  @Override
  public void mouseClicked(Minecraft mc, int mouseX, int mouseY, int button) {
    if (this.mousePressed(mc, mouseX, mouseY)) {
      this.focused = true;
    }
    
    this.textField.mouseClicked(mouseX, mouseY + this.scroll, button);
  }
  
  @Override
  public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
  }
  
  @Override
  public void mouseReleased(int mouseX, int mouseY, int button) {
  }
  
  @Override
  public void drawButtonForegroundLayer(int mouseX, int mouseY) { }
  
  @Override
  public void mouseScrolled(int mouseX, int mouseY, int scroll) {
    this.scroll = this.correctScroll(this.scroll + scroll);
  }
  
  @Override
  public void keyTyped(char ch, int key) {
    this.textField.textboxKeyTyped(ch, key);
  }
  
  public int correctScroll(int scroll) {
    if (scroll < 0) {
      scroll = 0;
    } else if (scroll > this.maxWidth) {
      scroll = this.maxWidth;
    }
    return scroll;
  }
}
