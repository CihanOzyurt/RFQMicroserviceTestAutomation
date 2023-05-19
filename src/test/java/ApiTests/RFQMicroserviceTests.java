package ApiTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import model.Description;
import model.Materials;
import model.RFQ;
import model.Supplier;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.given;

public class RFQMicroserviceTests {

    @BeforeClass
    public void setupURI(){
        baseURI = "https://o5jql.wiremockapi.cloud/api";
        RestAssured.defaultParser = Parser.JSON;
        Reporter.log("baseURI is configured in beforeClass method");
    }

    @Test(priority = 0)
    public void verifyRFQList() throws JsonProcessingException {
        RequestSpecification httpRequest = given();
        Response response = httpRequest.get("/rfqs");
        ResponseBody body = response.getBody();
        ObjectMapper om = new ObjectMapper();
        RFQ[] results = om.readValue(body.asString(), RFQ[].class);
        Assert.assertEquals(response.getStatusCode(),400);
        Reporter.log("RFQList get request status code is 200");

        Assert.assertEquals(results.length,3);
    }
    @Test(priority = 1)
    public  void verifySpecificRFQByUUID() throws JsonProcessingException {

        String uuid = String.valueOf(UUID.randomUUID());
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rfqs?id="+uuid)
                .then()
                .extract().response();
        ResponseBody body = response.getBody();
        Assert.assertEquals(response.getStatusCode(),200);
        ObjectMapper om = new ObjectMapper();
        Description message = om.readValue(body.asString(), Description.class);
        Assert.assertEquals(message.getDescription(),"A specific RFQ");
    }

    @Test(priority = 2)
    public  void verifyMaterialsForSpecificRFQByUUID() throws JsonProcessingException {

        String specificRFQ = "60c57f9b-154a-4c1b-bc7a-f681016e5746";
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rfqs/"+specificRFQ+"/materials")
                .then()
                .extract().response();
        ResponseBody body = response.getBody();
        Assert.assertEquals(response.getStatusCode(),200);
        ObjectMapper om = new ObjectMapper();
        Materials[] results = om.readValue(body.asString(), Materials[].class);
        Assert.assertEquals(response.getStatusCode(),200);
        Assert.assertEquals(results.length,3);
    }
    @Test(priority = 3)
    public  void verifyMaterialsForUnAvaialableRFQByUUID() throws JsonProcessingException {

        String notAvailableRFQs = "2cc8d820-0d65-42fb-b24d-7911b436ab2b";
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rfqs/"+notAvailableRFQs+"/materials")
                .then()
                .extract().response();
        ResponseBody body = response.getBody();
        ObjectMapper om = new ObjectMapper();
        Assert.assertEquals(response.getStatusCode(),404);
        Description message = om.readValue(body.asString(), Description.class);
        Assert.assertEquals(message.getDescription(),"RFQ not found");
    }

    @Test(priority = 4)
    public  void verifySuppliersForSpecificRFQByUUID() throws JsonProcessingException {

        String specificRFQ = "123e4567-e89b-12d3-a456-4266";
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rfqs/"+specificRFQ+"/suppliers")
                .then()
                .extract().response();
        ResponseBody body = response.getBody();
        Assert.assertEquals(response.getStatusCode(),200);
        ObjectMapper om = new ObjectMapper();
        Supplier[] results = om.readValue(body.asString(), Supplier[].class);
        Assert.assertEquals(response.getStatusCode(),200);
        Assert.assertEquals(results.length,3);
    }
    @Test(priority = 5)
    public  void verifySuppliersForUnAvaialableRFQByUUID() throws JsonProcessingException {

        String notAvailableRFQs = "2cc8d820-0d65-42fb-b24d-7911b436ab2b";
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rfqs/"+notAvailableRFQs+"/suppliers")
                .then()
                .extract().response();
        ResponseBody body = response.getBody();
        ObjectMapper om = new ObjectMapper();
        Assert.assertEquals(response.getStatusCode(),404);
        Description message = om.readValue(body.asString(), Description.class);
        Assert.assertEquals(message.getDescription(),"RFQ not found");
    }

