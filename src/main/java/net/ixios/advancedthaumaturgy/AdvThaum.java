package net.ixios.advancedthaumaturgy;

import net.ixios.advancedthaumaturgy.blocks.BlockAltarDeployer;
import net.ixios.advancedthaumaturgy.blocks.BlockCreativeNode;
import net.ixios.advancedthaumaturgy.blocks.BlockEssentiaEngine;
import net.ixios.advancedthaumaturgy.blocks.BlockEtherealJar;
import net.ixios.advancedthaumaturgy.blocks.BlockMicrolith;
import net.ixios.advancedthaumaturgy.blocks.BlockNodeModifier;
import net.ixios.advancedthaumaturgy.blocks.BlockPlaceholder;
import net.ixios.advancedthaumaturgy.blocks.BlockThaumicFertilizer;
import net.ixios.advancedthaumaturgy.blocks.BlockThaumicVulcanizer;
import net.ixios.advancedthaumaturgy.integration.waila.WailaEssentiaEngineHandler;
import net.ixios.advancedthaumaturgy.items.ItemAeroSphere;
import net.ixios.advancedthaumaturgy.items.ItemArcaneCrystal;
import net.ixios.advancedthaumaturgy.items.ItemEndstoneChunk;
import net.ixios.advancedthaumaturgy.items.ItemEtherealJar;
import net.ixios.advancedthaumaturgy.items.ItemFocusVoidCage;
import net.ixios.advancedthaumaturgy.items.ItemInfusedThaumium;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRod;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRodBase;
import net.ixios.advancedthaumaturgy.items.ItemMercurialWand;
import net.ixios.advancedthaumaturgy.misc.ATCreativeTab;
import net.ixios.advancedthaumaturgy.misc.ATEventHandler;
import net.ixios.advancedthaumaturgy.misc.ATServerCommand;
import net.ixios.advancedthaumaturgy.misc.ArcingDamageManager;
import net.ixios.advancedthaumaturgy.misc.ChunkLoadingClass;
import net.ixios.advancedthaumaturgy.network.PacketStartNodeModification;
import net.ixios.advancedthaumaturgy.proxies.CommonProxy;
import net.ixios.advancedthaumaturgy.tileentities.TileEssentiaEngine;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.wands.WandRod;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.Thaumcraft;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.waila.api.IWailaRegistrar;

@Mod(modid=AdvThaum.MODID, version=AdvThaum.VERSION, name=AdvThaum.NAME, 
	dependencies="required-after:Forge;required-after:Thaumcraft", acceptedMinecraftVersions=AdvThaum.MC_VERSION)
public class AdvThaum
{
	public final static String MODID = "advthaum";
	public final static String VERSION = "@version@";
	public final static String NAME = "Advanced Thaumaturgy";
	public final static String MC_VERSION = "@mc_version@";

	public final static Logger logger = LogManager.getFormatterLogger(MODID);
	
	@Instance
	public static AdvThaum instance;
	
	@SidedProxy(clientSide="net.ixios.advancedthaumaturgy.proxies.ClientProxy",
				serverSide="net.ixios.advancedthaumaturgy.proxies.CommonProxy")
	public static CommonProxy proxy;
	
	public static SimpleNetworkWrapper channel;
	
	public static CreativeTabs tabAdvThaum = new ATCreativeTab("advthaum");
	public static Configuration config = null;
	
	// items
	public static ItemMercurialRod MercurialRod;
	public static ItemMercurialRodBase MercurialRodBase;
	public static ItemMercurialWand MercurialWand;
	public static ItemInfusedThaumium InfusedThaumium;
	
	public static ItemFocusVoidCage FocusVoidCage;
	public static ItemEtherealJar itemEtherealJar;
	public static ItemAeroSphere AeroSphere;
	public static ItemArcaneCrystal ArcaneCrystal;
	public static ItemEndstoneChunk EndstoneChunk;
	
