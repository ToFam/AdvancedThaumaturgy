package net.ixios.advancedthaumaturgy.misc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.ixios.advancedthaumaturgy.AdvThaum;
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
			rtn = drainFromSources(aspectsByPriority);
			if (rtn == null)
			{
				// updateSources
			}
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
				connectedTube.orientation = orientation;
				if ((connectedTube != null) 
				 && (connectedTube.tube.getEssentiaAmount(orientation.getOpposite()) > 0))
				{
					Aspect aspect = connectedTube.tube.getEssentiaType(orientation.getOpposite());
					if (found.get(aspect) != null)
					{
						// check suction
					}
					else
					{
						found.put(aspect, connectedTube);
					}
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
		for (Aspect a : aspectsByPriority)
		{
			TileJarFillable jar = Utilities.findEssentiaJar(world, a, xCoord, yCoord, zCoord, 10, 2, 10);
			if (jar != null && jar.amount > 0)
			{
				jar.takeFromContainer(a, 1);
				if (world.isRemote)
				{
		            AdvThaum.proxy.createParticle(world, (float)jar.xCoord + 0.5F, jar.yCoord + 1, (float)jar.zCoord + 0.5F, 
		            		(float)xCoord + 0.5F, (float)yCoord + 0.8F, (float)zCoord + 0.5F, a.getColor());
				}

				world.markBlockForUpdate(jar.xCoord, jar.yCoord, jar.zCoord);
				return a;
			}
		}
		return null;
	}
	
	private class Tube
	{
		public IEssentiaTransport tube;
		public ForgeDirection orientation;
	}
}
