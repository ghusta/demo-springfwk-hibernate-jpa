package org.example.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.conditions.ArchConditions;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.conditions.ArchConditions.have;

public class JavaClassConditions {

    /**
     * @deprecated Use {@link ArchConditions#beFinal()}
     */
    @Deprecated
    static ArchCondition<JavaClass> beFinal = ArchConditions.beFinal();

    /**
     * @deprecated Use {@link ArchConditions#notBeFinal()}
     */
    @Deprecated
    static ArchCondition<JavaClass> notBeFinal = ArchConditions.notBeFinal();

    static ArchCondition<JavaClass> haveNoArgConstructor = have(describe("no arg constructor public or protected", javaClass -> {
        JavaConstructor noArgConstructor;
        try {
            noArgConstructor = javaClass.getConstructor();
        } catch (IllegalArgumentException e) {
            return false;
        }
        return noArgConstructor.getModifiers().contains(JavaModifier.PUBLIC)
                || noArgConstructor.getModifiers().contains(JavaModifier.PROTECTED);
    }));

}
