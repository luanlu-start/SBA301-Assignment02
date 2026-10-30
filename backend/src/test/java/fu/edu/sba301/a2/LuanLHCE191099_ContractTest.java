package fu.edu.sba301.a2;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

// Integration tests load the application and send HTTP-style requests through MockMvc and Spring Security.
// The test profile uses an isolated H2 database; each test transaction rolls back its changes afterward.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LuanLHCE191099_ContractTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    // Attach a server session and include Spring's CSRF value for write operations.
    JsonNode call(String method, String path, Object body, MockHttpSession session, int expected)
        throws Exception {
        var builder = request(HttpMethod.valueOf(method), path).contentType(
            MediaType.APPLICATION_JSON
        );
        if (body != null) {
            builder.content(mapper.writeValueAsBytes(body));
        }
        if (session != null) {
            builder.session(session);
        }
        if (!method.equals("GET")) {
            builder.with(csrf());
        }
        MvcResult result = mvc.perform(builder).andReturn();
        assertEquals(
            expected,
            result.getResponse().getStatus(),
            path + ": " + result.getResponse().getContentAsString()
        );
        String bodyText = result.getResponse().getContentAsString();
        return bodyText.isBlank() ? mapper.nullNode() : mapper.readTree(bodyText);
    }

    MockHttpSession login(String email, String password) throws Exception {
        MvcResult result = mvc
            .perform(
                post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(Map.of("email", email, "password", password)))
            )
            .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    String unique() {
        return "test" + System.nanoTime();
    }

    MockHttpSession admin() throws Exception {
        return login("admin@funews.local", "Admin@123");
    }

    MockHttpSession staff() throws Exception {
        return login("staff@funews.local", "Staff@123");
    }

    Map<String, Object> category(String name) {
        return Map.of("name", name, "description", "Test category", "status", 1);
    }

    Map<String, Object> news(String title, int status) {
        return Map.of(
            "title",
            title,
            "headline",
            "Test headline",
            "content",
            "Test article content",
            "source",
            "Test source",
            "categoryId",
            1,
            "status",
            status,
            "tags",
            List.of(Map.of("name", "test", "note", "Tag note"))
        );
    }

    @Test
    // Anonymous readers receive active news with populated relation DTO fields; drafts and private APIs remain hidden.
    void publicReadersSeeOnlyActiveNews() throws Exception {
        JsonNode rows = call("GET", "/api/public/news", null, null, 200);
        assertFalse(rows.isEmpty());
        for (JsonNode row : rows) {
            assertEquals(1, row.get("status").asInt());
            assertFalse(row.get("categoryId").isNull());
            assertFalse(row.get("categoryName").isNull());
            assertFalse(row.get("createdByName").isNull());
        }
        call("GET", "/api/public/news/2", null, null, 404);
        call("GET", "/api/accounts", null, null, 401);
    }

    @Test
    // A session identifies the caller; method security still enforces Admin/Staff permissions.
    void rolesAreEnforcedByBackend() throws Exception {
        MockHttpSession adminSession = admin();
        MockHttpSession staffSession = staff();
        call("GET", "/api/accounts", null, adminSession, 200);
        call("GET", "/api/accounts", null, staffSession, 403);
        call("GET", "/api/categories", null, adminSession, 403);
        call("GET", "/api/categories", null, staffSession, 200);
        call("GET", "/api/news", null, new MockHttpSession(), 401);
    }

    @Test
    // Verify account Create/Read/Update/Delete, duplicate-email rejection and omission of password fields.
    void accountsSupportCrudWithoutExposingPasswords() throws Exception {
        MockHttpSession session = admin();
        String email = unique() + "@example.com";
        JsonNode created = call(
            "POST",
            "/api/accounts",
            Map.of("name", "Test Staff", "email", email, "password", "Test@123", "role", 2),
            session,
            201
        );
        long id = created.get("id").asLong();
        assertFalse(created.has("password"));
        call("GET", "/api/accounts/" + id, null, session, 200);
        call(
            "PUT",
            "/api/accounts/" + id,
            Map.of("name", "Edited Staff", "email", email, "password", "", "role", 2),
            session,
            200
        );
        call(
            "POST",
            "/api/accounts",
            Map.of("name", "Duplicate", "email", email, "password", "Test@123", "role", 2),
            session,
            409
        );
        call("DELETE", "/api/accounts/" + id, null, session, 204);
        call("GET", "/api/accounts/" + id, null, session, 404);
    }

    @Test
    void accountSearchSupportsNamesEmailsCaseAndWhitespace() throws Exception {
        MockHttpSession session = admin();
        String email = unique() + "@example.com";
        String name = "SearchName" + unique();
        JsonNode created = call(
            "POST",
            "/api/accounts",
            Map.of("name", name, "email", email, "password", "Test@123", "role", 2),
            session,
            201
        );

        // A browser search may contain uppercase letters and surrounding spaces.
        MvcResult result = mvc
            .perform(
                get("/api/accounts")
                    .param("q", "  " + email.toUpperCase(Locale.ROOT) + "  ")
                    .session(session)
            )
            .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        JsonNode matches = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(1, matches.size());
        assertEquals(created.get("id"), matches.get(0).get("id"));
        assertFalse(matches.get(0).has("password"));

        assertEquals(1, call("GET", "/api/accounts?q=" + name, null, session, 200).size());
        assertTrue(
            call("GET", "/api/accounts?q=no-match-" + unique(), null, session, 200).isEmpty()
        );
        assertEquals(3, call("GET", "/api/accounts", null, session, 200).size());
    }

    @Test
    // Verify relation guards return 409 instead of deleting referenced authors/categories.
    void relatedAccountsAndCategoriesCannotBeDeleted() throws Exception {
        call("DELETE", "/api/accounts/2", null, admin(), 409);
        call("DELETE", "/api/categories/1", null, staff(), 409);
    }

    @Test
    // Verify category CRUD/search and reject a category being its own parent.
    void categoriesSupportCrudSearchAndParentValidation() throws Exception {
        MockHttpSession session = staff();
        String name = unique();
        long id = call("POST", "/api/categories", category(name), session, 201).get("id").asLong();
        assertEquals(1, call("GET", "/api/categories?q=" + name, null, session, 200).size());
        call(
            "PUT",
            "/api/categories/" + id,
            Map.of("name", name, "description", "Changed", "status", 0),
            session,
            200
        );
        call(
            "PUT",
            "/api/categories/" + id,
            Map.of("name", name, "parentId", id, "status", 1),
            session,
            400
        );
        call("DELETE", "/api/categories/" + id, null, session, 204);
    }

    @Test
    // Verify tags persist, history finds the author's article and inactive updates disappear from public reads.
    void articlesSupportTagsUpdateHistoryAndDeletion() throws Exception {
        MockHttpSession session = staff();
        String title = unique();
        JsonNode created = call("POST", "/api/news", news(title, 1), session, 201);
        long id = created.get("id").asLong();
        assertEquals("test", created.get("tags").get(0).get("name").asText());
        assertEquals(1, call("GET", "/api/news/history?q=" + title, null, session, 200).size());
        call("PUT", "/api/news/" + id, news(title + " edited", 0), session, 200);
        call("GET", "/api/public/news/" + id, null, null, 404);
        call("DELETE", "/api/news/" + id, null, session, 204);
        call("GET", "/api/news/" + id, null, session, 404);
    }

    @Test
    // Verify self-service edits preserve the Staff role and reject a role field supplied by the client.
    void staffProfileCannotChangeItsRole() throws Exception {
        MockHttpSession session = staff();
        JsonNode profile = call(
            "PUT",
            "/api/profile",
            Map.of("name", "Edited profile", "email", "staff@funews.local", "password", ""),
            session,
            200
        );
        assertEquals(2, profile.get("role").asInt());
        call(
            "PUT",
            "/api/profile",
            Map.of("name", "Spoof", "email", "staff@funews.local", "role", 1),
            session,
            400
        );
    }

    @Test
    // Verify validation failures return 400 and incorrect credentials return 401.
    void invalidInputAndBadCredentialsHaveDefinedErrors() throws Exception {
        call(
            "POST",
            "/api/auth/login",
            Map.of("email", "staff@funews.local", "password", "wrong"),
            null,
            401
        );
        call("POST", "/api/auth/login", Map.of("email", "invalid", "password", "x"), null, 400);
        call("POST", "/api/categories", category("   "), staff(), 400);
        call(
            "POST",
            "/api/accounts",
            Map.of(
                "name",
                "Test",
                "email",
                unique() + "@example.com",
                "password",
                "Test@123",
                "role",
                3
            ),
            admin(),
            400
        );
    }

    @Test
    void loginRotatesSessionAndLogoutInvalidatesIt() throws Exception {
        MvcResult csrfResult = mvc.perform(get("/api/auth/csrf")).andReturn();
        assertEquals(200, csrfResult.getResponse().getStatus());
        JsonNode csrfData = mapper.readTree(csrfResult.getResponse().getContentAsString());
        MockHttpSession session = (MockHttpSession) csrfResult.getRequest().getSession(false);
        String previousSessionId = session.getId();

        MvcResult loginResult = mvc
            .perform(
                post("/api/auth/login")
                    .session(session)
                    .header(csrfData.get("headerName").asText(), csrfData.get("token").asText())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsBytes(
                            Map.of("email", "staff@funews.local", "password", "Staff@123")
                        )
                    )
            )
            .andReturn();
        assertEquals(200, loginResult.getResponse().getStatus());
        assertNotEquals(previousSessionId, session.getId());
        JsonNode loginData = mapper.readTree(loginResult.getResponse().getContentAsString());
        assertEquals("STAFF", loginData.get("user").get("role").asText());
        assertFalse(loginData.has("token"));
        assertFalse(loginData.get("user").has("password"));
        call("GET", "/api/auth/me", null, session, 200);

        call("POST", "/api/auth/logout", null, session, 204);
        assertTrue(session.isInvalid());
        call("GET", "/api/auth/me", null, null, 401);
    }

    @Test
    void cookieAuthenticatedWritesRequireCsrf() throws Exception {
        MockHttpSession session = staff();
        MvcResult missingCsrf = mvc
            .perform(
                post("/api/categories")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(category(unique())))
            )
            .andReturn();
        assertEquals(403, missingCsrf.getResponse().getStatus());
        call("POST", "/api/categories", category(unique()), session, 201);
    }
}
