package net.ixios.advancedthaumaturgy.tileentities;

import java.util.HashMap;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Utilities;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.client.fx.bolt.FXLightningBolt;
import thaumcraft.common.tiles.TileJarFillable;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
//import cpw.mods.fml.common.Optional.Method;

public class TileEssentiaEngine extends TileEntity implements IEnergyProvider, IEssentiaTransport
{
	private final static int BASE_RF = 6000;
	private static HashMap<Aspect, Float> aspectModifiers;
	static {
		aspectModifiers = new HashMap<Aspect, Float>();
		aspectModifiers.put(Aspect.FIRE, 1.0F);
		aspectModifiers.put(Aspect.EARTH, 0.5F);
		aspectModifiers.put(Aspect.AIR, 0.5F);
		aspectModifiers.put(Aspect.WATER, 0.5F);
		aspectModifiers.put(Aspect.ORDER, 1.0F);
		aspectModifiers.put(Aspect.ENTROPY, 0.5F);
		
		aspectModifiers.put(Aspect.TREE, 0.25F);
		aspectModifiers.put(Aspect.PLANT, 0.25F);
		aspectModifiers.put(Aspect.ENERGY, 2.0F);
		aspectModifiers.put(Aspect.LIGHT, 1.0F);
	}
	
	private EnergyStorage energy;

	private Aspect curraspect;
	private boolean currentlyactive;
	private int counter;
	
	public TileEssentiaEngine()
	{
		energy = new EnergyStorage(32000, 80);
		currentlyactive = false;
	}

	public void setActive(boolean value)
	{
		currentlyactive = value;
		if (!worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public boolean canUpdate() 
	{
		return true;
	}
	
	private int generate(int max, boolean simulated) {
		float perAmount = BASE_RF * aspectModifiers.get(curraspect);
		int d = energy.getMaxEnergyStored() - energy.getEnergyStored();
		int store = (int) (d / perAmount);
		if (!simulated)
			energy.modifyEnergyStored((int)(store * perAmount));
		return store;
	}
	
	private void switchAspect() {
		// TODO
	}
	
	private void fill() {
		if (generate(1, true) != 1)
			return;
		
		TileEntity te = null;
		IEssentiaTransport ic = null;
		for (ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS) {
			if (isConnectable(orientation)) {
				te = ThaumcraftApiHelper.getConnectableTile(this.worldObj, xCoord, yCoord, zCoord, orientation);
				if (te != null) {
					ic = (IEssentiaTransport) te;
					if ((ic.getEssentiaAmount(orientation.getOpposite()) > 0) 
					 && (ic.getSuctionAmount(orientation.getOpposite()) < getSuctionAmount(null)) 
					 && (getSuctionAmount(null) >= ic.getMinimumSuction())) {
						int ess = ic.takeEssentia(curraspect, 1, orientation.getOpposite());
						if (ess > 0) {
							generate(ess, false);
							return;
						}
	            	}
				}
			}
		}

		counter++;
		if (counter > 20) {
			switchAspect();
			counter = 0;
		}
	}
	
	private void restockEnergy() {
		if (energy.getEnergyStored() >= energy.getMaxEnergyStored())
			return;
	
		if (hasEssentiaTubeConnection()) {
			fill();
		} else {
			TileJarFillable essentiaJar = Utilities.findEssentiaJar(worldObj, curraspect, this, 20, 2, 20);
			if (essentiaJar == null || essentiaJar.amount == 0) {
				switchAspect();
				return;
			}
			
			if (generate(1, true) == 1) {
				// createParticls is a blank method in common proxy, and has actual code in client proxy
	            AdvThaum.proxy.createParticle(worldObj, (float)essentiaJar.xCoord + 0.5F, essentiaJar.yCoord + 1, (float)essentiaJar.zCoord + 0.5F, 
	            		(float)xCoord + 0.5F, (float)yCoord + 0.8F, (float)zCoord + 0.5F, essentiaJar.aspect.getColor());
	            essentiaJar.takeFromContainer(curraspect, 1);
				generate(1, false);
	            if (!worldObj.isRemote)
	            {
	            	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	            	worldObj.markBlockForUpdate(essentiaJar.xCoord, essentiaJar.yCoord, essentiaJar.zCoord);
	            }
			}
		}
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if ((worldObj.isRemote) && (curraspect != null))
		{
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
			
			if (curraspect != null)
			{
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
			}
		}
		
		restockEnergy();
		
		if (!currentlyactive || energy.getEnergyStored() <= 0)
			return;
		
		TileEntity tile = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
		if (tile != null && tile instanceof IEnergyReceiver) {
			IEnergyReceiver receiver = (IEnergyReceiver) tile;
			
			int maxReceived = receiver.receiveEnergy(ForgeDirection.DOWN, energy.getMaxExtract(), true);
			receiver.receiveEnergy(ForgeDirection.DOWN, energy.extractEnergy(maxReceived, false), false);
			
			if (worldObj.getWorldTime() % 4 == 0)
				AdvThaum.proxy.createEngineParticle(worldObj, xCoord, yCoord, zCoord, ForgeDirection.UP, 0xFF00FFFF);
			
			if ((!worldObj.isRemote))
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	private boolean hasEssentiaTubeConnection()
	{
		for (ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = worldObj.getTileEntity(xCoord + orientation.offsetX, yCoord + orientation.offsetY, zCoord + orientation.offsetZ);
			if (tile instanceof IEssentiaTransport)
				return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		energy.readFromNBT(nbt);
		if (nbt.hasKey("aspect"))
			curraspect = Aspect.getAspect(nbt.getString("aspect").toLowerCase());
		currentlyactive = nbt.getBoolean("active");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		energy.writeToNBT(nbt);
		if (curraspect != null)
			nbt.setString("aspect", curraspect.getName().toLowerCase());
		nbt.setBoolean("active", currentlyactive);
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

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract,
			boolean simulate) {
		if (currentlyactive)
			return energy.extractEnergy(maxExtract, simulate);
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean isConnectable(ForgeDirection face) {
		switch (face) {
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
	public boolean canInputFrom(ForgeDirection face) {
		return isConnectable(face);
	}

	@Override
	public Aspect getSuctionType(ForgeDirection face) {
		return curraspect;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) {
		return (curraspect != null) ? 128 : 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return canInputFrom(face) ? generate(amount, false) : 0;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return 0;
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {
		return false;
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {
	}

	@Override
	public Aspect getEssentiaType(ForgeDirection face) {
		return null;
	}

	@Override
	public int getEssentiaAmount(ForgeDirection face) {
		return 0;
	}

	@Override
	public int getMinimumSuction() {
		return 0;
	}

	@Override
	public boolean renderExtendedTube() {
		return false;
	}

	
}
