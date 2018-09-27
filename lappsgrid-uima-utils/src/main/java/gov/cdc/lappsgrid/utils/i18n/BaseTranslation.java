/*-
 * Copyright 2018 The Centers for Disease Control and Prevention
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package gov.cdc.lappsgrid.utils.i18n;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * The BaseTranslation class is used to provide alternate language translations
 * for the static text in an application.
 * <p>
 * Extend the BaseTranslation class and define <code>public final
 * String</code> instance fields for each translatable string. When an instance
 * of the subcleass is created the BaseTranslation class will
 * initialize these fields from a {@link java.util.ResourceBundle}.
 * <b>Note</b> It is important the the fields are not declared <em>static</em>.
 * <p>
 * The fields should be initialized to null and the default values specified
 * with a <code>@Default</code> annotation on the field.
 * Derived classes <b>must</b> call the <code>init</code> method in their
 * constructor to initialize the <code>public final String</code> fields. The
 * <code>BaseTranslation</code> class will load the ResourceBundle for the
 * current locale and use the String values in the bundle to initialize the
 * fields in the class.
 * <p>
 * Call the {@link #save}
 * method to generate a properties file containing the default text for each
 * field. The name of the properties file is the fully qualified class name
 * of the subclass. Use this file as the basis for other translations.
 * <p>
 *
 * <b>Example</b>
 * <p>
 * In Messages.java
 *
 * <pre>
 *     class Messages extends BaseTranslation
 *     {
 *         @Default("Hello world.")
 *         public final String HELLO_WORLD = null;
 *
 *         public Messages()
 *         {
 *             init();
 *         }
 *     }
 * </pre>
 * <p>
 * In MyApp.java
 *
 * <pre>
 *     package org.anc.example
 *     import org.anc.example.i18n.Messages;
 *     public class MyApp
 *     {
 *         public static final MESSAGES = new Messages();
 *
 *         public static void main(String[] args)
 *         {
 *             System.out.println(MESSAGES.HELLO_WORLD);
 *             MESSAGES.save();
 *         }
 *     }
 *
 * </pre>
 *
 * <p>
 *
 * @author Keith Suderman
 * @since Version 2.0.0
 *
 */
public class BaseTranslation
{
	/**
	 * Annotation type used by subclasses to declare default values for the
	 * String constants.
	 *
	 * @author Keith Suderman
	 *
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Default
	{
		String value();
	}

	public static final String LANG_PROPERTY = "org.anc.lang";
	public static final String DEFAULT_LANG_LOCATION = "i18n";

	/**
	 * Locale used by the text strings. Used as the source language when
	 * translating into other languages. This field will be set by the
	 * {@link #init} method.
	 */
//   protected Locale locale;

	public BaseTranslation()
	{

	}

	public static String complete(String template, String arg1)
	{
		return replace(template, "$1", arg1);
	}

	public static String complete(String template, String arg1, String arg2)
	{
		String result = replace(template, "$1", arg1);
		return replace(result, "$2", arg2);
	}

	public static String complete(String template, String[] args)
	{
		String result = template;
		int i = 0;
		for (String arg : args)
		{
			++i;
			String pattern = "$" + i;
			result = replace(result, pattern, arg);
		}
		return result;
	}

	public static String replace(String template, String pattern,
								 String replacement)
	{
		if (template.indexOf(pattern) < 0)
		{
			return template;
		}
		return template.replace(pattern, replacement);
	}

	public static void translate(BaseTranslation[] classes, String lang)
			throws InstantiationException, IllegalAccessException,
			FileNotFoundException, UnsupportedEncodingException
	{
		for (BaseTranslation msgClass : classes)
		{
			msgClass.translate(lang);
		}
	}

	public void translate(Locale locale) throws FileNotFoundException,
			UnsupportedEncodingException, InstantiationException,
			IllegalAccessException
	{
		translate(locale.getCountry().toLowerCase());
	}

	public void translate(String lang) throws InstantiationException,
			IllegalAccessException, FileNotFoundException,
			UnsupportedEncodingException
	{
		File directory = new File(DEFAULT_LANG_LOCATION);
		if (!directory.exists())
		{
			if (!directory.mkdirs())
			{
				System.err.println("Unable to create the directory "
						+ directory.getPath());
				return;
			}
		}
		String className = this.getClass().getName();
		File file = new File(directory, className + "." + lang);
		if (file.exists())
		{
			System.err.println("Skipping. Translation already exists.");
			return;
		}
	}

	public void save() throws InstantiationException, IllegalAccessException, IOException
	{
		save(false);
	}

	public void save(boolean overwrite) throws InstantiationException, IllegalAccessException,
			IOException
	{
		save(new File("src/main/resources"), overwrite);
	}

	public void save(String path) throws InstantiationException,
			IllegalAccessException, IOException
	{
		save(new File(path), false);
	}

