package net.ixios.advancedthaumaturgy.compat.energy;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.Optional.Method;

@Optional.InterfaceList({ })
public class RFCompatChecker extends EnergyCompatBase 
{
	
	@Method(modid = "CoFHLib")
	@Override
	public void register()
	{
		ispresent = true;
	}
	
}
