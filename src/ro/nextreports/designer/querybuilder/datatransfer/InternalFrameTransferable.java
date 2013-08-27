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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JInternalFrame;

/**
 * @author Decebal Suiu
 */
public class InternalFrameTransferable implements Transferable {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(
            InternalFrameTransferable.class, "Internal Frame Data Flavor");

    private JInternalFrame iFrame;

    public InternalFrameTransferable(JInternalFrame iFrame) {
        this.iFrame = iFrame;
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        if (flavor.equals(DATA_FLAVOR)) {
            return iFrame;
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
