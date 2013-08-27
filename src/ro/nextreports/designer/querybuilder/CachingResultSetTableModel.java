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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryResult;

/**
 * This class caches the result set data; it can be used
 * if scrolling cursors are not supported.
 *
 * @author Decebal Suiu
 */
public class CachingResultSetTableModel extends ResultSetTableModel {

    private static final Log LOG = LogFactory.getLog(CachingResultSetTableModel.class);
    
	private List<Object[]> cache;
    private volatile boolean stop = false;

    public CachingResultSetTableModel(QueryResult result) {
        this(result, false);
    }

    public CachingResultSetTableModel(QueryResult result, boolean stop) {
		super(result);
		cache = new ArrayList<Object[]>();
        this.stop =  stop;
	}

    public void init() {
        try {
            initCache(result);
        } catch (QueryException e) {
            LOG.error(e.getMessage(), e);
            // TODO
            throw new RuntimeException(e);
        }
    }

    public Object getValueAt(int row, int column) {
		if (result == null) {
			return null;
		}

		if (isBlobColumn(column)) {
			return BLOB_VALUE;
		} else if (isClobColumn(column)) {
			return CLOB_VALUE;
		}
		
		if (row < cache.size()) {
	         return cache.get(row)[column];
		} else {
	         return null;
		}
	}

	public int getRowCount() {
		if (result == null) {
			return 0;
		}

		return cache.size();
	}
	
	private void initCache(QueryResult result) throws QueryException {
		if (result == null) {
			return;
		}
		
		/* 
		 * place all data in an array list of Object[] arrays
         * I don't use an Object[][] because we don't know
         * how many rows are in the result set
		 */		
		while (result.hasNext()) {
            if (stop) {
                break;
            }
            Object[] row = new Object[result.getColumnCount()];
			for (int i = 0; i < row.length; i++) {
				row[i] = result.nextValue(i);
			}
			cache.add(row);
		}	
	}

    public void setStop(boolean stop) {        
        this.stop = stop;
    }
    
}
