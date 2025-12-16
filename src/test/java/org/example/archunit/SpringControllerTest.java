package org.example.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.conditions.ArchConditions;
import de.rweisleder.archunit.spring.framework.SpringControllerRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

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

    static final ArchRule no_trailing_slash_in_request_mappings =
            methods()
                    .that().areAnnotatedWith(GetMapping.class)
                    .or().areAnnotatedWith(PostMapping.class)
                    .or().areAnnotatedWith(PutMapping.class)
                    .or().areAnnotatedWith(DeleteMapping.class)
                    .should(ArchConditions.notBeStatic());

}
