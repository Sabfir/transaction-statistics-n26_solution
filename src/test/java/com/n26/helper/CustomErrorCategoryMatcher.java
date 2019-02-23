package com.n26.helper;

import com.n26.validator.exception.ErrorCategory;
import com.n26.validator.exception.ValidationException;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class CustomErrorCategoryMatcher extends TypeSafeMatcher<ValidationException> {

    public static CustomErrorCategoryMatcher hasCategory(ErrorCategory category) {
        return new CustomErrorCategoryMatcher(category);
    }

    private ErrorCategory foundErrorCategory;
    private final ErrorCategory expectedErrorCategory;

    private CustomErrorCategoryMatcher(ErrorCategory expectedErrorCategory) {
        this.expectedErrorCategory = expectedErrorCategory;
    }

    @Override
    protected boolean matchesSafely(final ValidationException exception) {
        foundErrorCategory = exception.getErrorMessage().getCategory();
        return foundErrorCategory == expectedErrorCategory;
    }

    @Override
    public void describeTo(Description description) {

        description.appendText("expected category is ")
                .appendValue(expectedErrorCategory.name())
                .appendText(" but was ")
                .appendValue(foundErrorCategory);
    }
}