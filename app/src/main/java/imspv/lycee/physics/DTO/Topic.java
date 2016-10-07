package imspv.lycee.physics.DTO;



public class Topic {

    int id;
    String topic;

    Topic(){}

    public Topic(int id, String topic) {
        this.id = id;
        this.topic = topic;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
