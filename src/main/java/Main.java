import sdk.*;
import sdk.ProducerMessages.SimpleTextMessage;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Connector connector = JLMQ.connector("ws://localhost:8080/queueTask");
        Producer producer = connector.producer("send_mail_queue");
        UserDto userDto = UserDto.builder().mail("timurbikmullin@mail.ru").build();
        producer.createTask("sendMail", userDto);


        Handler<SimpleTextMessage> messageHandler = message -> {
            System.out.println(message.getText());
        };

        Consumer consumer = connector.consumer();
        consumer.subscribe("send_mail_queue");
        consumer.setHandler(messageHandler, SimpleTextMessage.class);
        connector.close();
    }
}
