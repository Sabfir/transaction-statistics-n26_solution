package com.n26.controller;

import com.n26.helper.TestHelper;
import com.n26.service.TransactionService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import static com.n26.helper.TestHelper.generateRandomTimestampWithingLastMinute;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


public class TransactionControllerTest {
    private TransactionController transactionController;
    @Mock
    private TransactionService transactionService;
    MockMvc mockMvc;

    @Before
    public void setUp() {
        initMocks(this);
        transactionController = new TransactionController(transactionService);

        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                // mock validator prevents MockMvc from performing any validation
                // we need it to prevent custom validation because of the problem of their initialization and mocking
                .setValidator(mock(Validator.class))
                .build();
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    public void getTransactionStatistic() throws Exception {
        when().
                get("/statistics").
        then().
                statusCode(SC_OK);

        verify(transactionService).getStatistic();
    }

    @Test
    public void createTransaction() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", "100");
        jsonObject.put("timestamp", generateRandomTimestampWithingLastMinute());

        String expectedJson = jsonObject.toString();

        given().
                contentType(APPLICATION_JSON_VALUE).
                body(expectedJson).
        when().
                post("/transactions").
        then().
                statusCode(SC_CREATED);

        verify(transactionService).create(any());
    }

    @Test
    public void removeAllTransactions() throws Exception {
        when().
                delete("/transactions").
        then().
                statusCode(SC_NO_CONTENT);

        verify(transactionService).removeAll();
    }

}