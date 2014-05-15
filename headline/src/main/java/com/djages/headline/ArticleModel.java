package com.djages.headline;

import android.os.Parcel;
import android.os.Parcelable;


import com.djages.common.AbstractModel;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


import java.util.TimeZone;


/**
 * Created by ll298lee on 5/9/14.
 */
public class ArticleModel extends AbstractModel implements Parcelable {
    private int press;
    private int category;
    private String title;
    private String link;
    private String guid;
    private String image;
    private String summary;
    private String description;
    private String author;
    private String pubdate;
//    private String pubDate;
    private String date;

    //non-remote variables
    private String formatedDate;


    public int getPress() {
        return press;
    }

    public void setPress(int press) {
        this.press = press;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

//    public String getPubDate() {
//        return pubDate;
//    }
//
//    public void setPubDate(String pubDate) {
//        this.pubDate = pubDate;
//    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(Object model) {
        return 0;
    }
    
    
    
    public ArticleModel(){
        super();
    }
    
    public ArticleModel(Parcel in){
        setId(in.readString());
        press = in.readInt();
        category = in.readInt();
        title = in.readString();
        link = in.readString();
        guid = in.readString();
        image = in.readString();
        summary = in.readString();
        description = in.readString();
        author = in.readString();
        pubdate = in.readString();
//        pubDate = in.readString();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getId());
        out.writeInt(press);
        out.writeInt(category);
        out.writeString(title);
        out.writeString(link);
        out.writeString(guid);
        out.writeString(image);
        out.writeString(summary);
        out.writeString(description);
        out.writeString(author);
        out.writeString(pubdate);
//        out.writeString(pubDate);
        out.writeString(date);
        
    }

    public static final Creator<ArticleModel> CREATOR = new Creator<ArticleModel>() {

        @Override
        public ArticleModel createFromParcel(Parcel in) {
            return new ArticleModel(in);
        }

        @Override
        public ArticleModel[] newArray(int size) {
            return new ArticleModel[size];
        }
    };
    

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDateString(){
        if(formatedDate == null) {
            DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(date);
            //DateTime dtNow = DateTime.now();
            //Interval interval = new Interval(dt, dtNow);
            DateTimeFormatter dtf = DateTimeFormat.forPattern("hh:mm aa, MM/dd/yyyy ").withZone(DateTimeZone.forTimeZone(TimeZone.getDefault()));
            formatedDate = dtf.print(dt);
        }
        return formatedDate;
    }

    
}
