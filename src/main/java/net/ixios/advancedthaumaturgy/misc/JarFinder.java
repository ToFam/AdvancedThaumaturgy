package net.ixios.advancedthaumaturgy.misc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileJarFillable;

public class JarFinder {
	private int xCoord;
	private int yCoord;
	private int zCoord;
	private World world;
	private boolean useTubes;
	private IEssentiaTransport tile;
	
	private HashMap<Aspect, List<TileJarFillable>> jarSources;
	private TileJarFillable cache;
	
	public JarFinder(int x, int y, int z, World world)
	{
		xCoord = x;
		yCoord = y;
		zCoord = z;
		this.world = world;
		this.useTubes = false;
	}
	
	public JarFinder(int x, int y, int z, World world, boolean useTubes, IEssentiaTransport tile)
	{
		this(x, y, z, world);
		this.tile = tile;
		this.useTubes = useTubes;
	}
	
	public Aspect drainEssentia(Collection<Aspect> aspectsByPriority)
	{
		Aspect rtn = null;
		if (useTubes)
		{
			rtn = drainFromTubes(aspectsByPriority);
		}
		else
		{
			if (cache != null && cache.aspect != null && cache.amount > 0)
			{
				drainJar(cache, cache.aspect);
				return cache.aspect;
			}
			else
			{
				cache = null;
			}
			
			rtn = drainFromSources(aspectsByPriority);
		}
		
		return rtn;
	}
	
	private Aspect drainFromTubes(Collection<Aspect> aspectsByPriority)
	{
		HashMap<Aspect, Tube> found = new HashMap<Aspect, Tube>();
		Tube connectedTube = null;
		for (ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS) 
		{
			if (tile.isConnectable(orientation)) 
			{
				connectedTube = new Tube();
				connectedTube.tube = (IEssentiaTransport) ThaumcraftApiHelper.getConnectableTile(world, xCoord, yCoord, zCoord, orientation);
				if ((connectedTube.tube != null) 
				 && (connectedTube.tube.getEssentiaAmount(orientation.getOpposite()) > 0)
				 && (connectedTube.tube.getSuctionAmount(orientation.getOpposite()) < tile.getSuctionAmount(orientation))
				 && (connectedTube.tube.getMinimumSuction() <= tile.getSuctionAmount(orientation)))
				{
					Aspect aspect = connectedTube.tube.getEssentiaType(orientation.getOpposite());
					connectedTube.orientation = orientation;
					found.put(aspect, connectedTube);
				}
			}
		}
		
		if (found.size() == 0)
			return null;
		
		for (Aspect aspect : aspectsByPriority)
		{
			connectedTube = found.get(aspect);
			if (connectedTube != null)
			{
				if (connectedTube.tube.takeEssentia(aspect, 1, connectedTube.orientation.getOpposite()) == 1)
					return aspect;
			}
		}
		return null;
	}
	
	private Aspect drainFromSources(Collection<Aspect> aspectsByPriority)
	{
		boolean searched = false;
		if (jarSources == null)
		{
			findSources();
			searched = true;
		}
		
		for (Aspect a : aspectsByPriority)
		{
			List<TileJarFillable> jarList = jarSources.get(a);
			for (Iterator<TileJarFillable> it = jarList.iterator(); it.hasNext();)
			{
				TileJarFillable jar = it.next();
				if (jar == null || jar.aspect != a || jar.amount <= 0)
				{
					it.remove();
					continue;
				}
				
				drainJar(jar, a);
				return a;
			}
			
			if (!searched)
				findSources();
		}
		return null;
	}
	
	private void findSources()
	{
		if (jarSources == null)
			jarSources = new HashMap<Aspect, List<TileJarFillable>>();
		else
			jarSources.clear();
		
		for (int dy = -1; dy <= 1; dy++)
		for (int dx = -20; dx <= 20; dx++)
		for (int dz = -20; dz <= 20; dz++)
		{
			TileEntity te = world.getTileEntity(xCoord + dx, yCoord + dy, zCoord + dz);
			if (te instanceof TileJarFillable)
			{
				TileJarFillable jar = (TileJarFillable) te;
				if (jar != null && jar.amount > 0 && jar.aspect != null)
				{
					List<TileJarFillable> jarList = jarSources.get(jar.aspect);
					if (jarList == null)
						jarList = new LinkedList<TileJarFillable>();
					jarList.add(jar);
				}
			}
		}
	}
	
	private void drainJar(TileJarFillable jar, Aspect a)
	{
		jar.takeFromContainer(a, 1);
		if (jar.amount > 0)
			cache = jar;
		if (world.isRemote)
		{
            AdvThaum.proxy.createParticle(world, (float)jar.xCoord + 0.5F, jar.yCoord + 1, (float)jar.zCoord + 0.5F, 
            		(float)xCoord + 0.5F, (float)yCoord + 0.8F, (float)zCoord + 0.5F, a.getColor());
		}

		world.markBlockForUpdate(jar.xCoord, jar.yCoord, jar.zCoord);
	}
	
	private class Tube
	{
		public IEssentiaTransport tube;
		public ForgeDirection orientation;
	}
}
