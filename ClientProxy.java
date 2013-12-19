package modderspolaroid2;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	public void init() {
		KeyBindingRegistry.registerKeyBinding(new ShotKeyHandler());
		KeyBindingRegistry.registerKeyBinding(new GuiKeyHandler());
	}
}
