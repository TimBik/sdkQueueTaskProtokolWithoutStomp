package sdk;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;

public class Connector {
    private ClientEndPoint clientEndPoint;
    private ApplicationContext applicationContext;
    private ObjectMapper objectMapper;
    private ConsumerManager consumerManager;

    public Connector(String uri, ApplicationContext ac) {
        applicationContext = ac;
        objectMapper = applicationContext.getBean("objectMapper", ObjectMapper.class);
        consumerManager = (ConsumerManager) applicationContext.getBean("consumerManager");
        clientEndPoint = new ClientEndPoint(this,
                (ConsumerManager) applicationContext.getBean("consumerManager"), objectMapper);
        clientEndPoint.connect(uri);
    }

    public Producer producer(String queueName) {
        return new Producer(this,queueName);
    }

    public void send(Object message) {
        if (clientEndPoint.isClosed()) {
            throw new IllegalStateException("Current session is closed");
        }
        try {
            clientEndPoint.syncSendMessage(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized Consumer consumer() {
        return new Consumer(this, objectMapper);
    }

    public boolean subscribe(String queue, Consumer consumer) {
        return consumerManager.putCurrentSession(queue, consumer);
    }

    public void close(){
        if(!clientEndPoint.isClosed()){
            clientEndPoint.disconnect();
        }
    }
}
