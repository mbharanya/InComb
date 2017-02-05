package com.incomb.server.indexing;

import java.util.Locale;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.indexing.conf.IIndexTypeConf;
import com.incomb.server.indexing.conf.fields.IIndexFieldConf;

/**
 * A element of a {@link IIndexTypeConf} from which a {@link Document} will be build.
 * It returns the values for the fields ({@link IIndexTypeConf#getFields()}) and some
 * meta information about the element.
 */
public interface IIndexElement {

	/**
	 * The name of the {@link Field} in which the id returned by {@link #getId()} will be stored.
	 */
	static final String FIELD_ID = "id";

	/**
	 * The name of the {@link Field} in which {@link IIndexTypeConf#getName()} will be stored.
	 */
	static final String FIELD_INDEX_TYPE = "indexType";

	/**
	 * The name of the {@link Field} in which the locale returned by {@link #getLocale()} will be stored.
	 */
	static final String FIELD_LOCALE = "locale";

	/**
	 * Returns the {@link EOperation} how this element should be modified in the index.
	 */
	EOperation getOperation();

	/**
	 * Returns an identifying identification over all elements with the same {@link IIndexTypeConf}.
	 */
	String getId();

	/**
	 * Returns the {@link Locale} of the element. If the element is locale independent than return <code>null</code>.
	 */
	Locale getLocale();

	/**
	 * Returns the value for the given field name. The fields are configured in {@link IIndexTypeConf#getFields()}.
	 * Please return the value in a type that the {@link IIndexFieldConf} can handle it.
	 * @param fieldName the field name to get the value.
	 * @return the value in the right type.
	 */
	Object getContent(String fieldName);
}
