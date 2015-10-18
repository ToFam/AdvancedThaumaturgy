package net.ixios.advancedthaumaturgy.integration.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.ixios.advancedthaumaturgy.blocks.BlockMicrolith;
import net.ixios.advancedthaumaturgy.tileentities.TileEssentiaEngine;

public class Waila {
	public static void wailaCallback(IWailaRegistrar reg)
	{
		reg.registerBodyProvider(new WailaEssentiaEngineHandler(), TileEssentiaEngine.class);
		reg.registerStackProvider(new WailaMicrolithHandler(), BlockMicrolith.class);
	}
}
