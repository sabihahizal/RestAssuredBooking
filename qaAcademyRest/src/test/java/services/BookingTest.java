package services;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BookingTest {

    String token;
    String bookingId;

    @BeforeClass
    public void CreateToken(){
        String tokenData="{\n" +
                "    \"username\" : \"admin\",\n" +
                "    \"password\" : \"password123\"\n" +
                "}";
        Response res= given()
                .body(tokenData)
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .post("https://restful-booker.herokuapp.com/auth").
                then().statusCode(200)
                .log().all().extract().response();

        token=res.jsonPath().getString("token");
    }


    @Test (priority = 1)
    public void postCreateBooking(){
        String postData= "{\n" +
                "    \"firstname\" : \"Jim\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        Response resId=  given()
                .contentType(ContentType.JSON)
                .body(postData)
                .log().all().
                when()
                .post("https://restful-booker.herokuapp.com/booking").
                then().statusCode(200)
                .log().all().extract().response();

        bookingId=resId.jsonPath().getString("bookingid");

    }

    @Test
    public void getBoking (){
        //GETBOOKING
        Response response=  RestAssured.get("https://restful-booker.herokuapp.com/booking");
        System.out.println("getBody:" +response.asString());
        System.out.println("getStatusCode:" +response.getStatusCode());
        System.out.println("getContentType:" +response.getContentType());
        System.out.println("getHeaders:" +response.getHeaders());
        System.out.println("getTime:" +response.getTime());
        Assert.assertEquals(response.getStatusCode(),200);
    }

/*
   @Test
    public void getBookingDetails(){
        given()
                .log().all().
                when()
                .get("https://restful-booker.herokuapp.com/booking/45").
                then().
                statusCode(200)
                //.body("firstname", startsWith("Jim"))
                //.body("totalprice", equalTo(111))
                .log().all();
    }

*/

    @DataProvider(name = "dataProvider")
    public Object[][] dataProvider(){
        return new Object[][]{
                {"3"},
                {"11"},
                {bookingId}
        };
    }
    @Test(dataProvider = "dataProvider",priority = 2)
    public void getBookingDetails(String id){
        given()
                .log().all().
                when()
                .get("https://restful-booker.herokuapp.com/booking/"+id).
                then().
                statusCode(200)
                .log().all();
    }


    @Test(priority = 3)
    public void postupdate (){
        String updateData= "{\n" +
                "    \"firstname\" : \"Jimss\",\n" +
                "    \"lastname\" : \"Browni\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        given()
                .contentType(ContentType.JSON)
                .header("Cookie","token="+token)
                .log().all()
                .body(updateData).
                when()
                .put("https://restful-booker.herokuapp.com/booking/"+bookingId).
                then().statusCode(200)
                .log().all();
    }


    @Test(priority = 4)
    public void partialUpdate(){
        Map<String,Object> queryParams= new HashMap<>();
        queryParams.put("firstname","Jamess");
        queryParams.put("lastname","Brownii");

        given()
                .contentType(ContentType.JSON)
                .header("Cookie","token="+token)
                .log().all()
                .body(queryParams).
                when()
                .patch("https://restful-booker.herokuapp.com/booking/"+bookingId).
                then().statusCode(200)
                .log().all();
    }


    @Test(priority = 5)
    public void delete(){

        given()
                .contentType(ContentType.JSON)
                .header("Cookie","token="+token)
                .log().all().
                when()
                .delete("https://restful-booker.herokuapp.com/booking/"+bookingId).
                then().statusCode(201)
                .log().all();

    }

    /*
    public String attachment(String baseUrl){

        String baseUrl="https://restful-booker.herokuapp.com/apidoc/index.html#api-Booking";
        String html="Url="+baseUrl+ "\n\n"+
                "request header" + ;
        Allure.addAttachment("requestdetail",html);

    }

*/




}