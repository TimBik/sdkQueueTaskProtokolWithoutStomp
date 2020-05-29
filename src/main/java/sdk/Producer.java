package sdk;

import sdk.ProducerMessages.ProducerUniformMessage;

public class Producer {
    private Connector connector;
    private String queue;

    public Producer(Connector connector, String queueName) {
        this.connector = connector;
        this.send(ProducerUniformMessage.builder().command("startProducer").queue(queueName).build());
        this.queue = queueName;
    }

    private void send(ProducerUniformMessage message) {
        connector.send(message);
    }

    public void createTask(String command, Object payload) {
        this.send(ProducerUniformMessage.builder().queue(queue).command(command).payload(payload).build());
    }
}
