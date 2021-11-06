package org.schors.gos;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.schors.gos");

        noClasses()
            .that()
            .resideInAnyPackage("org.schors.gos.service..")
            .or()
            .resideInAnyPackage("org.schors.gos.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..org.schors.gos.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
