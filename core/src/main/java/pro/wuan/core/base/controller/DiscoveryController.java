package pro.wuan.core.base.controller;

import lombok.experimental.Delegate;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.feignapi.messageapi.feign.MessageFeignClient;

import java.util.List;

/**
 * This class is responsible for handling the discovery server requests.
 */
@RestController
public class DiscoveryController {

    private final DiscoveryClient discoveryClient;

    @Lazy
    private final MessageFeignClient messageFeignClient;

    public DiscoveryController(DiscoveryClient discoveryClient, MessageFeignClient messageFeignClient) {
        this.discoveryClient = discoveryClient;
        this.messageFeignClient = messageFeignClient;
    }

    /**
     * Returns the list of services registered with the discovery server.
     * @return  The list of services.
     */
    @Delegate
    @GetMapping("/services")
    public List<String> services() {
        return discoveryClient.getServices();
    }


    /**
     * Returns the service instances for the given service ID.
     * @param serviceId The service ID.
     * @return  The list of service instances.
     */
    @GetMapping("/services/{serviceId}")
    public List<ServiceInstance> serviceInstances(@PathVariable String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }

    /**
     * Sends a message to the RabbitMQ server.
     * @param msg   The message to send.
     * @return    The response entity.
     */
    @GetMapping("/send/{queue}/{msg}")
    public ResponseEntity<String> sendMsg(@PathVariable String queue,@PathVariable String msg) {
        messageFeignClient.sendMsg(queue, msg);
        return ResponseEntity.ok("Message sent to "+queue+" : " + msg);
    }

}

