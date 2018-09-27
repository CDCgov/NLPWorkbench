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
package gov.cdc.lappsgrid.utils.messages;

import java.io.IOException;

/**
 * The Messages class holds static final instances of the class that contain
 * messages; in this case just an instance of the ErrorMessages class.
 * <p>
 * Run the <code>main</code> method to generate a properties file containing
 * all the default messages.
 */
public class Messages
{
	public static final ErrorMessages Error = new ErrorMessages();

	/**
	 * Have a private default constructor to prevent instances being created.
	 */
	private Messages()
	{
		// Intentionally left empty.
	}

	public static void main(String[] args) {
		try
		{
			new ErrorMessages().save(true);
		}
		catch (InstantiationException | IllegalAccessException | IOException e)
		{
			e.printStackTrace();
		}
	}

}
