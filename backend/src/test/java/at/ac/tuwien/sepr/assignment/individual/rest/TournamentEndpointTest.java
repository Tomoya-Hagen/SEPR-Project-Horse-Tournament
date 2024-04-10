package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class TournamentEndpointTest extends TestBase {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  //Tests whether a non-existent URL returns a 404. Negative test.
  @Test
  public void getNonexistentUrlReturns404() throws Exception {
    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/torunamrnts")
        ).andExpect(status().isNotFound());
  }

  //Tests whether an existent tournament ID returns a 200. Positive test.
  @Test
  public void getExistingTournamentReturns200() throws Exception {
    // Perform GET request to retrieve an existing tournament
    mockMvc.perform(MockMvcRequestBuilders
            .get("/tournaments", -1L)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }


}
