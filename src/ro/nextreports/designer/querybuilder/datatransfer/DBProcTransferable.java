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
package ro.nextreports.designer.querybuilder.datatransfer;


import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.io.Serializable;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 27-Apr-2009
// Time: 10:44:10

import ro.nextreports.designer.dbviewer.common.DBProcedure;

//
public class DBProcTransferable implements Transferable, Serializable {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(DBProcTransferable.class,
            "DBProc Data Flavor");

    private DBProcedure proc;

    public DBProcTransferable(DBProcedure proc) {
        this.proc = proc;
    }

    public Object getTransferData(DataFlavor flavor) {
        if (isDataFlavorSupported(flavor)) {
            return proc;
        }

        return null;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DATA_FLAVOR };
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.equals(DATA_FLAVOR)) {
            return true;
        }

        return false;
    }

}
