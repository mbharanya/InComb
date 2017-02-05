package com.incomb.server.indexing.util;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;

import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.conf.IIndexTypeConf;
import com.incomb.server.indexing.conf.fields.IIndexFieldConf;

/**
 * Contains methods to build queries for different conditions.
 */
public class QueryUtil {

	/**
	 * Filters the given {@link BooleanQuery} with the given {@link IIndexTypeConf}.
	 */
	public static void addTypeConf(final BooleanQuery query, final IIndexTypeConf indexType) {
		query.add(new TermQuery(new Term(IIndexElement.FIELD_INDEX_TYPE, indexType.getName())), Occur.MUST);
	}

	/**
	 * Filters the given {@link BooleanQuery} with the given {@link Locale} which may be null.
	 * In this case the query will not be modified.
	 */
	public static void addLocale(final BooleanQuery query, final Locale locale) {
		if(locale != null) {
			query.add(new TermQuery(new Term(IIndexElement.FIELD_LOCALE, locale.getLanguage())), Occur.MUST);
		}
	}

	/**
	 * Filters the given {@link BooleanQuery} to find only documents with the given value
	 * in the {@link IIndexElement#FIELD_ID} field.
	 */
	public static void addId(final BooleanQuery query, final String id) {
		query.add(new TermQuery(new Term(IIndexElement.FIELD_ID, id)), Occur.MUST);
	}

	/**
	 * Adds a filter for the given search text to the given {@link BooleanQuery}.
	 * If the search text is blank, than the query will not modified.
	 *
	 * Base boost will be 1.0f. The more the match is exact than a bigger boost will be used.
	 *
	 * @param query the {@link BooleanQuery} to add the sub queries.
	 * @param searchText the search text. May be blank (null or contain only white spaces).
	 * @param indexType the type in which should be searched.
	 */
	public static void addSearchText(final BooleanQuery query,
			final String searchText, final IIndexTypeConf indexType) {
		addSearchText(query, searchText, indexType, 1.0f);
	}

	/**
	 * Adds a filter for the given search text to the given {@link BooleanQuery}.
	 * If the search text is blank, than the query will not modified.
	 * For each part of the search text {@link #buildSearchTermQuery(IIndexTypeConf, String, float)}
	 * will be called.
	 *
	 * @param query the {@link BooleanQuery} to add the sub queries.
	 * @param searchText the search text. May be blank (null or contain only white spaces).
	 * @param indexType the type in which should be searched.
	 * @param baseBoost highest possible boost of the query. The more the match is exact
	 * 			than a bigger boost will be used.
	 */
	public static void addSearchText(final BooleanQuery query,
			final String searchText, final IIndexTypeConf indexType, final float baseBoost) {

		if(StringUtils.isBlank(searchText)) {
			return;
		}

		query.setMinimumNumberShouldMatch(1); // at least one "should" should match
		query.add(buildSearchTermQuery(indexType, searchText, baseBoost), Occur.SHOULD);

		final BooleanQuery partsQuery = new BooleanQuery();
		query.add(partsQuery, Occur.SHOULD);

		final String[] searchTexts = searchText.toLowerCase().split("\\s");
		for (final String search : searchTexts) {
			partsQuery.add(buildSearchTermQuery(indexType, search, baseBoost), Occur.MUST); // each part has to match
		}
	}

	/**
	 * Returns a {@link BooleanQuery} for the given search text.
	 *
	 * @param indexType the type in which should be searched.
	 * @param search the search text.
	 * @param baseBoost highest possible boost of the query. The more the match is exact
	 * 			than a bigger boost will be used.
	 */
	private static BooleanQuery buildSearchTermQuery(final IIndexTypeConf indexType, final String search,
			final float baseBoost) {
		final BooleanQuery subQuery = new BooleanQuery();

		final String lowerCase = StringUtils.lowerCase(search);
		final String capitalized = StringUtils.capitalize(search);

		addSearchTermQueries(indexType, search, subQuery, baseBoost);

		if(!lowerCase.equals(search)) {
			addSearchTermQueries(indexType, lowerCase, subQuery, 0.8f*baseBoost);
		}

		if(!capitalized.equals(search)) {
			addSearchTermQueries(indexType, capitalized, subQuery, 0.8f*baseBoost);
		}

		return subQuery;
	}

	/**
	 * Adds for every field in {@link IIndexTypeConf} a {@link TermQuery}, {@link PrefixQuery} and
	 * a {@link FuzzyQuery} with a suitable boost relative to the given base boost.
	 *
	 * @param indexType the type in which should be searched.
	 * @param search the search text.
	 * @param subQuery the {@link BooleanQuery} to add the sub queries.
	 * @param baseBoost highest possible boost of the query. The more the match is exact
	 * 			than a bigger boost will be used.
	 */
	private static void addSearchTermQueries(final IIndexTypeConf indexType, final String search,
			final BooleanQuery subQuery, final float baseBoost) {

		for(final IIndexFieldConf<?> field : indexType.getFields()) {
			final Term term = new Term(field.getName(), search);

			final TermQuery exactQuery = new TermQuery(term);
			exactQuery.setBoost(baseBoost);
			subQuery.add(exactQuery, Occur.SHOULD);

			final PrefixQuery pfQuery = new PrefixQuery(term);
			pfQuery.setBoost(0.7f*baseBoost);
			subQuery.add(pfQuery, Occur.SHOULD);

			final FuzzyQuery fuzzyQuery = new FuzzyQuery(term);
			fuzzyQuery.setBoost(0.5f*baseBoost);
			subQuery.add(fuzzyQuery, Occur.SHOULD);
		}
	}
}
