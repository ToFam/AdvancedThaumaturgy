package net.ixios.advancedthaumaturgy.misc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.common.config.ConfigItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ATCreativeTab extends CreativeTabs 
{

    public ATCreativeTab(String label)
    {
    	super(label);
    }
    
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem()
    {
    	return new ItemStack(ConfigItems.itemResource, 1, 3).getItem();
    }
}
