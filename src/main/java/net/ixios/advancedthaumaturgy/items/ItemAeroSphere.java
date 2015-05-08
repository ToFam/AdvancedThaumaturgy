package net.ixios.advancedthaumaturgy.items;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Vector3;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemAeroSphere extends Item
{
	public final static int RADIUS = 6;

	public ItemAeroSphere()
    {
	    setUnlocalizedName("aerosphere");
    }
	
	public void register()
	{
		GameRegistry.registerItem(this, getUnlocalizedName());
		this.setCreativeTab(AdvThaum.tabAdvThaum);
		
		/* ATResearchItem ri = new ATResearchItem("AREOSPHERE", "ADVTHAUM",
					(new AspectList()).add(Aspect.AIR, 1).add(Aspect.WATER, 1).add(Aspect.ORDER, 1),
					-5, 0, 5,
					new ItemStack(this));
			ri.setTitle("at.research.aerosphere.title");
			ri.setInfo("at.research.aerosphere.desc");
			//ri.setParents("UPGRADECRYSTAL");
			ri.setParentsHidden("INFUSION");
			ri.setSiblings("ROD_mercurial", "MERCURIALWAND");
			ri.setPages(new ResearchPage("at.research.mercurialcore.pg1"),
					new ResearchPage("at.research.mercurialcore.pg2"),
					new ResearchPage(recipe));
			
			ri.setConcealed();
			
			ri.registerResearchItem();*/
			
	}
	
	@Override
	public void registerIcons(IIconRegister ir)
	{
	    itemIcon = ir.registerIcon("advthaum:aerosphere");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotid, boolean holding)
	{
	    super.onUpdate(stack, world, entity, slotid, holding);
		
		if (world.isRemote)
			return;
		
	    if (!(entity instanceof EntityPlayer) || !holding)
	    	return;
	    
	    EntityPlayer player = (EntityPlayer)entity;
	    
	    // check for blocks in range and if not placeholder, replace with placeholder and store
	    Vector3F plrpos = new Vector3F((float)player.posX, (float)player.posY, (float)player.posZ);
	    
	    for (int cx = (int)plrpos.x - 8; cx <= (int)plrpos.x + 8; cx++)
	    {
	    	for (int cy = (int)plrpos.y - 8; cy <= (int)plrpos.y + 8; cy++)
	    	{
	    		for (int cz = (int)plrpos.z - 8; cz <= (int)plrpos.z + 8; cz++)
	    		{
	    			Vector3 blockpos = new Vector3(cx, cy, cz);
	    			Block block = world.getBlock(blockpos.x, blockpos.y, blockpos.z);
	    			if (block == null || block.equals(AdvThaum.Placeholder))
	    				continue;
	    			if (plrpos.distanceTo(blockpos) <= RADIUS && world.getBlock(cx, cy,  cz).getMaterial().isLiquid())
    				{
	    				AdvThaum.proxy.beginMonitoring(player, blockpos, block);			
    				}
	    		}
	    	}
	    }
	    
	  
	    
	}

	
}
