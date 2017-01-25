package DBtest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import hello.Application;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest (
	classes = Application.class,
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@ContextConfiguration
public class QueriesTest {
	
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8"));

	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext webApplicationContext;

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

	}

	@Test
	public void registerTest() throws Exception {
		mockMvc.perform(post("/register")
				.param("User", "testuser")
				.param("Password", "testpassword")
				.contentType(contentType))
				.andDo(print())
				.andExpect(status().isOk());
	}
	
	@Test
	public void authenticateTest() throws Exception {
		mockMvc.perform(post("/authenticate")
				.param("User", "testuser")
				.param("Password", "testpassword")
				.contentType(contentType))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}
	
	@Test
	public void userTest() throws Exception {
		mockMvc.perform(get("/user")
				.param("User", "testuser")
				.contentType(contentType))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").exists());
	}
	
	@Test
	public void searchMatchWithoutTokenTest() throws Exception {
		mockMvc.perform(get("/searchmatch")
				.contentType(contentType))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.err", is(1)));
	}

}
