package net.ixios.advancedthaumaturgy.items;

import java.util.List;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.blocks.ItemJarFilled;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.blocks.BlockEtherealJar;
import net.ixios.advancedthaumaturgy.tileentities.TileEtherealJar;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemEtherealJar extends ItemJarFilled
{
	public ItemEtherealJar(int id)
	{
		super(id);
		this.setMaxStackSize(4);
		this.setUnlocalizedName("at.etherealjar");
	}

	@Override
	public void registerIcons(IconRegister ir)
	{
		itemIcon = ir.registerIcon("advthaum:etherealjar");
	}
	
	@Override
	public Icon getIcon(ItemStack stack, int pass) 
	{
		return itemIcon;
	}
	
	@Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, 
            float hitX, float hitY, float hitZ, int metadata)
    {
        if (!world.setBlock(x, y, z, BlockEtherealJar.blockID, metadata, 3))
            return false;
        if (world.getBlockId(x, y, z) == BlockEtherealJar.blockID)
        {
        	TileEntity te = world.getBlockTileEntity(x, y, z);
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
        	
            Block.blocksList[BlockEtherealJar.blockID].onBlockPlacedBy(world, x, y, z, player, stack);
            Block.blocksList[BlockEtherealJar.blockID].onPostBlockPlaced(world, x, y, z, metadata);
        	
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