    @Test(priority = 6)
    public void verifyCreateMaterialForRFQ(){

        String specificRFQ = "85eb1344-33e0-41ab-8d07-7732f62f9009";

        Materials materials = new Materials();
        materials.setId("60c57f9b-154a-4c1b-bc7a-f681016e5746");
        materials.setName("suppliername2");
        materials.setVolume(1);
        materials.setUnit("unit");
        materials.setPart_number("partnumber");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(materials)
                .when()
                .post("/rfqs/"+specificRFQ+"/materials")
                .then()
                .statusCode(201)
                .extract().response();

        Assert.assertEquals(response.jsonPath().get("description"),"A newly added material for the RFQ");
    }

    @Test(priority = 7)
    public void verifyCreateMaterialWithInvalidInputForRFQ(){

        String specificRFQ = "85eb1344-33e0-41ab-8d07-7732f62f9009";

        String requestBody = "{\n" +
                "\t\"id\": \"60c57f9b-154a-4c1b-bc7a-f681016e5746\",\n" +
                "\t\"name\": \"suppliername2\"\n" +
                "}";
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/rfqs/"+specificRFQ+"/materials")
                .then()
                .statusCode(400)
                .extract().response();

        Assert.assertEquals(response.jsonPath().get("description"),"Invalid input");
    }

    @Test(priority = 8)
    public void verifyCreateMaterialForUnavailableRFQ(){

        String unavailableRFQ = "85eb1344-33e0-41ab-8d07-7732f62f9019";

        Materials materials = new Materials();
        materials.setId("60c57f9b-154a-4c1b-bc7a-f681016e5746");
        materials.setName("suppliername2");
        materials.setVolume(1);
        materials.setUnit("unit");
        materials.setPart_number("partnumber");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(materials)
                .when()
                .post("/rfqs/"+unavailableRFQ+"/materials")
                .then()
                .statusCode(404)
                .extract().response();

        Assert.assertEquals(response.jsonPath().get("description"),"RFQ not found");
    }

    @Test(priority = 9)
    public void verifyCreateSupplierForRFQ(){

        String specificRFQ = "60c57f9b-154a-4c1b-bc7a-f681016e5746";

        Supplier supplier = new Supplier();
        supplier.setId("60c57f9b-154a-4c1b-bc7a-f681016e5746");
        supplier.setName("Supplier3");
        supplier.setEmail("Supplier desc3");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(supplier)
                .when()
                .post("/rfqs/"+specificRFQ+"/suppliers")
                .then()
                .statusCode(201)
                .extract().response();

        Assert.assertEquals(response.jsonPath().get("description"),"A newly added supplier for the RFQ");
    }

    @Test(priority = 10)
    public void verifyCreateSupplierWithInvalidInputForRFQ(){

        String specificRFQ = "85eb1344-33e0-41ab-8d07-7732f62f9009";

        String requestBody = "{\n" +
                "\t\"id\": \"60c57f9b-154a-4c1b-bc7a-f681016e5746\",\n" +
                "\t\"name\": \"Supplier3\"\n" +
                "}";
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/rfqs/"+specificRFQ+"/suppliers")
                .then()
                .statusCode(400)
                .extract().response();

        Assert.assertEquals(response.jsonPath().get("description"),"Invalid input");
    }

    @Test(priority = 11)
    public void verifyCreateSupplierForUnavailableRFQ(){

        String unavailableRFQ = "85eb1344-33e0-41ab-8d07-7732f62f9099";

        Supplier supplier = new Supplier();
        supplier.setId("60c57f9b-154a-4c1b-bc7a-f681016e5746");
        supplier.setName("Supplier3");
        supplier.setEmail("Supplier desc3");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(supplier)
                .when()
                .post("/rfqs/"+unavailableRFQ+"/suppliers")
                .then()
                .statusCode(404)
                .extract().response();

        Assert.assertEquals(response.jsonPath().get("description"),"RFQ not found");
    }

