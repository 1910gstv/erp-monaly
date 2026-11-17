package com.monaly.erpmonaly.application.DTOs.products;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateProductRequestValidationTest {
    private static Validator validator;
    private static AutoCloseable validatorFactory;

    @BeforeAll
    static void setUpValidator() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validatorFactory = factory::close;
    }

    @AfterAll
    static void closeValidator() throws Exception {
        validatorFactory.close();
    }

    @Test
    void acceptsValidRequestWithOptionalSkuOmitted() {
        assertTrue(validator.validate(request("Product", new BigDecimal("0.00"), null)).isEmpty());
    }

    @Test
    void acceptsSkuWithinMaximumLength() {
        assertTrue(validator.validate(request("Product", new BigDecimal("12.34"), "S".repeat(100))).isEmpty());
    }

    @Test
    void rejectsBlankName() {
        assertHasViolation(request("  ", new BigDecimal("1.00"), null), "name");
    }

    @Test
    void rejectsMissingName() {
        assertHasViolation(request(null, new BigDecimal("1.00"), null), "name");
    }

    @Test
    void rejectsNameLongerThan150Characters() {
        assertHasViolation(request("N".repeat(151), new BigDecimal("1.00"), null), "name");
    }

    @Test
    void rejectsMissingPrice() {
        assertHasViolation(request("Product", null, null), "price");
    }

    @Test
    void rejectsNegativePrice() {
        assertHasViolation(request("Product", new BigDecimal("-0.01"), null), "price");
    }

    @Test
    void rejectsPriceWithMoreThanTwoFractionDigits() {
        assertHasViolation(request("Product", new BigDecimal("1.001"), null), "price");
    }

    @Test
    void rejectsPriceExceedingDatabasePrecision() {
        assertHasViolation(request("Product", new BigDecimal("100000000.00"), null), "price");
    }

    @Test
    void rejectsSkuLongerThan100Characters() {
        assertHasViolation(request("Product", new BigDecimal("1.00"), "S".repeat(101)), "sku");
    }

    @Test
    void rejectsSkuContainingOnlyWhitespace() {
        assertHasViolation(request("Product", new BigDecimal("1.00"), "   "), "sku");
    }

    @Test
    void rejectsBlankDescription() {
        assertHasViolation(new CreateProductRequest("Product", BigDecimal.ONE, null, " ", "ACTIVE"), "description");
    }

    @Test
    void rejectsDescriptionLongerThan400Characters() {
        assertHasViolation(
                new CreateProductRequest("Product", BigDecimal.ONE, null, "D".repeat(401), "ACTIVE"),
                "description"
        );
    }

    @Test
    void rejectsBlankState() {
        assertHasViolation(new CreateProductRequest("Product", BigDecimal.ONE, null, "Description", " "), "state");
    }

    private static CreateProductRequest request(String name, BigDecimal price, String sku) {
        return new CreateProductRequest(name, price, sku, "Description", "ACTIVE");
    }

    private static void assertHasViolation(CreateProductRequest request, String field) {
        assertFalse(validator.validate(request).stream().noneMatch(violation ->
                violation.getPropertyPath().toString().equals(field)));
    }
}