	public void save(String path, boolean overwrite) throws InstantiationException, IllegalAccessException, IOException
	{
		save(new File(path), overwrite);
	}

	public void save(File directory) throws InstantiationException, IllegalAccessException, IOException
	{
		save(directory, false);
	}

	public void save(File directory, boolean overwrite) throws InstantiationException,
			IllegalAccessException, IOException
	{
		if (!directory.exists())
		{
			throw new FileNotFoundException("Specified directory does not exist: " + directory.getPath());
		}

		String className = BaseTranslation.getClassName(this.getClass());
		File file = new File(directory, className + ".properties"); // $NON-NLS-1$
		if (file.exists() && !overwrite)
		{
			System.out.println("Default language file already exists.");
			return;
		}
		System.out.println("Saving messages for " + className + " to "
				+ file.getPath());
		write(file);
	}

	public void write(File file) throws IOException
	{
		write(new FileOutputStream(file), "Default translation."); // $NON-NLS-1$
	}

	public void write(OutputStream out)
	{
		write(out, "Default translation"); // $NON-NLS-1$
	}

	public void write(OutputStream out, String comment)
	{
		Properties props = new Properties();
		try
		{
			Class<? extends BaseTranslation> theClass = this.getClass();
			Field[] fields = theClass.getFields();
			for (Field field : fields)
			{
				// Only save non-static final strings. Everything else
				// should be ignored.
				if (isTranslatable(field))
				{
					String name = field.getName();
					String value = (String) field.get(this);
					if (value != null)
					{
						props.setProperty(name, value);
					}
					else
					{
						System.out.println("WARNING: " + name + " has not been initialized.");
					}
				}
			}
			props.store(out, comment);

		}
		catch (FileNotFoundException e)
		{
			// This should never happen since we test if the files exists.
			// So if it does happen it means something REALLY bad went wrong so
			// we promote this to a RuntimeException.
			throw new RuntimeException("Unable to save the translation file.", e);
		}
		catch (Exception e)
		{
			// TODO this should be logged.
			e.printStackTrace();
		}
		finally
		{
			if (out != null)
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					// nothing we can do, so do nothing.
				}
		}
	}

	protected void init()
	{
		init(Locale.US);
	}

	protected void init(Locale locale)
	{
		Class<? extends BaseTranslation> subclass = this.getClass();
		String className = getClassName(subclass);

		ResourceBundle translation = null;
		try
		{
			translation = ResourceBundle.getBundle(className);
		}
		catch (MissingResourceException e)
		{
			System.err.println(className + ": No resource bundle found for the default Locale, using English translation.");
			// Ignore the missing resource exception and use the values provided
			// by the @Default annotations.
		}

		// Now that we have figured out the language we can initialize the
		// fields.
		Field[] fields = subclass.getDeclaredFields();
		for (Field field : fields)
		{
			// We only set non-static public final String fields.
			if (isTranslatable(field))
			{
				// Not sure if this is needed any more, but it is a final field.
				field.setAccessible(true);

				// Try to get the field value from the property file. If that
				// does not work then use the value of the @Default annotation.
				String value = null;
				if (translation != null)
				{
					try
					{
						value = translation.getString(field.getName());
					}
					catch (MissingResourceException e)
					{
						// Leaving value == nulll will cause the default value to
						// be used.  However, we should notify the user that the
						// resource is incomplete.
						System.err.println(className + ": Resource bundle is missing a string for the key "
								+ field.getName());
					}
				}
				// If the value is still null initialize with the value of the
				// Default annotation.
				if (value == null)
				{
					Default defaultValue = field.getAnnotation(Default.class);
					if (defaultValue != null)
					{
						value = defaultValue.value();
					}
					else
					{
						// If the defaultValue is null then there is a programming
						// error and someone forgot the @Default annotation on a
						// field. Programming errors like this should be caught early
						// and thrown as hard as possible.
						throw new RuntimeException(
								"Missing a @Default annotation on " + field.getName());
					}
				}

				// Now try setting the field. Log any errors but we need to
				// continue, even in the event of an exception.
				try
				{
					field.set(this, value);
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace(System.err);
				}
			}
		}
	}

	protected static String getClassName(Class<?> theClass)
	{
		//return theClass.getName().replace(".", "_");
		return theClass.getSimpleName();
	}

	/**
	 * Returns true if this field should be initialized at runtime with the
	 * value of its <code>@Default</code> attribute (if any).
	 * <p>
	 * A field is tranlatable <i>iff</i>:
	 * <ul>
	 * <li>it holds a String,
	 * <li>it is public,
	 * <li>it is final, and
	 * <li>it is <b>not</b> static.
	 * </ul>
	 * @param field The field to test.
	 */
	protected boolean isTranslatable(Field field)
	{
		int flags = field.getModifiers();
		return field.getType().equals(String.class) && Modifier.isPublic(flags)
				&& Modifier.isFinal(flags) && !Modifier.isStatic(flags);
	}
}
