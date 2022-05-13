package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Comment {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    public long id;
    public String name;
    public String text;

    public Comment() {
    }

    /**
     * Constructor for the Comment.
     * @param text - the text of the comment
     */
    public Comment(String text) {
        this.text = text;
    }

    /**
     * Setter for the name.
     * @param name - the name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for the text.
     * @param text - the text to be set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Getter for the text.
     * @return - returns the text
     */
    public String getText() {
        return text;
    }

    /**
     * Getter for the name.
     * @return - returns the name
     */
    public String getName() {
        return name;
    }

    public String chatString() {
        return name + " " + text;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("text", text)
                .toString();
    }
}
