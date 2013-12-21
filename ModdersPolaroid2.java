package modderspolaroid2;

import java.util.logging.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = "ModdersPolaroid2", name = "ModdersPolaroid2", version = "1.6.4.6")
public class ModdersPolaroid2 {
	@SidedProxy(clientSide = "modderspolaroid2.ClientProxy", serverSide = "modderspolaroid2.CommonProxy")
	public static CommonProxy proxy;

	@Instance("ModdersPolaroid2")
	public static ModdersPolaroid2 instance;

	public static Logger logger = Logger.getLogger("Minecraft");

	public static Config config = new Config();

	@Mod.Init
	public void load(FMLInitializationEvent event) {
		proxy.init();
		NetworkRegistry.instance().registerGuiHandler(instance, proxy);
	}

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		config.load(event.getSuggestedConfigurationFile());
	}
}