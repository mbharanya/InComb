/**
 * This class is generated by jOOQ
 */
package com.incomb.server.model.tables;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RssFeedContentSourceTable extends org.jooq.impl.TableImpl<com.incomb.server.model.records.RssFeedContentSourceRecord> {

	private static final long serialVersionUID = 2004621744;

	/**
	 * The reference instance of <code>incomb.rss_feed_content_source</code>
	 */
	public static final com.incomb.server.model.tables.RssFeedContentSourceTable RSS_FEED_CONTENT_SOURCE = new com.incomb.server.model.tables.RssFeedContentSourceTable();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.incomb.server.model.records.RssFeedContentSourceRecord> getRecordType() {
		return com.incomb.server.model.records.RssFeedContentSourceRecord.class;
	}

	/**
	 * The column <code>incomb.rss_feed_content_source.content_source_id</code>.
	 */
	public final org.jooq.TableField<com.incomb.server.model.records.RssFeedContentSourceRecord, java.lang.Integer> CONTENT_SOURCE_ID = createField("content_source_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * Create a <code>incomb.rss_feed_content_source</code> table reference
	 */
	public RssFeedContentSourceTable() {
		this("rss_feed_content_source", null);
	}

	/**
	 * Create an aliased <code>incomb.rss_feed_content_source</code> table reference
	 */
	public RssFeedContentSourceTable(java.lang.String alias) {
		this(alias, com.incomb.server.model.tables.RssFeedContentSourceTable.RSS_FEED_CONTENT_SOURCE);
	}

	private RssFeedContentSourceTable(java.lang.String alias, org.jooq.Table<com.incomb.server.model.records.RssFeedContentSourceRecord> aliased) {
		this(alias, aliased, null);
	}

	private RssFeedContentSourceTable(java.lang.String alias, org.jooq.Table<com.incomb.server.model.records.RssFeedContentSourceRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.incomb.server.model.tables.IncombTable.INCOMB, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<com.incomb.server.model.records.RssFeedContentSourceRecord> getPrimaryKey() {
		return com.incomb.server.model.tables.Keys.KEY_RSS_FEED_CONTENT_SOURCE_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<com.incomb.server.model.records.RssFeedContentSourceRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<com.incomb.server.model.records.RssFeedContentSourceRecord>>asList(com.incomb.server.model.tables.Keys.KEY_RSS_FEED_CONTENT_SOURCE_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<com.incomb.server.model.records.RssFeedContentSourceRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<com.incomb.server.model.records.RssFeedContentSourceRecord, ?>>asList(com.incomb.server.model.tables.Keys.FK_RSSFEEDCONTENTSOURCE_CONTENTSOURCE1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.incomb.server.model.tables.RssFeedContentSourceTable as(java.lang.String alias) {
		return new com.incomb.server.model.tables.RssFeedContentSourceTable(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.incomb.server.model.tables.RssFeedContentSourceTable rename(java.lang.String name) {
		return new com.incomb.server.model.tables.RssFeedContentSourceTable(name, null);
	}
}
