/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import java.util.ArrayList;
import java.util.List;

public class Aic extends NamedLinkContainer {

  private List<Criteria> criterias;
  private static final String TYPE = "STRING";

  public Aic() {
    defaultCriteria();
  }

  public List<Criteria> getCriterias() {
    return criterias;
  }

  public void setCriterias(List<Criteria> criterias) {
    this.criterias = criterias;
  }

  private void defaultCriteria() {
    criterias = new ArrayList<Criteria>();
    //String name, String label, String type, String pKeyMinAttr, String pKeyNMaxAttr, String pKeyValuesAttr, boolean indexed
    Criteria to = new Criteria("To", "To", TYPE, null, null, "pkeys.values02", true);
    criterias.add(to);

    Criteria from = new Criteria("From", "From", TYPE, null, null, "pkeys.values01", true);
    criterias.add(from);

    Criteria cc = new Criteria("Cc", "Cc", TYPE, null, null, null, false);
    criterias.add(cc);

    Criteria bcc = new Criteria("Bcc", "Bcc", TYPE, null, null, null, false);
    criterias.add(bcc);

    Criteria dls = new Criteria("DLs", "Distribution Lists", TYPE, null, null, null, false);
    criterias.add(dls);

    Criteria sub = new Criteria("Subject", "Message Subject", TYPE, null, null, null, true);
    criterias.add(sub);

    Criteria sent = new Criteria("Sent", "Sent Date", "DATETIME", "pkeys.dateTime01", "pkeys.dateTime02", null, true);
    criterias.add(sent);

    Criteria delivery = new Criteria("Delivery", "Delivery Date", "DATETIME", "pkeys.dateTime01", "pkeys.dateTime02", null, false);
    criterias.add(delivery);

    Criteria domain = new Criteria("Domain", "Mail Domain", TYPE, null, null, null, true);
    criterias.add(domain);
  }
}
