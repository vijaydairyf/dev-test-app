package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicProfileEntity implements Entity {

    private OrgProfileEntity organization = new OrgProfileEntity();

    private ChillingCenterProfileEntity chillingCenter = new ChillingCenterProfileEntity();

    private RouteProfileEntity route = new RouteProfileEntity();

    private CollectionCenterProfileEntity collectionCenter = new CollectionCenterProfileEntity();

    public OrgProfileEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrgProfileEntity organization) {
        this.organization = organization;
    }

    public ChillingCenterProfileEntity getChillingCenter() {
        return chillingCenter;
    }

    public void setChillingCenter(ChillingCenterProfileEntity chillingCenter) {
        this.chillingCenter = chillingCenter;
    }

    public RouteProfileEntity getRoute() {
        return route;
    }

    public void setRoute(RouteProfileEntity route) {
        this.route = route;
    }

    public CollectionCenterProfileEntity getCollectionCenter() {
        return collectionCenter;
    }

    public void setCollectionCenter(CollectionCenterProfileEntity collectionCenter) {
        this.collectionCenter = collectionCenter;
    }

    @Override
    public Object getPrimaryKeyId() {
        return null;
    }

    @Override
    public void setPrimaryKeyId(Object id) {

    }
}
