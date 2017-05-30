[![Travis](https://img.shields.io/travis/Enterprise-Content-Management/infoarchive-sip-sdk.svg)](https://travis-ci.org/Enterprise-Content-Management/infoarchive-sip-sdk)
[![codecov.io](https://img.shields.io/codecov/c/github/Enterprise-Content-Management/infoarchive-sip-sdk.svg)](https://codecov.io/github/Enterprise-Content-Management/infoarchive-sip-sdk)
[![Maven Central](https://img.shields.io/maven-central/v/com.emc.ia/infoarchive-sip-sdk-core.svg)](https://repo1.maven.org/maven2/com/emc/ia/)
[![License: MPL2](https://img.shields.io/badge/license-mpl2-ff69b4.svg)](https://www.mozilla.org/en-US/MPL/2.0/)

# InfoArchive SIP SDK

The [OpenText InfoArchive](http://documentum.opentext.com/infoarchive/) SIP SDK is a Java library that 
makes it quick and easy to create SIPs regardless of what type of data it contains or where that data originates
from. A SIP (_Submission Information Package_) is a package consisting of packaging information, metadata (structured
data in the form of XML) and optionally a collection of unstructured data files.

The IA SIP SDK aims to make the process of creating SIPs simpler by allowing a developer to dynamically assemble both
the XML file containing the structured data as well as the entire SIP itself. It's especially easy to create SIPs from
any collection or stream of Plain Old Java Objects regardless of if they represent files, SQL query result sets, emails, 
tweets, etc.

While the focus of the SIP SDK is on assembling SIPs, you can also use it to ingest those SIPs into InfoArchive and
even to configure an InfoArchive holding. For this functionality you must have access to a running InfoArchive server.
The SIP SDK supports version 4.0 of InfoArchive and newer.


## Quick Start

The SDK consists of the following jars:

- `infoarchive-sip-sdk-core` is the core of the SDK
- `infoarchive-sip-sdk-stringtemplate` contains code for working with the [StringTemplate](http://www.stringtemplate.org/) template engine
- `infoarchive-sip-sdk-velocity` contains code for working with the [Velocity](http://velocity.apache.org/) template engine

All jars can be found in the [Central Repository](https://repo1.maven.org/maven2/com/emc/ia/). The easiest way to get
them is through a dependency management system like [Gradle](http://gradle.org/) or [Maven](https://maven.apache.org/).
For the latest version, see the `maven-central` button at the top of this page.

**Gradle**

    dependencies { 
      compile 'com.emc.ia:infoarchive-sip-sdk-core:5.7.0'
    }
    
**Maven**

    <dependencies>
      <dependency>
        <groupId>com.emc.ia</groupId>
        <artifactId>infoarchive-sip-sdk-core</artifactId>
        <version>5.7.0</version>
      </dependency>
    </dependencies>
    
    
## Versioning
    
The SIP SDK uses [semantic versioning](https://semver.org).
Any [breaking changes](https://github.com/Enterprise-Content-Management/infoarchive-sip-sdk/wiki/Breaking-changes) in major
versions are documented on the wiki.


## Additional Information

For an [introduction](https://github.com/Enterprise-Content-Management/infoarchive-sip-sdk-lab/releases/download/1.0.2/presentation.pdf)
to the SDK and some [lab exercises](https://github.com/Enterprise-Content-Management/infoarchive-sip-sdk-lab/releases/download/1.0.2/lab.pdf),
see the related [lab](https://github.com/Enterprise-Content-Management/infoarchive-sip-sdk-lab) project.
For examples on how to use the SDK, see the [sample programs](https://github.com/Enterprise-Content-Management/infoarchive-sip-sdk/tree/master/samples).
