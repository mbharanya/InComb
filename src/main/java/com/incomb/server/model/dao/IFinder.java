package com.incomb.server.model.dao;

import java.util.List;

/**
 * The {@link IFinder} interface is for DAO classes. The reason is, jOOQ's DAOs, and our DAOs aren't subclassable.
 * So this class is used for a common ground.
 * 
 * @param <T> Class the {@link IFinder} is searching for.
 */
public interface IFinder<T> {

	/**
	 * Method for finding all objects of e specific type.
	 * @return
	 */
	public List<T> findAll();

	/**
	 * Returns the class which is the {@link IFinder} searching on the database
	 * @return
	 */
	public Class<?> findForClass();

}
