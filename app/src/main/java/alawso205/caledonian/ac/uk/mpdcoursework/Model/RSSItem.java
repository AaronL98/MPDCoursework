package alawso205.caledonian.ac.uk.mpdcoursework.Model;

import java.util.Date;

import alawso205.caledonian.ac.uk.mpdcoursework.Helper.RSSItemType;

/**
 * Aaron Lawson (S1624910)
 */

public class RSSItem {

    private RSSItemType rssItemType;
    private String title;
    private String description;
    private float lat;
    private float lng;
    private Date pubDate;
    private Date startDate;
    private Date endDate;

    public RSSItem(RSSItemType rssItemType, String title, String description, float lat, float lng, Date pubDate, Date startDate, Date endDate) {
        this.rssItemType = rssItemType;
        this.title = title;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.pubDate = pubDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public Date getStartDate(){
        return startDate;
    }

    public Date getEndDate(){
        return endDate;
    }

    public RSSItemType getRssItemType() {
        return rssItemType;
    }


    public void setRssItemType(RSSItemType rssItemType) {
        this.rssItemType = rssItemType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public void setStartDate(Date startDate){
        this.startDate = startDate;

    }

    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }
}
