package org.koitharu.kotatsu.parsers.util

/**
 * Utility functions for validating fetched data integrity.
 * Addresses the question "verileri dogru çektimi?" (Did I pull the data correctly?)
 */

/**
 * Validates that a manga title is not blank
 * @param title The title to validate
 * @param context Additional context for error message (e.g., manga ID)
 * @throws IllegalArgumentException if title is blank
 */
public fun validateMangaTitle(title: String, context: String = ""): String {
	require(title.isNotBlank()) { 
		"Manga title is blank${if (context.isNotEmpty()) " for $context" else ""}" 
	}
	return title.trim()
}

/**
 * Validates that a URL is not blank
 * @param url The URL to validate  
 * @param urlType Type of URL for error message (e.g., "cover", "public")
 * @param context Additional context for error message (e.g., manga ID)
 * @throws IllegalArgumentException if URL is blank
 */
public fun validateUrl(url: String, urlType: String = "URL", context: String = ""): String {
	require(url.isNotBlank()) { 
		"$urlType is blank${if (context.isNotEmpty()) " for $context" else ""}" 
	}
	return url
}

/**
 * Validates that a collection of IDs contains only positive integers
 * @param ids Collection of IDs to validate
 * @param source Source of the IDs for error message
 * @return Filtered collection containing only valid positive IDs
 */
public fun validatePositiveIds(ids: Collection<Int>, source: String = ""): Set<Int> {
	val validIds = ids.filter { it > 0 }.toSet()
	
	// If we had data but no valid IDs, that's suspicious
	if (ids.isNotEmpty() && validIds.isEmpty()) {
		throw IllegalStateException(
			"Data validation failed: got ${ids.size} IDs but none were valid positive integers" +
			if (source.isNotEmpty()) " from $source" else ""
		)
	}
	
	return validIds
}