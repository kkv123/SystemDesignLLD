package factoryManagement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;


interface Publisher {
    public void publish(List<FactoryContentManager> factoryContentManagers);
}

interface Notification {
    public void update(FactoryContentManager factoryContentManager);
}

interface TimeSeriesRepository {
    void write(List<FactoryContentManager> batch);
}

class Factory {
    String factoryName;
    String factoryId;
    List<Machine> machine;

    public Factory(String factoryName, String factoryId) {
        this.factoryName = factoryName;
        this.factoryId = factoryId;
        machine = new ArrayList<>();
    }

    public void setMachine(Machine m){
        machine.add(m);
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public List<Machine> getMachine() {
        return machine;
    }

    public void setMachine(List<Machine> machine) {
        this.machine = machine;
    }

}

class Machine {
    String machineId;
    String machineName;
    List<Sensors> sensors;

    public Machine(String machineId, String machineName ) {
        this.machineId = machineId;
        this.machineName = machineName;
        sensors= new ArrayList<>();
    }

    public void addSensors(Sensors s){
        sensors.add(s);
    }
    public List<Sensors> getSensors() {
        return sensors;
    }

    public String getMachineName() {
        return machineName;
    }

    public String getMachineId() {
        return machineId;
    }


}

abstract class Sensors {
    String sensorId;
    String unit;
    int value;

    public Sensors(String sensorId, String unit, int value) {
        this.sensorId = sensorId;
        this.unit = unit;
        this.value = value;
    }

    public FactoryContentManager toFactoryContent(Factory factory, Machine machine) {
        return new FactoryContentManager(factory.getFactoryId(), machine.getMachineId(), sensorId,
                unit, getReading(), UUID.randomUUID().toString(), Instant.now().toString());
    }

    public abstract int getReading();
}

class TemperatureSensors extends Sensors {

    public TemperatureSensors(String sensorId, String unit, int value) {
        super(sensorId, unit, value);
    }

    @Override
    public int getReading() {
        double val = Math.random() * 50;
        return (int) (value + val);
    }
}

class VibrationSensors extends Sensors {

    public VibrationSensors(String sensorId, String unit, int value) {
        super(sensorId, unit, value);
    }

    @Override
    public int getReading() {
        double val = Math.random() * 5;
        return (int) (value + val);
    }
}

class FactoryContentManager {
    String factoryId;
    String machineId;
    String sensorId;
    String unit;
    int value;
    String messageId;
    String timeStamp;

    public FactoryContentManager(String factoryId, String machineId, String sensorId, String unit, int value, String messageId, String timeStamp) {
        this.factoryId = factoryId;
        this.machineId = machineId;
        this.sensorId = sensorId;
        this.unit = unit;
        this.value = value;
        this.messageId = messageId;
        this.timeStamp = timeStamp;
    }

    public String getUnit() {
        return unit;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public int getValue() {
        return value;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }


    @Override
    public String toString() {
        return "FactoryContentManager{" +
                "factoryId='" + factoryId + '\'' +
                ", machineId='" + machineId + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", unit='" + unit + '\'' +
                ", value=" + value +
                ", messageId='" + messageId + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}

class KafkaPublisher implements Publisher {

    @Override
    public void publish(List<FactoryContentManager> factoryContentManagers) {
        System.out.println("publishing data " + factoryContentManagers);
    }
}

class Gateway {
    Publisher publisher;
    Factory factory;

    public Gateway(Publisher publisher, Factory factory) {
        this.publisher = publisher;
        this.factory = factory;
    }

    public void CollectAndPublish() {
        System.out.println("Collecting & Publishing through Gateway -");
//        List<FactoryContentManager> factoryContentManagers = new ArrayList<>();
//        for (int i = 0; i < factory.getMachine().size(); i++) {
//            List<Machine> machine = factory.getMachine();
//            for (int j = 0; j < machine.size(); i++) {
//                List<Sensors> sensors = machine.get(i).getSensors();
//                for (Sensors sensors1 : sensors) {
//                    factoryContentManagers.add(sensors1.toFactoryContent(factory, machine.get(j)));
//                }
//            }
//        }
        List<FactoryContentManager> factoryContentManagers = factory.getMachine().stream().flatMap(
                machine -> machine.getSensors().stream().map(sensors -> sensors
                        .toFactoryContent(factory, machine))).collect(Collectors.toList());

        publisher.publish(factoryContentManagers);
    }
}

class ThresholdAlert implements Notification {
    double threshold;

    public ThresholdAlert(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void update(FactoryContentManager factoryContentManager) {
        boolean check = factoryContentManager.getValue() > threshold ? true : false;
        if (check) {
            System.out.println("Notification need to send for this");
        }
    }
}

class AlertManager {
    List<Notification> notifications = new ArrayList<>();

    public AlertManager(Notification notification) {
        notifications.add(notification);
    }

    public void getAlert(FactoryContentManager factoryContentManager) {
        for (Notification it : notifications) {
            it.update(factoryContentManager);
        }
    }
}

class InMemoryTimeSeriesRepository implements TimeSeriesRepository {
    private List<FactoryContentManager> store = new ArrayList<>();

    @Override
    public void write(List<FactoryContentManager> batch) {
        store.addAll(batch);
        System.out.println("TSDB wrote " + batch.size() + " points. Total=" + store.size());
    }
}

class StreamProcessor{
    AlertManager notification;
    InMemoryTimeSeriesRepository inMemoryTimeSeriesRepository;

    public StreamProcessor(AlertManager notification, InMemoryTimeSeriesRepository inMemoryTimeSeriesRepository){
        this.inMemoryTimeSeriesRepository=inMemoryTimeSeriesRepository;
        this.notification= notification;
    }

    public void getStatus(List<FactoryContentManager> factoryContentManagers){
        for(FactoryContentManager factoryContent: factoryContentManagers){
            System.out.println(factoryContent.getFactoryId()+" "+factoryContent.machineId+" "+factoryContent.sensorId);
            notification.getAlert(factoryContent);
        }
        System.out.println("writing data for ");
        inMemoryTimeSeriesRepository.write(factoryContentManagers);

    }
}

public class MainFactory {

    public static void main(String[] args) {
        Factory f1= new Factory("F1","Pune-F1");

        Machine m1= new Machine("M-1","machine1");
        Machine m2= new Machine("M-2","machine2");

        m1.addSensors(new TemperatureSensors("S1","C",45));
        m2.addSensors(new VibrationSensors("S1","C",55));
        f1.setMachine(m1);
        f1.setMachine(m2);

        Publisher publisher = new KafkaPublisher();
        Gateway gateway = new Gateway( publisher, f1);
        gateway.CollectAndPublish();

        AlertManager alertManager = new AlertManager(new ThresholdAlert(98));

        StreamProcessor streamProcessor = new StreamProcessor(alertManager,new InMemoryTimeSeriesRepository());

        List<FactoryContentManager> factoryContentManagers = f1.getMachine().stream().flatMap(machine -> machine.getSensors()
                .stream().map(sensors -> sensors.toFactoryContent(f1,machine))).collect(Collectors.toList());

        streamProcessor.getStatus(factoryContentManagers);
    }
}

