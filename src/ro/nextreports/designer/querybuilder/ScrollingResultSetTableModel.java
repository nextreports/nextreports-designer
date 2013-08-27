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
package ro.nextreports.designer.querybuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.engine.queryexec.QueryResult;

/**
 * This class uses a scrolling cursor, a JDBC 2 feature.
 * 
 * @author Decebal Suiu
 */
public class ScrollingResultSetTableModel extends ResultSetTableModel {

    private static final Log LOG = LogFactory.getLog(ScrollingResultSetTableModel.class);
    
	public ScrollingResultSetTableModel(QueryResult result) {
		super(result);
	}

	/**
	 * Obtain value in particular row and column.
	 */
	public Object getValueAt(int row, int column) {
		if (result == null) {
			return null;
		}

		if (isBlobColumn(column)) {
			return BLOB_VALUE;
		} else if (isClobColumn(column)) {
			return CLOB_VALUE;
		}

		try {
			Object value = result.getValueAt(row, column);
			if (value == null) {
				return NULL_VALUE;
			}

			return value;
		} catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
//            throw new IllegalStateException(e);
		}

		return "";
	}
	
	/**
	 * Return number of rows.
	 */
	public int getRowCount() {
		if (result == null) {
			return 0;
		}

		return result.getRowCount();
	}

}
