package com.mycompany.myapp.web.rest.unit;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import java.util.*;


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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.service.CustomerService;
import com.mycompany.myapp.web.rest.CustomerResource;
import com.mycompany.myapp.web.rest.TestUtil;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(value = CustomerResource.class, secure = false)
//@ActiveProfiles("test")
public class CustomerResourceUnitTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private CustomerService customerService;

	private Customer testCustomer;
	private Customer testCustomer2;

	@Before
	public void setUp() {
		testCustomer = new Customer();
		testCustomer.setName("test user");
		testCustomer.setAddress("sweden");
		testCustomer.setNotes("test note");

		testCustomer2 = new Customer();
		testCustomer2.setName("test customer");
		testCustomer2.setAddress("UAE");
		testCustomer2.setNotes("test note");
		testCustomer2.setId(2L);

		ArrayList<Customer> customers = new ArrayList();
		customers.add(testCustomer);

		when(customerService.findAll()).thenReturn(customers);
		when(customerService.save(Mockito.any(Customer.class))).thenReturn(testCustomer2);
		when(customerService.findOne(2L)).thenReturn(Optional.ofNullable(testCustomer2));
		//when(customerService.delete(2L)).thenReturn(Optional.ofNullable(testCustomer2));
		// given(customerService.findAll()).willReturn;

	}

	@Test
	public void createCustomerWithId() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(testCustomer2);
		// An entity with ID must return a Bad request
		mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(status().is(400));
	}

	@Test
	public void createCustomer() throws Exception {
		testCustomer.setId(null);
		mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(testCustomer))).andExpect(status().isCreated());
	}

	@Test
	public void getAllCustomer() throws Exception {
		// Get all the customerList
		mockMvc.perform(get("/api/customers")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$", hasSize(1)));

	}

	@Test
	public void checkNameIsRequired() throws Exception {
		// Create the Customer, which fails.
		testCustomer.setName(null);
		mockMvc.perform(post("/api/customers").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(testCustomer))).andExpect(status().isBadRequest());

	}

	@Test
	public void checkAddressIsrequired() throws Exception {

		testCustomer.setAddress(null);
		mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(testCustomer))).andExpect(status().isBadRequest());
	}

	@Test
	public void getNonExistingCustomer() throws Exception {
		// Get the customer
		mockMvc.perform(get("/api/customers/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
	}
	
	@Test
	public void getCustomer() throws Exception {
		mockMvc.perform(get("/api/customers/{id}",2L))
		.andExpect(jsonPath("$.id").value(testCustomer2.getId()))
		 .andExpect(jsonPath("$.name").value(testCustomer2.getName()));
	}
	
	@Test(expected = Exception.class)
	public void getNullCustomer() throws Exception {
		mockMvc.perform(get("/api/customers/{id}",null));
	}
	
	
    @Test
    public void updateNonExistingCustomer() throws Exception {
        mockMvc.perform(put("/api/customers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(testCustomer)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/{id}", testCustomer2.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

    }
    

}
