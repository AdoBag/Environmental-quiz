package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.io.Serializable;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;


@Entity
public class Activity implements Serializable {
    @Id
    public String id;
    public String image_path;
    public String title;
    public Long consumption_in_wh;
    @Lob
    @Column(name = "source", columnDefinition = "CLOB")
    public String source;
    @Lob
    @Column(name = "image", columnDefinition = "BLOB")
    public byte[] image;


    public Activity() {
    }

    /**
     * Constructor that is used for creating a new activity.
     */
    public Activity(String id, String image_path, String title, long consumption_in_wh, String source) {
        this.id = id;
        this.image_path = image_path;
        this.title = title;
        this.consumption_in_wh = consumption_in_wh;
        this.source = source;

    }

    /**
     * Setter for the image.
     * @param a - the image to be assigned
     */
    public void setImage(byte[] a) {
        image = a;
    }

    /**
     * Getter for an image.
     * @return - returns the image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Getter for the ID.
     * @return - returns the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for the ID.
     * @param id - the id to be set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for the image path.
     * @return - returns the image path
     */
    public String getImagePath() {
        return image_path;
    }

    /**
     * Setter for the image path.
     * @param image_path - the image path to be set
     */
    public void setImagePath(String image_path) {
        this.image_path = image_path;
    }

    /**
     * Getter fo the title.
     * @return - returns the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title.
     * @param title - the title to be set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the consumption.
     * @return - returns the consumption
     */
    public long getConsumption() {
        return consumption_in_wh;
    }

    /**
     * Setter for the consumption.
     * @param consumption_in_wh - the consumption to be set
     */
    public void setConsumption(long consumption_in_wh) {
        this.consumption_in_wh = consumption_in_wh;
    }

    /**
     * Getter for the source.
     * @return - returns the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Getter for the source.
     * @param source - the source to be set
     */
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
