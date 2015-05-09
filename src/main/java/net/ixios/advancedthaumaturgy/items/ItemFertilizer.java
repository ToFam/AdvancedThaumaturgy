package net.ixios.advancedthaumaturgy.items;

import java.util.List;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

public class ItemFertilizer extends ItemBlock
{
	public ItemFertilizer(Block block)
	{
		super(block);
		this.setCreativeTab(AdvThaum.tabAdvThaum);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack,	EntityPlayer player, List list, boolean par4)
	{
		boolean shiftdown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    	if (!shiftdown)
    		return;
    	String desc = StatCollector.translateToLocal("tile.blockThaumicFertilizer.desc");
		String[] lines = desc.split("\\|");
		for (String s : lines)
			list.add(s);
	}
}
