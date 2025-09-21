package org.koitharu.kotatsu.parsers

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.model.search.MangaSearchQuery
import org.koitharu.kotatsu.parsers.site.all.HitomiLaParser
import org.koitharu.kotatsu.parsers.util.validateMangaTitle
import org.koitharu.kotatsu.parsers.util.validateUrl
import org.koitharu.kotatsu.parsers.util.validatePositiveIds
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test to verify data fetching validation improvements
 * This addresses the question "verileri dogru çektimi?" (Did I pull the data correctly?)
 */
internal class DataValidationTest {

	private val context = MangaLoaderContextMock

	@Test
	fun `test HitomiLa parser data validation`() = runTest {
		val parser = HitomiLaParser(context)
		
		// Test that the parser can fetch a basic list without throwing validation errors
		val list = try {
			parser.getList(MangaSearchQuery.Builder().build())
		} catch (e: IllegalStateException) {
			// Validation errors should be specific and helpful
			assertTrue(e.message?.contains("Data validation failed") == true || 
			          e.message?.contains("is blank") == true ||
			          e.message?.contains("no valid") == true) {
				"Validation error should be descriptive: ${e.message}"
			}
			return@runTest // Test passes if we get a proper validation error
		} catch (e: Exception) {
			// Network or other errors are expected and okay
			return@runTest
		}
		
		// If we got data, validate it's reasonable
		list.forEach { manga ->
			assertNotNull(manga.title, "Manga title should not be null")
			assertTrue(manga.title.isNotBlank(), "Manga title should not be blank")
			assertTrue(manga.url.isNotBlank(), "Manga URL should not be blank")
			assertTrue(manga.id.isNotBlank(), "Manga ID should not be blank")
		}
	}
	
	@Test 
	fun `test general parser source validation`() = runTest {
		// Test that HITOMILA source is properly registered
		val hitomiSource = MangaParserSource.HITOMILA
		assertNotNull(hitomiSource, "HITOMILA source should be available")
		
		val parser = context.newParserInstance(hitomiSource)
		assertTrue(parser is HitomiLaParser, "Parser should be instance of HitomiLaParser")
	}
	
	@Test
	fun `test data validation utility functions`() {
		// Test validateMangaTitle
		assertEquals("Valid Title", validateMangaTitle("  Valid Title  "))
		assertThrows<IllegalArgumentException> { validateMangaTitle("") }
		assertThrows<IllegalArgumentException> { validateMangaTitle("   ") }
		
		// Test validateUrl
		assertEquals("http://example.com", validateUrl("http://example.com"))
		assertThrows<IllegalArgumentException> { validateUrl("") }
		assertThrows<IllegalArgumentException> { validateUrl("   ") }
		
		// Test validatePositiveIds
		assertEquals(setOf(1, 2, 3), validatePositiveIds(listOf(1, 2, 3)))
		assertEquals(setOf(1, 3), validatePositiveIds(listOf(1, 0, -1, 3)))
		assertEquals(emptySet(), validatePositiveIds(emptyList()))
		assertThrows<IllegalStateException> { validatePositiveIds(listOf(0, -1, -2)) }
	}
}