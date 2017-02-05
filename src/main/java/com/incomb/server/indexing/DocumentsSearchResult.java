package com.incomb.server.indexing;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

public class DocumentsSearchResult extends SimpleSearchResult<Document> {

	/**
	 * Lucenes scores of the found {@link Document}s.
	 * The {@link Document} of the score is referenced by the index.
	 */
	private final List<Float> scores = new ArrayList<>();

	/**
	 * Lucenes {@link ScoreDoc} of the last returned {@link Document}.
	 * If no {@link Document}s were returned than it's <code>null</code>.
	 */
	private ScoreDoc lastScoreDoc = null;

	/**
	 * Adds a {@link Document} with it's Lucene query score to this result.
	 * @param document the {@link Document} to add.
	 * @param score the score of the document
	 */
	public void addDocument(final Document document, final float score) {
		results.add(document);
		scores.add(score);
	}

	/**
	 * Returns Lucenes query score for the given {@link Document}.
	 */
	public float getScore(final Document doc) {
		return scores.get(results.indexOf(doc));
	}

	/**
	 * Returns Lucenes {@link ScoreDoc} of the last returned {@link Document}.
	 * If no {@link Document}s were returned than it returns <code>null</code>.
	 */
	public ScoreDoc getLastScoreDoc() {
		return lastScoreDoc;
	}

	/**
	 * Sets Lucenes {@link ScoreDoc} of the last returned {@link Document}.
	 */
	public void setLastScoreDoc(final ScoreDoc lastScoreDoc) {
		this.lastScoreDoc = lastScoreDoc;
	}
}
