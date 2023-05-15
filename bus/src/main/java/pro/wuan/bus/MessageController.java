package pro.wuan.bus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.event.Destination;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.bus.config.MyApplicationEvent;

@RestController
@RefreshScope
public class MessageController {
    @Autowired
    private ApplicationEventPublisher publisher;

    @RequestMapping("/send/{message}")
    @ResponseBody
    public ResponseEntity<String> handleMessage(@PathVariable String message) {
        RemoteApplicationEvent event = new MyApplicationEvent(this, "backendbaseplatform", getDestination("backendbaseplatform"),message);
        publisher.publishEvent(event);
        return ResponseEntity.ok(message);
    }

    @EventListener
    public void onMyCustomEvent(MyApplicationEvent event) {
        System.out.println("Received event: " + event.getMessage());
    }


	public Destination getDestination(String originalDestination) {
		String path = originalDestination;
		if (path == null) {
			path = "**";
		}
		// If the path is not already a wildcard, match everything that
		// follows if there at most two path elements, and last element is not a global
		// wildcard already
		if (!"**".equals(path)) {
			if (StringUtils.countOccurrencesOf(path, ":") <= 1 && !StringUtils.endsWithIgnoreCase(path, ":**")) {
				// All instances of the destination unless specifically requested
				path = path + ":**";
			}
		}

		final String finalPath = path;
		return () -> finalPath;
	}
}
