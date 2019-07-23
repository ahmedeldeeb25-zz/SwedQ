package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.TestproApp;

import com.mycompany.myapp.domain.Vehicle;
import com.mycompany.myapp.repository.VehicleRepository;
import com.mycompany.myapp.service.VehicleService;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the VehicleResource REST controller.
 *
 * @see VehicleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestproApp.class)
public class VehicleResourceIntTest {

	private static final String DEFAULT_REG = "AAAAAAAAAA";
	private static final String UPDATED_REG = "BBBBBBBBBB";

	private static final String DEFAULT_STATUS = "AAAAAAAAAA";
	private static final String UPDATED_STATUS = "BBBBBBBBBB";

	private static final String DEFAULT_ONLINE = "false";
	private static final String UPDATED_ONLINE = "true";

	private static final String DEFAULT_TIME = "AAAAAAAAAA";
	private static final String UPDATED_TIME = "BBBBBBBBBB";

	private static final String DEFAULT_COORDINATES = "AAAAAAAAAA";
	private static final String UPDATED_COORDINATES = "BBBBBBBBBB";

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private EntityManager em;

	@Autowired
	private Validator validator;

	private MockMvc restVehicleMockMvc;

	private Vehicle vehicle;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final VehicleResource vehicleResource = new VehicleResource(vehicleService);
		this.restVehicleMockMvc = MockMvcBuilders.standaloneSetup(vehicleResource)
				.setCustomArgumentResolvers(pageableArgumentResolver).setControllerAdvice(exceptionTranslator)
				.setConversionService(createFormattingConversionService()).setMessageConverters(jacksonMessageConverter)
				.setValidator(validator).build();
	}

	/**
	 * Create an entity for this test.
	 *
	 * This is a static method, as tests for other entities might also need it, if
	 * they test an entity which requires the current entity.
	 */
	public static Vehicle createEntity(EntityManager em) {
		Vehicle vehicle = new Vehicle().reg(DEFAULT_REG).status(DEFAULT_STATUS).online(DEFAULT_ONLINE)
				.time(DEFAULT_TIME).coordinates(DEFAULT_COORDINATES);
		return vehicle;
	}

	@Before
	public void initTest() {
		vehicle = createEntity(em);
	}

	@Test
	@Transactional
	public void createVehicle() throws Exception {
		int databaseSizeBeforeCreate = vehicleRepository.findAll().size();
		// Create the Vehicle
		restVehicleMockMvc.perform(post("/api/vehicles").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(vehicle))).andExpect(status().isCreated());

		// Validate the Vehicle in the database
		List<Vehicle> vehicleList = vehicleRepository.findAll();
		assertThat(vehicleList).hasSize(databaseSizeBeforeCreate + 1);
		Vehicle testVehicle = vehicleList.get(vehicleList.size() - 1);
		assertThat(testVehicle.getReg()).isEqualTo(DEFAULT_REG);
		assertThat(testVehicle.getStatus()).isEqualTo(DEFAULT_STATUS);
		assertThat(testVehicle.getTime()).isEqualTo(DEFAULT_TIME);
		assertThat(testVehicle.getCoordinates()).isEqualTo(DEFAULT_COORDINATES);
	}

	@Test
	@Transactional
	public void createVehicleWithExistingId() throws Exception {
		int databaseSizeBeforeCreate = vehicleRepository.findAll().size();

		// Create the Vehicle with an existing ID
		vehicle.setId("1");

		// An entity with an existing ID cannot be created, so this API call must fail
		restVehicleMockMvc.perform(post("/api/vehicles").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(vehicle))).andExpect(status().isBadRequest());

		// Validate the Vehicle in the database
		List<Vehicle> vehicleList = vehicleRepository.findAll();
		assertThat(vehicleList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void checkRegIsRequired() throws Exception {
		int databaseSizeBeforeTest = vehicleRepository.findAll().size();
		// set the field null
		vehicle.setReg(null);

		// Create the Vehicle, which fails.

		restVehicleMockMvc.perform(post("/api/vehicles").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(vehicle))).andExpect(status().isBadRequest());

		List<Vehicle> vehicleList = vehicleRepository.findAll();
		assertThat(vehicleList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void getAllVehicles() throws Exception {
		// Initialize the database
		vehicleRepository.saveAndFlush(vehicle);

		// Get all the vehicleList
		restVehicleMockMvc.perform(get("/api/vehicles?sort=id,desc")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.[*].id").value(hasItem(vehicle.getId().toString())))
				.andExpect(jsonPath("$.[*].reg").value(hasItem(DEFAULT_REG.toString())))
				.andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
				.andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())))
				.andExpect(jsonPath("$.[*].coordinates").value(hasItem(DEFAULT_COORDINATES.toString())));
	}

	@Test
	@Transactional
	public void getVehicle() throws Exception {
		// Initialize the database
		vehicleRepository.saveAndFlush(vehicle);

		// Get the vehicle
		restVehicleMockMvc.perform(get("/api/vehicles/{id}", vehicle.getId())).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.id").value(vehicle.getId().toString()))
				.andExpect(jsonPath("$.reg").value(DEFAULT_REG.toString()))
				.andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
				.andExpect(jsonPath("$.time").value(DEFAULT_TIME.toString()))
				.andExpect(jsonPath("$.coordinates").value(DEFAULT_COORDINATES.toString()));
	}

	@Test
	@Transactional
	public void getNonExistingVehicle() throws Exception {
		// Get the vehicle
		restVehicleMockMvc.perform(get("/api/vehicles/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateVehicle() throws Exception {
		// Initialize the database
		vehicleService.save(vehicle);

		int databaseSizeBeforeUpdate = vehicleRepository.findAll().size();

		// Update the vehicle
		Vehicle updatedVehicle = vehicleRepository.findById(vehicle.getId()).get();
		// Disconnect from session so that the updates on updatedVehicle are not
		// directly saved in db
		em.detach(updatedVehicle);
		updatedVehicle.reg(UPDATED_REG).status(UPDATED_STATUS).online(UPDATED_ONLINE).time(UPDATED_TIME)
				.coordinates(UPDATED_COORDINATES);

		restVehicleMockMvc.perform(put("/api/vehicles").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(updatedVehicle))).andExpect(status().isOk());

		// Validate the Vehicle in the database
		List<Vehicle> vehicleList = vehicleRepository.findAll();
		assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate);
		Vehicle testVehicle = vehicleList.get(vehicleList.size() - 1);
		assertThat(testVehicle.getReg()).isEqualTo(UPDATED_REG);
		assertThat(testVehicle.getStatus()).isEqualTo(UPDATED_STATUS);
		assertThat(testVehicle.getTime()).isEqualTo(UPDATED_TIME);
		assertThat(testVehicle.getCoordinates()).isEqualTo(UPDATED_COORDINATES);
	}

	@Test
	@Transactional
	public void updateNonExistingVehicle() throws Exception {
		int databaseSizeBeforeUpdate = vehicleRepository.findAll().size();

		// Create the Vehicle

		// If the entity doesn't have an ID, it will throw BadRequestAlertException
		restVehicleMockMvc.perform(put("/api/vehicles").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(vehicle))).andExpect(status().isBadRequest());

		// Validate the Vehicle in the database
		List<Vehicle> vehicleList = vehicleRepository.findAll();
		assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate);
	}

	@Test
	@Transactional
	public void deleteVehicle() throws Exception {
		// Initialize the database
		vehicleService.save(vehicle);

		int databaseSizeBeforeDelete = vehicleRepository.findAll().size();

		// Delete the vehicle
		restVehicleMockMvc.perform(delete("/api/vehicles/{id}", vehicle.getId()).accept(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		// Validate the database is empty
		List<Vehicle> vehicleList = vehicleRepository.findAll();
		assertThat(vehicleList).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Test
	@Transactional
	public void equalsVerifier() throws Exception {
		TestUtil.equalsVerifier(Vehicle.class);
		Vehicle vehicle1 = new Vehicle();
		vehicle1.setId("1");
		Vehicle vehicle2 = new Vehicle();
		vehicle2.setId(vehicle1.getId());
		assertThat(vehicle1).isEqualTo(vehicle2);
		vehicle2.setId("2L");
		assertThat(vehicle1).isNotEqualTo(vehicle2);
		vehicle1.setId(null);
		assertThat(vehicle1).isNotEqualTo(vehicle2);
	}
}
