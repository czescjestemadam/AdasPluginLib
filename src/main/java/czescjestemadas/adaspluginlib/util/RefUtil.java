package czescjestemadas.adaspluginlib.util;

import java.lang.ref.Reference;

public abstract class RefUtil
{
	public static <T> T safeGet(Reference<T> ref)
	{
		return ref == null ? null : ref.get();
	}
}
