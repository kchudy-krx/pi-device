package pl.com.krx.malinowka;

import org.iot.raspberry.grovepi.devices.GroveLightSensor;
import org.iot.raspberry.grovepi.devices.GroveTemperatureAndHumiditySensor;
import org.iot.raspberry.grovepi.devices.GroveTemperatureAndHumidityValue;
import org.iot.raspberry.grovepi.pi4j.GrovePi4J;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    public static void main(String[] args) throws IOException {
        Logger.getLogger("GrovePi").setLevel(Level.WARNING);
        Logger.getLogger("RaspberryPi").setLevel(Level.WARNING);

        GrovePi4J grovePi4J = new GrovePi4J();

        GroveTemperatureAndHumiditySensor groveTemperatureAndHumiditySensor = new GroveTemperatureAndHumiditySensor(grovePi4J, 7, GroveTemperatureAndHumiditySensor.Type.DHT11);
        GroveTemperatureAndHumidityValue groveTemperatureAndHumidityValue = groveTemperatureAndHumiditySensor.get();

        GroveLightSensor groveLightSensor = new GroveLightSensor(grovePi4J, 0);
        Double lightSensorValue = groveLightSensor.get();

        DataSender.send(UUID.fromString("6699e603-6e66-4070-b8c0-d841e02f918f"),groveTemperatureAndHumidityValue.getTemperature());
        DataSender.send(UUID.fromString("b391f090-4fc6-46e2-97f7-49737d2d1e2c"),groveTemperatureAndHumidityValue.getHumidity());
        DataSender.send(UUID.fromString("746992ae-40c8-49fb-924a-c6685e3ca92b"),lightSensorValue);
    }

}
