package org.example.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
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
import java.util.stream.Stream;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.lang.conditions.ArchConditions.have;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static de.rweisleder.archunit.spring.SpringAnnotationPredicates.springAnnotatedWith;

class SpringControllerTest {

    private static JavaClasses importedClasses;
    private static JavaClasses importedClassesFromPackageController;

    @BeforeAll
    static void setUpGlobal() {
        importedClasses = new ClassFileImporter()
                .withImportOption(DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example");

        importedClassesFromPackageController = new ClassFileImporter()
                .withImportOption(DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example.controller");
    }

    @Test
    void controller_name_without_request_mapping() {
        SpringControllerRules.ControllerNameWithoutRequestMapping.check(importedClassesFromPackageController);
    }

    @Test
    void test_request_mapping_in_controller_classes() {
        request_mapping_in_controller_classes.check(importedClassesFromPackageController);
    }

    static final ArchRule request_mapping_in_controller_classes =
            methods()
                    .that(are(springAnnotatedWith(RequestMapping.class)))
                    .should().beDeclaredInClassesThat(are(springAnnotatedWith(Controller.class)));

    @Test
    void controller_all_paths_lowercase() {
        classes_all_paths_lowercase.check(importedClassesFromPackageController);
        methods_all_paths_lowercase.check(importedClassesFromPackageController);
    }

    static final ArchRule classes_all_paths_lowercase =
            classes()
                    .that(are(springAnnotatedWith(RequestMapping.class)
                            .as("annotated with @RequestMapping (or @GetMapping, @PostMapping etc.)")))
                    .should(havePathLowercaseInRequestMappingForClass());

    static final ArchRule methods_all_paths_lowercase =
            methods()
                    .that(are(springAnnotatedWith(RequestMapping.class)
                            .as("annotated with @RequestMapping (or @GetMapping, @PostMapping etc.)")))
                    .should(havePathLowercaseInRequestMappingForMethod());

    private static ArchCondition<? super JavaClass> havePathLowercaseInRequestMappingForClass() {
        return have(springAnnotatedWith(RequestMapping.class,
                describe("@RequestMapping(path=<lower-case>)",
                        SpringControllerTest::isPathLowercase))
        ).as("have path containing only lowercase characters");
    }

    private static ArchCondition<? super JavaMethod> havePathLowercaseInRequestMappingForMethod() {
        return have(springAnnotatedWith(RequestMapping.class,
                describe("@RequestMapping(path=<lower-case>)",
                        SpringControllerTest::isPathLowercase))
        ).as("have path containing only lowercase characters");
    }

    @Test
    void controller_methods_not_having_trailing_slash_url() {
        methods_not_having_trailing_slash_url.check(importedClassesFromPackageController);
    }

    static final ArchRule methods_not_having_trailing_slash_url =
            methods()
                    .that(are(springAnnotatedWith(RequestMapping.class)
                            .as("annotated with @RequestMapping (or @GetMapping, @PostMapping etc.)")))
                    .should(haveNoTrailingSlashInPathInRequestMappingForMethod());

    private static ArchCondition<? super JavaMethod> haveNoTrailingSlashInPathInRequestMappingForMethod() {
        return have(springAnnotatedWith(RequestMapping.class,
                describe("No trailing slash in path in @RequestMapping",
                        SpringControllerTest::isPathNotEndingWithTrailingSlash))
        ).as("have path no ending with slash");
    }

    private static boolean isPathLowercase(RequestMapping requestMapping) {
        return Stream.concat(
                        Arrays.stream(requestMapping.value()),
                        Arrays.stream(requestMapping.path())
                )
                .allMatch(path -> path.toLowerCase().equals(path));
    }

    private static boolean isPathNotEndingWithTrailingSlash(RequestMapping requestMapping) {
        return Stream.concat(
                        Arrays.stream(requestMapping.value()),
                        Arrays.stream(requestMapping.path())
                )
                .filter(path -> !path.equals("/"))
                .noneMatch(path -> path.endsWith("/"));
    }

    static final ArchRule no_trailing_slash_in_request_mappings =
            methods()
                    .that().areAnnotatedWith(GetMapping.class)
                    .or().areAnnotatedWith(PostMapping.class)
                    .or().areAnnotatedWith(PutMapping.class)
                    .or().areAnnotatedWith(DeleteMapping.class)
                    .should(ArchConditions.notBeStatic());

}
