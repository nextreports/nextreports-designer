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
package ro.nextreports.test;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 19, 2006
 * Time: 1:25:51 PM
 */

import java.lang.reflect.Method;

import org.testng.annotations.*;
import org.testng.Assert;

import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DataSourceType;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.exception.NonUniqueException;
import ro.nextreports.designer.datasource.exception.NotFoundException;


public class DataSourceTest {

    @BeforeClass
    public void setUp() {
    }

    @DataProvider(name = "dp")
    public Object[][] createData(Method m) {
        System.out.println(m.getName());  // print test method name

        DataSource source = new DataSource();
        source.setName("KSI");
        source.setType("MySQL");
        source.setDriver("sql.driver");
        source.setUrl("http://www.aaa.ro:8080");
        source.setUser("sam");
        source.setPassword("23s2@");
        source.setStatus(DataSourceType.DISCONNECTED);

        String otherUser = "Other";

        return new Object[][]{new Object[]{source, otherUser}};
    }

    /**
     * Test that a datasource.xml file with at least one datasource exists
     */
    @Test(groups = {"datasource"})
    public void load() {
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
        manager.load();

        Assert.assertTrue(manager.getDataSources().size() > 0);

        for (DataSource source : manager.getDataSources()) {
            System.out.println(source);
        }
    }

    /**
     * Add data source -> test it is written
     * Add the same data source only with user modified (must have same name) -> test NonUniqueException is risen
     * Delete data source -> test it is removed
     */
    @Test(groups = {"datasource"}, dataProvider = "dp")
    public void save(DataSource source, String otherUser) {
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
        manager.load();
        int size = manager.getDataSources().size();

        try {
            manager.addDataSource(source);
        } catch (NonUniqueException e) {
            Assert.fail("NonUnique", e);
        }

        manager.save();
        manager.load();

        try {
            source.setUser(otherUser);
            manager.addDataSource(source);
            Assert.fail("NonUnique data source added!");
        } catch (NonUniqueException e) {
            // should occur!
        }

        Assert.assertTrue(manager.getDataSources().size() == size+1);

        try {
            manager.removeDataSource(source.getName());
        } catch (NotFoundException e) {
            Assert.fail("NotFound", e);
        }

        manager.save();
        manager.load();

        Assert.assertTrue(manager.getDataSources().size() == size);
    }


}
