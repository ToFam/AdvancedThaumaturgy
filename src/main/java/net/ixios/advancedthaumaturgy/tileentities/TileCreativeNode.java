package net.ixios.advancedthaumaturgy.tileentities;

import net.ixios.advancedthaumaturgy.blocks.BlockCreativeNode;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.tiles.TileNode;

public class TileCreativeNode extends TileNode
{
	public TileCreativeNode()
	{
		
	}
	
	@Override
	public AspectList getAspects()
	{
		return BlockCreativeNode.aspects;
	}
	
	@Override
	public int containerContains(Aspect aspect) { return aspect.isPrimal() ? 100 : 0; }

	@Override
	public boolean doesContainerContain(AspectList list) 
	{
		return (list.getPrimalAspects().length <= 6);
	}

	@Override
	public boolean doesContainerContainAmount(Aspect aspect, int amount)
	{
		return (aspect.isPrimal() && (amount <= 100));
	}

	@Override
	public boolean takeFromContainer(AspectList list) { return true; }

	@Override
	public boolean takeFromContainer(Aspect aspect, int amount) { return true; }

	@Override
	public NodeModifier getNodeModifier() { return NodeModifier.BRIGHT; }

	@Override
	public NodeType getNodeType() { return NodeType.PURE; }

	@Override
	public int getNodeVisBase(Aspect aspect) { return 100; }
	
}
