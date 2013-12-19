package modderspolaroid2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == Config.guiId) {
			GuiPolaroid.index = Config.start;
			GuiPolaroid.tick = 0;
			GuiPolaroid.dicIndex = 0;
			GuiPolaroid.show = true;
			GuiPolaroid.date = new java.util.Date();
			return new GuiPolaroid(player.inventory, world, x, y, z);
		}
		return null;
	}

	public void init() {

	}

}
