import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class FirstTest {

    @Test
    public void TestGetBookByPauloCoelho(){
        Response response = given().param("q","paulo+coelho").
                when().get("https://www.googleapis.com/books/v1/volumes").
                then().statusCode(200).extract().response();

        int totalItems = JsonPath.read(response.asString(),"$.totalItems");
        List<Map<String,Object>> allBooks = JsonPath.read(response.asString(),"$.items[*].volumeInfo");

        Assert.assertTrue(totalItems>0,"Total Items should be greater than 0");
        for(Map<String,Object> book : allBooks ){
            List<String> authors = (List<String>)book.get("authors");
            String author = authors.get(0).toLowerCase();
            Assert.assertTrue(author.contains("coelho") ,   "should contain Paulo Coelho but found " + Arrays.toString(authors.toArray()));
        }
    }

    @Test
    public void TestGetHarryPotterBooks(){
        Response response = given().param("q","potter").
                when().get("https://www.googleapis.com/books/v1/volumes").
                then().statusCode(200).extract().response();

        int totalItems = JsonPath.read(response.asString(),"$.totalItems");
        List<Object> allBooks = JsonPath.read(response.asString(),"$.items");

        Assert.assertTrue(totalItems>0,"Total Items should be greater than 0");
        Assert.assertEquals(allBooks.size(),10, "10 Books should be returned by default");
    }

    @Test
    public void TestGetDanBrownBooks(){
        Response response = given().param("q","dan+brown").param("maxResults","25").
                when().get("https://www.googleapis.com/books/v1/volumes").
                then().statusCode(200).extract().response();

        int totalItems = JsonPath.read(response.asString(),"$.totalItems");
        List<Object> allBooks = JsonPath.read(response.asString(),"$.items");


        Assert.assertTrue(totalItems>0,"Total Items should be greater than 0");
        Assert.assertEquals(allBooks.size(),25, "25 Books should be returned");
    }

    @Test
    public void TestGetWithEmptyQueryParams(){
        Response response = given().param("q","").
                when().get("https://www.googleapis.com/books/v1/volumes").
                then().statusCode(400).extract().response();

        String errorMessage = JsonPath.read(response.asString(),"$.error.message");
        String paramName = JsonPath.read(response.asString(),"$.error.errors[0].location");

        Assert.assertTrue(errorMessage.equals("Missing query.") && paramName.equals("q"),"Search Query Parameter Missing Error should be displayed");
    }

    @Test
    public void TestGetWithInvalidQueryParams(){
        Response response = given().param("q","potter").param("maxResults","50").
                when().get("https://www.googleapis.com/books/v1/volumes").
                then().statusCode(400).extract().response();

        String errorMessage = JsonPath.read(response.asString(),"$.error.message");
        String paramName = JsonPath.read(response.asString(),"$.error.errors[0].location");

        Assert.assertTrue(errorMessage.contains("Values must be within the range: [0, 40]") && paramName.equals("maxResults"),"MaxResults invalid range Error should be displayed");
    }
}
