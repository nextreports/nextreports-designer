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
package ro.nextreports.designer.datasource;


import java.util.List;

import ro.nextreports.designer.datasource.exception.ConnectionException;
import ro.nextreports.designer.datasource.exception.ModificationException;
import ro.nextreports.designer.datasource.exception.NonUniqueException;
import ro.nextreports.designer.datasource.exception.NotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 15, 2006
 * Time: 2:28:48 PM
 */
public interface DataSourceManager {

    public void addDataSource(DataSource source) throws NonUniqueException;

    public void modifyDataSource(DataSource oldSource, DataSource newSource) throws ModificationException, NonUniqueException;

    public void removeDataSource(String name) throws NotFoundException;

    public List<DataSource> getDataSources();

    public List<DataSource> getDataSources(String type);

    public DataSource getDataSource(String name);

    public void connect(String name) throws ConnectionException, NotFoundException;

    public void disconnect(String name) throws NotFoundException;

    public DataSource getConnectedDataSource();

    public boolean save();

    public boolean save(String file, List<DataSource> sources);

    public void load();

    public List<DataSource> load(String file);

}
