package com.incomb.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.incomb.server.ATest;
import com.incomb.server.utils.ConfigUtil;

public class ConfigTest extends ATest {
	private final Config config = new Config(ConfigUtil.getDocBase() + "configTest.json");

	@Test
	public void testBasicValues() throws JsonParseException, JsonMappingException, IOException {
		assertEquals("John", config.getProperty("firstName", String.class));
		assertEquals(true, config.getProperty("isAlive", Boolean.class));
		assertEquals(new Integer(25), config.getProperty("age", Integer.class));
		assertEquals(new Double(167.6), config.getProperty("height_cm", Double.class));
		assertNull(config.getProperty("spouse", Object.class));
	}
	
	@Test
	@Ignore
	public void testPrimitiveDataTypes(){
		assertEquals(true, (boolean) config.getProperty("isAlive", boolean.class));
		assertEquals(25, (int) config.getProperty("age", int.class));
		assertEquals(167.6d, config.getProperty("height_cm", double.class), 0.0);
	}

	@Test
	public void testArray() throws JsonParseException, JsonMappingException, IOException {
		assertTrue(config.getProperty("phoneNumbers", List.class) instanceof List);
		final List<Map<String, String>> testList = new ArrayList<>();

		Map<String, String> testMapEntry = new HashMap<>();
		testMapEntry.put("type", "home");
		testMapEntry.put("number", "212 555-1234");
		testList.add(testMapEntry);
		testMapEntry = new HashMap<>();
		testMapEntry.put("type", "office");
		testMapEntry.put("number", "646 555-4567");
		testList.add(testMapEntry);

		assertEquals(testList, config.getProperty("phoneNumbers", List.class));
		assertEquals(new ArrayList<>(), config.getProperty("children", List.class));
	}

	@Test
	public void testMap() throws JsonParseException, JsonMappingException, IOException {
		assertTrue(config.getProperty("address", Map.class) instanceof Map<?, ?>);
		final Map<String, Object> testMap = new HashMap<>();
		testMap.put("streetAddress", "21 2nd Street");
		testMap.put("city", "New York");

		final Map<String, Object> locationMap = new LinkedHashMap<String, Object>();
		locationMap.put("state", "NY");
		locationMap.put("zip", 123456);

		testMap.put("location", locationMap);
		assertEquals(testMap, config.getProperty("address", Map.class));
	}

	@Test
	public void testCascadingProperty() throws JsonParseException, JsonMappingException, IOException {
		assertEquals("New York", config.getProperty("address.city", String.class));
		assertEquals(123456, config.getIntProperty("address.location.zip"));
		assertEquals("NY", config.getProperty("address.location.state", String.class));
	}

	@Test
	public void testDoesPropertyExist() {
		assertEquals(true, config.doesPropertyExist("address.location.state", String.class));
		assertEquals(false, config.doesPropertyExist("not.existing", String.class));
	}
}
