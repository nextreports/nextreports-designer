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

import java.util.*;

import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.querybuilder.sql.util.CollectionUtil;
import ro.nextreports.engine.util.ParameterNotFoundException;
import ro.nextreports.engine.util.ParameterUtil;

/**
 * @author Decebal Suiu
 */
public class ParameterManager {

	private static ParameterManager instance = new ParameterManager();

	private LinkedHashMap<String, QueryParameter> parameters;

	private ParameterManager() {
		parameters = new LinkedHashMap<String, QueryParameter>();
	}

	public static ParameterManager getInstance() {
		return instance;
	}

	public void addParameter(QueryParameter parameter) {
		if (parameter == null) {
			throw new IllegalArgumentException("null");
		}

		String paramName = parameter.getName();
		if ((paramName == null) || (paramName.trim().length() == 0)) {
			throw new IllegalArgumentException("name");
		}

		parameters.put(paramName, parameter);		
		RuntimeParametersPanel.resetParametersValues();
	}

	public void deleteParameter(QueryParameter parameter) {
		if (parameter == null) {
			throw new IllegalArgumentException("null");
		}

		String paramName = parameter.getName();
		if ((paramName == null) || (paramName.trim().length() == 0)) {
			throw new IllegalArgumentException("name");
		}

		parameters.remove(paramName);
		RuntimeParametersPanel.resetParametersValues();
	}

    public void modifyParameter(QueryParameter oldParameter, QueryParameter newParameter) {
        if (oldParameter == null) {
			throw new IllegalArgumentException("null");
		}
        int position = getPosition(oldParameter.getName());
        deleteParameter(oldParameter);
        addParameter(newParameter);
        List<String> list = getParameterNames();
        CollectionUtil.moveItem(list, newParameter.getName(), position);
        recreateMap(list);
        RuntimeParametersPanel.resetParametersValues();
	}

    public int getPosition(String parameterName) {
        List<String> list = getParameterNames();
        int position = -1;
        for (int i=0, size=list.size(); i<size; i++) {
            if (parameterName.equals(list.get(i))) {
                position = i;
                break;
            }
        }
        return position;
    }


    public void moveParameter(String parameterName, boolean up) {
        int position = getPosition(parameterName);
        List<String> list = getParameterNames();
        if (position != -1) {
            if (up && (position > 0)) {
                CollectionUtil.moveItem(list, parameterName, position-1);
                recreateMap(list);
            }
            if (!up && (position < list.size()-1)) {
                CollectionUtil.moveItem(list, parameterName, position+1);
                recreateMap(list);
            }
        }
    }

    private void recreateMap(List<String> parameterNames) {
        LinkedHashMap<String, QueryParameter> newParameters = new LinkedHashMap<String, QueryParameter>();
        for (String name : parameterNames) {
             newParameters.put(name, parameters.get(name));
        }
        parameters = newParameters;
    }

    public void deleteParameter(String paramName) {
		parameters.remove(paramName);
		RuntimeParametersPanel.resetParametersValues();
	}

	public boolean containsParameter(String paramName) {
		return parameters.containsKey(paramName);
	}

	public QueryParameter getParameter(String paramName) {
		return parameters.get(paramName);
	}

	public int getParameterCount() {
		return parameters.size();
	}

	public Map<String, QueryParameter> getParametersMap() {
		return parameters;
	}

    public Map<String, QueryParameter> getUsedParametersMap(Query query) {    	
        return ParameterUtil.getUsedParametersMap(query, parameters);
    }

    public List<String> getParameterNames() {
		return new LinkedList<String>(parameters.keySet());
	}

    public List<String> getUsedParameterNames(String sql) {
		return new LinkedList<String>(ParameterUtil.getUsedParametersMap(sql, parameters).keySet());
	}

    public List<QueryParameter> getParameters() {
		return new LinkedList<QueryParameter>(parameters.values());
	}

    public List<QueryParameter> getClonedParameters() {
        LinkedList<QueryParameter> list = new LinkedList<QueryParameter>();
        for (QueryParameter qp : parameters.values()) {
            list.add(qp);
        }
        return list;
	}

    public void setParameters(List<QueryParameter> params) {
		parameters.clear();
		for (QueryParameter parameter : params) {
			addParameter(parameter);
		}
		RuntimeParametersPanel.resetParametersValues();
	}

	public void clearParameters() {
		parameters.clear();
	}

	public Map<String, QueryParameter> getChildDependentParameters(QueryParameter p) {
		Map<String, QueryParameter> result = new HashMap<String, QueryParameter>();
		List<QueryParameter> params = getParameters();
		for (QueryParameter param : params) {
			if (!param.equals(p)) {
				if (param.isDependent()) {
                    List<String> names = param.getDependentParameterNames();
                    if (names.contains(p.getName())) {
                        result.put(param.getName(), param);
                    }
                }
			}
		}
		return result;
	}

	public Map<String, QueryParameter> getParentDependentParameters(QueryParameter p) {
		List<String> names = p.getDependentParameterNames();
		Map<String, QueryParameter> result = new HashMap<String, QueryParameter>();
		for (String name : names) {
			result.put(name, getParameter(name));
		}
		return result;
	}

    /**
     * Test if all parameters used in sql are defined
     * @param sql sql
     * @throws ro.nextreports.engine.util.ParameterNotFoundException if a parameter used in the sql is not defined
     */
    public void parametersAreDefined(String sql) throws ParameterNotFoundException {
        String[] paramNames;
        Query query = new Query(sql);
        paramNames = query.getParameterNames();

        List<QueryParameter> parameters = getParameters();

        for (String paramName : paramNames) {
            QueryParameter param = null;
            for (QueryParameter p : parameters) {
                if (paramName.equals(p.getName())) {
                    param = p;
                }
            }
            if (param == null) {
                throw new ParameterNotFoundException(paramName);
            }
        }
    }

    /**
     * Test if all parameters are for stored procedure call
     * @return the name of the first parameter that is not for a stored procedure call, null if all parameters
     * are ok
     */
    public String parametersAreForStoredProcedure() {
        List<QueryParameter> parameters = getParameters();
        for (QueryParameter p : parameters) {
            if (!p.isProcedureParameter()) {
                return p.getName();
            }
        }
        return null;
    }


}
