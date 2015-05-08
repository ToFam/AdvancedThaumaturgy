package net.ixios.advancedthaumaturgy.items;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemEndstoneChunk extends Item
{

	public ItemEndstoneChunk()
    {
	    setUnlocalizedName("endstonechunk");
    }

	public void register()
	{
		GameRegistry.registerItem(this, getUnlocalizedName());
		setCreativeTab(AdvThaum.tabAdvThaum);
		
		ItemStack endstone = new ItemStack(Blocks.end_stone);
		
		GameRegistry.addRecipe(new ItemStack(this, 4, 0), new Object[] 
				{ "ESE", "SCS", "ESE", 'S', endstone, 'C', TCItems.anyshard, 'E', Items.emerald });
		
		GameRegistry.addSmelting(this, new ItemStack(AdvThaum.ArcaneCrystal, 1, 0), 0);
		
	}
}
