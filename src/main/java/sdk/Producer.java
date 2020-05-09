package sdk;

import sdk.ProducerMessages.ProducerUniformMessage;

public class Producer {
    private Connector connector;

    public Producer(Connector connector) {
        this.connector = connector;
        this.send(ProducerUniformMessage.builder().command("startProducer").build());
    }

    private void send(ProducerUniformMessage message) {
        connector.send(message);
    }

    public void createTask(String command, String queueName, Object payload) {
        this.send(ProducerUniformMessage.builder().queue(queueName).command(command).payload(payload).build());
    }
}
