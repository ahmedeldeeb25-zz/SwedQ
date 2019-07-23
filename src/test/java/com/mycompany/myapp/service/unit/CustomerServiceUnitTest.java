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
import com.mycompany.myapp.repository.CustomerRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.CustomerService;

// SpringRunner provides support for loading a Spring ApplicationContext and having beans @Autowired into your test instance and more
// (MockitoJUnitRunner) provides support for creating mocks and spies with Mockito.
@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceUnitTest {

	@Mock
	CustomerRepository customerRepository;
	@InjectMocks
	CustomerService customerService;

	private Customer customer;

	@Before
	public void setUp() {
		customer = new Customer();
		customer.setName("test customer");
		customer.setId(1L);

		ArrayList<Customer> customers = new ArrayList();
		customers.add(customer);

		when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(customer);
		when(customerRepository.findAll()).thenReturn(customers);
		when(customerRepository.findById(1L)).thenReturn(Optional.ofNullable(customer));
	}

	@Test
	public void createCustomer() {
		Customer newCustomer = customerRepository.save(customer);
		assertThat(newCustomer.getName()).isEqualTo(customer.getName());
	}

	@Test
	public void getAllcustomers() {
		List<Customer> newCustomers = customerRepository.findAll();
		assertThat(newCustomers.size()).isEqualTo(1);
	}

	@Test
	public void findOne() {
		Customer newCustomer = customerRepository.findById(1L).get();
		assertThat(newCustomer.getName()).isEqualTo(customer.getName());
	}
}
