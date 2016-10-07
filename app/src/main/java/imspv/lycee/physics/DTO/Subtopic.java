package imspv.lycee.physics.DTO;



public class Subtopic {

    int id;
    String subtopic;

    Subtopic(){}

    public Subtopic(int id, String subtopic) {
        this.id = id;
        this.subtopic = subtopic;
    }

    public int getId() {

        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getSubtopic() {
        return subtopic;
    }
    public void setSubtopic(String subtopic) {
        this.subtopic = subtopic;
    }
}
