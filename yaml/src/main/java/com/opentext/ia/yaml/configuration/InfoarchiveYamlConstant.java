/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class InfoarchiveYamlConstant {

    public static final List<String> RESOURCE_CONTAINER_PATHS = Collections.unmodifiableList(Arrays.asList("/.+/content(/\\d+)?",
        "/customPresentationConfiguration(s/[^/]+)?/htmlTemplate", "/database(s/[^/]+)?/metadata/\\d+",
        "/transformation(s/[^/]+)?/xquery", "/transformation(s/[^/]+)?/xslt", "/xform(s/[^/]+)?/form",
        "/(xquery|xqueries/[^/]+)/query"));

    public static final String TEXT = "text";

    private InfoarchiveYamlConstant() {

    }
}
