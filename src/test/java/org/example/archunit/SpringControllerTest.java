package org.example.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.conditions.ArchConditions;
import de.rweisleder.archunit.spring.framework.SpringControllerRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.lang.conditions.ArchConditions.be;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static de.rweisleder.archunit.spring.SpringAnnotationPredicates.springAnnotatedWith;

class SpringControllerTest {

    private static JavaClasses importedClasses;
    private static JavaClasses importedClassesController;

    @BeforeAll
    static void setUpGlobal() {
        importedClasses = new ClassFileImporter()
                .withImportOption(DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example");

        importedClassesController = new ClassFileImporter()
                .withImportOption(DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example.controller");
    }

    @Test
    void controller_name_without_request_mapping() {
        SpringControllerRules.ControllerNameWithoutRequestMapping.check(importedClassesController);
    }

    @Test
    void test_request_mapping_in_controller_classes() {
        request_mapping_in_controller_classes.check(importedClassesController);
    }

    static final ArchRule request_mapping_in_controller_classes =
            methods()
                    .that(are(springAnnotatedWith(RequestMapping.class)))
                    .should().beDeclaredInClassesThat(are(springAnnotatedWith(Controller.class)));

    @Test
    void controller_all_paths_lowercase() {
        all_paths_lowercase.check(importedClassesController);
    }

    static final ArchRule all_paths_lowercase =
            methods()
                    .that(
                            are(springAnnotatedWith(RequestMapping.class)
                                    .as("annotated with @RequestMapping (or @GetMapping, @PostMapping etc.)"))
                    )
                    .should(
                            be(springAnnotatedWith(RequestMapping.class,
                                    describe("@RequestMapping(path=<lower-case>)",
                                            requestMapping -> Arrays.stream(requestMapping.path())
                                                    .allMatch(path -> path.toLowerCase().equals(path))))
                            ).as("have path containing only lowercase characters")
                    );

    static final ArchRule no_trailing_slash_in_request_mappings =
            methods()
                    .that().areAnnotatedWith(GetMapping.class)
                    .or().areAnnotatedWith(PostMapping.class)
                    .or().areAnnotatedWith(PutMapping.class)
                    .or().areAnnotatedWith(DeleteMapping.class)
                    .should(ArchConditions.notBeStatic());

}
