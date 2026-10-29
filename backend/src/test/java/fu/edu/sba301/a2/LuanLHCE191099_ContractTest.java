package fu.edu.sba301.a2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LuanLHCE191099_ContractTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    JsonNode call(String method,String path,Object body,String token,int expected) throws Exception {
        var builder=request(HttpMethod.valueOf(method),path).contentType(MediaType.APPLICATION_JSON);
        if(body!=null) builder.content(mapper.writeValueAsBytes(body));
        if(token!=null) builder.header("Authorization","Bearer "+token);
        MvcResult result=mvc.perform(builder).andReturn();
        assertEquals(expected,result.getResponse().getStatus(),path+": "+result.getResponse().getContentAsString());
        String text=result.getResponse().getContentAsString();
        return text.isBlank()?mapper.nullNode():mapper.readTree(text);
    }
    String login(String email,String password) throws Exception {return call("POST","/api/auth/login",Map.of("email",email,"password",password),null,200).get("token").asText();}
    String unique() {return "test"+System.nanoTime();}

    String admin() throws Exception {return login("admin@funews.local","Admin@123");}
    String staff() throws Exception {return login("staff@funews.local","Staff@123");}
    Map<String,Object> category(String name) {return Map.of("name",name,"description","Test category","status",1);}
    Map<String,Object> news(String title,int status) {return Map.of("title",title,"headline","Test headline","content","Test article content","source","Test source","categoryId",1,"status",status,"tags",List.of(Map.of("name","test","note","Tag note")));}
    @Test void publicReadersSeeOnlyActiveNews() throws Exception {
        JsonNode rows=call("GET","/api/public/news",null,null,200);
        assertFalse(rows.isEmpty());for(JsonNode row:rows) assertEquals(1,row.get("status").asInt());
        call("GET","/api/public/news/2",null,null,404);
        call("GET","/api/accounts",null,null,401);
    }
    @Test void rolesAreEnforcedByBackend() throws Exception {
        String a=admin(),s=staff();
        call("GET","/api/accounts",null,a,200);call("GET","/api/accounts",null,s,403);
        call("GET","/api/categories",null,a,403);call("GET","/api/categories",null,s,200);
        call("GET","/api/news",null,"not-a-jwt",401);
    }
    @Test void accountsSupportCrudWithoutExposingPasswords() throws Exception {
        String token=admin(),email=unique()+"@example.com";
        JsonNode created=call("POST","/api/accounts",Map.of("name","Test Staff","email",email,"password","Test@123","role",2),token,201);
        long id=created.get("id").asLong();assertFalse(created.has("password"));
        call("GET","/api/accounts/"+id,null,token,200);
        call("PUT","/api/accounts/"+id,Map.of("name","Edited Staff","email",email,"password","","role",2),token,200);
        call("POST","/api/accounts",Map.of("name","Duplicate","email",email,"password","Test@123","role",2),token,409);
        call("DELETE","/api/accounts/"+id,null,token,204);call("GET","/api/accounts/"+id,null,token,404);
    }
    @Test void relatedAccountsAndCategoriesCannotBeDeleted() throws Exception {
        call("DELETE","/api/accounts/2",null,admin(),409);
        call("DELETE","/api/categories/1",null,staff(),409);
    }
    @Test void categoriesSupportCrudSearchAndParentValidation() throws Exception {
        String token=staff(),name=unique();
        long id=call("POST","/api/categories",category(name),token,201).get("id").asLong();
        assertEquals(1,call("GET","/api/categories?q="+name,null,token,200).size());
        call("PUT","/api/categories/"+id,Map.of("name",name,"description","Changed","status",0),token,200);
        call("PUT","/api/categories/"+id,Map.of("name",name,"parentId",id,"status",1),token,400);
        call("DELETE","/api/categories/"+id,null,token,204);
    }
    @Test void articlesSupportTagsUpdateHistoryAndDeletion() throws Exception {
        String token=staff(),title=unique();
        JsonNode created=call("POST","/api/news",news(title,1),token,201);
        long id=created.get("id").asLong();assertEquals("test",created.get("tags").get(0).get("name").asText());
        assertEquals(1,call("GET","/api/news/history?q="+title,null,token,200).size());
        call("PUT","/api/news/"+id,news(title+" edited",0),token,200);
        call("GET","/api/public/news/"+id,null,null,404);
        call("DELETE","/api/news/"+id,null,token,204);call("GET","/api/news/"+id,null,token,404);
    }
    @Test void staffProfileCannotChangeItsRole() throws Exception {
        String token=staff();
        JsonNode profile=call("PUT","/api/profile",Map.of("name","Edited profile","email","staff@funews.local","password",""),token,200);
        assertEquals(2,profile.get("role").asInt());
        call("PUT","/api/profile",Map.of("name","Spoof","email","staff@funews.local","role",1),token,400);
    }
    @Test void invalidInputAndBadCredentialsHaveDefinedErrors() throws Exception {
        call("POST","/api/auth/login",Map.of("email","staff@funews.local","password","wrong"),null,401);
        call("POST","/api/auth/login",Map.of("email","invalid","password","x"),null,400);
        call("POST","/api/categories",category("   "),staff(),400);
        call("POST","/api/accounts",Map.of("name","Test","email",unique()+"@example.com","password","Test@123","role",3),admin(),400);
    }

}
