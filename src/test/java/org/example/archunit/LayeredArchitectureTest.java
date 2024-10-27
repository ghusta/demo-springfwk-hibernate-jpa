package org.example.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.conditions.ArchConditions;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.conditions.ArchConditions.not;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.example.archunit.JavaClassConditions.beFinal;
import static org.example.archunit.JavaClassConditions.haveNoArgConstructor;
import static org.example.archunit.JavaClassConditions.notBeFinal;

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

    /**
     * Spring Services often need to be proxied, for transaction management for example.
     * As a consequence, they should not be declared final.
     * <p>
     * See :
     *     <ul>
     *         <li><a href="https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html">Using @Transactional</a></li>
     *     </ul>
     * </p>
     */
    @Test
    void services_should_not_be_final() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service..")
                .and().areAnnotatedWith(Service.class)
                .should(beFinal);

        rule.check(importedClasses);
    }

    @Test
    void jpa_entities_should_be_public() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should().bePublic();

        rule.check(importedClasses);
    }

    /**
     * To avoid this error :
     * <pre>
     *     WARN [main] org.hibernate.metamodel.internal.EntityRepresentationStrategyPojoStandard.createProxyFactory HHH000305: Could not create proxy factory for:org.example.model.Country
     *         org.hibernate.HibernateException: Getter methods of lazy classes cannot be final: org.example.model.Country#getCode2
     * </pre>
     */
    @Test
    void jpa_entities_getters_should_not_be_final() {
        ArchRule rule = methods()
                .that().areDeclaredInClassesThat().areAnnotatedWith(Entity.class)
                .and().haveNameMatching("get[A-Z].*")  // Matches methods starting with "get"
                .or().haveNameMatching("is[A-Z].*")    // Matches methods starting with "is" (for booleans)
                .should(not(ArchConditions.beFinal()));

        rule.check(importedClasses);
    }

    @Test
    void jpa_entities_should_be_top_level_classes() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should().beTopLevelClasses();

        rule.check(importedClasses);
    }

    /**
     * JPA Entities often need to be proxied, for lazy loading for example.
     * As a consequence, they should not be declared final.
     * <p>
     * See :
     *     <ul>
     *         <li><a href="https://thorben-janssen.com/hibernate-proxies/">Hibernate Proxies</a></li>
     *     </ul>
     * </p>
     */
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
