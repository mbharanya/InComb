/**
 * This class is generated by jOOQ
 */
package com.incomb.server.model.records;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TagPreferenceRecord extends org.jooq.impl.UpdatableRecordImpl<com.incomb.server.model.records.TagPreferenceRecord> implements org.jooq.Record2<java.lang.Long, java.lang.String> {

	private static final long serialVersionUID = 357190054;

	/**
	 * Setter for <code>incomb.tag_preference.user_id</code>.
	 */
	public void setUserId(java.lang.Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>incomb.tag_preference.user_id</code>.
	 */
	public java.lang.Long getUserId() {
		return (java.lang.Long) getValue(0);
	}

	/**
	 * Setter for <code>incomb.tag_preference.tag</code>.
	 */
	public void setTag(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>incomb.tag_preference.tag</code>.
	 */
	public java.lang.String getTag() {
		return (java.lang.String) getValue(1);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record2<java.lang.Long, java.lang.String> key() {
		return (org.jooq.Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row2<java.lang.Long, java.lang.String> fieldsRow() {
		return (org.jooq.Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row2<java.lang.Long, java.lang.String> valuesRow() {
		return (org.jooq.Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field1() {
		return com.incomb.server.model.tables.TagPreferenceTable.TAG_PREFERENCE.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return com.incomb.server.model.tables.TagPreferenceTable.TAG_PREFERENCE.TAG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value1() {
		return getUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value2() {
		return getTag();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagPreferenceRecord value1(java.lang.Long value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagPreferenceRecord value2(java.lang.String value) {
		setTag(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagPreferenceRecord values(java.lang.Long value1, java.lang.String value2) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached TagPreferenceRecord
	 */
	public TagPreferenceRecord() {
		super(com.incomb.server.model.tables.TagPreferenceTable.TAG_PREFERENCE);
	}

	/**
	 * Create a detached, initialised TagPreferenceRecord
	 */
	public TagPreferenceRecord(java.lang.Long userId, java.lang.String tag) {
		super(com.incomb.server.model.tables.TagPreferenceTable.TAG_PREFERENCE);

		setValue(0, userId);
		setValue(1, tag);
	}
}
