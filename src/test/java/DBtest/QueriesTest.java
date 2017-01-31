package DBtest;

import org.junit.After;
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

import config.ReadConfigFile;
import database.DBQueries;
import hello.ActiveMatchesController;
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
	
	private static String token1;
	private static String token2;
	private static Integer testMatch;
	
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8"));

	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext webApplicationContext;

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		ReadConfigFile r = ReadConfigFile.getInstance();
		token1 = r.getTestToken1();
		token2 = r.getTestToken2();
		
		//Creo una partita di test con i due token
		DBQueries.createMatch(token1, token2);
		testMatch = DBQueries.getActiveMatchesByToken(token1).getMatch_id();
		ActiveMatchesController.InsertMatch(testMatch, token1, token2); //per recuperare il match della partita ho riutilizzato dei metodi creati in precedenza...

	}
	
	@After
	public void closeTest() throws Exception {
		DBQueries.EndMatch(testMatch, "testuser");
	}

	@Test
	public void registerTest() throws Exception {
		mockMvc.perform(post("/register")
				.param("username", "testuser")
				.param("password", "testpassword")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	
	@Test
	public void authenticateTest() throws Exception {
		mockMvc.perform(post("/authenticate")
				.param("username", "testuser")
				.param("password", "testpassword")
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}
	
	@Test
	public void userTest() throws Exception {
		mockMvc.perform(get("/user")
				.param("username", "testuser")
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").exists());
	}
	
	@Test
	public void searchMatchTest() throws Exception {
		mockMvc.perform(get("/searchmatch")
				.contentType(contentType))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error", is(1)));
		
		mockMvc.perform(get("/searchmatch")
				.param("token", token1)
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.match_id", is(testMatch)));
	}
	
	@Test
	public void getQuestionTest() throws Exception {
		// caso di token errato
		mockMvc.perform(get("/question")
				.param("match_id", testMatch.toString())
				.param("number", "1")
				.param("token", "TokenErrato")
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error", is(1)));
		
		// caso di numero domanda non valido
		mockMvc.perform(get("/question")
				.param("match_id", testMatch.toString())
				.param("number", "0")
				.param("token", token1)
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error", is(3)));
		
		// match_id non esistente
		mockMvc.perform(get("/question")
				.param("match_id", "23897042")
				.param("number", "1")
				.param("token", token1)
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error", is(4)));
		
		//caso di domanda non ancora disponibile
		mockMvc.perform(get("/question")
				.param("match_id", testMatch.toString())
				.param("number", "4")
				.param("token", token1)
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error", is(5)));
		
		// caso corretto
		mockMvc.perform(get("/question")
				.param("match_id", testMatch.toString())
				.param("number", "1")
				.param("token", token1)
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.question").exists())
				.andExpect(jsonPath("$.answer_one").exists())
				.andExpect(jsonPath("$.score", is(0)));
	}
	
	@Test
	public void replyTest() throws Exception {
		//Token errato
		mockMvc.perform(post("/reply")
				.param("token", "TOKENERRATO")
				.param("number", "1")
				.param("reply_n", "2")
				.param("match_id", testMatch.toString())
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error", is(1)));
		
		//Numero di domanda non valido
		mockMvc.perform(post("/reply")
				.param("token", token1)
				.param("number", "40")
				.param("reply_n", "2")
				.param("match_id", testMatch.toString())
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error", is(3)));
		
		// Match errato
		mockMvc.perform(post("/reply")
				.param("token", token1)
				.param("number", "1")
				.param("reply_n", "2")
				.param("match_id", "13298312")
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error", is(4)));
		
		
		
		// caso corretto
		mockMvc.perform(post("/reply")
				.param("token", token1)
				.param("number", "1")
				.param("reply_n", "2")
				.param("match_id", testMatch.toString())
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.correct").exists());
		
		// Risposta già inviata
		mockMvc.perform(post("/reply")
				.param("token", token1)
				.param("number", "1")
				.param("reply_n", "2")
				.param("match_id", testMatch.toString())
				.contentType(contentType))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error", is(5)));
	}

}
