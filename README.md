[![License: MPL2](https://img.shields.io/badge/license-mpl2-blue.svg)](https://www.mozilla.org/en-US/MPL/2.0/)
[![GitHub release](https://img.shields.io/github/release/Enterprise-Content-Management/infoarchive-sip-sdk.svg)](https://github.com/Enterprise-Content-Management/infoarchive-sip-sdk/releases/latest)

# InfoArchive SIP SDK

The [EMC InfoArchive](http://www.emc.com/enterprise-content-management/infoarchive/) SIP SDK is a Java library that 
makes it quick and easy to create SIPs regardless of what type of data it contains or where that data originates
from. A SIP (Submission Information Package) is a package consisting of packaging information, metadata (structured
data in the form of XML) and optionally a collection of unstructured data files.

It has long been perceived by partners and customers that creating SIPs is difficult. The EMC IA SIP SDK aims to make
the process simpler by allowing a developer to easily and dynamically assembly both the XML file containing the
structured data as well as the entire SIP itself. This makes it possible to quickly create SIPs from any collection or
stream of Plain Old Java Objects regardless of if they represent files, SQL query result sets, emails, tweets, etc.
