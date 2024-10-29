package org.example.archunit

import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.conditions.ArchConditions.*
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import jakarta.persistence.Entity
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service

/**
 * Check rules at § 2.1 here : [JPA 3.0 specs](https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.pdf)
 */
@DisplayNameGeneration(ReplaceUnderscores::class)
internal class LayeredArchitectureTest {

    @Test
    fun services_should_not_depend_on_controllers() {
        val rule: ArchRule = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")

        rule.check(importedClasses)
    }

    @Test
    fun services_should_not_be_final() {
        val rule: ArchRule = noClasses()
            .that().resideInAPackage("..service..")
            .and().areAnnotatedWith(Service::class.java)
            .should(beFinal)

        rule.check(importedClasses)
    }

    @Test
    fun jpa_entities_should_be_public() {
        val rule: ArchRule = classes()
            .that().areAnnotatedWith(Entity::class.java)
            .should().bePublic()

        rule.check(importedClasses)
    }

    @Test
    fun jpa_entities_should_be_top_level_classes() {
        val rule: ArchRule = classes()
            .that().areAnnotatedWith(Entity::class.java)
            .should().beTopLevelClasses()

        rule.check(importedClasses)
    }

    @Test
    fun jpa_entities_should_not_be_final() {
        val rule: ArchRule = classes()
            .that().areAnnotatedWith(Entity::class.java)
            .should(notBeFinal)

        // Could also use this :
        // .should().notHaveModifier(JavaModifier.FINAL);
        rule.check(importedClasses)
    }

    /**
     * To avoid this error :
     * <pre>
     * WARN [main] org.hibernate.metamodel.internal.EntityRepresentationStrategyPojoStandard.createProxyFactory HHH000305: Could not create proxy factory for:org.example.model.Country
     * org.hibernate.HibernateException: Getter methods of lazy classes cannot be final: org.example.model.Country#getCode2
     * </pre>
     */
    @Test
    fun jpa_entities_getters_should_not_be_final() {
        val rule: ArchRule = ArchRuleDefinition.methods()
            .that().areDeclaredInClassesThat().areAnnotatedWith(Entity::class.java)
            .should(not(beFinal()))

        rule.check(importedClasses)
    }

    @Test
    fun jpa_entities_should_have_no_arg_constructor() {
        val rule: ArchRule = classes()
            .that().areAnnotatedWith(Entity::class.java)
            .should(haveNoArgConstructor)

        rule.check(importedClasses)
    }

    companion object {
        private var importedClasses: JavaClasses? = null

        @JvmStatic
        @BeforeAll
        fun setUpGlobal() {
            importedClasses =
                ClassFileImporter().importPackages("org.example")
        }

        var beFinal: ArchCondition<JavaClass?>? = be(
            describe("final") { javaClass -> javaClass?.modifiers?.contains(JavaModifier.FINAL) == true }
        )

        var notBeFinal: ArchCondition<JavaClass?>? = not(beFinal)

        val haveNoArgConstructor: ArchCondition<JavaClass> = have(
            describe("no-arg constructor public or protected") { javaClass ->
                val noArgConstructor = try {
                    javaClass.getConstructor()
                } catch (_: IllegalArgumentException) {
                    return@describe false
                }
                return@describe noArgConstructor.modifiers.contains(JavaModifier.PUBLIC) ||
                        noArgConstructor.modifiers.contains(JavaModifier.PROTECTED)
            }
        )
    }
}
