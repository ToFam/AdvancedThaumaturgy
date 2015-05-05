package net.ixios.advancedthaumaturgy.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileJarFillable;

public class Utilities
{
	/**
	 * Stolen from Vazkii
	 * 
	 * Read nbt data from dat file, create file if it does not exist already
	 * @param cache filename
	 * @return stored nbt data
	 * @author Vazkii
	 */
	public static NBTTagCompound getCacheCompound(File cache)
	{
        if (cache == null)
        	throw new RuntimeException("No cache file!");

        try 
        {
        	NBTTagCompound cmp = CompressedStreamTools.readCompressed(new FileInputStream(cache));
    		return cmp;
        }
        catch (IOException e) 
        {
        	NBTTagCompound cmp = new NBTTagCompound();

        	writeCacheCompound(cmp, cache);
        	return getCacheCompound(cache);
        	
        }
	}
	
	public static void writeCacheCompound(NBTTagCompound tag, File cache)
	{
		try
		{
			CompressedStreamTools.writeCompressed(tag, new FileOutputStream(cache));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static TileJarFillable findEssentiaJar(World world, Aspect aspect, int srcx, int srcy, int srcz, int xrange, int yrange, int zrange)
    {
    	for (int cx = srcx - (xrange / 2); cx < srcx + (xrange / 2); cx++)
        {
            for (int cy = srcy - (yrange / 2); cy < srcy + (yrange / 2); cy++)
            {
                for (int cz = srcz - (zrange / 2); cz < srcz + (zrange / 2); cz++)
                {
                    TileEntity te = world.getTileEntity(cx,  cy,  cz);
                    if ((te instanceof TileJarFillable))
                    {
                        TileJarFillable jar = (TileJarFillable)te;
                        
                        if (jar.amount == 0)
                            continue;
                        
                        if (jar.aspect == null)
                            continue;
                         
                        if (jar.doesContainerContainAmount(aspect, 1))
                        {
                            return (TileJarFillable)te;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static TileJarFillable findEssentiaJar(World world, Aspect aspect, TileEntity src, int xrange, int yrange, int zrange)
    {
        return findEssentiaJar(world, aspect, src.xCoord, src.yCoord, src.zCoord, xrange, yrange, zrange);
    }
   
	public static ItemFocusBasic getEquippedFocus(ItemStack stack)
	{
		 if ((stack == null) || !(stack.getItem() instanceof ItemWandCasting))
			 return null;
	 
		 ItemWandCasting wand = (ItemWandCasting)stack.getItem();
		 return wand.getFocus(stack);
	}
	
	public static boolean isOp(EntityPlayer player)
	{
		return MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());
	}
	
	 @SuppressWarnings("unchecked")
	 public static void broadcastMessage(String text)
	 {
		 
	     List<EntityPlayer> players;
	     
	     if (Minecraft.getMinecraft().theWorld.isRemote)
	    	 players = Minecraft.getMinecraft().theWorld.playerEntities;
	     else
	    	 players = MinecraftServer.getServer().worldServers[0].playerEntities;
	     
	     for (int t = 0; t < players.size(); t++)
	     {
	         players.get(t).addChatMessage(new ChatComponentText(text));
	     }
	 }
	 
	 public static boolean removeResearch(EntityPlayer player, String research)
	 {
		 ArrayList<String> list = (ArrayList<String>) ResearchManager.getResearchForPlayer(player.getDisplayName());
		 for (Iterator<String>it = list.iterator(); it.hasNext();)
		 {
			 String current = (String)it.next();
			
			 if (current.equalsIgnoreCase(research))
			 {
				 it.remove();
				 return true;
			 }
		 }
		 return false;
	 }
	 
	 public static ResearchItem findResearch(String key)
	 {
		 for (String categorykey : ResearchCategories.researchCategories.keySet())
         {
             ResearchCategoryList cat = ResearchCategories.researchCategories.get(categorykey);
             
             for (ResearchItem ri : cat.research.values())
             {       
             	if (ri.key.equalsIgnoreCase(key))
             		return ri;
             }
         }
		 return null;
	 }
}
