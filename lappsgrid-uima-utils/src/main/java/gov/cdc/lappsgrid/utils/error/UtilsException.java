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
package gov.cdc.lappsgrid.utils.error;

/**
 * Exception thrown by the utility methods.
 */
public class UtilsException extends Exception
{
	public UtilsException()
	{

	}

	public UtilsException(String message)
	{
		super(message);
	}

	public UtilsException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public UtilsException(Throwable cause)
	{
		super(cause);
	}

	public UtilsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
