/**
 * This class is generated by jOOQ
 */
package com.incomb.server.model.dao.internal;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class InternalCategoryPreferenceDao extends org.jooq.impl.DAOImpl<com.incomb.server.model.records.CategoryPreferenceRecord, com.incomb.server.model.CategoryPreference, org.jooq.Record2<java.lang.Long, java.lang.Integer>> {

	/**
	 * Create a new InternalCategoryPreferenceDao without any configuration
	 */
	public InternalCategoryPreferenceDao() {
		super(com.incomb.server.model.tables.CategoryPreferenceTable.CATEGORY_PREFERENCE, com.incomb.server.model.CategoryPreference.class);
	}

	/**
	 * Create a new InternalCategoryPreferenceDao with an attached configuration
	 */
	public InternalCategoryPreferenceDao(org.jooq.Configuration configuration) {
		super(com.incomb.server.model.tables.CategoryPreferenceTable.CATEGORY_PREFERENCE, com.incomb.server.model.CategoryPreference.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected org.jooq.Record2<java.lang.Long, java.lang.Integer> getId(com.incomb.server.model.CategoryPreference object) {
		return compositeKeyRecord(object.getUserId(), object.getCategoryId());
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public java.util.List<com.incomb.server.model.CategoryPreference> fetchByUserId(java.lang.Long... values) {
		return fetch(com.incomb.server.model.tables.CategoryPreferenceTable.CATEGORY_PREFERENCE.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>category_id IN (values)</code>
	 */
	public java.util.List<com.incomb.server.model.CategoryPreference> fetchByCategoryId(java.lang.Integer... values) {
		return fetch(com.incomb.server.model.tables.CategoryPreferenceTable.CATEGORY_PREFERENCE.CATEGORY_ID, values);
	}

	/**
	 * Fetch records that have <code>factor IN (values)</code>
	 */
	public java.util.List<com.incomb.server.model.CategoryPreference> fetchByFactor(java.lang.Double... values) {
		return fetch(com.incomb.server.model.tables.CategoryPreferenceTable.CATEGORY_PREFERENCE.FACTOR, values);
	}
}
