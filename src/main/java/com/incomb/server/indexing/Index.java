package com.incomb.server.indexing;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.utils.ConfigUtil;

/**
 * This class represents a Lucene index.
 * It abstracts Lucenes {@link IndexWriter}.
 *
 * No public methods because this is a class which may be used only in the indexing package.
 */
class Index implements Closeable {

	/**
	 * The location in the file system where the index will be stored.
	 */
	static final String INDEX_LOCATION = ConfigUtil.getDocBase() + "/WEB-INF/indexes/";

	/**
	 * {@link Analyzer} for locale independent {@link Document}s.
	 */
	private static final Analyzer DEFAULT_ANALYZER = new WhitespaceAnalyzer();

	/**
	 * For each locale language is one language dependent {@link Analyzer} assigned.
	 *
	 * <br /><br />
	 * <table>
	 * 	<tr>
	 * 		<th>Language</th>
	 * 		<th>Analyzer</th>
	 *	</tr>
	 *	<tr>
	 *		<td>German (de)</td>
	 *		<td>GermanAnalyzer</td>
	 *	</tr>
	 *	<tr>
	 *		<td>English (en)</td>
	 *		<td>EnglishAnalyzer</td>
	 *	</tr>
	 * </table>
	 */
	private static final Map<String, Analyzer> ANALYZERS = new HashMap<String, Analyzer>();
	{
		{
			ANALYZERS.put(Locale.GERMAN.getLanguage(), new GermanAnalyzer());
			ANALYZERS.put(Locale.ENGLISH.getLanguage(), new EnglishAnalyzer());
		}
	}

	/**
	 * <p>
	 * The {@link Logger} for this class.
	 * </p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Index.class);

	/**
	 * The name of the index.
	 */
	private final String name;

	/**
	 * The location of the index in the file system.
	 * @see #INDEX_LOCATION
	 */
	private final File location;

	/**
	 * Lucenes {@link Directory} where the index files exists.
	 * @see #location
	 */
	private Directory directory;

	/**
	 * The {@link IndexWriter} to change the index.
	 */
	private IndexWriter writer;

	/**
	 * The {@link IndexReader} to search in the index.
	 * After every commit {@link #commit()} a new {@link IndexReader} will be created so
	 * that changes take effect.
	 */
	private IndexReader reader;

	/**
	 * Creates a new instance for the name.
	 * A directory with the name of the index will be created in {@value #INDEX_LOCATION}
	 * if it doesn't exist yet. {@link IndexWriter} and {@link IndexReader} will be opened.
	 *
	 * @param name the name of the index.
	 */
	Index(final String name) {
		if (name == null) {
			throw new IllegalArgumentException("Index must have a name.");
		}

		this.name = name;
		this.location = new File(INDEX_LOCATION, name);

		// create directories if they don't exist yet
		if (!location.exists()) {
			location.mkdirs();
		}

		// create Directory, IndexWriter and IndexReader
		createDirectory();
		createWriter();
		createReader();
	}

	/**
	 * Creates a new {@link IndexReader} for the {@link #directory} and sets it to {@link #reader}.
	 * If an {@link IOException} during creation will be thrown it will be logged and the old
	 * {@link IndexReader} will be kept.
	 */
	private void createReader() {
		try {
			reader = DirectoryReader.open(directory);
		} catch (final IOException e) {
			LOGGER.error("Exception was thrown during instantiation of IndexReader for location {}.",
					location.getAbsolutePath(), e);
		}
	}

	/**
	 * Creates a new {@link IndexWriter} for the {@link #directory} and sets it to {@link #writer}.
	 * If an {@link IOException} during creation will be thrown it will be logged and the old
	 * {@link IndexWriter} will be kept.
	 */
	private void createWriter() {
		// create IndexWriter
		final IndexWriterConfig config = new IndexWriterConfig(IndexManager.LUCENE_VERSION, DEFAULT_ANALYZER);

		try {
			unlock();
			writer = new IndexWriter(directory, config);
			writer.commit();
		} catch (final IOException e) {
			LOGGER.error("Can't create IndexWriter for path "
					+ "{} because of an exception.", location.getAbsolutePath(), e);
		}
	}

