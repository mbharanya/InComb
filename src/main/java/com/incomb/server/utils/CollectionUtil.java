package com.incomb.server.utils;

import java.util.Collections;
import java.util.List;

/**
 * This class contains helper methods for doing things with {@link Collections}.
 */
public class CollectionUtil {

	private CollectionUtil() {
		// private constructor because class contains only static helper methods.
	}

	/**
	 * Returns a {@link List} containing the elements of the given {@link List} from offset and only the given amount.
	 * If there aren't enough elements in the given {@link List} for the requested amount the rest of the {@link List}
	 * will returned. Returns null if the given {@link List} is null.
	 *
	 * @param list the {@link List} to get a sublist from.
	 * @param offset start of elements to return
	 * @param amount amount of elements to return
	 * @return the sublist or null.
	 */
	public static <T> List<T> subList(final List<T> list, final int offset, final int amount) {
		if(list == null) {
			return null;
		}

		if(offset >= list.size()) {
			return Collections.emptyList();
		}

		int toPos = offset + amount;
		if(toPos >= list.size()) {
			toPos = list.size();
		}

		return list.subList(offset, toPos);
	}
}
