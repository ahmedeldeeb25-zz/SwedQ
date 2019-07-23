package com.mycompany.myapp.service.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.Vehicle;
import com.mycompany.myapp.repository.CustomerRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.repository.VehicleRepository;
import com.mycompany.myapp.service.CustomerService;
import com.mycompany.myapp.service.VehicleService;

// SpringRunner provides support for loading a Spring ApplicationContext and having beans @Autowired into your test instance and more
// (MockitoJUnitRunner) provides support for creating mocks and spies with Mockito.
@RunWith(MockitoJUnitRunner.class)
public class VehicleServiceUnitTest {

	@Mock
	VehicleRepository vehicleRepository;
	@InjectMocks
	VehicleService vehicleService;

	private Vehicle vehicle;

	@Before
	public void setUp() {
		vehicle = new Vehicle();
		vehicle.setReg("test reg");
		vehicle.setId("longId");

		ArrayList<Vehicle> vehicles = new ArrayList();
		vehicles.add(vehicle);

		when(vehicleRepository.save(Mockito.any(Vehicle.class))).thenReturn(vehicle);
		when(vehicleRepository.findAll()).thenReturn(vehicles);
		when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.ofNullable(vehicle));
	}

	@Test
	public void createVehicle() {
		Vehicle newVehicle = vehicleRepository.save(vehicle);
		assertThat(newVehicle.getReg()).isEqualTo(vehicle.getReg());
	}
	
	@Test
	public void getAllVehicles() {
		List<Vehicle> newCustomers = vehicleRepository.findAll();
		assertThat(newCustomers.size()).isEqualTo(1);
	}

	@Test
	public void findOne() {
		Vehicle newVehicle = vehicleRepository.findById(vehicle.getId()).get();
		assertThat(newVehicle.getId()).isEqualTo(vehicle.getId());
	}
	
	@Test(expected = Exception.class)
	public void findNullObject() {
		 vehicleRepository.findById(null).get();
	}
	
}
