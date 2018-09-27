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

import gov.cdc.lappsgrid.utils.i18n.BaseTranslation;

/**
 * Error messages emitted by the utility methods.
 * <p>
 * If the ErrorMessages.properties file does not exist, or if it does not contain
 * a definition for the field the value of the <code>@Default</code> annotation
 * will be used.
 */
public class ErrorMessages extends BaseTranslation
{
	@Default("Unable to save the TypeSystemDescription.")
	public final String SAVING_TYPESYSTEM = null;

	@Default("Unable to read the string.")
	public final String READING_STRING = null;
	@Default("Unable to create the CAS object.")
	public final String CREATING_CAS = null;
	@Default("Uable to load the CAS object.")
	public final String LOADING_CAS = null;
	@Default("Unable to open the input stream.")
	public final String OPENING_STREAM = null;

	@Default("Unable to merge default type systems.")
	public final String MERGING_TYPE_SYSTEM = null;
	@Default("Unable to parse type system description")
	public final String PARSING_TYPE_SYSTEM = null;
	@Default("Unable to load type system description")
	public final String LOADING_TYPE_SYSTEM = null;
	@Default("Unable to get resource URL for")
	public final String GETTING_RESOURCE_URL = null;
	@Default("/typesystem directory not found.")
	public final String DIRECTORY_NOT_FOUND = null;

	/* The following are used for testing only. */
	@Default("Fail")
	public final String FROM_FILE = null;

	// whenever a new properties file is generated this should be commented out.
	@Default("passed")
	public final String FROM_ANNOTATION = null;


	ErrorMessages()
	{
		init();
	}

}
