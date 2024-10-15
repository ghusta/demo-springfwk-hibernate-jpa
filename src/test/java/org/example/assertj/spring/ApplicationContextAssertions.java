package org.example.assertj.spring;

import org.assertj.core.api.AbstractAssert;
import org.springframework.context.ApplicationContext;

/**
 * AssertJ extension for Spring FWK, to make assertions on {@link ApplicationContext}.
 */
public class ApplicationContextAssertions extends AbstractAssert<ApplicationContextAssertions, ApplicationContext> {

    public ApplicationContextAssertions(ApplicationContext actual) {
        super(actual, ApplicationContextAssertions.class);
    }

    public static ApplicationContextAssertions assertThat(ApplicationContext actual) {
        return new ApplicationContextAssertions(actual);
    }

    public ApplicationContextAssertions hasBean(String beanName) {
        return containsBean(beanName);
    }

    // Custom method to assert the existence of a bean
    public ApplicationContextAssertions containsBean(String beanName) {
        isNotNull();

        if (!actual.containsBean(beanName)) {
            failWithMessage("Expected ApplicationContext to contain bean <%s> but it does not.", beanName);
        }

        return this;
    }

    public ApplicationContextAssertions containsBeans(String... beanNames) {
        isNotNull(); // Ensures that the ApplicationContext is not null

        for (String beanName : beanNames) {
            if (!actual.containsBean(beanName)) {
                failWithMessage("Expected ApplicationContext to contain bean <%s> but it does not.", beanName);
            }
        }
        return this;
    }

    /**
     * Expects at least one bean of this type.
     *
     * @param type Expected type
     */
    public ApplicationContextAssertions hasBeansOfType(Class<?> type) {
        isNotNull();

        if (actual.getBeansOfType(type).isEmpty()) {
            failWithMessage("Expected ApplicationContext to contain bean of type <%s> but it does not.", type.getName());
        }

        return this;
    }

    /**
     * Expects exactly one bean of this type.
     *
     * @param type Expected type
     */
    public ApplicationContextAssertions hasExactlyOneBeanOfType(Class<?> type) {
        isNotNull();

        if (actual.getBeansOfType(type).size() != 1) {
            failWithMessage("Expected ApplicationContext to contain exactly one bean of type <%s> but it does not.", type.getName());
        }

        return this;
    }
}
