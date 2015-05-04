	package net.ixios.advancedthaumaturgy.blocks;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemFertilizer;
import net.ixios.advancedthaumaturgy.items.TCItems;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.tileentities.TileThaumicFertilizer;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockThaumicFertilizer extends BlockContainer
{

	public final int renderID;
	public static int blockID;
	
    public BlockThaumicFertilizer(int id, Material material)
    {
        super(id, material);
        blockID = id;
        this.setCreativeTab(AdvThaum.tabAdvThaum);
        this.setUnlocalizedName("at.fertilizer");
        this.setHardness(1.0f);
        renderID = RenderingRegistry.getNextAvailableRenderId();
    }

    public void register()
    {
        GameRegistry.registerBlock(this, ItemFertilizer.class, "blockThaumicFertilizer");
        GameRegistry.registerTileEntity(TileThaumicFertilizer.class, "tileentityThaumicFertilizer");
  
        ItemStack jar = new ItemStack(ConfigBlocks.blockJar, 1, 0);
		ItemStack water = new ItemStack(Item.bucketWater);
					
		 ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe("FERTILIZER", new ItemStack(this),
	                (new AspectList()).add(Aspect.WATER, 16).add(Aspect.AIR, 16),
	                "BSB", "SJS", "BSB", 'B', water, 'S', TCItems.watershard, 'J', jar );
	        
	    ConfigResearch.recipes.put("Fertilizer", recipe);
	        
        ATResearchItem ri = new ATResearchItem("FERTILIZER", "ARTIFICE",
				(new AspectList()).add(Aspect.PLANT, 1).add(Aspect.WATER, 1).add(Aspect.AIR, 1).add(Aspect.CROP, 1),
				-7, 9, 3,
				new ItemStack(this));
		ri.setTitle("at.research.fertilizer.title");
		ri.setInfo("at.research.fertilizer.desc");
		ri.setPages(new ResearchPage("at.research.fertilizer.pg1"), new ResearchPage(recipe));

		ri.setConcealed();
		
		ri.registerResearchItem();
    }
    
    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return null;
    }
    
    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileThaumicFertilizer();
    }
  
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ir)
	{
		blockIcon = ir.registerIcon("advthaum:thaum_sprinkler_tex");
	}

	@Override
	public Icon getIcon(int par1, int par2)
	{
	    return blockIcon;
	}

	@Override
	public boolean isOpaqueCube()
	{
	   return false;
	}
	
	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
    	return renderID;
    }
    
}
	
