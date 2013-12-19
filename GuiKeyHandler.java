package modderspolaroid2;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class GuiKeyHandler extends KeyHandler {

	static KeyBinding nextKeyBinding = new KeyBinding("Modder's Polaroid 2 Next", Keyboard.KEY_RIGHT);
	static KeyBinding prevKeyBinding = new KeyBinding("Modder's Polaroid 2 Prev", Keyboard.KEY_LEFT);
	static KeyBinding shotKeyBinding = new KeyBinding("Modder's Polaroid 2 Shot", Keyboard.KEY_UP);
	static KeyBinding autoKeyBinding = new KeyBinding("Modder's Polaroid 2 Auto", Keyboard.KEY_P);
	static KeyBinding showKeyBinding = new KeyBinding("Modder's Polaroid 2 Show/Hide", Keyboard.KEY_DOWN);

	public GuiKeyHandler() {
		super(new KeyBinding[] { nextKeyBinding, prevKeyBinding, shotKeyBinding, autoKeyBinding, showKeyBinding }, new boolean[] { true, true, false, false, false });
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
		if(guiscreen != null && guiscreen instanceof GuiPolaroid) {
			if(tickEnd) {
				if(kb.keyCode == nextKeyBinding.keyCode) {
					GuiPolaroid.next();
				}
				if(kb.keyCode == prevKeyBinding.keyCode) {
					GuiPolaroid.prev();
				}
				if(kb.keyCode == shotKeyBinding.keyCode) {
					GuiPolaroid.saveScreenshot();
				}
				if(kb.keyCode == autoKeyBinding.keyCode) {
					GuiPolaroid.auto = !GuiPolaroid.auto;
				}
				if(kb.keyCode == showKeyBinding.keyCode) {
					GuiPolaroid.show = !GuiPolaroid.show;
				}
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
