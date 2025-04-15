package order.configs;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	@Value("${app.rabbit.queue-name}")
	private String queueName;
	@Value("${app.rabbit.exchange-name}")
	private String exchangeName;
	@Value("${app.rabbit.routing-key}")
	private String routingKey;
	
	@Bean
	public Queue queue() {
		return new Queue(queueName, false);
	}
	
	 @Bean
	    public DirectExchange directExchange() {
	        return new DirectExchange(exchangeName);
	    }

	    @Bean
	    public Binding binding(Queue queue, DirectExchange directExchange) {
	        return BindingBuilder.bind(queue).to(directExchange).with(routingKey);
	    }

	    @Bean
	    public MessageConverter jsonMessageConverter() {
	        return new Jackson2JsonMessageConverter();
	    }

	    @Bean
	    public AmqpTemplate rabbitMqTemplate(ConnectionFactory connectionFactory) {
	        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
	        rabbitTemplate.setMessageConverter(jsonMessageConverter());
	        return rabbitTemplate;
	    }
}
