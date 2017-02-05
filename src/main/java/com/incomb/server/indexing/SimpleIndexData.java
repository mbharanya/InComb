package com.incomb.server.indexing;

import java.util.ArrayList;
import java.util.List;

import com.incomb.server.indexing.conf.IIndexTypeConf;

/**
 * A simple implementation of {@link IIndexData} with properties for the values.
 */
public class SimpleIndexData implements IIndexData {

	/**
	 * The {@link IIndexTypeConf} of the {@link IIndexElement} returned by {@link #getElements()}.
	 */
	private final IIndexTypeConf indexTypeConf;

	/**
	 * The {@link IIndexElement}s which should be modified in the index.
	 * {@link IIndexElement#getOperation()} returns the wanted operation.
	 */
	private final List<IIndexElement> indexElements = new ArrayList<>();

	/**
	 * Creates a new instance with the given {@link IIndexTypeConf} and an empty index elements list.
	 */
	public SimpleIndexData(final IIndexTypeConf indexTypeConf) {
		this.indexTypeConf = indexTypeConf;
	}

	/**
	 * Creates a new instance with the given {@link IIndexTypeConf} and the {@link List} with the {@link IIndexElement}.
	 */
	public SimpleIndexData(final IIndexTypeConf indexTypeConf,
			final List<? extends IIndexElement> indexElements) {

		this(indexTypeConf);
		this.indexElements.addAll(indexElements);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IIndexTypeConf getConf() {
		return indexTypeConf;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends IIndexElement> getElements() {
		return indexElements;
	}

	/**
	 * Adds a {@link IIndexElement} to the {@link List}.
	 * @param element the {@link IIndexElement} to add.
	 */
	public void addElement(final IIndexElement element) {
		this.indexElements.add(element);
	}
}
