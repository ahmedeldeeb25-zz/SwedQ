package com.mycompany.myapp.web.rest.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	CustomerResourceUnitTest.class,
	VehicleResourceUnitTest.class
})
public class ResourcesUnitTestSuite {

}
