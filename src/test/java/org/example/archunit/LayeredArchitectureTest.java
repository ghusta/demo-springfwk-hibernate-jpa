package org.example.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

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

    static ArchCondition<JavaClass> beFinal = new ArchCondition<>("be final") {
        @Override
        public void check(JavaClass javaClass, ConditionEvents events) {
            boolean isFinal = javaClass.getModifiers().contains(JavaModifier.FINAL);
            if (!isFinal) {
                String message = String.format("%s is not final", javaClass.getName());
                events.add(SimpleConditionEvent.violated(javaClass, message));
            }
        }
    };

    static ArchCondition<JavaClass> notBeFinal = new ArchCondition<>("not be final") {
        @Override
        public void check(JavaClass javaClass, ConditionEvents events) {
            boolean isFinal = javaClass.getModifiers().contains(JavaModifier.FINAL);
            if (isFinal) {
                String message = String.format("%s is final", javaClass.getName());
                events.add(SimpleConditionEvent.violated(javaClass, message));
            }
        }
    };

    @Test
    void jpa_entities_should_not_be_final() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should(notBeFinal);

        rule.check(importedClasses);
    }

}
