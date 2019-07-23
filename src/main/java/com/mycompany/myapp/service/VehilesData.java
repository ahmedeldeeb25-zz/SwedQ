package com.mycompany.myapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mycompany.myapp.domain.Vehicle;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class VehilesData {

	private final Logger LOG = LoggerFactory.getLogger(VehilesData.class);
	@Autowired
	private VehicleService vehicleService;
	private SimpleDateFormat format = new SimpleDateFormat("d-mm-yyyy HH:mm:ss");
	private Random rand = new Random();

	// Trigger this every 1 minute
	@Scheduled(cron = "0 */1 * * * *")
	public synchronized void mailPollingsForEvery1Hour() {
		LOG.info("-------------->>>>>>>>>>>>>>> Generate Random Data for Vehicle ---------------->>>>>>>>>>");

		Calendar cal = Calendar.getInstance();
		String end = format.format(cal.getTime());

		try {

			List<Vehicle> vehicles = vehicleService.findAll();

			for (Vehicle vehicle : vehicles) {
				
				int status = rand.nextInt(5);
				int online = rand.nextInt(8);
				
				if (online > 0) {
					vehicle.setOnline("true");
					vehicle.setTime(end);
					vehicle.setStatus(status > 0 ? "on" : "off");
				} else {
					vehicle.setOnline("false");
				}
				LOG.info("---------- Vehicle Data {}", vehicle);

				vehicleService.save(vehicle);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
