package io.github.enderor.proxy;

import io.github.enderor.items.EnderORItemHandler;

public class ClientProxy extends CommonProxy{
  @Override
  public void registerModel() {
    EnderORItemHandler.registerModel();
    super.registerModel();
  }
}
