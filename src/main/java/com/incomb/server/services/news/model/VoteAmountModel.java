package com.incomb.server.services.news.model;

import com.incomb.server.model.News;

/**
 * Holds the amount of Ins and Combs of a {@link News}.
 */
public class VoteAmountModel {

	/**
	 * The amount of given Ins to a {@link News}.
	 */
	private final int insAmount;

	/**
	 * The amount of given Combs to a {@link News}.
	 */
	private final int combsAmount;

	/**
	 * Create a new instance with all properties.
	 * @param insAmount the amount of given Ins to a {@link News}.
	 * @param combsAmount the amount of given Combs to a {@link News}.
	 */
	public VoteAmountModel(final int insAmount, final int combsAmount) {
		this.insAmount = insAmount;
		this.combsAmount = combsAmount;
	}

	/**
	 * @return the amount of given Ins to a {@link News}.
	 */
	public int getInsAmount() {
		return insAmount;
	}

	/**
	 * @return the amount of given Combs to a {@link News}.
	 */
	public int getCombsAmount() {
		return combsAmount;
	}
}
