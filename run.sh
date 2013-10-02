#!/bin/sh

java -Xms128m -Xmx1024m -cp lib/*:jdbc-drivers/*:. ro.nextreports.designer.Main
