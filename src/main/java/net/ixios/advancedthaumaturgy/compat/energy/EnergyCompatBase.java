package net.ixios.advancedthaumaturgy.compat.energy;

public abstract class EnergyCompatBase 
{
	protected static boolean ispresent = false;
	
	public abstract void register();
	
	public static boolean isPresent()
	{
		return ispresent;
	}
	
	public static void forceEnable()
	{
		ispresent = true;
	}
	
}
