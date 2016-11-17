package hello;

public class Greeting {

    private final long id;
    private final String content;
    private final String content2;
    
    
    public Greeting(long id, String content, String content2) {
        this.id = id;
        this.content = content;
        this.content2 = content2;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
    
    public String getContent2() {
        return content2;
    }
    
    
}