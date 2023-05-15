package pro.wuan.bus.config;

import org.springframework.cloud.bus.event.Destination;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

public class MyApplicationEvent extends RemoteApplicationEvent {
    private String message;

    // 供反序列化使用的构造器，必须存在
    public MyApplicationEvent() {
    }

    // 你自己的构造器，你可以按照你的需要添加参数
    public MyApplicationEvent(Object source, String originService, Destination destinationService, String message) {
        // 调用父类构造器，设置事件源和服务的名字
        super(source, originService, destinationService);
        this.message = message;
    }

    // 提供一个get方法，使你可以访问这个字段
    public String getMessage() {
        return message;
    }

    // 如果需要，你也可以添加一个set方法
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MyApplicationEvent{" + "originService='" + getOriginService() + '\'' + ", destinationService='" + getDestinationService() + '\'' + ", value=" + getSource() + '}';
    }

}
