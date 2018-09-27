package gov.cdc.lappsgrid.utils.messages;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 *
 */
public class MessageTest
{
	private static final int PUBLIC_FINAL = Modifier.FINAL | Modifier.PUBLIC;
	@Test
	public void testMessages() throws IllegalAccessException
	{
		/* We just check that the fields are public and final and are not null. */
		Class<?> theClass = Messages.Error.getClass();
		for (Field field : theClass.getDeclaredFields()) {
			System.out.println("Field: " + field.getName());
			assertEquals(String.class, field.getType());
			assertEquals(PUBLIC_FINAL, field.getModifiers());
			Object message = field.get(Messages.Error);
			assertNotNull(message);
			System.out.println(message.toString());
		}
	}
}
