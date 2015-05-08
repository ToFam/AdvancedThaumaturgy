package net.ixios.advancedthaumaturgy.items;

import java.lang.reflect.Constructor;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.EntityUtils;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemFocusVoidCage extends ItemFocusBasic
{

	private AspectList cost = null;
	
	public ItemFocusVoidCage()
    {
	    cost = new AspectList().add(Aspect.ORDER, 5000).add(Aspect.ENTROPY, 5000);
	    setUnlocalizedName("voidcage");
    }

	public void register()
	{
		GameRegistry.registerItem(this, getUnlocalizedName());
		setCreativeTab(AdvThaum.tabAdvThaum);
		
		// research
		// if tt parent to dislocation and place off
		//else
		// put where dislocation is
		
		// bestia, vacuos, praecantatio
		
	}
	
	@Override
	public void registerIcons(IIconRegister ir)
	{
        super.icon = ir.registerIcon("advthaum:voidcage");
    }
	
	@Override
    public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer player, MovingObjectPosition mop)
    {
		NBTTagCompound tag = itemstack.getTagCompound();
    	
    	if (tag == null || !tag.hasKey("classname"))
    	{
		
	 		Entity pointedEntity = EntityUtils.getPointedEntity(world, player, 1D, 32D, 0f);
	 		ItemWandCasting wand = ((ItemWandCasting)itemstack.getItem());
	 		
			if (pointedEntity != null && wand.consumeAllVis(itemstack, player, cost, true, false))
			{
				if (!world.isRemote)
	    		{
					pointedEntity.writeToNBT(tag);
					tag.setString("classname", pointedEntity.getClass().getCanonicalName());
					world.removeEntity(pointedEntity);
					NBTTagList pos = tag.getTagList("pos",6);
                    float x = (float) pos.func_150309_d(0);
					float y = (float) pos.func_150309_d(1);
					float z = (float) pos.func_150309_d(2);
					AdvThaum.proxy.createSparkleBurst(world, x + 0.5F, y + 1, z + 0.5F, 15, 0xFFFF00FF);
	    		}
				else
					player.swingItem();
			}
    			
    	}
		else if (tag.hasKey("classname") && mop != null)
		{
			EntityLivingBase entity = null;
			String classname = tag.getString("classname");
			
			@SuppressWarnings("rawtypes")
			Class c;
			
			ForgeDirection fd = ForgeDirection.getOrientation(mop.sideHit);
			
            try
            {
                c = Class.forName(classname);
            } catch (Exception e)
            {
            	return itemstack;
            }
            
			try
            {
				@SuppressWarnings("unchecked")
				Constructor<? extends EntityLivingBase> constructor = c.getConstructor(World.class);
				entity = constructor.newInstance(world);
				
				NBTTagList newpos = new NBTTagList();
				newpos.appendTag(new NBTTagDouble(mop.blockX + fd.offsetX));
				newpos.appendTag(new NBTTagDouble(mop.blockY + fd.offsetY));
				newpos.appendTag(new NBTTagDouble(mop.blockZ + fd.offsetZ));

				NBTTagList motion = new NBTTagList();
				motion.appendTag(new NBTTagDouble(0D));
				motion.appendTag(new NBTTagDouble(0D));
				motion.appendTag(new NBTTagDouble(0D));
				
				tag.removeTag("Pos");
				tag.removeTag("Motion");
				
				tag.setTag("Pos", newpos);
				tag.setTag("Motion", motion);
				
				entity.readFromNBT(tag);
				tag.removeTag("classname");
			    
            } catch (Exception e) 
            { 
            	return itemstack;
            }
			if (entity != null)
			{
				
				AdvThaum.proxy.createSparkleBurst(world, mop.blockX + 0.5F, mop.blockY + 1F, mop.blockZ + 0.5F, 15, 0xFFFF00FF);
				if (!world.isRemote)
					world.spawnEntityInWorld(entity);
				else
					player.swingItem();
			}
		}
    	
        return itemstack; 
    }
    
    @Override
    public AspectList getVisCost(ItemStack focusstack)
    {
        return cost;
    }
    
    /*
     * TODO: Find equivalent
    @Override
    public boolean acceptsEnchant(int id)
    {
        return id == ThaumcraftApi.enchantFrugal;
    }*/
    
    
}