	// blocks
	public static BlockNodeModifier NodeModifier;
	public static BlockThaumicFertilizer ThaumicFertilizer;
	public static BlockCreativeNode CreativeNode;
	public static BlockEssentiaEngine EssentiaEngine;
	public static BlockThaumicVulcanizer ThaumicVulcanizer;
	public static BlockPlaceholder Placeholder;
	public static BlockEtherealJar EtherealJar;
	public static BlockMicrolith Microlith;
	public static BlockAltarDeployer AltarDeployer;
	
	public static boolean debug = false;
	 
	public static void log(String text)
	{
	    logger.info(FMLCommonHandler.instance().getEffectiveSide().toString() + " " + text);
	}
	
	public static void wailaCallback(IWailaRegistrar reg)
	{
		reg.registerBodyProvider(new WailaEssentiaEngineHandler(), TileEssentiaEngine.class);
	}
	
	 @EventHandler
     public void preInit(FMLPreInitializationEvent event)
	 {
	     NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
	     channel = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	     channel.registerMessage(PacketStartNodeModification.Handler.class, PacketStartNodeModification.class, 1, Side.SERVER);

	     Placeholder = new BlockPlaceholder(Material.air);
	     
	     config = new Configuration(event.getSuggestedConfigurationFile());
	     config.load();
	     
	     ////////////////////////////////////////////////////////
	 	     
	     // Blocks
	     if (config.get("Feature Control", "enable_altar_deployer", true).getBoolean(true))
	    	 AltarDeployer = new BlockAltarDeployer();
	     
	     if (config.get("Feature Control", "enable_node_modifier", true).getBoolean(true))
	    	 NodeModifier = new BlockNodeModifier(Material.ground);
	     
	     if (config.get("Feature Control", "enable_vulcanizer", true).getBoolean(true))
	    	 ThaumicVulcanizer = new BlockThaumicVulcanizer(Material.ground);
	     
	     if (config.get("Feature Control", "enable_fertilizer", true).getBoolean(true))
	    	 ThaumicFertilizer = new BlockThaumicFertilizer(Material.ground);
	     
	     if (config.get("Feature Control", "enable_miniligh", true).getBoolean(true))
	    	 Microlith = new BlockMicrolith(Material.ground);

	     if (config.get("Feature Control", "enable_engine", true).getBoolean(true))
	    	 EssentiaEngine = new BlockEssentiaEngine(Material.rock);

	     if (config.get("Feature Control", "enable_creative_node", true).getBoolean(true))
	    	 CreativeNode = new BlockCreativeNode();

	     if (config.get("Feature Control", "enable_ethereal_jar", true).getBoolean(true))
	     {
	    	 EtherealJar = new BlockEtherealJar();
	    	 
	     // Items
	    	 itemEtherealJar = new ItemEtherealJar();
	     }
	     
	     if (config.get("Feature Control", "enable_infused_thaumium", true).getBoolean(true))
	    	 InfusedThaumium = new ItemInfusedThaumium();
	     
	     if (config.get("Feature Control", "enable_focus_void_cage", true).getBoolean(true))
	    	 FocusVoidCage = new ItemFocusVoidCage();
	     
	     if (config.get("Feature Control", "enable_aerosphere", true).getBoolean(true))
	    	 AeroSphere = new ItemAeroSphere();
	     
	     if (config.get("Feature Control", "enable_wand_upgrades", true).getBoolean(true))
	     {
	    	 ArcaneCrystal = new ItemArcaneCrystal();
	    	 EndstoneChunk = new ItemEndstoneChunk();
	     }

	     ////////////////////////////////////////////////////////
	     
	     MinecraftForge.EVENT_BUS.register(new ATEventHandler());
	     FMLCommonHandler.instance().bus().register(new ArcingDamageManager());
	     
	     ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkLoadingClass());
	     