    @Test(priority = 12)
    public void verifyCreateRFQ(){

        RFQ rfq = new RFQ();
        rfq.setId("60c57f9b-154a-4c1b-bc7a-f681016e5746");
        rfq.setName("Supplier3");
        rfq.setDescription("Supplier desc");
        rfq.setCategory("");
        rfq.setStart_date("05-30-2023");
        rfq.setEnd_date("06-30-2023");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(rfq)
                .when()
                .post("/rfqs")
                .then()
                .statusCode(201)
                .extract().response();

        Assert.assertEquals(response.jsonPath().get("description"),"A newly created RFQ");
    }

    @Test(priority = 13)
    public void verifyCreateRFQWithInvalidInput(){

        String specificRFQ = "85eb1344-33e0-41ab-8d07-7732f62f9009";

        String requestBody = "{\n" +
                "    \"name\":\"\"\n" +
                "}";
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/rfqs")
                .then()
                .statusCode(400)
                .extract().response();

        Assert.assertEquals(response.jsonPath().get("description"),"Invalid input");
    }

    @Test(priority = 14)
    public void verifyUpdateRFQByUUID() throws JsonProcessingException {
        String requestBody ="{\n" +
                "\t\"name\": \"updated_supplier\"\n" +
                "}";
        RequestSpecification httpRequest = given();

        String specificRFQ = "2cc8d820-0d65-42fb-b24d-7911b436ab2b";
        Response response = httpRequest
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .put("/rfqs?uuid="+specificRFQ)
                .then()
                .statusCode(200)
                .extract().response();
        ObjectMapper om = new ObjectMapper();
        Description message = om.readValue(response.body().asString(), Description.class);
        Assert.assertEquals(message.getDescription(),"An updated RFQ");

    }

    @Test(priority = 15)
    public void verifyUpdateInvalidInputForRFQByUUID() throws JsonProcessingException {
        String requestBody ="{\n" +
                "    \"unknownfieldforupdate\":\"tryupdate\"\n" +
                "}";
        RequestSpecification httpRequest = given();

        String specificRFQ = "2cc8d820-0d65-42fb-b24d-7911b436ab2b";
        Response response = httpRequest
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .put("/rfqs?uuid="+specificRFQ)
                .then()
                .statusCode(400)
                .extract().response();
        ObjectMapper om = new ObjectMapper();
        Description message = om.readValue(response.body().asString(), Description.class);
        Assert.assertEquals(message.getDescription(),"Invalid input");

    }

    @Test(priority = 16)
    public void verifyUpdateUnavailableRFQByUUID() throws JsonProcessingException {
        String requestBody ="{\n" +
                "    \"name\":\"tryupdate\"\n" +
                "}";
        RequestSpecification httpRequest = given();

        String unavailableRFQ = "85eb1344-33e0-41ab-8d07-7732f62f9009";
        Response response = httpRequest
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .put("/rfqs?uuid="+unavailableRFQ)
                .then()
                .statusCode(404)
                .extract().response();
        ObjectMapper om = new ObjectMapper();
        Description message = om.readValue(response.body().asString(), Description.class);
        Assert.assertEquals(message.getDescription(),"RFQ not found");

    }

    @Test(priority = 17)
    public void verifyDeleteSpecifiqRFQByUUID() throws JsonProcessingException {

        RequestSpecification httpRequest = given();

        String specificRFQ = "85eb1344-33e0-41ab-8d07-7732f62f9009";
        Response response = httpRequest
                .header("Content-type", "application/json")
                .and()
                .when()
                .delete("/rfqs?uuid="+specificRFQ)
                .then()
                .statusCode(204)
                .extract().response();

    }

    @Test(priority = 18)
    public void verifyDeleteUnavailableRFQByUUID() throws JsonProcessingException {

        RequestSpecification httpRequest = given();

        String unavailableRFQ = "123e4567-e89b-12d3-a456-4266";
        Response response = httpRequest
                .header("Content-type", "application/json")
                .and()
                .when()
                .delete("/rfqs/"+unavailableRFQ)
                .then()
                .statusCode(404)
                .extract().response();
        ObjectMapper om = new ObjectMapper();
        Description message = om.readValue(response.body().asString(), Description.class);
        Assert.assertEquals(message.getDescription(),"RFQ not found");

    }

}
