package modderspolaroid2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class GuiPolaroid extends GuiContainer {
	protected static RenderItem itemRenderer = new RenderItem();
	private int imageWidth = 175;
	private int imageHeight = 165;
	static boolean auto = false;

	static String[] heightName;
	static String[] widthName;

	public static int index = 0;
	public static int tick = 0;
	public static int dicIndex = 0;
	public static Date date;
	public static boolean show;

	public GuiPolaroid(InventoryPlayer inventory, World par2World, int par3, int par4, int par5) {
		super(new ContainerWorkbench(inventory, par2World, par3, par4, par5));
		heightName = ObfuscationReflectionHelper.remapFieldNames(ShapedOreRecipe.class.getName(), "height");
		widthName = ObfuscationReflectionHelper.remapFieldNames(ShapedOreRecipe.class.getName(), "width");
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.crafting"), 28, 6, 4210752);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

		if(index < 0 || index  >= CraftingManager.getInstance().getRecipeList().size()) {
			return;
		}

		Object obj = CraftingManager.getInstance().getRecipeList().get(index);

		for(int i = 0; i < 11; ++i) {
			((Slot)this.inventorySlots.inventorySlots.get(i)).putStack(null);
		}
		boolean dicEnd = true;
		IRecipe recipe = (IRecipe)obj;
		if(obj instanceof ShapedRecipes) {
			drawShapedRecipes((ShapedRecipes) obj);
			if(auto) {
				saveScreenshot(index, recipe.getRecipeOutput());
			}
		}
		else if(obj instanceof ShapelessRecipes) {
			drawShapelessRecipes((ShapelessRecipes) obj);
			if(auto) {
				saveScreenshot(index, recipe.getRecipeOutput());
			}
		}
		else if(obj instanceof ShapedOreRecipe) {
			dicEnd = drawShapedOreRecipe((ShapedOreRecipe) obj);
			if(auto) {
				saveScreenshot(index, recipe.getRecipeOutput());
			}
		}
		else if(obj instanceof ShapelessOreRecipe) {
			dicEnd = drawShapelessOreRecipe((ShapelessOreRecipe) obj);
			if(auto) {
				saveScreenshot(index, recipe.getRecipeOutput());
			}
		}

		if(auto) {
			if(tick++ > Config.interval) {
				if(dicEnd) {
					if(index < CraftingManager.getInstance().getRecipeList().size() - 1) {
						index++;
						dicIndex = 0;
						System.out.println(index);
					}
				}
				else {
					dicIndex++;
				}
				tick = 0;
			}
		}
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
	}

	public void showInfomation() {
		IRecipe recipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(index);
		String s = "";

		int y = 0;
		//this.zLevel = 0.0F;

		s = "Key Binding";
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
		y += 10;

		s = "Next:" + Keyboard.getKeyName(GuiKeyHandler.nextKeyBinding.keyCode);
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
		y += 10;

		s = "Prev:" + Keyboard.getKeyName(GuiKeyHandler.prevKeyBinding.keyCode);
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
		y += 10;

		s = "Shot:" + Keyboard.getKeyName(GuiKeyHandler.shotKeyBinding.keyCode);
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
		y += 10;

		s = "Auto:" + Keyboard.getKeyName(GuiKeyHandler.autoKeyBinding.keyCode);
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
		y += 10;

		s = "Show/Hide:" + Keyboard.getKeyName(GuiKeyHandler.showKeyBinding.keyCode);
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
		y += 20;


		s = "Type:" + recipe.getClass().getSimpleName();
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
		y += 10;
		s = "Recipe Output:";
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
		y += 10;
		ItemStack output = recipe.getRecipeOutput();
		s = ((output == null) ? "----" : output.getDisplayName());
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
		y += 10;

		s = "Recipe Input:";
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
		y += 10;
		if(recipe instanceof ShapedRecipes) {
			ItemStack[] input = ((ShapedRecipes)recipe).recipeItems;
			for(int i = 0; i < input.length; ++i) {
				s = String.valueOf(i+1) + ":" + ((input[i] == null) ? "----" : input[i].getDisplayName());
				fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
				y += 10;
			}
		}
		if(recipe instanceof ShapelessRecipes) {
			List input = ((ShapelessRecipes)recipe).recipeItems;
			for(int i = 0; i < input.size(); ++i) {
				s = String.valueOf(i+1) + ":" + ((input.get(i) == null) ? "----" : ((ItemStack) input.get(i)).getDisplayName());
				fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
				y += 10;
			}
		}
		if(recipe instanceof ShapedOreRecipe) {
			Object[] input = ((ShapedOreRecipe)recipe).getInput();
			for(int i = 0; i < input.length; ++i) {
				if(input[i] == null) {
					s = String.valueOf(i+1) + ":" + ((input[i] == null) ? "----" : ((ItemStack) input[i]).getDisplayName());
					fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
					y += 10;
				}
				else if(input[i] instanceof ArrayList) {
					ArrayList list = (ArrayList) input[i];
					if(list.size() == 0) {
						s = String.valueOf(i+1) + ":" + "UNKNOWN";
						fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, Color.RED.getRGB());
						y += 10;
					}
					else {
						ItemStack src = (ItemStack) ((dicIndex < list.size()) ? list.get(dicIndex) : list.get(list.size() - 1));
						s = String.valueOf(i+1) + ":" + ((src == null) ? "----" : src.getDisplayName());
						fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
						y += 10;
					}
				}
				else if(input[i] instanceof ItemStack) {
					s = String.valueOf(i+1) + ":" + ((input[i] == null) ? "----" : ((ItemStack) input[i]).getDisplayName());
					fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
					y += 10;
				}
			}
		}
		if(recipe instanceof ShapelessOreRecipe) {
			List input = ((ShapelessOreRecipe)recipe).getInput();
			for(int i = 0; i < input.size(); ++i) {
				if(input.get(i) instanceof ArrayList) {
					ArrayList list = (ArrayList) input.get(i);
					if(list.size() == 0) {
						s = String.valueOf(i+1) + ":" + "UNKNOWN";
						fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, Color.RED.getRGB());
						y += 10;
					}
					else {
						ItemStack src = (ItemStack) ((dicIndex < list.size()) ? list.get(dicIndex) : list.get(list.size() - 1));
						s = String.valueOf(i+1) + ":" + ((src == null) ? "----" : src.getDisplayName());
						fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
						y += 10;
					}
				}
				if(input.get(i) instanceof ItemStack) {
					s = String.valueOf(i+1) + ":" + ((input.get(i) == null) ? "----" : ((ItemStack) input.get(i)).getDisplayName());
					fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
					y += 10;
				}
			}
		}

		y = this.height - 10;

		s = String.valueOf(index + 1) + "/" + String.valueOf(CraftingManager.getInstance().getRecipeList().size());
		fontRenderer.drawString(s, this.width - fontRenderer.getStringWidth(s), y, 0xffffff);
	}

	private static int getDicSize(IRecipe recipe) {
		if(recipe instanceof ShapedRecipes) {
			return 1;
		}
		if(recipe instanceof ShapelessRecipes) {
			return 1;
		}
		if(recipe instanceof ShapedOreRecipe) {
			int nMax = 0;
			for (int n = 0; n <  ((ShapedOreRecipe)recipe).getInput().length; ++n) {
				Object input = ((ShapedOreRecipe)recipe).getInput()[n];
				if(input == null) {
					continue;
				}
				if(input instanceof ArrayList) {
					ArrayList list = (ArrayList)input;
					if(nMax < list.size()) {
						nMax = list.size();
					}
				}
			}
			return nMax;
		}
		if(recipe instanceof ShapelessOreRecipe) {
			int nMax = 0;
			for (int n = 0; n <  ((ShapelessOreRecipe)recipe).getInput().size(); ++n) {
				Object input = ((ShapelessOreRecipe)recipe).getInput().get(n);
				if(input instanceof ArrayList) {
					ArrayList list = (ArrayList)input;
					if(nMax < list.size()) {
						nMax = list.size();
					}
				}
			}
			return nMax;
		}
		return 1;
	}

	private boolean drawShapelessOreRecipe(ShapelessOreRecipe recipe) {
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		int nMax = 0;
		for (int n = 0; n <  recipe.getInput().size(); ++n) {
			Object input = recipe.getInput().get(n);
			if(input instanceof ArrayList) {
				ArrayList list = (ArrayList)input;
				if(nMax < list.size()) {
					nMax = list.size();
				}
				if(list.size() > 0) {
					ItemStack src = ((ItemStack) ((dicIndex < list.size()) ? list.get(dicIndex) : list.get(list.size() - 1))).copy();
					if(src.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
						src.setItemDamage(0);
					}
					((Slot) this.inventorySlots.inventorySlots.get(n+1)).putStack(src);
				}
			}
			else if(input instanceof ItemStack) {
				ItemStack src = ((ItemStack)input).copy();
				if(src.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					src.setItemDamage(0);
				}
				((Slot) this.inventorySlots.inventorySlots.get(n+1)).putStack(src);
			}
		}
		((Slot)this.inventorySlots.inventorySlots.get(0)).putStack(recipe.getRecipeOutput());

		return dicIndex >= (nMax - 1);
	}

	private boolean drawShapedOreRecipe(ShapedOreRecipe recipe) {
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		int recipeHeight = ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, (ShapedOreRecipe) recipe, heightName);
		int recipeWidth = ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, (ShapedOreRecipe) recipe, widthName);

		int nMax = 0;
		for (int i = 0; i < recipeHeight; ++i) {
			for (int j = 0; j < recipeWidth; ++j) {
				Object input = recipe.getInput()[i * recipeWidth + j];
				if (input == null) {
					continue;
				}
				if(input instanceof ArrayList) {
					ArrayList list = (ArrayList)input;
					if(nMax < list.size()) {
						nMax = list.size();
					}
					if(list.size() > 0) {
						ItemStack src = ((ItemStack) ((dicIndex < list.size()) ? list.get(dicIndex) : list.get(list.size() - 1))).copy();
						if(src.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
							src.setItemDamage(0);
						}
						((Slot) this.inventorySlots.inventorySlots.get(i * 3 + j+1)).putStack(src);
					}
				}
				else if(input instanceof ItemStack) {
					ItemStack src = ((ItemStack)input).copy();
					if(src.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
						src.setItemDamage(0);
					}
					((Slot) this.inventorySlots.inventorySlots.get(i * 3 + j+1)).putStack(src);
				}
			}
		}
		((Slot)this.inventorySlots.inventorySlots.get(0)).putStack(recipe.getRecipeOutput());

		return dicIndex >= (nMax - 1);
	}

	private void drawShapelessRecipes(ShapelessRecipes recipe) {
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		for(int n = 0; n < recipe.recipeItems.size(); ++n) {
			ItemStack src = ((ItemStack) recipe.recipeItems.get(n)).copy();
			if(src.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
				src.setItemDamage(0);
			}
			((Slot) this.inventorySlots.inventorySlots.get(n+1)).putStack(src);
		}
		((Slot)this.inventorySlots.inventorySlots.get(0)).putStack(recipe.getRecipeOutput());
	}

	private void drawShapedRecipes(ShapedRecipes recipe) {
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		for (int i = 0; i < recipe.recipeHeight; ++i) {
			for (int j = 0; j < recipe.recipeWidth; ++j) {
				Object obj = recipe.recipeItems[i * recipe.recipeWidth + j];
				if (obj == null) {
					continue;
				}
				ItemStack src = ((ItemStack)obj).copy();
				if(src.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					src.setItemDamage(0);
				}
				src.stackSize = 1;
				((Slot) this.inventorySlots.inventorySlots.get(i * 3 + j+1)).putStack(src);
			}
		}
		((Slot)this.inventorySlots.inventorySlots.get(0)).putStack(recipe.getRecipeOutput());
	}

	public static void saveScreenshot() {
		saveScreenshot(index, ((IRecipe) CraftingManager.getInstance().getRecipeList().get(index)).getRecipeOutput());
	}

	public static void saveScreenshot(int n, ItemStack itemStack) {
		Minecraft mc = Minecraft.getMinecraft();
		if(!mc.theWorld.isRemote) return;

		int displayWidth = mc.displayWidth;
		int displayHeight = mc.displayHeight;

		try {
			int size = displayWidth * displayHeight;
			IntBuffer buf = BufferUtils.createIntBuffer(size);
			int[] pixels = new int[size];

			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			buf.clear();
			GL11.glReadPixels(0, 0, displayWidth, displayHeight, GL12.GL_BGRA,
					GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buf);
			buf.get(pixels);
			arrayCopy(pixels, displayWidth, displayHeight);
			BufferedImage bufferedimage = new BufferedImage(displayWidth, displayHeight, 1);
			bufferedimage.setRGB(0, 0, displayWidth, displayHeight, pixels, 0, displayWidth);

			int x = Config.x;
			int y = Config.y;
			int w = Config.width;
			int h = Config.height;
			BufferedImage trimmingImage = bufferedimage.getSubimage(x, y, w, h);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String dateStr = sdf.format(date);
			File screenshotsDir = new File(mc.getMinecraftDir(), "screenshots");
			File dir = new File(screenshotsDir, dateStr);
			dir.mkdir();

			String outputItemName;
			if(itemStack == null) {
				return;
			}
			else if(itemStack.getDisplayName() != null) {
				outputItemName = itemStack.getDisplayName();
			}
			else {
				outputItemName = itemStack.getItemName();
			}
			String fileName = String.valueOf(n) + "_" + outputItemName;
			File file = new File(dir, fileName + ".png");

			if(file.exists()) {
				return;
			}

			ImageIO.write(trimmingImage, "png", file);
			mc.ingameGUI.getChatGUI().printChatMessage("Modder's Polaroid save: " + file.getName());

		} catch (Exception exception) {
			exception.printStackTrace();
			mc.ingameGUI.getChatGUI().printChatMessage("Failed to save: " + exception);
		}
	}

	private static void arrayCopy(int[] pixels, int displayWidth, int displayHeight) {
		int[] aint1 = new int[displayWidth];
		int k = displayHeight / 2;

		for (int l = 0; l < k; ++l) {
			System.arraycopy(pixels, l * displayWidth, aint1, 0, displayWidth);
			System.arraycopy(pixels, (displayHeight - 1 - l) * displayWidth,
					pixels, l * displayWidth, displayWidth);
			System.arraycopy(aint1, 0, pixels, (displayHeight - 1 - l)
					* displayWidth, displayWidth);
		}
	}

	public static void next() {
		IRecipe recipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(index);
		int dicSize = getDicSize(recipe);
		if(dicIndex >= dicSize - 1) {
			index = (index + 1) % CraftingManager.getInstance().getRecipeList().size();
			dicIndex = 0;
		}
		else {
			dicIndex++;
		}
	}

	public static void prev() {
		IRecipe recipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(index);
		if(dicIndex <= 0) {
			int size = CraftingManager.getInstance().getRecipeList().size();;
			index = (index + size - 1) % size;
			dicIndex = 0;
		}
		else {
			dicIndex--;
		}
	}

	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture("/gui/crafting.png");
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		if (show) {
			showInfomation();
		}
	}

}
