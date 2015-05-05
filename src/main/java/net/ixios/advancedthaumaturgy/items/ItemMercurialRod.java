package net.ixios.advancedthaumaturgy.items;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.wands.WandRod;

public class ItemMercurialRod extends WandRod
{

	ResourceLocation texture = new ResourceLocation("advthaum:wand_rod_quicksilver");
	
	public ItemMercurialRod(int storage)
	{
		//
		super("mercurial", Math.max(500, Math.min(storage, 1000)), new ItemStack(AdvThaum.MercurialRodBase), 10);
		setGlowing(true);
		texture = new ResourceLocation("advthaum:textures/models/wand_rod_mercurial.png");
	}

    @Override
    public ResourceLocation getTexture()
    {
	    return texture;
    }
    
}
