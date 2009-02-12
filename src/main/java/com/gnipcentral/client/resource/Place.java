package com.gnipcentral.client.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * Model object that represents a place of a Gnip {@link Activity}. For activities
 * and simple notifications, the {@link Place} contains information that was originally
 * sent to the {@link Publisher}.
 * <br/>
 * <br/>
 * Typically, a Gnip user would create a {@link Place} object in order to publish data
 * into Gnip and would receive one from an {@link Activity} retrieved via
 * a {@link com.gnipcentral.client.GnipConnection}.
 */
@XmlRootElement(name = "place")
@XmlAccessorType(XmlAccessType.FIELD)
public class Place {
    
    @XmlElement
    @XmlList
    @XmlSchemaType(name = "pointType")
    private List<Double> point;
    @XmlElement(name = "elev")
    private Double elevation;
    @XmlElement
    private Integer floor;
    @XmlElement(name = "featuretypetag")
    private String featureTypeTag;
    @XmlElement(name = "featurename")
    private String featureName;
    @XmlElement(name = "relationshiptag")
    private String relationshipTag;

    /**
     * Create an empty Place object.
     */
    public Place() {
    }
    
    /**
     * Create a Place object with specified point.
     * @param point the Place point array.
     */
    public Place(double [] point) {
        if (point == null || point.length == 0) {
            throw new IllegalArgumentException("Invalid point array specified '"+point+"'");
        }
        setPoint(point);
    }

    /**
     * Create an Activity Place object with all optional
     * attributes.
     * @param point the optional Place point array.
     * @param elevation the optional Place elevation.
     * @param floor the optional Place floor number.
     * @param featureTypeTag the optional Place feature type tag.
     * @param featureName the optional Place feature name.
     * @param relationshipTag the optional Place relationship tag.
     */
    public Place(double [] point, Double elevation, Integer floor, String featureTypeTag, String featureName, String relationshipTag)
    {
        setPoint(point);
        this.elevation = elevation;
        this.floor = floor;
        this.featureTypeTag = featureTypeTag;
        this.featureName = featureName;
        this.relationshipTag = relationshipTag;
    }
    
    /**
     * Retrieve this Place's point array.
     * @return the Place point array.
     */
    public double [] getPoint() {
        double [] pointArray = null;
        if (point != null) {
            pointArray = new double[point.size()];
            int pointElementIndex = 0;
            for (Double pointElement : point) {
                pointArray[pointElementIndex++] = pointElement;
            }
        }
        return pointArray;
    }

    /**
     * Set this Place's point array.
     * @param point the Place point array.
     */
    public void setPoint(double [] point) {
        if (point != null && point.length == 0) {
            throw new IllegalArgumentException("Invalid point array specified '"+point+"'");
        }

        if (point != null) {
            this.point = new ArrayList<Double>(point.length);
            for (Double pointElement : point) {
                this.point.add(pointElement);
            }
        } else {
            this.point = null;
        }
    }

    /**
     * Retrieve this Place's elevation.
     * @return the Place elevation.
     */
    public Double getElevation() {
        return elevation;
    }

    /**
     * Set this Place's elevation.
     * @param elevation the Place elevation.
     */
    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    /**
     * Retrieve this Place's floor number.
     * @return the Place floor number.
     */
    public Integer getFloor() {
        return floor;
    }

    /**
     * Set this Place's floor number.
     * @param floor the Place floor number.
     */
    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    /**
     * Retrieve this Place's feature type tag.
     * @return the Place feature type tag.
     */
    public String getFeatureTypeTag() {
        return featureTypeTag;
    }

    /**
     * Set this Place's feature type tag.
     * @param featureTypeTag the Place feature type tag.
     */
    public void setFeatureTypeTag(String featureTypeTag) {
        this.featureTypeTag = featureTypeTag;
    }

    /**
     * Retrieve this Place's feature name.
     * @return the Place feature name.
     */
    public String getFeatureName() {
        return featureName;
    }

    /**
     * Set this Place's feature name.
     * @param featureName the Place feature name.
     */
    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    /**
     * Retrieve this Place's relationship tag.
     * @return the Place relationship tag.
     */
    public String getRelationshipTag() {
        return relationshipTag;
    }

    /**
     * Set this Place's relationship tag.
     * @param relationshipTag the Place relationship tag.
     */
    public void setRelationshipTag(String relationshipTag) {
        this.relationshipTag = relationshipTag;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (point != null ? !point.equals(place.point) : place.point != null) return false;
        if (elevation != null ? !elevation.equals(place.elevation) : place.elevation != null) return false;
        if (floor != null ? !floor.equals(place.floor) : place.floor != null) return false;
        if (featureTypeTag != null ? !featureTypeTag.equals(place.featureTypeTag) : place.featureTypeTag != null) return false;
        if (featureName != null ? !featureName.equals(place.featureName) : place.featureName != null) return false;
        if (relationshipTag != null ? !relationshipTag.equals(place.relationshipTag) : place.relationshipTag != null) return false;

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = (point != null ? point.hashCode() : 0);
        result = 31 * result + (elevation != null ? elevation.hashCode() : 0);
        result = 31 * result + (floor != null ? floor.hashCode() : 0);
        result = 31 * result + (featureTypeTag != null ? featureTypeTag.hashCode() : 0);
        result = 31 * result + (featureName != null ? featureName.hashCode() : 0);
        result = 31 * result + (relationshipTag != null ? relationshipTag.hashCode() : 0);
        return result;
    }    
}
