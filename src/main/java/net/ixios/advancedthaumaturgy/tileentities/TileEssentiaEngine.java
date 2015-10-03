package net.ixios.advancedthaumaturgy.tileentities;

import java.util.Collection;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.EssentiaGenerator;
import net.ixios.advancedthaumaturgy.misc.JarFinder;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.client.fx.bolt.FXLightningBolt;

public class TileEssentiaEngine extends TileEntity implements IEnergyProvider, IEssentiaTransport, IAspectContainer
{
	private static boolean needTubes;

	private JarFinder finder;
	private EssentiaGenerator generator;
	private Aspect aspect;
	private int essentia;
	private boolean active;
	
	public static void init()
	{
		AdvThaum.config.addCustomCategoryComment("essentiaengine", "RF calculation: baseRF * AspectRatio (pun intended)");
		needTubes = AdvThaum.config.get("EssentiaEngine", "needsEssentiaTubes", true, 
				"Set to false if the Engine should pull essentia from nearby jars like the Infusion Altar").getBoolean();
	}
	
	public TileEssentiaEngine()
	{
		generator = new EssentiaGenerator();
		active = true;
	}

	public void setActive(boolean value)
	{
		active = value;
		if (!worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public boolean canUpdate() 
	{
		return true;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		Collection<Aspect> needed = generator.needs();
		if (needed.size() != 0)
		{
			if (finder == null)
				finder = new JarFinder(xCoord, yCoord, zCoord, worldObj, needTubes, this);
			
			Aspect drained = finder.drainEssentia(needed);
			if (drained != null)
			{
				generator.addToContainer(drained, 1);
			}
		}
		
		generator.generate();
		syncAspect();
		
		if (active && generator.getEnergyStored(ForgeDirection.UP) > 0)
			outputEnergy();
		
		if ((worldObj.isRemote) && (aspect != null))
			render();
	}
	
	private void outputEnergy()
	{
		TileEntity tile = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
		if (tile != null && tile instanceof IEnergyReceiver) 
		{
			IEnergyReceiver receiver = (IEnergyReceiver) tile;
			
			int maxReceived = receiver.receiveEnergy(ForgeDirection.DOWN, generator.getMaxExtract(), true);
			receiver.receiveEnergy(ForgeDirection.DOWN, generator.extractEnergy(ForgeDirection.UP, maxReceived, false), false);
			
			if (worldObj.getWorldTime() % 4 == 0)
				AdvThaum.proxy.createEngineParticle(worldObj, xCoord, yCoord, zCoord, ForgeDirection.UP, 0xFF00FFFF);
			
			if ((!worldObj.isRemote))
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	private void render()
	{
		if (!worldObj.isRemote)
			return;
		Vector3F src = new Vector3F(xCoord + 0.5F, yCoord + 1.0F, zCoord + 0.5F);
		Vector3F dst = new Vector3F(src.x, yCoord, src.z);
		
		/*src.x += (worldObj.rand.nextFloat() - 0.5F);
		src.y += (worldObj.rand.nextFloat() - 0.5F);
		src.z += (worldObj.rand.nextFloat() - 0.5F);*/
		
		dst.x += (worldObj.rand.nextFloat() - 0.5F);
		dst.y += (worldObj.rand.nextFloat() - 0.5F);
		dst.z += (worldObj.rand.nextFloat() - 0.5F);
		
		if (Minecraft.getMinecraft().renderViewEntity.ticksExisted % 60 == 0)
		{
			FXLightningBolt bolt = new FXLightningBolt(worldObj, src.x, src.y, src.z, dst.x, dst.y, dst.z, worldObj.rand.nextLong(), 5, 1);
			bolt.defaultFractal();
			bolt.setType(0);
			bolt.finalizeBolt();
		}
		
		if (aspect != null)
		{
			AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
			AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
			AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
			AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
		}
	}
	
	private void syncAspect()
	{
		AspectList l = generator.getAspects();
		if (l.size() == 0)
		{
			aspect = null;
			essentia = 0;
		}
		else
		{
			aspect = l.getAspects()[0];
			essentia = l.getAmount(aspect);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		generator.readFromNBT(nbt);
		active = nbt.getBoolean("active");
		syncAspect();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		generator.writeToNBT(nbt);
		nbt.setBoolean("active", active);
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.func_148857_g());
	}
	
	/*
	 * IEnergyProvider
	 */

	@Override
	public boolean canConnectEnergy(ForgeDirection from) 
	{
		return true;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) 
	{
		if (active)
			return generator.extractEnergy(from, maxExtract, simulate);
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) 
	{
		return generator.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) 
	{
		return generator.getMaxEnergyStored(from);
	}
	
	/*
	 * IEssentiaTransport
	 */

	@Override
	public boolean isConnectable(ForgeDirection face) 
	{
		switch (face) 
		{
		case NORTH:
		case EAST:
		case SOUTH:
		case WEST:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) 
	{
		return isConnectable(face);
	}

	@Override
	public Aspect getSuctionType(ForgeDirection face) 
	{
		return aspect;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) 
	{
		return (aspect != null) ? 128 : 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) 
	{
		return canInputFrom(face) ? generator.addToContainer(aspect, amount) : 0;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) 
	{
		return 0;
	}
	
	@Override
	public int getEssentiaAmount(ForgeDirection face) 
	{
		return essentia;
	}
	
	@Override
	public Aspect getEssentiaType(ForgeDirection face) 
	{
		return generator.getAspects().getAspects()[0];
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {return false;}

	@Override
	public void setSuction(Aspect aspect, int amount) {}

	@Override
	public int getMinimumSuction() {return 0;}

	@Override
	public boolean renderExtendedTube() {return false;}

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
		return aspect == tag;
	}

	@Override
	public int addToContainer(Aspect tag, int amount) 
	{
		int rtn = generator.addToContainer(tag, amount);
		if (rtn > 0 && !worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return rtn;
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

	@Override
	public void setAspects(AspectList aspects) {}

	@Override
	public boolean takeFromContainer(Aspect tag, int amount) {return false;}

	@Override
	public boolean takeFromContainer(AspectList ot) {return false;}
}
