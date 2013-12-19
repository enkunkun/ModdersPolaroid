package modderspolaroid2;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class ShotKeyHandler extends KeyHandler {

	static KeyBinding shotKeyBinding = new KeyBinding("Modder's Polaroid 2", Keyboard.KEY_P);

	public ShotKeyHandler() {
		super(new KeyBinding[] { shotKeyBinding }, new boolean[] { false });
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if(tickEnd) {
			Minecraft mc = Minecraft.getMinecraft();
			if(mc.currentScreen == null && mc.ingameGUI.getChatGUI().getChatOpen() == false) {
				mc.thePlayer.openGui(ModdersPolaroid2.instance, Config.guiId, mc.theWorld, (int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ);
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
