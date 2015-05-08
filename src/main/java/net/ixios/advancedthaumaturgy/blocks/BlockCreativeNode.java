package net.ixios.advancedthaumaturgy.blocks;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemCreativeNode;
import net.ixios.advancedthaumaturgy.tileentities.TileCreativeNode;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockAiry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockCreativeNode extends BlockAiry
{
	public static int renderID;
	
	public BlockCreativeNode()
	{
		setCreativeTab(AdvThaum.tabAdvThaum);
		setBlockUnbreakable();
		setBlockName("blockCreativeNode");
		renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	public void register()
	{
		GameRegistry.registerBlock(this, ItemCreativeNode.class, getUnlocalizedName());
		GameRegistry.registerTileEntity(TileCreativeNode.class, "tileentityCreativeNode");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister ir)
	{
		blockIcon = ir.registerIcon("advthaum:node");
	}
	
	@Override
	public IIcon getIcon(int par1, int par2) 
	{
		return blockIcon;
	}
	
	@Override
	public boolean renderAsNormalBlock() 
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() 
	{
		return false;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata)
	{
		return new TileCreativeNode();
	}
	
}
