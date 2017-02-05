package com.incomb.server.indexing.conf.fields;

/**
 * Default implementation a {@link IIndexFieldConf} with the name as a property.
 * @param <T> the type of the content which it wraps.
 */
public abstract class ADefaultIndexFieldConf<T> implements IIndexFieldConf<T> {

	/**
	 * The name of the field.
	 */
	private final String name;

	/**
	 * Creates a new instance with the name of the field.
	 * @param name the name of the field
	 */
	public ADefaultIndexFieldConf(final String name) {
		this.name = name;
	}

	/**
	 * @see com.incomb.server.indexing.conf.fields.IIndexFieldConf#getName()
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}
}
