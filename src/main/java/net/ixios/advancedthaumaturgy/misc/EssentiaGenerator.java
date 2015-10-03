package net.ixios.advancedthaumaturgy.misc;

import static net.ixios.advancedthaumaturgy.AdvThaum.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

/**
 * Essentia goes in, RF comes out
 * 
 * @author ToFam
 */
public class EssentiaGenerator implements IEnergyProvider, IAspectContainer {
	private static final int MAX_ESSENTIA = 64;
	private static int baseRF;
	private static HashMap<Aspect, Double> modifierByAspect;
	private static List<Aspect> sortedAspects;
	
	private EnergyStorage energy;
	private int essentia;
	private Aspect aspect;
	
	public static void initModifiers()
	{
		modifierByAspect = new HashMap<Aspect, Double>();
		
		baseRF = config.get("essentiaengine", "baseRF", 6000, "RF generated per Essentia").getInt();
		ConfigCategory ratios;
		if (config.hasCategory("essentiaengine.AspectRatios"))
		{
			ratios = config.getCategory("essentiaengine.AspectRatios");
			for (Entry<String, Property> e : ratios.getValues().entrySet())
			{
				if (Aspect.getAspect(e.getKey()) != null)
					modifierByAspect.put(Aspect.getAspect(e.getKey()), e.getValue().getDouble());
			}
		}
		else
		{
			ratios = config.getCategory("essentiaengine.AspectRatios"); // creates Category

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
			
			for (Entry<Aspect, Double> e : modifierByAspect.entrySet())
			{
				Property p = new Property(e.getKey().getTag(), e.getValue().toString(), Property.Type.DOUBLE);
				ratios.put(p.getName(), p);
			}
		}
		
		sortedAspects = new LinkedList<Aspect>();
		sortedAspects.addAll(modifierByAspect.keySet());
		Comparator<Aspect> c = new ModifierComparator(modifierByAspect);
		Collections.sort(sortedAspects, c);
	}
	
	public EssentiaGenerator()
	{
		energy = new EnergyStorage(32000, 32000, 80);
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
				if (essentia == 0)
					aspect = null;
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
			return sortedAspects;
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
		nbt.setString("aspect", aspect == null ? "" : aspect.getTag());
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
			aspect = tag;
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
	
	/**
	 * To sort the aspect list from best modifier to least
	 * @author ToFam
	 */
	private static class ModifierComparator implements Comparator<Aspect>
	{
		private HashMap<Aspect, Double> modifierByAspect;
		
		public ModifierComparator(HashMap<Aspect, Double> modifierByAspect)
		{
			this.modifierByAspect = modifierByAspect;
		}
		
		@Override
		public int compare(Aspect a, Aspect b) {
			double mA = modifierByAspect.get(a);
			double mB = modifierByAspect.get(b);
			if (mA > mB)
				return -1;
			if (mA == mB)
				return 0;
			else
				return 1;
		}
		
	}
}
