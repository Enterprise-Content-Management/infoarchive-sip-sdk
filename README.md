[![Travis](https://img.shields.io/travis/Enterprise-Content-Management/infoarchive-sip-sdk.svg)](https://travis-ci.org/Enterprise-Content-Management/infoarchive-sip-sdk)
[![codecov.io](https://img.shields.io/codecov/c/github/Enterprise-Content-Management/infoarchive-sip-sdk.svg)](https://codecov.io/github/Enterprise-Content-Management/infoarchive-sip-sdk)
[![Maven Central](https://img.shields.io/maven-central/v/com.emc.ia/infoarchive-sip-sdk-core.svg)](https://repo1.maven.org/maven2/com/emc/ia/)
[![License: MPL2](https://img.shields.io/badge/license-mpl2-ff69b4.svg)](https://www.mozilla.org/en-US/MPL/2.0/)

# InfoArchive SIP SDK

The [EMC InfoArchive](http://www.emc.com/enterprise-content-management/infoarchive/) SIP SDK is a Java library that 
makes it quick and easy to create SIPs regardless of what type of data it contains or where that data originates
from. A SIP (Submission Information Package) is a package consisting of packaging information, metadata (structured
data in the form of XML) and optionally a collection of unstructured data files.

It has long been perceived by partners and customers that creating SIPs is difficult. The EMC IA SIP SDK aims to make
the process simpler by allowing a developer to easily and dynamically assembly both the XML file containing the
structured data as well as the entire SIP itself. This makes it possible to quickly create SIPs from any collection or
stream of Plain Old Java Objects regardless of if they represent files, SQL query result sets, emails, tweets, etc.


## Quick Start

Gradle:

    repositories { mavenCentral() }
    dependencies { compile 'com.emc.ia:infoarchive-sip-sdk-core:1.0.1' }
    
Maven:

    <dependencies>
      <dependency>
        <groupId>com.emc.ia</groupId>
        <artifactId>infoarchive-sip-sdk-core</artifactId>
        <version>1.0.1</version>
      </dependency>
    </dependencies>
    

## Additional Information

For a presentation and lab exercises on how to use the SDK, see our
[lab](https://github.com/Enterprise-Content-Management/infoarchive-sip-sdk-lab).