	/**
	 * Creates a new {@link MMapDirectory} for the {@link #location} and sets it to {@link #directory}.
	 * If an {@link IOException} during creation will be thrown it will be logged and the old
	 * {@link Directory} will be kept.
	 */
	private void createDirectory() {
		try {
			directory = new MMapDirectory(location);
		} catch (final IOException e) {
			LOGGER.error("Exception was thrown during instantiaton of MMapDirectory for location {}.",
					location.getAbsolutePath(), e);
		}
	}

	/**
	 * @return The {@link IndexReader} to search in the index.
	 */
	IndexReader getIndexReader() {
		return reader;
	}

	/**
	 * @return The name of the index.
	 */
	String getName() {
		return name;
	}

	/**
	 * Returns the {@link Locale} specific {@link Analyzer}.
	 * If {@link Locale} is null than the {@link #DEFAULT_ANALYZER} will be returned.
	 * @param locale the {@link Locale} to get the {@link Analyzer}.
	 * @return the {@link Analyzer}. Null if no {@link Analyzer} was defined for the given {@link Locale}.
	 */
	Analyzer getAnalyzer(final Locale locale) {
		if(locale == null) {
			return DEFAULT_ANALYZER;
		}

		return ANALYZERS.get(locale.getLanguage());
	}

	/**
	 * Adds the {@link Document} with the locale specific {@link Analyzer} to the index.
	 * @param doc the {@link Document} to add.
	 * @param locale the {@link Locale} to get the {@link Analyzer}.
	 * 			If null than the {@link #DEFAULT_ANALYZER} will be used.
	 * @throws IOException if there is a low-level IO error
	 */
	void addDocument(final Document doc, final Locale locale) throws IOException {
		try {
			if(locale == null) {
				writer.addDocument(doc);
				return;
			}

			writer.addDocument(doc, getAnalyzer(locale));
		}
		catch(final AlreadyClosedException e) {
			reopenIndex();
		}
	}

	/**
	 * Delete all {@link Document}s from the index which match with the given Query.
	 * @param query all {@link Document}s matching this {@link Query} will be removed.
	 * @throws IOException if there is a low-level IO error
	 */
	void deleteDocuments(final Query query) throws IOException {
		try {
			writer.deleteDocuments(query);
		}
		catch(final AlreadyClosedException e) {
			reopenIndex();
		}
	}

	/**
	 * Deletes every document in this index.
	 * Please double check if you want to call this method or {@link #deleteDocuments(Query)}.
	 * @see #deleteDocuments(Query)
	 * @throws IOException if there is a low-level IO error
	 */
	void deleteAllDocuments() throws IOException {
		try {
			writer.deleteAll();
		}
		catch(final AlreadyClosedException e) {
			reopenIndex();
		}
	}

	/**
	 * Commits any changes since the last commit or rollback.
	 * Afterwards a new {@link IndexReader} will be created.
	 * @throws IOException if there is a low-level IO error
	 */
	void commit() throws IOException {
		try {
			writer.commit();

			if(writer.hasDeletions()) {
				writer.forceMergeDeletes(); // delete now the documents which are marked to delete.
			}
		}
		catch(final AlreadyClosedException e) {
			reopenIndex();
		}
		finally {
			unlock();

			// new reader because new data is available.
			createReader();
		}
	}

	/**
	 * Rollbacks any changes since the last commit or rollback.
	 * @throws IOException if there is a low-level IO error
	 */
	void rollback() throws IOException {
		try {
			writer.rollback();
		}
		catch(final AlreadyClosedException e) {
			reopenIndex();
		}
		finally {
			unlock();
		}
	}

	/**
	 * Checks if the index is currently locked and unlocks it if it is.
	 * @throws IOException if there is a low-level IO error
	 */
	private void unlock() throws IOException {
		if(IndexWriter.isLocked(directory)) {
			IndexWriter.unlock(directory);
		}
	}

	/**
	 * Remove this when the problem with closed indexes was fixed.
	 * @throws IOException if there is a low-level IO error
	 */
	private void reopenIndex() throws IOException {
		LOGGER.error("Reopening directory, writer and reader because an AlreadyClosedException was thrown.");
		createDirectory();
		createWriter();
		createReader();
		unlock();
	}

	/**
	 * Closes the {@link IndexWriter}, {@link IndexReader} and other things belonging to the index.
	 */
	@Override
	public void close() throws IOException {
		LOGGER.warn("Index will be closed now! :( Callstack: ", new RuntimeException());
		writer.close();
		reader.close();
		unlock();
		directory.close();
	}
}
