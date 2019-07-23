package com.mycompany.myapp.web.rest.unit;

import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.Vehicle;
import com.mycompany.myapp.service.VehicleService;
import com.mycompany.myapp.web.rest.TestUtil;
import com.mycompany.myapp.web.rest.VehicleResource;

import static org.hamcrest.Matchers.hasSize;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Test class for the VehicleResource REST controller.
 *
 * @see VehicleResource
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = VehicleResource.class, secure = false)
public class VehicleResourceUnitTest {

	@MockBean
	private VehicleService vehicleService;
	@Autowired
	private MockMvc restVehicleMockMvc;

	private Vehicle vehicle;
	private Vehicle testVehicle;

	@Before
	public void createEntities() {
		Customer customer = new Customer();
		customer.setName("test customer");
		customer.setAddress("UAE");
		customer.setNotes("test note");
		customer.setId(1L);

		vehicle = new Vehicle();
		vehicle.setCoordinates(null);
		vehicle.setCustomer(customer);
		vehicle.setReg("unique reg");
		vehicle.setStatus("off");
		vehicle.setOnline("false");

		testVehicle = new Vehicle();
		testVehicle.setCoordinates(null);
		testVehicle.setCustomer(customer);
		testVehicle.setReg("unique reg2");
		testVehicle.setStatus("on");
		testVehicle.setOnline("true");
		testVehicle.setId("longId");
		
		
		ArrayList<Vehicle> vehicles = new ArrayList<>();
		vehicles.add(testVehicle);
		when(vehicleService.save(Mockito.any(Vehicle.class))).thenReturn(testVehicle);
		when(vehicleService.findAll()).thenReturn(vehicles);
		when(vehicleService.findOne(testVehicle.getId())).thenReturn(Optional.ofNullable(testVehicle));

	}


	 @Test
	 public void createVehicle() throws Exception {
	 restVehicleMockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON_UTF8)
	 .content(TestUtil.convertObjectToJsonBytes(vehicle))).andExpect(status().isCreated());
	 }

	@Test
	public void createVehicleWithExistingId() throws Exception {
		// Create the Vehicle with an existing ID
		// An entity with an existing ID cannot be created, so this API call must fail
		restVehicleMockMvc.perform(post("/api/vehicles").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(testVehicle))).andExpect(status().isBadRequest());
	}

	@Test
	public void checkRegIsRequired() throws Exception {
		// Create the Vehicle, which fails.
		vehicle.setReg(null);
		restVehicleMockMvc.perform(post("/api/vehicles").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(vehicle))).andExpect(status().isBadRequest());

	}

	@Test
	public void getAllVehicles() throws Exception {
		restVehicleMockMvc.perform(get("/api/vehicles?sort=id,desc")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void getVehicle() throws Exception {
		restVehicleMockMvc.perform(get("/api/vehicles/{id}", testVehicle.getId())).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.id").value(testVehicle.getId().toString()));
	}

	@Test
	public void getNonExistingVehicle() throws Exception {
		// Get the vehicle
		restVehicleMockMvc.perform(get("/api/vehicles/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
	}
	
	 @Test
	 public void updateNonExistingVehicle() throws Exception {
	 // If the entity doesn't have an ID, it will throw BadRequestAlertException
	 restVehicleMockMvc.perform(put("/api/vehicles").contentType(TestUtil.APPLICATION_JSON_UTF8)
	 .content(TestUtil.convertObjectToJsonBytes(vehicle))).andExpect(status().isBadRequest());
	 }
}
