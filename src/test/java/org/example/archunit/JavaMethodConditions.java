package org.example.archunit;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.conditions.ArchConditions;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.conditions.ArchConditions.be;
import static com.tngtech.archunit.lang.conditions.ArchConditions.not;

public class JavaMethodConditions {

    /**
     * @deprecated Use {@link ArchConditions#beFinal()}
     */
    @Deprecated
    static ArchCondition<JavaMethod> beFinal = be(describe("final", method ->
            method.getModifiers().contains(JavaModifier.FINAL))
    );

    @Deprecated
    static ArchCondition<JavaMethod> notBeFinal = not(beFinal);

}
