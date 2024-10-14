package org.example.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.conditions.ArchConditions.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Check rules at § 2.1 here : <a href="https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.pdf">JPA 3.0 specs</a>
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LayeredArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setUpGlobal() {
        importedClasses = new ClassFileImporter().importPackages("org.example");
    }

    @Test
    void services_should_not_depend_on_controllers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..");

        rule.check(importedClasses);
    }

    @Test
    void jpa_entities_should_be_public() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should().bePublic();

        rule.check(importedClasses);
    }

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

    @Test
    void jpa_entities_should_be_top_level_classes() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should().beTopLevelClasses();

        rule.check(importedClasses);
    }

    @Test
    void jpa_entities_should_not_be_final() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should(notBeFinal);
        // Could also use this :
        // .should().notHaveModifier(JavaModifier.FINAL);

        rule.check(importedClasses);
    }

    @Test
    void jpa_entities_should_have_no_arg_constructor() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should(haveNoArgConstructor);

        rule.check(importedClasses);
    }

}
