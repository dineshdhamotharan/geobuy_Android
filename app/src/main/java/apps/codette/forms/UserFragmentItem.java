package apps.codette.forms;

public class UserFragmentItem {

    private int drawable;
    private String text;

    public UserFragmentItem(int drawable, String text){
        this.drawable = drawable;
        this.text = text;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
