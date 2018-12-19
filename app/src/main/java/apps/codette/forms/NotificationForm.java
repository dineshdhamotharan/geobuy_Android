package apps.codette.forms;

public class NotificationForm {
    private Notification notification;
    private String[] topic;
    private String image;
    private String link;
    private String[] linkId;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String[] getTopic() {
        return topic;
    }

    public void setTopic(String[] topic) {
        this.topic = topic;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String[] getLinkId() {
        return linkId;
    }

    public void setLinkId(String[] linkId) {
        this.linkId = linkId;
    }
}
