package io.github.enderor.gui.basic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.init.SoundEvents;
import org.jetbrains.annotations.NotNull;

public abstract class EnderORGuiBasic {
  public int x, y, width, height, id;
  public boolean enabled, visible;
  
  public EnderORGuiBasic(int id, int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
  
  public abstract void draw(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks);
  public abstract void mouseClicked(Minecraft mc, int mouseX, int mouseY, int button);
  public abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);
  public abstract void mouseReleased(int mouseX, int mouseY, int button);
  public abstract void drawButtonForegroundLayer(int mouseX, int mouseY);
  public abstract void mouseScrolled(int mouseX, int mouseY, int scroll);
  public abstract void keyTyped(char ch, int key);
  
  public void playPressSound(@NotNull SoundHandler soundHandlerIn) {
    soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
  }
  
  public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) { return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height; }
}
