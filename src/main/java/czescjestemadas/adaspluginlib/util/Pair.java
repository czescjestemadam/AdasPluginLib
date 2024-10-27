package czescjestemadas.adaspluginlib.util;

import java.util.Objects;

public class Pair<A, B>
{
	public A a;
	public B b;

	public Pair(A a, B b)
	{
		this.a = a;
		this.b = b;
	}

	public boolean hasA()
	{
		return a != null;
	}

	public A getA()
	{
		return a;
	}

	public void setA(A a)
	{
		this.a = a;
	}

	public boolean hasB()
	{
		return b != null;
	}

	public B getB()
	{
		return b;
	}

	public void setB(B b)
	{
		this.b = b;
	}

	public boolean isEmpty()
	{
		return !hasA() && !hasB();
	}

	public boolean isFull()
	{
		return hasA() && hasB();
	}

	@Override
	public final boolean equals(Object object)
	{
		if (this == object) return true;
		if (!(object instanceof Pair<?, ?> pair)) return false;

		return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(a, b);
	}

	@Override
	public String toString()
	{
		return "Pair{" +
				"a=" + a +
				", b=" + b +
				'}';
	}


	public static <T, R> Pair<T, R> of(T a, R b)
	{
		return new Pair<>(a, b);
	}

	public static <T, R> Pair<T, R> empty()
	{
		return new Pair<>(null, null);
	}
}
