package de.st_ddt.crazyutil.paramitrisable;

public abstract class NumberParamitrisable<S extends Number> extends TypedParamitrisable<S>
{

	public NumberParamitrisable(final S defaultValue)
	{
		super(defaultValue);
	}

	public S getValue(final S def)
	{
		return value == null ? def : getValue();
	}

	public byte getValueAByte(final byte def)
	{
		return value == null ? def : getValue().byteValue();
	}

	public int getValueAsInteger(final int def)
	{
		return value == null ? def : getValue().intValue();
	}

	public long getValueAsInteger(final long def)
	{
		return value == null ? def : getValue().longValue();
	}

	public float getValueAsInteger(final float def)
	{
		return value == null ? def : getValue().floatValue();
	}

	public double getValueAsInteger(final double def)
	{
		return value == null ? def : getValue().doubleValue();
	}
}
