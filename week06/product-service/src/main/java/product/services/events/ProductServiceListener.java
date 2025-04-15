package product.services.events;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import product.dtos.MessageResponse;
import product.services.ProductService;

@Service
public class ProductServiceListener implements MessageListener{
	private final ProductService productService;
	
	@Autowired
	public ProductServiceListener(ProductService productService) {
		this.productService = productService;
	}

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		
		String body = new String(message.getBody());
		ObjectMapper mapper = new ObjectMapper();
		MessageResponse messageResponse = null;
		try {
			messageResponse = mapper.readValue(body, MessageResponse.class);

			System.out.println("Product id: " + messageResponse.getProductId());

		} catch (JsonProcessingException e) {
			System.out.println(e.getMessage());
		}
	}

}
