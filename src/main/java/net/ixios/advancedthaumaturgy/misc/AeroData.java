package net.ixios.advancedthaumaturgy.misc;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

public class AeroData
{
	public EntityPlayer player;
	public Vector3 vector;
	public Block block;
	public int blockmeta;
	
	public AeroData(EntityPlayer plr, Vector3 vec, Block block, int meta)
	{
		this.player = plr;
		this.vector = vec;
		this.block = block;
		this.blockmeta = meta;
	}
}
