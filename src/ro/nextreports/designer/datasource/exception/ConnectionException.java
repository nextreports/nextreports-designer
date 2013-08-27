/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.designer.datasource.exception;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 15, 2006
 * Time: 2:30:53 PM
 */
public class ConnectionException extends Exception {

    public ConnectionException() {
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }

	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder();
		
		sb.append(super.toString());
		sb.append("\n\r");
		Throwable cause = getCause();
		while (cause != null) {
			sb.append(cause.toString());
			cause = cause.getCause();
		}
		
		return sb.toString();
	}
    
}
