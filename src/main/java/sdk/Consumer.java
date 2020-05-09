package sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import sdk.ConsumerMessages.ConsumerMessage;

import javax.websocket.MessageHandler;

@Data
public class Consumer {
    private Connector connector;
    private Thread thread;
    private ObjectMapper objectMapper;
    private Handler<JsonNode> customHandler;

    public Consumer(Connector connector, ObjectMapper objectMapper) {
        this.connector = connector;
        this.objectMapper = objectMapper;
        thread = new SimpleForeverThread();
        thread.start();
        customHandler = new CustomHandleObject<>();
    }

    public void subscribe(String queue) {
        boolean result = connector.subscribe(queue, this);
        if (!result) {
            this.close();
            return;
        }
        connector.send(ConsumerMessage.subscribe(queue));
    }

    public <T> void setHandler(Handler<T> type, Class<T> messageType) {
        customHandler = new CustomHandleObject<>(type, messageType, objectMapper);
    }

    public void handle(JsonNode node) {
        try {
            connector.send(ConsumerMessage.notifyMessage(node.get("objectId").asText()));
            customHandler.handle(node.get("payload"));
            connector.send(ConsumerMessage.completeMessage(node.get("objectId").asText(), node.get("queue").asText()));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void close() {
        thread.interrupt();
    }
}
