package fr.travauxetservices.model;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

/**
 * Created by Phobos on 04/02/15.
 */
@Entity
@AssociationOverrides({
        @AssociationOverride(name = "pk.user",
                joinColumns = @JoinColumn(name = "USER_ID")),
        @AssociationOverride(name = "pk.context",
                joinColumns = @JoinColumn(name = "CTXT_ID"))})
public class AdContext extends Ad {
    public AdContext() {
        super();
    }
}
