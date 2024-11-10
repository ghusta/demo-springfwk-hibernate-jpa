package org.example.archunit;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.conditions.ArchConditions;

public class JavaMethodConditions {

    /**
     * @deprecated Use {@link ArchConditions#beFinal()}
     */
    @Deprecated
    static ArchCondition<JavaMethod> beFinal = ArchConditions.beFinal();

    /**
     * @deprecated Use {@link ArchConditions#notBeFinal()}
     */
    @Deprecated
    static ArchCondition<JavaMethod> notBeFinal = ArchConditions.notBeFinal();

}