	     FMLInterModComms.sendMessage("Waila", "register", this.getClass().getName() + ".wailaCallback");
	    
     }
	
	 private void registerStuff()
	 {
		if (EssentiaEngine != null)
			AdvThaum.EssentiaEngine.register();
			
		 if (InfusedThaumium != null)
			 InfusedThaumium.register();
		 
		 if (NodeModifier != null)
			 NodeModifier.register();
		 
		 if (ThaumicFertilizer != null)
			 ThaumicFertilizer.register();
		 
		 if (CreativeNode != null)
			 CreativeNode.register();
		 
		 if (EtherealJar != null && itemEtherealJar != null)
			 EtherealJar.register();
		 
		 if (Microlith != null)
			 Microlith.register();
		  
		 if (FocusVoidCage != null)
			 FocusVoidCage.register();
		 
		 if (AeroSphere != null)
			 AeroSphere.register();
		 
		 if (ArcaneCrystal != null)
			 ArcaneCrystal.register();
		 
		 if (EndstoneChunk != null)
			 EndstoneChunk.register();

		 if (AltarDeployer != null)
			 AltarDeployer.register();
		 
	 }
	 
	 @EventHandler
     public void load(FMLInitializationEvent event) 
     {
		 
     }
    
	 @EventHandler  
     public void postInit(FMLPostInitializationEvent event) 
     {
		 
		 ResearchCategories.registerCategory("ADVTHAUM",
				 new ResourceLocation("thaumcraft", "textures/items/thaumonomiconcheat.png"),
				 new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
		 	 
	     if (config.get("Feature Control", "enable_mercurial_core", true).getBoolean(true))
	     {
	    	int capacity = 500;
	    	for (WandRod rod : WandRod.rods.values())
	    		capacity = Math.max(capacity, rod.getCapacity());
		     
	    	 MercurialRodBase = new ItemMercurialRodBase();
	    	 MercurialRod = new ItemMercurialRod(capacity);
	    	 
	    	 if (config.get("Feature Control", "enable_mercurial_wand", true).getBoolean(true))
	    		 MercurialWand = new ItemMercurialWand();
	     }
		    
		 if (MercurialRodBase != null)
			 MercurialRodBase.register();
		
		 if (MercurialWand != null)
			 MercurialWand.register();
		 
	     registerStuff();
	     proxy.register();
		 
		 //ThaumicInkwell.register();
		 //ThaumicVulcanizer.register();
		 
		 // enable activating node in a jar by wanding the top wood slabs
		 WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 4, Blocks.wooden_slab, -1, MODID);
		 
		 if (config.get("Feature Control", "add_permutatio_to_eggs", true).getBoolean(true))
		 {
			 AspectList list = ThaumcraftApiHelper.getObjectAspects(new ItemStack(Items.egg));
			 if (list == null)
				 list = new AspectList();
			 if (!list.aspects.containsKey(Aspect.EXCHANGE))
			 {
				list.add(Aspect.EXCHANGE, 1); 
				 ThaumcraftApi.registerObjectTag(new ItemStack(Items.egg), list);
			 }
		 }
		 
		 if (config.get("Feature Control", "add_exanimus_to_bones", true).getBoolean(true))
		 {
			 AspectList list = ThaumcraftApiHelper.getObjectAspects(new ItemStack(Items.bone));
			 if (list == null)
				 list = new AspectList();
			 if (!list.aspects.containsKey(Aspect.UNDEAD))
			 {
				 list.add(Aspect.UNDEAD, 1);
				 ThaumcraftApi.registerObjectTag(new ItemStack(Items.bone), list);
			 }
		 }
			 
		 config.save();
     }
	 
	 @EventHandler
	 public void serverLoad(FMLServerStartingEvent event)
	 {
		 event.registerServerCommand(new ATServerCommand());
	 }
	 
	 @EventHandler
	 public void serverStarted(FMLServerStartingEvent event)
	 {
		 proxy.loadData();
	 }
	 
	 @EventHandler 
	 public void serverStopping(FMLServerStoppingEvent event)
	 {
		 proxy.saveData();	
	 }
}

