#!/bin/sh
$JAVA_HOME/bin/java --module-path pub/target/pub-1.0-SNAPSHOT.jar:customer/target/customer-1.0-SNAPSHOT.jar:beer/target/beer-1.0-SNAPSHOT.jar:base/target/base-1.0-SNAPSHOT.jar -m org.modules.pub/org.modules.pub.Pub