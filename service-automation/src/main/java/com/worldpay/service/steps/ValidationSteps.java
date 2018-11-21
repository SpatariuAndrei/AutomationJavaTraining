package com.worldpay.service.steps;

import static com.worldpay.service.constants.Constants.CONTENT_TYPE;
import static com.worldpay.service.constants.Constants.RESPONSE_CODE;
import static com.worldpay.service.constants.Constants.RESPONSE_CODE_BODY;
import static com.worldpay.service.constants.Constants.RESPONSE_MESSAGE_BODY;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jbehave.core.annotations.Then;

import com.worldpay.service.constants.Constants;
import com.worldpay.service.constants.JsonResponseConstants.Response;
import com.worldpay.service.entities.SharedData;
import com.worldpay.service.util.CustomAssert;

import io.restassured.http.Header;

public class ValidationSteps {

    private SharedData share;

    public ValidationSteps(SharedData share) {
        this.share = share;
    }

    @Then("I check the response code")
    public void thenCheckResponse() {
        int actualResponseCode = share.getResponse().getStatusCode();
        int expectedResponseCode = share.getTestData().getInt(RESPONSE_CODE);
        assertThat(CustomAssert.buildFailureReason(share), actualResponseCode, equalTo(expectedResponseCode));
    }

    @Then("I check the content type")
    public void thenCheckContentType() {
        share.getResponse().then().assertThat().contentType(share.getTestData().getString(CONTENT_TYPE));
    }

    @Then("I check that the $headerName header is not present in the response")
    public void thenCheckNoSpecificHeaderInResponse(String headerName) {
        List<Header> headers = share.getResponse().getHeaders().asList();
        for (Header headerNameFromResponse : headers) {
            assertThat(String.format("Header %s should not be present in the response, it expose sensitive data", headerNameFromResponse), headerNameFromResponse.getName(),
                    not(headerName));
        }
    }

    @Then("I check the $field field in the response body")
    public void thenCheckFieldExistence(String field) {
        share.getResponse().then().assertThat().body("$", hasKey(field));
        share.getResponse().then().assertThat().body(field, equalTo(share.getTestData().getString(field)));
    }

    @Then("I check that the $field field is not present in the response body")
    public void thenCheckFieldNonExistence(String field) {
        share.getResponse().then().assertThat().body(Response.BODY, not(hasKey(field)));
    }

    @Then("I check the response body for fail response")
    public void thenCheckFailResponseBody() {
        share.getResponse().then().assertThat().body(Response.RESPONSE_CODE, equalTo(share.getTestData().getString(RESPONSE_CODE_BODY))).and()
                .body(Response.RESPONSE_MESSAGE, equalTo(share.getTestData().getString(RESPONSE_MESSAGE_BODY)));
    }

    @Then("I check the body for failed RVL validation")
    public void thenCheckFailedRvlValidation() {
        String expectedErrorMessage = share.getTestData().getString(Constants.ERROR).replace("pipe", "|");
        share.getResponse().then().assertThat().body(Response.RESPONSE_CODE, equalTo(share.getTestData().getString(Constants.ERROR_CODE))).and()
                .body(Response.RESPONSE_MESSAGE, equalTo(expectedErrorMessage));
    }

    @Then("I check the response body for missing fields")
    public void thenCheckMissingFields() {
        share.getResponse().then().assertThat().body(share.getTestData().getString(Constants.TAG),
                not(hasKey(share.getTestData().getString(Constants.SUB_TAG))));
    }

    @Then("I check that the number of parameters from response is $number")
    public void thenCheckParametersNumber(Integer number) {
        share.getResponse().then().assertThat().body("size()", is(number));
    }

}
