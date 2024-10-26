package org.example.assertj.spring

import org.assertj.core.api.AbstractAssert
import org.springframework.context.ApplicationContext

/**
 * AssertJ extension for Spring FWK, to make assertions on [ApplicationContext].
 */
class ApplicationContextAssertions(actual: ApplicationContext?) :
    AbstractAssert<ApplicationContextAssertions?, ApplicationContext?>(
        actual,
        ApplicationContextAssertions::class.java
    ) {

    fun hasBean(beanName: String): ApplicationContextAssertions {
        return containsBean(beanName)
    }

    // Custom method to assert the existence of a bean
    fun containsBean(beanName: String): ApplicationContextAssertions {
        isNotNull()

        if (actual!!.containsBean(beanName) == false) {
            failWithMessage("Expected ApplicationContext to contain bean <%s> but it does not.", beanName)
        }

        return this
    }

    fun hasBeans(vararg beanNames: String): ApplicationContextAssertions {
        return containsBeans(*beanNames)
    }

    fun containsBeans(vararg beanNames: String): ApplicationContextAssertions {
        isNotNull() // Ensures that the ApplicationContext is not null

        for (beanName in beanNames) {
            if (actual!!.containsBean(beanName) == false) {
                failWithMessage("Expected ApplicationContext to contain bean <%s> but it does not.", beanName)
            }
        }
        return this
    }

    /**
     * Expects that bean is of given type or assignable to it.
     *
     * @param beanName Bean name
     * @param type     Expected type
     */
    fun isBeanOfType(beanName: String, type: Class<*>?): ApplicationContextAssertions {
        isNotNull()
        if (type == null) {
            failWithMessage("Parameter type was null")
            return this
        }

        // first check the bean exists
        containsBean(beanName)

        val actualType: Class<*>? = actual!!.getType(beanName)
        if (actualType == null || !type.isAssignableFrom(actualType)) {
            failWithMessage(
                "Expected ApplicationContext to contain bean <%s> of type <%s> " +
                        "but the actual type was <%s>.",
                beanName,
                type.getName(),
                (if (actualType != null) actualType.getName() else "null")
            )
        }
        return this
    }

    /**
     * Expects at least one bean of this type.
     *
     * @param type Expected type
     */
    fun hasBeansOfType(type: Class<*>): ApplicationContextAssertions {
        isNotNull()

        if (actual!!.getBeansOfType(type).isEmpty()) {
            failWithMessage("Expected ApplicationContext to contain bean of type <%s> but it does not.", type.getName())
        }

        return this
    }

    /**
     * Expects exactly one bean of this type.
     *
     * @param type Expected type
     */
    fun hasExactlyOneBeanOfType(type: Class<*>): ApplicationContextAssertions {
        isNotNull()

        val beansCount: Int = actual!!.getBeansOfType(type).size
        if (beansCount != 1) {
            failWithMessage(
                "Expected ApplicationContext to contain exactly one bean of type <%s> but it does not (found %s).",
                type.getName(), beansCount
            )
        }

        return this
    }

    companion object {
        fun assertThat(actual: ApplicationContext): ApplicationContextAssertions {
            return ApplicationContextAssertions(actual)
        }
    }
}
