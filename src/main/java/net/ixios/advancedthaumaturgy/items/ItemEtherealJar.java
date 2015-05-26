package net.ixios.advancedthaumaturgy.items;

import java.util.List;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.tileentities.TileEtherealJar;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.ItemJarFilled;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileJarFillable;

public class ItemEtherealJar extends ItemJarFilled
{
	public ItemEtherealJar()
	{
		this.setMaxStackSize(4);
		this.setUnlocalizedName("etherealjar");
	}

	@Override
	public void registerIcons(IIconRegister ir)
	{
		itemIcon = ir.registerIcon("advthaum:etherealjar");
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass) 
	{
		return itemIcon;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		Block block = world.getBlock(x, y, z);

	    if ((block == Blocks.snow_layer) && ((world.getBlockMetadata(x, y, z) & 0x7) < 1))
	    {
	    	side = 1;
	    }
	    else if ((block != Blocks.vine) && (block != Blocks.tallgrass) && (block != Blocks.deadbush) && (!block.isReplaceable(world, x, y, z)))
	    {
			if (side == 0)
			{
			  y--;
			}
			
			if (side == 1)
			{
			  y++;
			}
			
			if (side == 2)
			{
			  z--;
			}
			
			if (side == 3)
			{
			  z++;
			}
			
			if (side == 4)
			{
			  x--;
			}
			
			if (side == 5)
			{
			  x++;
			}
	    }

	    if (stack.stackSize == 0)
	    {
	    	return false;
	    }
	    if (!player.canPlayerEdit(x, y, z, side, stack))
	    {
	    	return false;
	    }
	    if ((y == 255) && (AdvThaum.EtherealJar.getMaterial().isSolid()))
	    {
	    	return false;
	    }
	    if (world.canPlaceEntityOnSide(AdvThaum.EtherealJar, x, y, z, false, side, player, stack))
	    {
		    Block var12 = AdvThaum.EtherealJar;
		    int var13 = getMetadata(stack.getItemDamage());
		    int var14 = AdvThaum.EtherealJar.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, var13);
	
		    if (placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, var14))
		    {
			    TileEntity te = world.getTileEntity(x, y, z);
			    if ((te != null) && ((te instanceof TileJarFillable)))
			    {
				    if (stack.hasTagCompound()) {
					    AspectList aspects = getAspects(stack);
					    if ((aspects != null) && (aspects.size() == 1)) {
					      ((TileJarFillable)te).amount = aspects.getAmount(aspects.getAspects()[0]);
					      ((TileJarFillable)te).aspect = aspects.getAspects()[0];
					    }
					    String tf = stack.stackTagCompound.getString("AspectFilter");
					    if (tf != null) ((TileJarFillable)te).aspectFilter = Aspect.getAspect(tf);
				    }
			    }
			    world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, var12.stepSound.func_150496_b(), (var12.stepSound.getVolume() + 1.0F) / 2.0F, var12.stepSound.getPitch() * 0.8F);
			    stack.stackSize -= 1;
		    }
	
		    return true;
	    }

	    return false;
	}
	
	@Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, 
            float hitX, float hitY, float hitZ, int metadata)
    {
        if (!world.setBlock(x, y, z, AdvThaum.EtherealJar, metadata, 3))
            return false;
        if (world.getBlock(x, y, z).equals(AdvThaum.EtherealJar))
        {
        	TileEntity te = world.getTileEntity(x, y, z);
        	if (te == null)
        		te = new TileEtherealJar();
        	
        	TileEtherealJar ej = (TileEtherealJar)te;
        	
        	AspectList aspects = ((ItemJarFilled)stack.getItem()).getAspects(stack); 
        	
        	if (aspects != null && aspects.getAspects() != null)
        	{
        		ej.aspect = aspects.getAspects()[0];
        	   	ej.amount = aspects.getAmount(ej.aspect);
        	}
        	
        	NBTTagCompound tag = stack.stackTagCompound;
        	
        	if (tag != null && tag.hasKey("AspectFilter"))
        		ej.aspectFilter = Aspect.getAspect(tag.getString("AspectFilter"));
        	
            AdvThaum.EtherealJar.onBlockPlacedBy(world, x, y, z, player, stack);
            AdvThaum.EtherealJar.onPostBlockPlaced(world, x, y, z, metadata);
        	
        }
        return true;
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack,	EntityPlayer player, List list, boolean showdetails)
	{
		AspectList aspects = ((ItemJarFilled)stack.getItem()).getAspects(stack);
		
		if (aspects != null)
			list.add(aspects.getAspects()[0].getName() + " x " + aspects.getAmount(aspects.getAspects()[0]));
			
	}
	
}
