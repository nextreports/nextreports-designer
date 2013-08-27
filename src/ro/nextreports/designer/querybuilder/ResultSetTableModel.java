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

import java.sql.Types;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.util.DialectUtil;

/**
 * This class is the base class for the scrolling and the
 *  caching result set table model. It stores the result.
 *  
 * @author Decebal Suiu
 */
public abstract class ResultSetTableModel extends AbstractTableModel {

	public static final String NULL_VALUE = "( null )";
	public static final String BLOB_VALUE = "( blob )";
	public static final String CLOB_VALUE = "( clob )";

    private static final Log LOG = LogFactory.getLog(ResultSetTableModel.class);
    
	protected QueryResult result;

    public ResultSetTableModel(QueryResult result) {
		this.result = result;
	}

	/**
	 * Get class that represents column type.
	 */
	public Class getColumnClass(int column) {
		if (result == null) {
			return Object.class;
		}

		try {
			String className = result.getColumnClassName(column);
			if (!isBlobColumn(column) && !isClobColumn(column)) {				
				return Class.forName(DialectUtil.getFullColumnClassName(className));
			}
		} catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
//            throw new IllegalStateException(e);
		}

		return Object.class;
	}		

	/**
	 * Get number of columns.
	 */
	public int getColumnCount() {
		if (result == null) {
			return 0;
		}

		return result.getColumnCount();
	}

	/**
	 * Get name of a particular column.
	 */
	@Override
	public String getColumnName(int column) {
		if (result == null) {
			return "";
		}

		return result.getColumnName(column);
	}

	protected boolean isBlobColumn(int column) {
		return result.getColumnType(column) == Types.BLOB;
	}

	protected boolean isClobColumn(int column) {
		return result.getColumnType(column) == Types.CLOB;
	}

}
