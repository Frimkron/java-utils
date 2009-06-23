package uk.co.markfrimston.utils.tests;

import uk.co.markfrimston.utils.*;
import org.junit.*;
import static org.junit.Assert.*;

public class StringUtilsTest
{
	@Test
	public void testEscape()
	{
		String input,expected,output;
		
		input = "The 'cat' sat\n on\r the mat";
		expected = "The \\'cat\\' sat\\n on\\r the mat";
		output = StringUtils.sqEscape(input);
		assertEquals(expected, output);
		
		input = "The \"cat\" sat\n on\r the mat";
		expected = "The \\\"cat\\\" sat\\n on\\r the mat";
		output = StringUtils.dqEscape(input);
		assertEquals(expected, output);
	}
}
