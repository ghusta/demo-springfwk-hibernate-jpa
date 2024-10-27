package org.example.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.conditions.ArchConditions.be;
import static com.tngtech.archunit.lang.conditions.ArchConditions.have;
import static com.tngtech.archunit.lang.conditions.ArchConditions.not;

public class JavaClassConditions {

    static ArchCondition<JavaClass> beFinal = be(describe("final", javaClass ->
            javaClass.getModifiers().contains(JavaModifier.FINAL))
    );

    static ArchCondition<JavaClass> notBeFinal = not(beFinal);

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
