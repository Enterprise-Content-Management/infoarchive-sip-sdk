/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.jobs.util;

import java.io.IOException;

import javax.annotation.Nullable;

import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.dto.JobInstance;
import com.opentext.ia.sdk.dto.NamedLinkContainer;
import com.opentext.ia.sdk.support.http.rest.RestClient;

public final class JobsUtil {

  private JobsUtil() {
  }

  @Nullable
  public static String getLog(NamedLinkContainer container, RestClient restClient)
      throws IOException {

    String logUri = container.getUri(InfoArchiveLinkRelations.LINK_LOG);

    if (logUri == null) {
      return null;
    }

    return restClient.get(logUri, String.class);
  }

  public static boolean isJobFinished(JobInstance jobInstance) {
    return !"SCHEDULED".equals(jobInstance.getStatus())
        && !"RUNNING".equals(jobInstance.getStatus());
  }

}
