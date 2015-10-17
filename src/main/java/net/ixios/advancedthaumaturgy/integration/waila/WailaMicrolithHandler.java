package net.ixios.advancedthaumaturgy.integration.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.ixios.advancedthaumaturgy.items.ItemMicrolith;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class WailaMicrolithHandler implements IWailaDataProvider 
{

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) 
	{
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, 
			IWailaDataAccessor accessor, IWailaConfigHandler config) 
	{
		MovingObjectPosition mop = accessor.getPosition();
		currenttip.add(ItemMicrolith.getName(accessor.getWorld().getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ)));
	    return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, 
			IWailaDataAccessor accessor, IWailaConfigHandler config) 
	{
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, 
			IWailaDataAccessor accessor, IWailaConfigHandler config) 
	{
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
			int y, int z) 
	{
		return tag;
	}

}
