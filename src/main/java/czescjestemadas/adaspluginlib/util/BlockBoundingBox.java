package czescjestemadas.adaspluginlib.util;

import org.bukkit.util.Vector;

import static czescjestemadas.adaspluginlib.util.NumUtil.isInRange;

public class BlockBoundingBox
{
	public int minX, minY, minZ;
	public int maxX, maxY, maxZ;

	public BlockBoundingBox()
	{
		this(0, 0, 0, 0, 0, 0);
	}

	public BlockBoundingBox(Vector min, Vector max)
	{
		this(min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());
	}

	public BlockBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	public boolean contains(int x, int y, int z)
	{
		return isInRange(x, minX, maxX) && isInRange(y, minY, maxY) && isInRange(z, minZ, maxZ);
	}

	public boolean contains(Vector vector)
	{
		return contains(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}

	@Override
	public String toString()
	{
		return "BlockBoundingBox{" +
				"minX=" + minX +
				", minY=" + minY +
				", minZ=" + minZ +
				", maxX=" + maxX +
				", maxY=" + maxY +
				", maxZ=" + maxZ +
				'}';
	}


	public static BlockBoundingBox of(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		return new BlockBoundingBox(
				Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
				Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)
		);
	}
}
