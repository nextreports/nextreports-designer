<!-- I cannot use jdk 1.6 in buildhive
Current build status: [![Build Status](https://buildhive.cloudbees.com/job/nextreports/job/nextreports-designer/badge/icon)](https://buildhive.cloudbees.com/job/nextreports/job/nextreports-designer/)
-->
Current build status: [![Build Status](https://travis-ci.org/nextreports/nextreports-designer.png?branch=master)](https://travis-ci.org/nextreports/nextreports-designer)

How to build
-------------------
Requirements: 
- [Git](http://git-scm.com/) 
- JDK 1.6 (test with `java -version`)
- [Apache Ant](http://ant.apache.org/) (test with `ant -version`)

Steps:
- create a local clone of this repository (with `git clone https://github.com/nextreports/nextreports-designer.git`)
- go to project's folder (with `cd nextreports-designer`) 
- build the artifacts (with `ant clean release`)

After above steps a folder _artifacts_ is created and all goodies are in that folder.

How to run
-------------------
It's very simple to run the nextreports-designer.   
First, you must build the project using above steps. After building process go to _dist_ folder and execute:
- run.bat (for windows)
- run.sh (for linux/unix)
 

