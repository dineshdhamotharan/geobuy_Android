package apps.codette.forms;

import java.util.List;

public class Issues {
    private String name;
    private List<QA> qa;

    public List<QA> getQa() {
        return qa;
    }

    public void setQa(List<QA> qa) {
        this.qa = qa;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
