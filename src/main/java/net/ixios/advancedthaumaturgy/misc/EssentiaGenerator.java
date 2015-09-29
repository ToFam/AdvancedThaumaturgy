package net.ixios.advancedthaumaturgy.misc;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;

/**
 * Essentia goes in, RF comes out
 * 
 * @author ToFam
 */
public class EssentiaGenerator implements IEnergyProvider, IAspectContainer {
	private static final int MAX_ESSENTIA = 64;
	private static int baseRF;
	private static HashMap<Aspect, Double> modifierByAspect;
	private static TreeMap<Double, Aspect> aspectByModifier;
	
	private EnergyStorage energy;
	private int essentia;
	private Aspect aspect;
	
	public static void initModifiers()
	{
		baseRF = AdvThaum.config.get("EssentiaEngine", "baseRF", 6000, "RF generated per Essentia").getInt();
		modifierByAspect = new HashMap<Aspect, Double>();
		modifierByAspect.put(Aspect.FIRE, 1.0);
		modifierByAspect.put(Aspect.EARTH, 0.5);
		modifierByAspect.put(Aspect.AIR, 0.5);
		modifierByAspect.put(Aspect.WATER, 0.5);
		modifierByAspect.put(Aspect.ORDER, 1.0);
		modifierByAspect.put(Aspect.ENTROPY, 0.5);
		
		modifierByAspect.put(Aspect.TREE, 0.25);
		modifierByAspect.put(Aspect.PLANT, 0.25);
		modifierByAspect.put(Aspect.ENERGY, 2.0);
		modifierByAspect.put(Aspect.LIGHT, 1.0);
		
		aspectByModifier = new TreeMap<Double, Aspect>();
		for (Entry<Aspect, Double> e : modifierByAspect.entrySet())
		{
			aspectByModifier.put(e.getValue(), e.getKey());
		}
	}
	
	public EssentiaGenerator()
	{
		energy = new EnergyStorage(32000, 80);
	}
	
	public void generate()
	{
		if (aspect != null && essentia > 0) 
		{
			int generate = (int)(baseRF * modifierByAspect.get(aspect));
			if (energy.receiveEnergy(generate, true) == generate) 
			{
				energy.receiveEnergy(generate, false);
				essentia--;
			}
		}
	}
	
	public Collection<Aspect> needs()
	{
		if (aspect != null && essentia > 0)
		{
			List<Aspect> rtn = new LinkedList<Aspect>();
			if (essentia < MAX_ESSENTIA)
				rtn.add(aspect);
			return rtn;
		}
		else
		{
			return aspectByModifier.values();
		}
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		energy.readFromNBT(nbt);
		aspect = Aspect.getAspect(nbt.getString("aspect"));
		essentia = nbt.getInteger("essentia");
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		energy.writeToNBT(nbt);
		nbt.setString("aspect", aspect.getName());
		nbt.setInteger("essentia", essentia);
	}
	
	public int getMaxExtract()
	{
		return energy.getMaxExtract();
	}
	
	/*
	 * IEnergyProvider
	 */
	
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
	{
		return energy.extractEnergy(maxExtract, simulate);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) 
	{
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) 
	{
		return energy.getMaxEnergyStored();
	}
	
	/*
	 * IAspectContainer
	 */

	@Override
	public AspectList getAspects() 
	{
		AspectList rtn = new AspectList();
		if (aspect != null && essentia > 0)
			rtn.add(aspect, essentia);
		return rtn;
	}

	@Override
	public boolean doesContainerAccept(Aspect tag) 
	{
		return modifierByAspect.containsKey(tag);
	}

	@Override
	public int addToContainer(Aspect tag, int amount) 
	{
		if (aspect == null || (tag == aspect)) 
		{
			int add = Math.min(MAX_ESSENTIA - essentia, amount);
			essentia += add;
			return add;
		}
		return 0;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount) 
	{
		return aspect == tag && essentia >= amount;
	}

	@Override
	public boolean doesContainerContain(AspectList ot) {
		if (essentia > 0)
			for (Aspect a : ot.getAspects())
				if (aspect == a)
					return true;
		return false;
	}

	@Override
	public int containerContains(Aspect tag) 
	{
		return tag == aspect ? essentia : 0;
	}
	
	/*
	 * unsupported
	 */

	@Override
	public void setAspects(AspectList aspects) {}

	@Override
	public boolean takeFromContainer(Aspect tag, int amount) {return false;}

	@Override
	public boolean takeFromContainer(AspectList ot) {return false;}
}
